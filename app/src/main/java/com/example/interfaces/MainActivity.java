package com.example.interfaces;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.example.interfaz1.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@Nullable FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //Pregunta si ya existe un usuario logueado
                if (user != null) {
                    irControlDeUsuarios();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
        super.onStart();
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(this);
    }
    @Override
    protected void onStop() {
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
        super.onStop();
    }

    public void iniciarSesion(View view){
        Intent i = new Intent(MainActivity.this,IniciarSesion.class);
        startActivity(i);
        /* startActivity(i); */
    }
    public void irControlDeUsuarios(){
        Intent i = new Intent(MainActivity.this,ControlDeUsuarios.class);
        startActivity(i);
        MainActivity.this.finish();
    }
    public void Registrarse(View view){
        Intent i = new Intent(MainActivity.this,Registro.class);
        startActivity(i);
        /* startActivity(i); */
    }
}
