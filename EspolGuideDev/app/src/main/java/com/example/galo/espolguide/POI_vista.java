package com.example.galo.espolguide;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.galo.espolguide.pois.Poi;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

/**
 * Esta clase es auxiliar. Ayuda a modelar y visualizar la información de cada POI.
 * Created by fabricio on 07/01/18.
 */

public class POI_vista extends Activity{
    private String codigo;
    private String nombre;
    private String unidad;
    private int favoritos_count;
    private String descripcion;
    private ArrayList<String> nombres_alternativos = new ArrayList<String>();

    public POI_vista(Poi poi){
        codigo = poi.getCodigo();
        nombre = poi.getNombre();
        unidad = poi.getUnidad();
        favoritos_count = poi.getFavoritos_count();
        descripcion = poi.getDescripcion();
    }

    protected void onCreate(Bundle savedInstanceState) {
        double ESPOL_CENTRAL_LONG = -79.96575;
        double ESPOL_CENTRAL_LAT = -2.14630;
        int START_ZOOM = 18;
        int ZOOM_MAX = 20;
        int SEARCH_POI_FONTSIZE = 15;

        super.onCreate(savedInstanceState);
        //Context ctx = getApplicationContext();
        //Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setContentView(R.layout.poi_info);
        //TextView name = (TextView) findViewById(R.id.text);
        //name.setText(nombre);
        //final Dialog dialog = new Dialog(this); // Context, this, etc.
        //dialog.setContentView(R.layout.poi_info);


    }

}
