package com.example.interfaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.interfaz1.R;

public class Sopa extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sopa);
    }
    public void irAjiaco(View view){
        Intent i = new Intent(Sopa.this,Ajiaco.class);
        startActivity(i);
        /* startActivity(i); */
    }
}
