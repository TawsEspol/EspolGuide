package com.example.galo.espolguide;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.galo.espolguide.pois.AppController;
import com.example.galo.espolguide.pois.Bloque;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;

import org.json.JSONArray;
import org.osmdroid.config.Configuration;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.content.Context;
import android.widget.Adapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import org.osmdroid.api.IMapController;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by galo on 29/12/17.
 */

public class MainActivity extends Activity {
    //String[] rank;
    EditText editsearch;
    ArrayList<String> country;
    SearchViewAdapter adapter;

    JSONObject jsonObj;
    ProgressDialog pDialog;

    String IP_LAB_SOFT = "172.19.66.151:8000";
    String IP_GALO = "192.168.0.13:8000";
    String IP_TAWS = "192.168.0.126:8000";

    String obtenerBloques_ws = "http://" + IP_TAWS + "/obtenerBloques/";
    String nombresAlternativo_ws = "http://" + IP_TAWS + "/nombresAlternativo/";
    //String geocampus_webserviceURL = "http://sigeo.espol.edu.ec/geoapi/geocampus/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=geocampus:BLOQUES&srsName=EPSG:4326&outputFormat=application%2Fjson";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        //rank = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

        country = new ArrayList<String>();
        country.add("China,Hola");
        country.add("India,Tracy");
        country.add("United States,Rezar");
        country.add("Indonesia,Apu");
        country.add("Brazil,Marmando");
        country.add("Pakistan,Chounli");
        country.add("Nigeria,Galo");
        country.add("Bangladesh,Jugo");
        country.add("Russia,Fabricio");
        country.add("Japan,Mabe");

        double ESPOL_CENTRAL_LONG = -79.96575;
        double ESPOL_CENTRAL_LAT = -2.14630;
        int START_ZOOM = 18;
        int ZOOM_MAX = 20;
        int SEARCH_POI_FONTSIZE = 15;

        super.onCreate(savedInstanceState);
        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_map);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final MapView map = (MapView) findViewById(R.id.mapview);

        map.setClickable(true);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        IMapController map_controller = map.getController();
        map_controller.setZoom(START_ZOOM);
        GeoPoint espol_central_point = new GeoPoint(ESPOL_CENTRAL_LAT, ESPOL_CENTRAL_LONG);
        map_controller.setCenter(espol_central_point);
        final ListView search_poi_sv = (ListView) findViewById(R.id.listview);
        /*
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_poi_sv.setVisibility(View.INVISIBLE);
            }
        });*/
        //(search_poi_sv, SEARCH_POI_FONTSIZE);

        // Locate the ListView in listview_main.xml

        // Pass results to ListViewAdapter Class

        adapter = new SearchViewAdapter(this, country);

        // Binds the Adapter to the ListView
        search_poi_sv.setAdapter(adapter);

        // Locate the EditText in listview_main.xml
        editsearch = (EditText) findViewById(R.id.search);

        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
                search_poi_sv.setVisibility(0);
            }
        });

        map.setMaxZoomLevel(ZOOM_MAX);

            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Espera mientras carga el mapa de ESPOL...");
            pDialog.setCancelable(false);
            if (!isNetworkAvailable(this)) {
                Toast.makeText(this, "No se pueden obtener datos, revisa tu conexion a Internet...", Toast.LENGTH_LONG).show();
            } else {
                pDialog.show();
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        obtenerBloques_ws, null,  new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray features = response.getJSONArray("features");
                            int total_features = features.length();
                            response = null;
                            for (int i=0; i<total_features; i++){
                                JSONObject jsonObj = (JSONObject) features.get(i);
                                String identificador = jsonObj.getString("identificador");
                                JSONObject jsonObj_geometry = jsonObj.getJSONObject("geometry");
                                JSONArray coordenadas = jsonObj_geometry.getJSONArray("coordinates").getJSONArray(0);
                                Bloque bloque = new Bloque(identificador);
                                bloque.construir_poligono(coordenadas, map, ctx);
                                jsonObj = null;
                                coordenadas = null;
                                pDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error cargando datos...", Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        }
                        finally {
                            System.gc();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("tag", "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Error HTTP...", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();

                    }
                });
                // Adding request to request queue
            AppController.getInstance(this).addToRequestQueue(jsonObjReq);
        }
    }

    public HashMap<String, ArrayList<String>> obtenerNombres(String webservice_url, MapView map,
                                                             Context ctx){
        HashMap<String, ArrayList<String>> nombres = new HashMap<>();

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Espera mientras cargan los nombres...");
        pDialog.setCancelable(false);
        if (!isNetworkAvailable(this)) {
            Toast.makeText(this, "No se pueden obtener los nombres, revisa tu conexion a Internet...", Toast.LENGTH_LONG).show();
        } else {
            pDialog.show();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    nombresAlternativo_ws, null,  new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray features = response.getJSONArray("features");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error cargando datos...", Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                    }
                    finally {
                        System.gc();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("tag", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Error HTTP...", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            });
            // Adding request to request queue
            AppController.getInstance(this).addToRequestQueue(jsonObjReq);
        }
        return nombres;
    }


    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }
}



