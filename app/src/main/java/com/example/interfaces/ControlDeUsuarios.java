package com.example.interfaces;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.interfaz1.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class ControlDeUsuarios extends AppCompatActivity {
    ImageView imageView;
    TextView name,email,id;
    Button signOut;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_de_usuarios);

        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null){
                    mostrarDatosUsuario(user);
                }
            }
        };
        imageView=findViewById(R.id.imageView);
        name=findViewById(R.id.textNombre);
        email=findViewById(R.id.textEmail);
        id=findViewById(R.id.textID);
        signOut=findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.signOut:
                        signOut();
                        break;
                }
            }
        });

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            name.setText(personName);
            email.setText(personEmail);
            id.setText(personId);
            imageView.setImageURI(personPhoto);
        }

    }

    private void mostrarDatosUsuario(FirebaseUser user) {
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
        id.setText(user.getUid());
        Glide.with(this).load(user.getPhotoUrl()).into(imageView);
        //registrarMySQL(user);
    }

   /*private void registrarMySQL(FirebaseUser user) {
        boolean valido=false;
        ConexionBaseDatos con=ConexionBaseDatos.getInstancia();
        if(user!=null){
            valido=con.nuevaHuellaUsuario(user.getEmail().toString(),user.getDisplayName().toString(),
            "Google",user.getUid(),user.getPhoneNumber().toString());
        }
        else {
            valido=con.nuevaHuellaUsuario(user.getEmail().toString(),user.getDisplayName().toString(),
                    "Otra",user.getUid(),user.getPhoneNumber().toString());
        }
        if(valido==true){
            Toast.makeText(ControlDeUsuarios.this,"SQL Exitoso",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(ControlDeUsuarios.this,"SQL Invalido",Toast.LENGTH_SHORT).show();
        }

    }*/

    private void signOut() {
        firebaseAuth.signOut();

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ControlDeUsuarios.this,"Cierre de sesión exitoso",Toast.LENGTH_SHORT).show();
                        irMain();
                        finish();
                    }
                });
    }

    public void irMain(){
        Intent i = new Intent(ControlDeUsuarios.this,MainActivity.class);
        startActivity(i);
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

}
