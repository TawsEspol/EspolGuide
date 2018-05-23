package espol.edu.ec.espolguide;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;


import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.geojson.Feature;

import espol.edu.ec.espolguide.controllers.adapters.RouteAdapter;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.IntentHelper;
import espol.edu.ec.espolguide.viewModels.MapViewModel;
import espol.edu.ec.espolguide.viewModels.PoiInfoViewModel;




import java.util.List;
import android.location.Location;
import android.support.annotation.NonNull;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;

// classes to calculate a route
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;

import static espol.edu.ec.espolguide.utils.Constants.REQUEST_CODE;


/**
* Created by galo on 29/12/17.
*/

/**public class MapActivity extends AppCompatActivity implements Observer, OnMapReadyCallback,
        MapboxMap.OnMapClickListener, LocationEngineListener, PermissionsListener{
    ViewHolder viewHolder;
    MapViewModel viewModel;*/


    public class MapActivity extends AppCompatActivity implements Observer, LocationEngineListener, PermissionsListener{
        ViewHolder viewHolder;
        MapViewModel viewModel;

    public LatLng selectedOrigin;
    public LatLng selectedDestination;


    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;
    // variables for adding a marker
    private Marker destinationMarker;
    private LatLng originCoord;
    private LatLng destinationCoord;

    // variables for calculating and drawing a route
    private Point originPosition;
    private Point destinationPosition;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_map);
        this.viewHolder = new ViewHolder();
        this.viewHolder.setMapOnClickListener();
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
            setEditTextListeners();
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
                    editOrigin.setText(getResources().getString(R.string.your_location));
                    enableLocationPlugin();
                    originCoord = new LatLng(originLocation.getLatitude(), originLocation.getLongitude());
                    selectedOrigin = originCoord;
                    originPosition = Point.fromLngLat(selectedOrigin.getLongitude(), selectedOrigin.getLatitude());
                    destinationPosition = Point.fromLngLat(selectedDestination.getLongitude(), selectedDestination.getLatitude());
                    getRoute(originPosition, destinationPosition);
                }
            });
        }

        private void setMapOnClickListener(){
            viewHolder.mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final MapboxMap mapboxMap) {
                    viewHolder.mapboxMap = mapboxMap;
                    mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng point) {
                            if (viewHolder.featureMarker != null) {
                                viewHolder.mapboxMap.removeMarker(viewHolder.featureMarker);
                            }
                            final PointF pixel = viewHolder.mapboxMap.getProjection().toScreenLocation(point);
                            List<Feature> features = viewHolder.mapboxMap.queryRenderedFeatures(pixel);
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
                        ;
                    });
                }
                ;
            });
        }

        private void setEditTextListeners(){
            this.editSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        routeBtn.setVisibility(View.GONE);
                    }
                }
            });
            this.editOrigin.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(MotionEvent.ACTION_UP == event.getAction()){
                        String text = editOrigin.getText().toString().trim();
                        Intent intent=new Intent(MapActivity.this,SearchResultsActivity.class);
                        intent.putExtra("text", text);
                        intent.putExtra("from", Constants.FROM_ORIGIN);
                        System.out.println("Tamano antes de enviar: " + viewModel.getAdapter().getArraylist().size());
                        IntentHelper.addObjectForKey(viewModel.getAdapter().getArraylist(), "namesItems");
                        startActivityForResult(intent, 1);
                        //startActivity(intent);
                    }
                    return false;
                }
            });


            this.editDestination.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(MotionEvent.ACTION_UP == event.getAction()){
                        String text = editDestination.getText().toString().trim();
                        Intent intent=new Intent(MapActivity.this,SearchResultsActivity.class);
                        intent.putExtra("text", text);
                        intent.putExtra("from", Constants.FROM_DESTINATION);
                        System.out.println("Tamano antes de enviar: " + viewModel.getAdapter().getArraylist().size());
                        IntentHelper.addObjectForKey(viewModel.getAdapter().getArraylist(), "namesItems");
                        startActivityForResult(intent, 1);
                        //startActivity(intent);
                    }
                    return false;
                }
            });

        }
    }

    public ViewHolder getViewHolder(){
        return this.viewHolder;
    }

    public void getRoute(Point origin, Point destination) {
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
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
                            navigationMapRoute = new NavigationMapRoute(null, viewHolder.mapView, viewHolder.mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create an instance of LOST location engine
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(viewHolder.mapView, viewHolder.mapboxMap, locationEngine);
            locationPlugin.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    public void initializeLocationEngine() {
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.LOW_POWER);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    public void setCameraPosition(Location location) {
        viewHolder.mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
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
        viewHolder.mapView.onStart();
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
        viewHolder.mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewHolder.mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        viewHolder.mapView.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewHolder.mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewHolder.mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        viewHolder.mapView.onSaveInstanceState(outState);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == Constants.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String from = data.getExtras().getString("from");
                String officialName = data.getExtras().getString("officialName");
                double selectedLat = data.getExtras().getDouble("selectedLat");
                double selectedLng = data.getExtras().getDouble("selectedLng");

                System.out.println("VALOR DE FROM: " + from);

                if(from == Constants.FROM_ORIGIN){
                    selectedOrigin = new LatLng(selectedLat, selectedLng);
                    originPosition = Point.fromLngLat(selectedOrigin.getLongitude(), selectedOrigin.getLatitude());
                    viewHolder.editOrigin.setText(officialName);
                }
                if (from == Constants.FROM_DESTINATION){
                    selectedDestination = new LatLng(selectedLat, selectedLng);
                    destinationPosition = Point.fromLngLat(selectedDestination.getLongitude(), selectedDestination.getLatitude());
                    viewHolder.editDestination.setText(officialName);
                }
                if(originPosition==null){
                    System.out.println("ORIGIN ES NULL");
                }
                if(destinationPosition==null){
                    System.out.println("DESTINATION ES NULL");
                }


                if(originPosition!=null && destinationPosition!=null){
                    System.out.println("(((((((((((((( NO ES NULO )))))))))))))))))))");
                    getRoute(originPosition, destinationPosition);
                }
                viewHolder.editSearch.setText(officialName);


            }
        }
    }
















    /**    @Override
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

    destinationCoord = point;
    originCoord = new LatLng(originLocation.getLatitude(), originLocation.getLongitude());
    destinationPosition = Point.fromLngLat(destinationCoord.getLongitude(), destinationCoord.getLatitude());
    originPosition = Point.fromLngLat(originCoord.getLongitude(), originCoord.getLatitude());
    getRoute(originPosition, destinationPosition);

    }*/
}
