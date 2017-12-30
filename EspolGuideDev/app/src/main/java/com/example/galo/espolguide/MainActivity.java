package com.example.galo.espolguide;

import java.io.File;
import java.io.BufferedReader;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.EditText;
import android.widget.SearchView;
import org.osmdroid.api.IMapController;



/**
 * Created by galo on 29/12/17.
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_map);
        MapView map = (MapView) findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(18);
        GeoPoint startPoint = new GeoPoint(-2.14630, -79.96575);
        mapController.setCenter(startPoint);
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



