
package espol.edu.ec.espolguide;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.google.gson.JsonElement;
import com.mapbox.geojson.Feature;

import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.viewModels.MapViewModel;

/**
* Created by galo on 29/12/17.
*/

public class MapActivity extends AppCompatActivity implements Observer,
        OnMapReadyCallback, MapboxMap.OnMapClickListener{
    ViewHolder viewHolder;
    MapViewModel viewModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_map);
        validateWritingPermission();
        this.viewHolder = new ViewHolder();
//        this.viewHolder.setMapInitialState();
        this.viewHolder.mapView.getMapAsync(this);
//        this.viewModel = new MapViewModel(this);
//        this.viewModel.addObserver(this);
//        this.viewModel.makelBocksShapesRequest();
//        this.viewModel.makeNamesRequest();
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

            String property;

            StringBuilder stringBuilder = new StringBuilder();
            if (feature.properties() != null) {
                for (Map.Entry<String, JsonElement> entry : feature.properties().entrySet()) {
                    System.out.print(entry.getKey() + "||");
                    System.out.println(entry.getValue());
                    stringBuilder.append(String.format("%s - %s", entry.getKey(), entry.getValue()));
                    stringBuilder.append(System.getProperty("line.separator"));
                }

                this.viewHolder.featureMarker = this.viewHolder.mapboxMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title("TITULO")
                        .snippet(stringBuilder.toString())
                );

            } else {
//                property = getString(R.string.query_feature_marker_snippet);
                this.viewHolder.featureMarker = this.viewHolder.mapboxMap.addMarker(new MarkerOptions()
                        .position(point)
//                        .snippet(property)
                );
            }
        } else {
            this.viewHolder.featureMarker = this.viewHolder.mapboxMap.addMarker(new MarkerOptions()
                    .position(point)
//                    .snippet(getString(R.string.query_feature_marker_snippet))
            );
        }
        this.viewHolder.mapboxMap.selectMarker(this.viewHolder.featureMarker);
    }

    public class ViewHolder{
        public ListView searchPoiLv;
        public EditText editSearch;
        public MapView mapView;
        public LinearLayout info;
        public Button closePoiInfoBtn;
        private Marker featureMarker;
        private MapboxMap mapboxMap;


        public ViewHolder(){
            findViews();
//            setMapInitialState();
            setClosePoiButtonListener();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        private void findViews(){
            mapView = (MapView) findViewById(R.id.mapView);
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
                }
            });
        }


/**        private void setMapInitialState(){
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final MapboxMap mapboxMap) {
                    map = mapboxMap;
                    mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng point) {
                            if (featureMarker != null) {
                                mapboxMap.removeMarker(featureMarker);
                            }
                            final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);
                            List<Feature> features = mapboxMap.queryRenderedFeatures(pixel);


                            if (features.size() > 0) {
                                Integer features_int = (Integer) features.size();
                                String features_str = features_int.toString();
                                Feature feature = features.get(0);

                                String property;

                                StringBuilder stringBuilder = new StringBuilder();
                                if (feature.getProperties() != null) {
                                    for (Map.Entry<String, JsonElement> entry : feature.getProperties().entrySet()) {
                                        System.out.println(entry.getKey());
                                        stringBuilder.append(String.format("%s - %s", entry.getKey(), entry.getValue()));
                                        stringBuilder.append(System.getProperty("line.separator"));
                                    }

                                    featureMarker = mapboxMap.addMarker(new MarkerOptions()
                                            .position(point)
                                            .title(features_str)
                                            .snippet(stringBuilder.toString())
                                    );
                                    mapboxMap.selectMarker(featureMarker);

                                } else {
                                    mapboxMap.removeMarker(featureMarker);
                                    //property = getString(R.string.query_feature_marker_snippet);
                                    //featureMarker = mapboxMap.addMarker(new MarkerOptions()
                                    //        .position(point)
                                    //        .snippet(property)
                                    //);
                                }
                            } else {
                                mapboxMap.removeMarker(featureMarker);
                                //featureMarker = mapboxMap.addMarker(new MarkerOptions()
                                //        .position(point)
                                //        .snippet(getString(R.string.query_feature_marker_snippet))
                                //);
                            }

                        }
                        ;
                    });

                }  ;
                });
        }*/
    }

    public void validateWritingPermission (){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG, "Permission is granted");
            } else {
                //Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
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
                    Toast.makeText(MapActivity.this, getResources().getString(R.string.failed_connection_msg),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        if (message == viewModel.DRAW_REQUEST_FAILED_LOADING) {
            MapActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MapActivity.this, getResources().getString(R.string.loading_blocks_shapes_error_msg),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        if (message == viewModel.DRAW_REQUEST_FAILED_HTTP) {
            MapActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MapActivity.this, getResources().getString(R.string.http_error_msg),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
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
