package espol.edu.ec.espolguide.viewModels;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import espol.edu.ec.espolguide.MapActivity;
import espol.edu.ec.espolguide.PoiInfo;
import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.controllers.adapters.RouteAdapter;
import espol.edu.ec.espolguide.controllers.adapters.SearchViewAdapter;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.SessionHelper;
import retrofit2.Call;
import retrofit2.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

public class MapViewModel extends Observable{
    public static String NAMES_REQUEST_STARTED = "names_request_started";
    public static String NAMES_REQUEST_SUCCEEDED = "names_request_succeeded";
    public static String NAMES_REQUEST_FAILED_LOADING = "names_request_failed_loading";
    public static String POI_INFO_REQUEST_STARTED = "poi_info_request_started";
    public static String POI_INFO_REQUEST_SUCCEEDED = "poi_info_request_succeeded";
    public static String POI_INFO_REQUEST_FAILED_LOADING = "poi_info_request_failed_loading";
    public static String ROUTE_REQUEST_STARTED = "route_request_started";
    public static String ROUTE_REQUEST_SUCCEEDED = "route_request_succeeded";
    public static String ROUTE_REQUEST_FAILED = "route_request_failed";
    public static String ADD_FAVORITES_REQUEST_STARTED = "add_favorites_request_started";
    public static String ADD_FAVORITES_REQUEST_SUCCEEDED = "add_favorites_request_succeeded";
    public static String ADD_FAVORITES_REQUEST_FAILED_LOADING = "add_favorites_request_failed_loading";
    public static String REQUEST_FAILED_HTTP = "request_failed_http";
    public static String REQUEST_FAILED_CONNECTION = "request_failed_connection";

    public static String MAP_CENTERING_REQUEST_STARTED = "map_centering_request_started";
    public static String MAP_CENTERING_REQUEST_SUCCEEDED = "map_centering_request_succeeded";
    public static String MAP_CENTERING_REQUEST_FAILED_LOADING = "map_centering_request_failed_loading";

    final private String FAVORITES_WS = Constants.getFavoritesURL();

    private ArrayList<String> favoriteBlocks;


    final private String POIS_NAMES_WS = Constants.getAlternativeNamesURL();
    final private ArrayList<String> namesItems = new ArrayList<>();
    private SearchViewAdapter adapter;

    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    private MapActivity activity;
    private String selectedRouteMode;

    private FusedLocationProviderClient mFusedLocationClient;
    final String COORDINATES_WS = Constants.getCoordinatesURL();


    public MapViewModel(MapActivity activity) { this.activity = activity; }

    public static String getTAG() {
        return TAG;
    }

    public void makeNamesRequest(){
        setChanged();
        notifyObservers(NAMES_REQUEST_STARTED);
        new Nombres().execute(activity);
    }

    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    public void setPermissionsManager(PermissionsManager permissionsManager) {
        this.permissionsManager = permissionsManager;
    }

    public LocationLayerPlugin getLocationPlugin() {
        return locationPlugin;
    }

    public void setLocationPlugin(LocationLayerPlugin locationPlugin) {
        this.locationPlugin = locationPlugin;
    }

    public LocationEngine getLocationEngine() {
        return locationEngine;
    }

    public void setLocationEngine(LocationEngine locationEngine) {
        this.locationEngine = locationEngine;
    }

    public DirectionsRoute getCurrentRoute() {
        return currentRoute;
    }

    public void setCurrentRoute(DirectionsRoute currentRoute) {
        this.currentRoute = currentRoute;
    }

    public NavigationMapRoute getNavigationMapRoute() {
        return navigationMapRoute;
    }

    public void setNavigationMapRoute(NavigationMapRoute navigationMapRoute) {
        this.navigationMapRoute = navigationMapRoute;
    }

    public String getSelectedRouteMode(){
        return this.selectedRouteMode;
    }

    public void setSelectedRouteMode(String selectedRouteMode){
        this.selectedRouteMode = selectedRouteMode;
    }

