package com.example.galo.espolguide;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import android.view.View;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;

import com.example.galo.espolguide.utils.Constants;
import com.example.galo.espolguide.viewModels.MapViewModel;

/**
 * Created by galo on 29/12/17.
 */

public class MapActivity extends AppCompatActivity implements Observer {
    ViewHolder viewHolder;
    MapViewModel viewModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        this.viewHolder = new ViewHolder();
        this.viewModel = new MapViewModel(this);
        this.viewModel.addObserver(this);
        this.viewModel.makelBocksShapesRequest();
        this.viewModel.makeNamesRequest();
    }

    public class ViewHolder{
        public ListView searchPoiLv;
        public EditText editSearch;
        public MapView mapView;
        public LinearLayout info;
        public Button closePoiInfoBtn;

        public ViewHolder(){
            findViews();
            setMapInitialState();
            setClosePoiButtonListener();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        private void findViews(){
            mapView = (MapView) findViewById(R.id.mapview);
            info = (LinearLayout) findViewById(R.id.overlay);
            closePoiInfoBtn = (Button) findViewById(R.id.close_poi_info_button);
            editSearch = (EditText) findViewById(R.id.search);
            searchPoiLv = (ListView) findViewById(R.id.listview);
            }

        private void setClosePoiButtonListener(){
            this.closePoiInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final LinearLayout info = (LinearLayout) findViewById(R.id.overlay);
                    info.setVisibility(View.GONE);
                    System.out.println("Capturo click en boton de cerrar");
                }
            });
        }

        private void setMapInitialState(){
            this.mapView.setClickable(true);
            this.mapView.setTileSource(TileSourceFactory.MAPNIK);
            this.mapView.setMultiTouchControls(true);
            IMapController map_controller = this.mapView.getController();
            map_controller.setZoom(Constants.START_ZOOM);
            GeoPoint espol_central_point = new GeoPoint(Constants.ESPOL_CENTRAL_LAT, Constants.ESPOL_CENTRAL_LONG);
            map_controller.setCenter(espol_central_point);
            this.mapView.setMaxZoomLevel(Constants.ZOOM_MAX);
        }
    }

    public ViewHolder getViewHolder(){
        return this.viewHolder;
    }

    @Override
    public void update(Observable o, Object arg) {
        String message = (String)arg;
        if (message == viewModel.DRAW_REQUEST_STARTED) {

        }
        if (message == viewModel.DRAW_REQUEST_SUCCEED) {

        }
        if (message == viewModel.DRAW_REQUEST_FAILED_CONNECTION) {
            MapActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MapActivity.this, "Conexión a Internet no disponible", Toast.LENGTH_LONG).show();
                }
            });
        }
        if (message == viewModel.DRAW_REQUEST_FAILED_LOADING) {
            MapActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MapActivity.this, "Error cargando bloques...", Toast.LENGTH_LONG).show();
                }
            });
        }
        if (message == viewModel.DRAW_REQUEST_FAILED_HTTP) {
            MapActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MapActivity.this, "Error HTTP", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (message == viewModel.NAMES_REQUEST_STARTED) {

        }
        if (message == viewModel.NAMES_REQUEST_FAILED_CONNECTION) {
            MapActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MapActivity.this, "Conexión a Internet no disponible", Toast.LENGTH_LONG).show();
                }
            });
        }
        if (message == viewModel.NAMES_REQUEST_FAILED_LOADING) {
            MapActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MapActivity.this, "Error cargando nombres de sitios...", Toast.LENGTH_LONG).show();
                }
            });
        }
        if (message == viewModel.NAMES_REQUEST_FAILED_HTTP) {
            MapActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MapActivity.this, "Error HTTP", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onResume(){
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }
}
