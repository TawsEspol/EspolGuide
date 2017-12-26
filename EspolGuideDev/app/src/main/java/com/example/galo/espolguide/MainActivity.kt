package com.example.galo.espolguide
import kotlinx.android.synthetic.main.activity_map.*


/**
 * Created by fabricio on 26/12/17.
 */
import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import org.osmdroid.config.Configuration


import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView


class MainActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        val mMapView : MapView
        val mMapController : MapController
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(R.layout.activity_map)

        val map = findViewById<View>(R.id.mapview) as MapView
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map.setBuiltInZoomControls(true)
        val map_ctrl = map.getController() as MapController
        map_ctrl.setZoom(18)
        val gPt = GeoPoint(-2.14630,-79.96575)
        map_ctrl.setCenter(gPt)
    }

    public override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
    }
}