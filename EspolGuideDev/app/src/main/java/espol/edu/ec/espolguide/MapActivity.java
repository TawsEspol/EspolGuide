package espol.edu.ec.espolguide;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.Constants;
import com.mapbox.services.android.location.LostLocationEngine;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import espol.edu.ec.espolguide.controllers.adapters.RouteAdapter;
import espol.edu.ec.espolguide.utils.IntentHelper;
import espol.edu.ec.espolguide.viewModels.MapViewModel;
import espol.edu.ec.espolguide.viewModels.PoiInfoViewModel;
import retrofit2.Call;
import retrofit2.Callback;

/**
* Created by galo on 29/12/17.
*/

public class MapActivity extends AppCompatActivity implements Observer, OnMapReadyCallback,
        MapboxMap.OnMapClickListener{
    ViewHolder viewHolder;
    MapViewModel viewModel;




    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;

    // variables for calculating and drawing a route
    private Point originPosition;
    private Point destinationPosition;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";


    // variables for adding a marker
    private org.osmdroid.views.overlay.Marker destinationMarker;
    private LatLng originCoord;
    private LatLng destinationCoord;





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
        this.viewHolder.setEditTextOnFocusListener();
    }

    public class ViewHolder{
        public ListView searchPoiLv;
        public EditText editSearch;
        public MapView mapView;
        public LinearLayout info;
        public Button closePoiInfoBtn;
        public Marker featureMarker;
        public MapboxMap mapboxMap;

        public EditText editOrigin;
        public EditText editDestination;
        public ListView originLv;
        public ListView destinationLv;
        public LinearLayout placesBox;
        public Button routeBtn;


        public ViewHolder(){
            findViews();
            setClosePoiButtonListener();
            setDrawRouteButtonListener();
//            setEditTextOnFocusListener();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        private void findViews(){
            mapView = (MapView) findViewById(R.id.mapView);
            info = (LinearLayout) findViewById(R.id.overlay);
            closePoiInfoBtn = (Button) findViewById(R.id.close_poi_info_button);
            editSearch = (EditText) findViewById(R.id.search_destiny);
            searchPoiLv = (ListView) findViewById(R.id.listview);

            editOrigin = (EditText) findViewById(R.id.search_origin);
            editDestination = (EditText) findViewById(R.id.search_destination);
            originLv = (ListView) findViewById(R.id.origin_results);
            destinationLv = (ListView) findViewById(R.id.destination_results);

            placesBox = (LinearLayout) findViewById(R.id.places_box);
            routeBtn = (Button) findViewById(R.id.route_btn);

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

        private void setDrawRouteButtonListener(){
            this.routeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editSearch.setVisibility(View.GONE);
                    placesBox.setVisibility(View.VISIBLE);
                    routeBtn.setVisibility(View.GONE);
                    editDestination.clearFocus();
                    editOrigin.clearFocus();
                }
            });
        }

        private void setEditTextOnFocusListener(){
            this.editSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        routeBtn.setVisibility(View.GONE);
                    }
                }
            });
            this.editOrigin.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(editOrigin.getText().toString().trim().length() != 0){
                        String text = editOrigin.getText().toString().trim();
                        Intent intent=new Intent(MapActivity.this,SearchResultsActivity.class);
                        intent.putExtra("text", text);
                        intent.putExtra("from", espol.edu.ec.espolguide.utils.Constants.FROM_ORIGIN);
                        if(viewModel.getNamesItems() == null){
                            System.out.println("NULO DESDE ANTES");
                            System.out.println("NULO DESDE ANTES");
                        }
                        else{
                            System.out.println("NO ES NULO DESDE ANTES");
                        }
                        IntentHelper.addObjectForKey(viewModel.getNamesItems(), "namesItems");
                        startActivity(intent);
                    }
                }
            });
            this.editDestination.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(editDestination.getText().toString().trim().length() != 0){
                        String text = editDestination.getText().toString().trim();
                        Intent intent=new Intent(MapActivity.this,SearchResultsActivity.class);
                        intent.putExtra("text", text);
                        intent.putExtra("from", espol.edu.ec.espolguide.utils.Constants.FROM_ORIGIN);
                        if(viewModel.getNamesItems() == null){
                            System.out.println("NULO DESDE ANTES");
                            System.out.println("NULO DESDE ANTES");
                        }
                        else{
                            System.out.println("NO ES NULO DESDE ANTES");
                        }
                        IntentHelper.addObjectForKey(viewModel.getNamesItems(), "namesItems");
                        startActivity(intent);
                    }
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







/**
    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .profile("walking")
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, retrofit2.Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }*/

/**
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create an instance of LOST location engine
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
            locationPlugin.setLocationLayerEnabled(LocationLayerMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        locationEngine = new LostLocationEngine(MapViewModel.this);
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void setCameraPosition(Location location) {
        activity.getViewHolder().mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 13));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            originLocation = location;
            setCameraPosition(location);
            locationEngine.removeLocationEngineListener(this);
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }*/
}
