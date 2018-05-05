package com.example.galo.espolguide;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.galo.espolguide.pois.AppController;
import com.example.galo.espolguide.pois.Bloque;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import org.osmdroid.api.IMapController;
import org.json.JSONException;
import org.json.JSONObject;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;

import static espolguide.helpers.constants.Constantes.IP_COMSOC;
import static espolguide.helpers.constants.Constantes.IP_FAB;
import static espolguide.helpers.constants.Constantes.IP_LAB_SOFT;
import static espolguide.helpers.constants.Constantes.IP_LAB_SOFT_FAB;
import static espolguide.helpers.constants.Constantes.IP_TAWS_FAB;

/**
 * Created by galo on 29/12/17.
 */

public class MainActivity extends Activity {
    final ArrayList<String> items_nombres = new ArrayList<>();
    ListView search_poi_sv;
    EditText editsearch;

    SearchViewAdapter adapter;
    MapView mapView;

    JSONObject jsonObj;




    String obtenerBloques_ws = "http://" + "192.168.0.5:8000" + "/obtenerBloques/";
    String nombresAlternativo_ws = "http://" + "192.168.0.5:8000" + "/nombresAlternativo/";


    //String geocampus_webserviceURL = "http://sigeo.espol.edu.ec/geoapi/geocampus/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=geocampus:BLOQUES&srsName=EPSG:4326&outputFormat=application%2Fjson";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        double ESPOL_CENTRAL_LONG = -79.96575;
        double ESPOL_CENTRAL_LAT = -2.14630;
        int START_ZOOM = 18;
        int ZOOM_MAX = 20;

        super.onCreate(savedInstanceState);
        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final MapView map = (MapView) findViewById(R.id.mapview);
        final LinearLayout info = (LinearLayout) findViewById(R.id.overlay);
        map.setClickable(true);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        IMapController map_controller = map.getController();
        map_controller.setZoom(START_ZOOM);
        GeoPoint espol_central_point = new GeoPoint(ESPOL_CENTRAL_LAT, ESPOL_CENTRAL_LONG);
        map_controller.setCenter(espol_central_point);

        Button close_poi_button = (Button) findViewById(R.id.close_poi_info_button);
        close_poi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout info = (LinearLayout) findViewById(R.id.overlay);
                info.setVisibility(View.GONE);
                System.out.println("Capturo click en boton de cerrar");
            }
        });

        map.setMaxZoomLevel(ZOOM_MAX);

        this.mapView = map;
        new Drawer().execute(new DrawingTools(this,map,info));
        new Nombres().execute(ctx);

    }

    public MapView getMapView(){
        return this.mapView;
    }

    private class DrawingTools {
        Context context;
        MapView map;
        View info;

        public DrawingTools(Context ctx, MapView map, View info){
            this.context = ctx;
            this.map = map;
            this.info = info;
        }
    }

    private class Drawer extends AsyncTask<DrawingTools, Void, Void>{
        DrawingTools actual ;
        @Override
        protected Void doInBackground(DrawingTools... dts) {
            actual = dts[0];
            if (!isNetworkAvailable(actual.context)) {
                Toast.makeText(actual.context, "Conexión no disponible", Toast.LENGTH_LONG).show();
            } else {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        obtenerBloques_ws, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray features = response.getJSONArray("features");
                            int total_features = features.length();
                            response = null;
                            for (int i = 0; i < total_features; i++) {
                                JSONObject jsonObj = (JSONObject) features.get(i);
                                String identificador = jsonObj.getString("identificador");
                                JSONObject jsonObj_geometry = jsonObj.getJSONObject("geometry");
                                JSONArray coordenadas = jsonObj_geometry.getJSONArray("coordinates").getJSONArray(0);
                                Bloque bloque = new Bloque(identificador);
                                bloque.construir_poligono(coordenadas, actual.map, actual.context, actual.info);
                                bloque = null;
                                jsonObj = null;
                                coordenadas = null;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error cargando datos...", Toast.LENGTH_LONG).show();
                        } finally {
                            System.gc();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("tag", "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Error HTTP", Toast.LENGTH_SHORT).show();
                    }
                });
                AppController.getInstance(actual.context).addToRequestQueue(jsonObjReq);
            }
            return null;
        }
    }

    private class Nombres extends AsyncTask<Context, Void, ArrayList> {
        Context context;
        @Override
        protected ArrayList doInBackground(Context... contexts) {
            context = contexts[0];
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    nombresAlternativo_ws, null,  new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Iterator<String> iter = response.keys();
                    while (iter.hasNext()) {
                        String identificador = iter.next();
                        Integer identificador_num = Integer.getInteger(identificador.substring(6));
                        //System.out.println(identificador_num);
                        if (identificador_num == null || identificador_num <= 69){

                        try {
                            String bloque_str = "";
                            JSONObject info_bloque = (JSONObject) response.get(identificador);
                            String nombre_oficial = (String) info_bloque.getString("NombreOficial");
                            JSONArray nombres_alternativos = info_bloque.getJSONArray("NombresAlternativos");
                            int total_alternativos = nombres_alternativos.length();
                            String cadena_alternativos = "";
                            for (int i = 0; i < total_alternativos; i++) {
                                String alternativo = (String) nombres_alternativos.get(i);
                                cadena_alternativos = cadena_alternativos + "|" + alternativo;
                            }
                            bloque_str = identificador +
                                    ";" + nombre_oficial + ";" + cadena_alternativos;
                            items_nombres.add(bloque_str);

                        } catch (JSONException e) {
                            continue;
                        }}
                    }


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
                        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                            // TODO Auto-generated method stub
                            search_poi_sv.setVisibility(View.VISIBLE);
                        }
                    });
                    search_poi_sv = (ListView) findViewById(R.id.listview);
                    adapter = new SearchViewAdapter(context,mapView, items_nombres,editsearch);
                    adapter.setMapView(getMapView());
                    // Binds the Adapter to the ListView
                    search_poi_sv.setAdapter(adapter);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("tag", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Error HTTP", Toast.LENGTH_SHORT).show();
                }
            });
            AppController.getInstance(context).addToRequestQueue(jsonObjReq);
            return items_nombres;
        }
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void onResume(){
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }
}