    private class Nombres extends AsyncTask<Context, Void, ArrayList> {
        Context context;
        @Override
        protected ArrayList doInBackground(Context... contexts) {
            context = contexts[0];
            if (!Constants.isNetworkAvailable(context)) {
                setChanged();
                notifyObservers(REQUEST_FAILED_CONNECTION);
            }
            else {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        POIS_NAMES_WS, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Iterator<String> iter = response.keys();
                        while (iter.hasNext()) {
                            String identifier = iter.next();
                            System.out.println("===============" + identifier + "===============");
                            if (identifier != null) {
                                try {
                                    String blockString = "";
                                    JSONObject blockInfo = (JSONObject) response.get(identifier);
                                    String blockName = (String) blockInfo.getString(Constants.BLOCKNAME_FIELD);
                                    String type = (String) blockInfo.getString(Constants.TYPE_FIELD);
                                    String codeGtsi = (String) blockInfo.getString(Constants.CODE_GTSI_FIELD);
                                    JSONArray alternativeNames = blockInfo.getJSONArray(Constants.ALTERNATIVE_NAMES_FIELD);
                                    int totalAlternatives = alternativeNames.length();
                                    String alternativeString = "";
                                    for (int i = 0; i < totalAlternatives; i++) {
                                        String alternative = (String) alternativeNames.get(i);
                                        alternativeString = alternativeString + " | " + alternative;
                                    }
                                    if(codeGtsi.length() < 1){
                                        codeGtsi = " ";
                                    }
                                    blockString = identifier +
                                            ";" + blockName + ";" + alternativeString + ";" + codeGtsi;
                                    namesItems.add(blockString);
                                } catch (JSONException e) {
                                    setChanged();
                                    notifyObservers(NAMES_REQUEST_FAILED_LOADING);
                                    continue;
                                }
                            }
                        }

                        adapter = new SearchViewAdapter(activity, activity.getViewHolder().mapboxMap, namesItems, activity.getViewHolder().editSearch,
                                activity.getViewHolder().featureMarker);
                        adapter.setMapView(activity.getViewHolder().mapView);
                        activity.getViewHolder().searchPoiLv.setAdapter(adapter);
                        activity.getViewHolder().editSearch.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void afterTextChanged(Editable arg0) {
                                // TODO Auto-generated method stub
                                String text = activity.getViewHolder().editSearch.getText().toString().toLowerCase(Locale.getDefault());
                                adapter.filter(text);
                            }
                            @Override
                            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                                // TODO Auto-generated method stub
                            }
                            @Override
                            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                                      int arg3) {
                                // TODO Auto-generated method stub
                                activity.getViewHolder().searchPoiLv.setVisibility(View.VISIBLE);
                            }
                        });

                        RouteAdapter routeAdapter = new RouteAdapter(namesItems, activity);
                        activity.getViewHolder().routesLv.setAdapter(routeAdapter);
                        activity.getViewHolder().editSearchRoutes.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void afterTextChanged(Editable arg0) {
                                // TODO Auto-generated method stub
                                String text = activity.getViewHolder().editSearchRoutes.getText().toString().toLowerCase(Locale.getDefault());
                                routeAdapter.filter(text);
                            }
                            @Override
                            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                                // TODO Auto-generated method stub
                            }
                            @Override
                            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                                      int arg3) {
                                activity.getViewHolder().routesLv.setVisibility(View.VISIBLE);
                                // TODO Auto-generated method stub
                            }
                        });

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("tag", "Error: " + error.getMessage());
                        setChanged();
                        notifyObservers(REQUEST_FAILED_HTTP);
                    }
                });
                AppController.getInstance(context).addToRequestQueue(jsonObjReq);
            }
            return namesItems;
        }
    }

    public SearchViewAdapter getAdapter(){
        return this.adapter;
    }

    public void changeRouteModeView(){
        if(this.selectedRouteMode == Constants.CAR_ROUTE_MODE){
            LinearLayout carButtonBackground = (LinearLayout) activity.getViewHolder().carBtn.getParent();
            carButtonBackground.setBackgroundResource(R.drawable.selected_mode_button);
            activity.getViewHolder().carBtn.setColorFilter(
                    ContextCompat.getColor(activity, R.color.second), android.graphics.PorterDuff.Mode.SRC_IN);

            LinearLayout walkButtonBackground = (LinearLayout) activity.getViewHolder().walkBtn.getParent();
            walkButtonBackground.setBackgroundResource(R.drawable.unselected_mode_button);
            activity.getViewHolder().walkBtn.setColorFilter(
                    ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        else{
            LinearLayout walkButtonBackground = (LinearLayout) activity.getViewHolder().walkBtn.getParent();
            walkButtonBackground.setBackgroundResource(R.drawable.selected_mode_button);
            activity.getViewHolder().walkBtn.setColorFilter(
                    ContextCompat.getColor(activity, R.color.second), android.graphics.PorterDuff.Mode.SRC_IN);

            LinearLayout carButtonBackground = (LinearLayout) activity.getViewHolder().carBtn.getParent();
            carButtonBackground.setBackgroundResource(R.drawable.unselected_mode_button);
            activity.getViewHolder().carBtn.setColorFilter(
                    ContextCompat.getColor(activity, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    public String getDirectionCriteria(){
        String directionsCriteria = DirectionsCriteria.PROFILE_WALKING;
        if(this.selectedRouteMode == Constants.CAR_ROUTE_MODE){
            directionsCriteria = DirectionsCriteria.PROFILE_DRIVING;
        }
        return directionsCriteria;
    }

    public void getRoute(Point origin, Point destination) {
        setChanged();
        notifyObservers(ROUTE_REQUEST_STARTED);
        this.changeRouteModeView();
        if(origin != null && destination!=null){
            NavigationRoute.builder()
                    .accessToken(Mapbox.getAccessToken())
                    .origin(origin)
                    .profile(this.getDirectionCriteria())
                    .destination(destination)
                    .build()
                    .getRoute(new Callback<DirectionsResponse>() {
                        @Override
                        public void onResponse(Call<DirectionsResponse> call, retrofit2.Response<DirectionsResponse> response) {
                            // You can get the generic HTTP info about the response
                            Log.d(getTAG(), "Response code: " + response.code());
                            if (response.body() == null) {
                                Log.e(getTAG(), "No routes found, make sure you set the right user and access token.");
                                return;
                            } else if (response.body().routes().size() < 1) {
                                Log.e(getTAG(), "No routes found");
                                return;
                            }
                            setCurrentRoute(response.body().routes().get(0));
                            if(activity.getViewHolder().featureMarker != null){
                                activity.getViewHolder().mapboxMap.removeMarker(activity.getViewHolder().featureMarker);
                            }
                            if(MapViewModel.this.getAdapter().getFeatureMarker() != null){
                                activity.getViewHolder().mapboxMap.removeMarker(MapViewModel.this.getAdapter().getFeatureMarker());
                            }
                            activity.getViewHolder().featureMarker = activity.getViewHolder().mapboxMap.addMarker(new MarkerOptions()
                                    .position(activity.getSelectedDestination())
                            );
                            if (getNavigationMapRoute() != null) {
                                getNavigationMapRoute().removeRoute();
                            } else {
                                setNavigationMapRoute(new NavigationMapRoute(null, activity.getViewHolder().mapView,
                                        activity.getViewHolder().mapboxMap, R.style.CustomNavigationMapRoute));
                            }
                            getNavigationMapRoute().addRoute(getCurrentRoute());
                            setRouteZoom();
                            setChanged();
                            notifyObservers(ROUTE_REQUEST_SUCCEEDED);
                        }

                        @Override
                        public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                            setChanged();
                            notifyObservers(ROUTE_REQUEST_FAILED);                        }
                    });
        }
    }

    public void setMapOnClickListener(){
        activity.getViewHolder().mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                activity.getViewHolder().setMapboxMap(mapboxMap);
                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        setChanged();
                        notifyObservers(POI_INFO_REQUEST_STARTED);
                        try{
                            if (activity.getViewHolder().featureMarker != null) {
                                activity.getViewHolder().mapboxMap.removeMarker(activity.getViewHolder().featureMarker);
                            }
                            if (activity.getViewHolder().featureMarker != null) {
                                activity.getViewHolder().mapboxMap.removeMarker(activity.getViewHolder().featureMarker);
                            }
                            final PointF pixel = activity.getViewHolder().mapboxMap.getProjection().toScreenLocation(point);
                            List<Feature> features = activity.getViewHolder().mapboxMap.queryRenderedFeatures(pixel);
                            if (features.size() > 0) {
                                Feature feature = features.get(0);
                                String blockName = "";
                                String academicUnit = "";
                                String description = "";
                                String codeInfrastructure = "";
                                if (feature.properties() != null && hasEspolAttributes(feature)) {
                                    if(feature.properties().has(Constants.BLOCKNAME_FIELD)){
                                        blockName = feature.getStringProperty(Constants.BLOCKNAME_FIELD).toString();
                                    }
                                    if(feature.properties().has(Constants.ACADEMIC_UNIT_FIELD)){
                                        academicUnit = feature.getStringProperty(Constants.ACADEMIC_UNIT_FIELD).toString();
                                    }
                                    if(feature.properties().has(Constants.CODE_INFRASTRUCTURE)){
                                        codeInfrastructure = feature.getStringProperty(Constants.CODE_INFRASTRUCTURE).toString();
                                    }
                                    if(feature.properties().has(Constants.DESCRIPTION_FIELD)){
                                        description = feature.getStringProperty(Constants.DESCRIPTION_FIELD).toString();
                                    }
                                    if(feature.properties().has(Constants.CODE_GTSI_FIELD)){
                                        String codeGtsi = feature.getStringProperty(Constants.CODE_GTSI_FIELD).toString();
                                        activity.setSelectedPoi(codeGtsi);
                                        //makeAddFavoriteRequest(codeGtsi);
                                    }
                                    new PoiInfoViewModel(new PoiInfo(blockName, academicUnit, description,
                                            codeInfrastructure, activity, activity.getViewHolder().info)).show();
                                    setChanged();
                                    notifyObservers(POI_INFO_REQUEST_SUCCEEDED);
                                }
                            }
                        } catch (Exception e){
                            setChanged();
                            notifyObservers(POI_INFO_REQUEST_FAILED_LOADING);
                        }

                    }

                });
            }
        });
    }

    public void setRouteZoom(){
        LineString lineString = LineString.fromPolyline(getCurrentRoute().geometry(), 6);
        List<Point> coordinates = lineString.coordinates();
        if(coordinates.size() > 1 &&
                !coordinates.get(0).equals(coordinates.get(coordinates.size()-1))){
            LinkedList<LatLng> points = new LinkedList<>();
            for (int i = 0; i < coordinates.size(); i++) {
                Double latitude = coordinates.get(i).latitude();
                Double longitude = coordinates.get(i).longitude();
                points.add(new LatLng(latitude, longitude));
            }
            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .includes(points)
                    .build();
            activity.getViewHolder().mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,
                    Constants.ROUTE_ZOOM_PADDING_LEFT, Constants.ROUTE_ZOOM_PADDING_TOP,
                    Constants.ROUTE_ZOOM_PADDING_RIGHT, Constants.ROUTE_ZOOM_PADDING_BOTTOM));
        }
    }

    public boolean hasEspolAttributes(Feature feature){
        return (feature.properties().has(Constants.CODE_GTSI_FIELD) ||
                feature.properties().has(Constants.BLOCKNAME_FIELD) ||
                feature.properties().has(Constants.ACADEMIC_UNIT_FIELD) ||
                feature.properties().has(Constants.CODE_INFRASTRUCTURE) ||
                feature.properties().has(Constants.DESCRIPTION_FIELD));
    }

    @SuppressWarnings( {"MissingPermission"})
    public void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            // Create an instance of location engine
            initializeLocationEngine();
            if(this.getLocationPlugin() == null){
                this.setLocationPlugin(new LocationLayerPlugin(activity.getViewHolder().mapView, activity.getViewHolder().mapboxMap,
                        this.getLocationEngine()));
                this.getLocationPlugin().setRenderMode(RenderMode.COMPASS);
            }
        } else {
            this.setPermissionsManager(new PermissionsManager(activity));
            this.getPermissionsManager().requestLocationPermissions(activity);
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    public void initializeLocationEngine() {
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(activity);
        this.setLocationEngine(locationEngineProvider.obtainBestLocationEngineAvailable());
        //Obtains the location with a 100m accuracy
        this.getLocationEngine().setPriority(LocationEnginePriority.BALANCED_POWER_ACCURACY);
        this.getLocationEngine().activate();
        Location lastLocation = this.getLocationEngine().getLastLocation();
        if (lastLocation != null) {
            activity.setOriginLocation(lastLocation);
        } else {
            this.getLocationEngine().addLocationEngineListener(activity);
        }
    }

    public void setRouteModeButtonsListeners(){
        activity.getViewHolder().walkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSelectedRouteMode() != Constants.WALKING_ROUTE_MODE){
                    setSelectedRouteMode(Constants.WALKING_ROUTE_MODE);
                    getRoute(activity.getOriginPosition(), activity.getDestinationPosition());
                }
            }
        });

        activity.getViewHolder().carBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSelectedRouteMode() != Constants.CAR_ROUTE_MODE){
                    setSelectedRouteMode(Constants.CAR_ROUTE_MODE);
                    getRoute(activity.getOriginPosition(), activity.getDestinationPosition());
                }

            }
        });
    }

    /**
     * Method that retrieves user's location at the onCreate Activity's state.
     *
     * This method retrieves the user's at the very beginning of the application's launching
     * in order to avoid location's issues later, when drawing a route.
     *
     * @return The method returns nothing.
     */
    public void getInitialPosition(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                activity.setOriginLocation(location);
                            }
                        }
                    });
            return;
        }
    }

    /**
     * Method that instances a FavoriteAdder class.
     *
     * @param codeGtsi The MapCentering URL request parameter.
     * @return The method returns nothing.
     */
    public void makeAddFavoriteRequest(String codeGtsi){
        setChanged();
        notifyObservers(ADD_FAVORITES_REQUEST_STARTED);
        new FavoriteAdder().execute(codeGtsi);
    }

    private class FavoriteAdder extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String codeGtsi = activity.getSelectedPoi();
            if(!SessionHelper.hasAccessToken(activity)){
                setChanged();
                notifyObservers(ADD_FAVORITES_REQUEST_FAILED_LOADING);
            }
            else{
                if (!Constants.isNetworkAvailable(activity)) {
                    setChanged();
                    notifyObservers(REQUEST_FAILED_CONNECTION);
                }
                else {
                    JSONObject jsonBody = new JSONObject();
                    try{
                        jsonBody.put(Constants.CODE_GTSI_KEY, codeGtsi);
                    }
                    catch (Exception e){ ;
                    }
                    String accessToken = SessionHelper.getAccessToken(activity);
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            FAVORITES_WS, jsonBody, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                JSONArray jsonArray = response.getJSONArray(Constants.CODES_GTSI_KEY);
                                ArrayList<String> favoritesList = new ArrayList<String>();
                                if (jsonArray != null) {
                                    int len = jsonArray.length();
                                    for (int i=0;i<len;i++){
                                        favoritesList.add(jsonArray.get(i).toString());
                                    }
                                }
                                Set<String> favoritesSet = new HashSet<>();
                                favoritesSet.addAll(favoritesList);
                                SessionHelper.saveFavoritePois(activity, favoritesSet);
                                setChanged();
                                notifyObservers(ADD_FAVORITES_REQUEST_SUCCEEDED);
                            }
                            catch (Exception e){
                                setChanged();
                                notifyObservers(ADD_FAVORITES_REQUEST_FAILED_LOADING);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d("tag", "Error: " + error.getMessage());
                            setChanged();
                            notifyObservers(REQUEST_FAILED_HTTP);
                        }
                    }) {
                        /**
                         * Passing some request headers
                         */
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            //headers.put("Content-Type", "application/json");
                            headers.put(Constants.ACCESS_TOKEN_HEADER_KEY, accessToken);
                            return headers;
                        }

                    };
                    AppController.getInstance(activity).addToRequestQueue(jsonObjReq);
                }
            }
            return null;
        }
    }

    /**
     * Method that instances a MapCentering class.
     *
     * @param codeGtsi The MapCentering URL request parameter.
     * @return The method returns nothing.
     */
    public void centerMapOnResult(String codeGtsi){
        setChanged();
        notifyObservers(MAP_CENTERING_REQUEST_STARTED);
        new MapCentering().execute(codeGtsi);
    }

    private class MapCentering extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String codeGtsi = strings[0];
            if (!Constants.isNetworkAvailable(activity)) {
                setChanged();
                notifyObservers(REQUEST_FAILED_CONNECTION);
            }
            else {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        COORDINATES_WS + codeGtsi, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            double lat = response.getDouble(Constants.LATITUDE_KEY);
                            double lon = response.getDouble(Constants.LONGITUDE_KEY);
                            LatLng point = new LatLng(lat, lon);
                            activity.setSelectedDestination(point);
                            activity.getViewHolder().editDestination.setText(codeGtsi);
                            activity.getViewHolder().editSearch.setText(codeGtsi);
                            activity.getViewHolder().editSearch.clearFocus();
                            activity.getViewHolder().mapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(MapboxMap mapboxMap) {
                                    if (activity.getViewHolder().featureMarker != null) {
                                        mapboxMap.removeMarker(activity.getViewHolder().featureMarker);
                                    }
                                    activity.getViewHolder().featureMarker = mapboxMap.addMarker(new MarkerOptions()
                                            .position(point)
                                    );
                                    mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                            .target(point)
                                            .zoom(Constants.CLOSE_ZOOM)
                                            .build());
                                }
                            });
                            adapter.getPois().clear();
                            setChanged();
                            notifyObservers(MAP_CENTERING_REQUEST_SUCCEEDED);
                        } catch (Exception e) {
                            e.printStackTrace();
                            setChanged();
                            notifyObservers(MAP_CENTERING_REQUEST_FAILED_LOADING);
                        } finally {
                            System.gc();
                            activity.getViewHolder().routeBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setChanged();
                        notifyObservers(REQUEST_FAILED_HTTP);
                    }
                });
                AppController.getInstance(activity).addToRequestQueue(jsonObjReq);
            }
        return null;
        }
    }

}