package com.example.interfaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.interfaz1.R;

public class Recetas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recetas);
    }
    public void irSopa(View view){
        Intent i = new Intent(Recetas.this,Sopa.class);
        startActivity(i);
        /* startActivity(i); */
    }
}
