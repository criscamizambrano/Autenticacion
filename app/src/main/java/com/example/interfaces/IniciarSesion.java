package com.example.interfaces;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.IndianCalendar;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
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

public class IniciarSesion extends AppCompatActivity{
    private ProgressBar progressBar;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN=0;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText correo,clave;
    private Button btnCorreoSQL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        correo=findViewById(R.id.correo_electronico);
        clave=findViewById(R.id.contraseña);
        btnCorreoSQL=findViewById(R.id.buttonCorreoSQL);
        btnCorreoSQL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo_text=correo.getText().toString();
                String contraseña_text=clave.getText().toString();
                if(validarCampos(correo_text,contraseña_text)){

                }
            }
        });
        progressBar= (ProgressBar) findViewById(R.id.progress);
        //Autenticación con Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@Nullable FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //Pregunta si ya existe un usuario logueado
                if (user != null) {
                    irControlDeUsuarios();
                    IniciarSesion.this.finish();
                }
            }
        };


        signInButton=(SignInButton) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.signInButton:
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

    private boolean validarCampos(String correo_text, String contraseña_text) {
        boolean sin_campos_vacios=false;
        boolean email_valido=false;

        if(correo_text.isEmpty() || contraseña_text.isEmpty()){
            Toast.makeText(IniciarSesion.this,"Existen campos vacios, intente nuevamente",Toast.LENGTH_SHORT).show();
        }else{
            sin_campos_vacios=true;
        }
        if(!validarEmail(correo_text)){
            Toast.makeText(IniciarSesion.this,"Email no valido",Toast.LENGTH_SHORT).show();
        }else{
            email_valido=true;
        }
        if(sin_campos_vacios && email_valido){
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
                            Toast.makeText(getApplicationContext(), "Autenticación fallida", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Acceso correcto",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void iniciarSesionCorreo(View view){
        final String correo_text=correo.getText().toString();
        final String clave_text=clave.getText().toString();
        Response.Listener<String> respuesta=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonRespuesta=new JSONObject(response);
                    boolean ok=jsonRespuesta.getBoolean("success");
                    if(ok==true){
                        String correo=jsonRespuesta.getString("correo");
                        Intent control=new Intent(IniciarSesion.this,ControlDeUsuarios.class);
                        control.putExtra("correo",correo);
                        IniciarSesion.this.startActivity(control);
                        IniciarSesion.this.finish();
                    }else
                    {
                        AlertDialog.Builder alerta = new AlertDialog.Builder(IniciarSesion.this);
                        alerta.setMessage("Fallo en el Login").setNegativeButton("Reintentar",null).create().show();
                    }
                }catch (JSONException e){
                    e.getMessage();
                }
            }
        };
        LoginBaseDatos r=new LoginBaseDatos(correo_text,clave_text, respuesta);
        RequestQueue cola= Volley.newRequestQueue(IniciarSesion.this);
        cola.add(r);

    }
    public void irControlDeUsuarios(){
        Intent i = new Intent(IniciarSesion.this,ControlDeUsuarios.class);
        startActivity(i);
    }

}
