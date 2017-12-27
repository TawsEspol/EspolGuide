package com.example.galo.espolguide
import kotlinx.android.synthetic.main.activity_map.*


/**
 *
 * Created by fabricio on 26/12/17.
 */

import java.io.File
import java.io.BufferedReader
import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import org.osmdroid.bonuspack.location.GeocoderNominatim
import org.osmdroid.config.Configuration

import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.bonuspack.location.POI
import org.osmdroid.bonuspack.location.NominatimPOIProvider
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.bonuspack.location.OverpassAPIProvider

class MainActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        //val mMapView : MapView
        //val mMapController : MapController
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(R.layout.activity_map)

        val map = findViewById<View>(R.id.mapview) as MapView
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map.setMultiTouchControls(true)
        val map_ctrl = map.getController() as MapController
        map_ctrl.setZoom(18)
        val gPt = GeoPoint(-2.14630, -79.96575)
        map_ctrl.setCenter(gPt)
        //val overpassProvider = OverpassAPIProvider()
        //overpassProvider.getPOIsFromUrl(overpassProvider.urlForPOISearch("restaurante", map.boundingBox, 200, 30))
/*
        val startMarker = Marker(map)
        startMarker.position = GeoPoint(-2.14630,-79.96575)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(startMarker)
*/
    }

    public override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
    }
    /*
    fun readFile(nomFile: String) {

        val lineList = mutableListOf<String>()

        File("kotlination.txt").useLines { lines -> lines.forEach { lineList.add(it) } }
        lineList.forEach { println(">  " + it) }
    }
    */

}