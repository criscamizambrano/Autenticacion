package com.example.interfaces;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegistroBaseDatos extends StringRequest {
    private static final String ruta="http://alimapp.com/registrar_con_correo.php";
    private Map<String,String> parametros;
    public RegistroBaseDatos(String nombre, String correo, String clave, Response.Listener<String> listener){
        super(Request.Method.POST,ruta,listener,null);
        parametros=new HashMap<>();
        parametros.put("correo",correo);
        parametros.put("nombre",nombre);
        parametros.put("clave",clave);
    }

    @Override
    protected Map<String, String> getParams(){
        return parametros;
    }

}
