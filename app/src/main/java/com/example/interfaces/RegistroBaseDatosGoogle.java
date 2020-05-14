package com.example.interfaces;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegistroBaseDatosGoogle extends StringRequest{
    private static final String ruta="http://alimapp.com/registrar_con_google.php";
    private Map<String,String> parametros;
    public RegistroBaseDatosGoogle(String nombre, String correo, String uid,String telefono, String url_foto, Response.Listener<String> listener){

        super(Request.Method.POST,ruta,listener,null);
        if(telefono==null){
            telefono="N/A";
        }
        parametros=new HashMap<>();
        parametros.put("correo",correo);
        parametros.put("nombre",nombre);
        parametros.put("uid",uid);
        parametros.put("telefono",telefono);
        parametros.put("urlfoto",url_foto);
    }

    @Override
    protected Map<String, String> getParams(){
        return parametros;
    }
}
