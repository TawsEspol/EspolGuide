package com.example.galo.espolguide;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.galo.espolguide.pois.AppController;
import com.example.galo.espolguide.pois.Bloque;
import com.example.galo.espolguide.pois.Poi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import espolguide.helpers.constants.Constantes;

import static espolguide.helpers.constants.Constantes.IP_FAB;
import static espolguide.helpers.constants.Constantes.IP_LAB_SOFT_FAB;
import static espolguide.helpers.constants.Constantes.isNetworkAvailable;

/**
 * Esta clase es auxiliar. Ayuda a modelar y visualizar la información de cada POI.
 * Created by fabricio on 07/01/18.
 */

public class POI_vista {

    private Context ctx;
    private View view;
    private String codigo;
    private String nombre;
    private String unidad;
    private int favoritos_count;
    private String descripcion;
    private ArrayList<String> nombres_alternativos = new ArrayList<String>();
    String infoBloque = "http://" + IP_FAB + "/infoBloque/";


    public POI_vista(Poi poi){
        codigo = poi.getCodigo();
        nombre = poi.getNombre();
        unidad = poi.getUnidad();
        favoritos_count = poi.getFavoritos_count();
        descripcion = poi.getDescripcion();
    }

    public POI_vista(String id, Context ctx, View view){
        this.codigo = String.valueOf(Integer.parseInt(id.split("Bloque")[1]) % 69);
        this.ctx = ctx;
        this.view = view;
        new Info().execute(this);

    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public int getFavoritos_count() {
        return favoritos_count;
    }

    public void setFavoritos_count(int favoritos_count) {
        this.favoritos_count = favoritos_count;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ArrayList<String> getNombres_alternativos() {
        return nombres_alternativos;
    }

    public void setNombres_alternativos(ArrayList<String> nombres_alternativos) {
        this.nombres_alternativos = nombres_alternativos;
    }


    private class Info extends AsyncTask<POI_vista, Void, ArrayList> {
        POI_vista data;

        @Override
        protected ArrayList doInBackground(POI_vista... pois) {
            data = pois[0];
            if (!isNetworkAvailable(data.ctx)) {
                Toast.makeText(data.ctx, "Conexión no disponible", Toast.LENGTH_LONG).show();
            } else {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        infoBloque + data.getCodigo(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray features = response.getJSONArray("features");
                            int total_features = features.length();
                            response = null;
                            for (int i = 0; i < total_features; i++) {
                                JSONObject jsonObj = (JSONObject) features.get(i);
                                JSONObject properties = jsonObj.getJSONObject("properties");
                                JSONObject jsonObj_geometry = jsonObj.getJSONObject("geometry");
                                JSONArray coordenadas = jsonObj_geometry.getJSONArray("coordinates").getJSONArray(0);
                                System.out.println(properties);

                                data.setCodigo(properties.getString("codigo"));
                                data.setUnidad(properties.getString("unidad"));
                                data.setDescripcion(properties.getString("descripcio"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(data.ctx, "Error cargando datos...", Toast.LENGTH_LONG).show();
                        } finally {
                            System.gc();
                        }
                        data.show();
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("tag", "Error: " + error.getMessage());
                        Toast.makeText(data.ctx, "Error HTTP", Toast.LENGTH_SHORT).show();
                    }
                });
                AppController.getInstance(data.ctx).addToRequestQueue(jsonObjReq);
                // }
                return null;
            }
            return null;
        }



    }

    public void show() {
            ViewGroup nextChild = (ViewGroup) ((ViewGroup)view).getChildAt(0);
            ViewGroup linear = (ViewGroup) nextChild.getChildAt(2);
            TextView codigo= (TextView) linear.getChildAt(0);
            codigo.setText(this.getCodigo());
            TextView facultad = (TextView) linear.getChildAt(1);
            facultad.setText(this.getUnidad());
            TextView descripcion = (TextView) nextChild.getChildAt(3);
            descripcion.setText(this.getDescripcion());
            view.setVisibility(View.VISIBLE);

        }


    }


