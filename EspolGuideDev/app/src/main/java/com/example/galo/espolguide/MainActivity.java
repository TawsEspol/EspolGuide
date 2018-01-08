package com.example.galo.espolguide;

import com.example.galo.espolguide.pois.Bloque;
import com.example.galo.espolguide.pois.Poi;

import java.io.File;
import java.io.BufferedReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.config.Configuration;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.bonuspack.location.OverpassAPIProvider;
import android.app.SearchManager;
import android.R.menu;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;




/**
 * Created by galo on 29/12/17.
 */

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        double ESPOL_CENTRAL_LONG = -79.96575;
        double ESPOL_CENTRAL_LAT = -2.14630;
        int START_ZOOM = 18;
        int ZOOM_MAX = 20;
        int SEARCH_POI_FONTSIZE = 15;

        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_map);
        MapView map = (MapView) findViewById(R.id.mapview);

        map.setClickable(true);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        IMapController map_controller = map.getController();
        map_controller.setZoom(START_ZOOM);
        GeoPoint espol_central_point = new GeoPoint(ESPOL_CENTRAL_LAT, ESPOL_CENTRAL_LONG);
        map_controller.setCenter(espol_central_point);
        SearchView search_poi_sv = (SearchView) findViewById(R.id.POI_search_view);
        setSearchviewTextSize(search_poi_sv, SEARCH_POI_FONTSIZE);

        Button view_poi = (Button) findViewById(R.id.button_id);
        view_poi.setX(430);
        view_poi.setY(1200);
        view_poi.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                openPOIinfo();
                /*
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast,
                        (ViewGroup) findViewById(R.id.custom_toast_container));

                TextView text = (TextView) layout.findViewById(R.id.text);
                //text.setText("This is a custom toast");
                //setContentView(R.layout.custom_toast);

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.TOP, 0, 0);
                //toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();*/
            }
        });
        map.setMaxZoomLevel(ZOOM_MAX);






        //PRUEBAS
         String codigo = "mp-1";
         String nombre = "Mopol";
         String unidad = "N/A";
         int favoritos_count = 55;
         String descripcion = "Area verde recreativa";
         ArrayList<String> nombres_alternativos = new ArrayList<String>();
         String geo_json_string = "{ \"type\": \"FeatureCollection\", \"features\": [ { \"type\": \"Feature\", \"properties\": {}, \"geometry\": { \"type\": \"Polygon\", \"coordinates\": [ [ [ -79.96602773666382, -2.1462682376840805 ], [ -79.96617794036865, -2.1463700901212497 ], [ -79.9659714102745, -2.146501426148642 ], [ -79.96582120656967, -2.14674265555733 ], [ -79.96550738811493, -2.1465979179166923 ], [ -79.96552348136902, -2.146356688485168 ], [ -79.96602773666382, -2.1462682376840805 ] ] ] } } ] }";
        nombres_alternativos.add("Mopolito");
        nombres_alternativos.add("Mopolito2");

         //Hay que construir el ImageView
        // ImageView foto_iv = new ImageView();
//         Bloque bloque_mopol_test = new Bloque(codigo, nombre, unidad, favoritos_count,
//                descripcion, nombres_alternativos, geo_json_string, foto_iv);



         Bloque bloque_mopol_test = new Bloque(codigo, nombre, unidad, favoritos_count,
                descripcion, nombres_alternativos, geo_json_string);
        bloque_mopol_test.construir_shape(map, ctx);




        //TERMINAN LAS PRUEBAS


    }

    public void openPOIinfo() {
        final Dialog dialog = new Dialog(this); // Context, this, etc.
        dialog.setContentView(R.layout.custom_toast);
        //dialog.setTitle("Prueba");
        dialog.show();
    }
    private void setSearchviewTextSize(SearchView searchView, int fontSize) {
        try {
            AutoCompleteTextView autoCompleteTextViewSearch = (AutoCompleteTextView) searchView.findViewById(searchView.getContext().getResources().getIdentifier("app:id/search_src_text", null, null));
            if (autoCompleteTextViewSearch != null) {
                autoCompleteTextViewSearch.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            } else {
                LinearLayout linearLayout1 = (LinearLayout) searchView.getChildAt(0);
                LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
                LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
                AutoCompleteTextView autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
                autoComplete.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            }
        } catch (Exception e) {
            LinearLayout linearLayout1 = (LinearLayout) searchView.getChildAt(0);
            LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
            LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
            AutoCompleteTextView autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
            autoComplete.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        }
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



