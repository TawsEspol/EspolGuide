package espol.edu.ec.espolguide;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.geojson.Feature;

import espol.edu.ec.espolguide.viewModels.MapViewModel;
import espol.edu.ec.espolguide.viewModels.PoiInfoViewModel;

/**
* Created by galo on 29/12/17.
*/

public class MapActivity extends AppCompatActivity implements Observer, OnMapReadyCallback,
        MapboxMap.OnMapClickListener{
    ViewHolder viewHolder;
    MapViewModel viewModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_map);
        this.viewHolder = new ViewHolder();
        this.viewHolder.mapView.getMapAsync(this);
        this.viewModel = new MapViewModel(this);
        this.viewModel.addObserver(this);
        this.viewModel.makeNamesRequest();
    }

    public class ViewHolder{
        public ListView searchPoiLv;
        public EditText editSearch;
        public MapView mapView;
        public LinearLayout info;
        public Button closePoiInfoBtn;
        public Marker featureMarker;
        public MapboxMap mapboxMap;

        public ViewHolder(){
            findViews();
            setClosePoiButtonListener();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        private void findViews(){
            mapView = (MapView) findViewById(R.id.mapView);
            info = (LinearLayout) findViewById(R.id.overlay);
            closePoiInfoBtn = (Button) findViewById(R.id.close_poi_info_button);
            editSearch = (EditText) findViewById(R.id.search_destiny);
            searchPoiLv = (ListView) findViewById(R.id.listview);
        }

        private void setClosePoiButtonListener(){
            this.closePoiInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final LinearLayout info = (LinearLayout) findViewById(R.id.overlay);
                    info.setVisibility(View.GONE);
                }
            });
        }
    }

    public ViewHolder getViewHolder(){
        return this.viewHolder;
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        MapActivity.this.viewHolder.mapboxMap = mapboxMap;
        mapboxMap.addOnMapClickListener(this);
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        if (this.viewHolder.featureMarker != null) {
            this.viewHolder.mapboxMap.removeMarker(this.viewHolder.featureMarker);
        }
        final PointF pixel = this.viewHolder.mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> features = this.viewHolder.mapboxMap.queryRenderedFeatures(pixel);
        if (features.size() > 0) {
            Feature feature = features.get(0);
            String blockName = "";
            String academicUnit = "";
            String description = "";
            if (feature.properties() != null && feature.properties().has("CODIGO")) {
                blockName = feature.getStringProperty("BLOQUE").toString();
                academicUnit = feature.getStringProperty("UNIDAD").toString();
                description = feature.getStringProperty("DESCRIPCIO").toString();
                new PoiInfoViewModel(new PoiInfo(blockName, academicUnit, description, MapActivity.this,
                        viewHolder.info)).show();
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        String message = (String)arg;
        if (message == viewModel.NAMES_REQUEST_STARTED) {

        }
        if (message == viewModel.NAMES_REQUEST_FAILED_CONNECTION) {
            MapActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MapActivity.this, getResources().getString(R.string.failed_connection_msg),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        if (message == viewModel.NAMES_REQUEST_FAILED_LOADING) {
            MapActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MapActivity.this, getResources().getString(R.string.loading_pois_names_error_msg),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        if (message == viewModel.NAMES_REQUEST_FAILED_HTTP) {
            MapActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MapActivity.this, getResources().getString(R.string.http_error_msg),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.viewHolder.mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.viewHolder.mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.viewHolder.mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.viewHolder.mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        this.viewHolder.mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.viewHolder.mapboxMap != null) {
            this.viewHolder.mapboxMap.removeOnMapClickListener(this);
        }
        this.viewHolder.mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.viewHolder.mapView.onSaveInstanceState(outState);
    }
}
