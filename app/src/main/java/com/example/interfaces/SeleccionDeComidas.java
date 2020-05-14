package com.example.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.interfaz1.R;
import com.github.clans.fab.FloatingActionMenu;

public class SeleccionDeComidas extends AppCompatActivity {
    FloatingActionMenu actionMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_de_comidas);

        //BOTON
        actionMenu=(FloatingActionMenu)findViewById(R.id.fabPrincipalButton);
        actionMenu.setClosedOnTouchOutside(true);

    }

    public void irDesayuno(View view){
        Intent i = new Intent(SeleccionDeComidas.this,Desayuno.class);
        startActivity(i);
        /* startActivity(i); */
    }
    public void irAlmuerzo(View view){
        Intent i = new Intent(SeleccionDeComidas.this,Almuerzo.class);
        startActivity(i);
        /* startActivity(i); */
    }
    public void irCena(View view){
        Intent i = new Intent(SeleccionDeComidas.this,Cena.class);
        startActivity(i);
        /* startActivity(i); */
    }
    public void irAlimentos(View view){
        Intent i = new Intent(SeleccionDeComidas.this,Alimentos.class);
        startActivity(i);
        /* startActivity(i); */
    }
}
