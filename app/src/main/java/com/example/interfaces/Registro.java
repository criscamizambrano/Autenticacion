package com.example.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.interfaz1.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {
    EditText nombre, correo, contraseña, contraseña_val;
    Button registro;
    private ProgressBar progressBar;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN=0;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nombre=findViewById(R.id.nombre_registro);
        correo=findViewById(R.id.correo_registro);
        contraseña=findViewById(R.id.clave_registro);
        contraseña_val=findViewById(R.id.clave_val_registro);
        registro=findViewById(R.id.button_registro);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombre_text=nombre.getText().toString();
                String correo_text=correo.getText().toString();
                String contraseña_text=contraseña.getText().toString();
                String contraseñav_text=contraseña_val.getText().toString();

                if(verificarDatos(nombre_text,correo_text,contraseña_text,contraseñav_text)){
                    registrarConSQL(nombre_text,correo_text,contraseña_text);
                }
            }
        }

        );


        progressBar= (ProgressBar) findViewById(R.id.progressR);
        //Autenticación con Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@Nullable FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //Pregunta si ya existe un usuario logueado
                if (user != null) {
                    registrarConGoogleSQL(user);
                    irControlDeUsuarios();
                    Registro.this.finish();
                }
            }
        };


        signInButton=(SignInButton) findViewById(R.id.signInButtonR);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.signInButtonR:
                        signIn();
                        break;
                }
            }
        });
        //Configuracion de opciones para el inicio de sesión
        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    @Override
    protected void onStart() {
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
        super.onStart();
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);
    }

    @Override
    protected void onStop() {
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
        super.onStop();
    }
    //Metodo que inicia sesion con Google
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado retornado de lanzar el Intent de GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // No hay necesidad de listener ya que la tarea siempre se completa
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Si el inicio de sesión resulta exitosa con google, procede a hacerlo con Firebase
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // El objeto ApiException nos provee de errores con mas detalles
            Log.w("Error:", "signInResult:failed code=" + e.getStatusCode());
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {
        progressBar.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.GONE);
        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@Nullable Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        signInButton.setVisibility(View.VISIBLE);
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registro fallido", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Registro con Google correcto",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registrarConGoogleSQL(FirebaseUser user) {
        Response.Listener<String> respuesta=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonRespuesta=new JSONObject(response);
                    boolean ok=jsonRespuesta.getBoolean("success");
                    if(ok==true){
                        Toast.makeText(Registro.this,"Respaldo exitoso",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Registro.this,"Respaldo fallido",Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                    e.getMessage();
                }

            }
        };
        String nombre_text=user.getDisplayName().toString();
        String correo_text=user.getEmail().toString();
        String uid_text=user.getUid().toString();
        String telefono_text=user.getPhoneNumber();
        String url_foto=user.getPhotoUrl().toString();
        RegistroBaseDatosGoogle r =new RegistroBaseDatosGoogle(nombre_text,correo_text,uid_text,telefono_text,url_foto, respuesta);
        RequestQueue cola= Volley.newRequestQueue(Registro.this);
        cola.add(r);
    }

    private void registrarConSQL(String nombre_text, String correo_text, String contraseña_text) {
        Response.Listener<String> respuesta=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonRespuesta=new JSONObject(response);
                    boolean ok=jsonRespuesta.getBoolean("success");
                    if(ok==true){
                        Toast.makeText(Registro.this,"Ha sido registrado, puede iniciar sesión",Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(Registro.this, IniciarSesion.class);
                        Registro.this.startActivity(i);
                        Registro.this.finish();
                    }else{
                        AlertDialog.Builder alerta = new AlertDialog.Builder(Registro.this);
                        alerta.setMessage("Fallo en el registro, verifique que el email no haya sido registrado previamente").setNegativeButton("Reintentar",null).create().show();
                    }

                }catch (JSONException e){
                    e.getMessage();
                }

            }
        };
        RegistroBaseDatos r=new RegistroBaseDatos(nombre_text,correo_text,contraseña_text, respuesta);
        RequestQueue cola= Volley.newRequestQueue(Registro.this);
        cola.add(r);
    }
    private boolean verificarDatos(String nombre_text, String correo_text, String contraseña_text, String contraseñav_text) {
        boolean contraseñas_iguales=false;
        boolean sin_campos_vacios=false;
        boolean email_valido=false;
        if(contraseña_text.equals(contraseñav_text)){
            contraseñas_iguales=true;
        }else{
            Toast.makeText(Registro.this,"Las contraseñas no coinciden",Toast.LENGTH_SHORT).show();
        }
        if(nombre_text.isEmpty() || correo_text.isEmpty() || contraseña_text.isEmpty()){
            Toast.makeText(Registro.this,"Existen campos vacios, intente nuevamente",Toast.LENGTH_SHORT).show();
        }else{
            sin_campos_vacios=true;
        }
        if(!validarEmail(correo_text)){
            Toast.makeText(Registro.this,"Email no valido",Toast.LENGTH_SHORT).show();
        }else{
            email_valido=true;
        }
        if(contraseñas_iguales && sin_campos_vacios && email_valido){
            return true;
        }
        return false;
    }

    private boolean validarEmail(String correo_text) {
        // Patrón para validar el email
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        // Matcher para validar
        Matcher mather = pattern.matcher(correo_text);

        if (mather.find() == true) {
            return true;
        }
        return false;
    }
    public void irControlDeUsuarios(){
        Intent i = new Intent(Registro.this,ControlDeUsuarios.class);
        startActivity(i);
    }

}

