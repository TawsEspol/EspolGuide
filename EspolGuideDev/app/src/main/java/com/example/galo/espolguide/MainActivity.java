package com.example.galo.espolguide;

import com.example.galo.espolguide.pois.Bloque;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;

import org.osmdroid.config.Configuration;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.content.Context;
import android.widget.Adapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.osmdroid.api.IMapController;




/**
 * Created by galo on 29/12/17.
 */

public class MainActivity extends Activity {
    //String[] rank;
    EditText editsearch;
    ArrayList<String> country;
    SearchViewAdapter adapter;
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



        final Bloque bloque_mopol_test = new Bloque(codigo, nombre, unidad, favoritos_count,
            descripcion, nombres_alternativos, geo_json_string);
        bloque_mopol_test.construir_shape(map, ctx);

        //View n = (View) findViewById(R.id.overlay);
        //bloque_mopol_test.onClick(n);


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



