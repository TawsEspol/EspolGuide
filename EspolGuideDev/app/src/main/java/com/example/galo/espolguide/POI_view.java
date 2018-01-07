package com.example.galo.espolguide;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.SearchView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

/**
 * Created by fabricio on 07/01/18.
 */

public class POI_view extends Activity{
    protected void onCreate(Bundle savedInstanceState) {
        double ESPOL_CENTRAL_LONG = -79.96575;
        double ESPOL_CENTRAL_LAT = -2.14630;
        int START_ZOOM = 18;
        int ZOOM_MAX = 20;
        int SEARCH_POI_FONTSIZE = 15;

        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.poi_info);
        //MapView map = (MapView) findViewById(R.id.mapview);

        //map.setClickable(true);
        //map.setTileSource(TileSourceFactory.MAPNIK);
        //map.setMultiTouchControls(true);
        //IMapController map_controller = map.getController();
        //map_controller.setZoom(START_ZOOM);
        //GeoPoint espol_central_point = new GeoPoint(ESPOL_CENTRAL_LAT, ESPOL_CENTRAL_LONG);
        //map_controller.setCenter(espol_central_point);
        //SearchView search_poi_sv = (SearchView) findViewById(R.id.POI_search_view);
        //setSearchviewTextSize(search_poi_sv, SEARCH_POI_FONTSIZE);

        //map.setMaxZoomLevel(ZOOM_MAX);


    }
}
