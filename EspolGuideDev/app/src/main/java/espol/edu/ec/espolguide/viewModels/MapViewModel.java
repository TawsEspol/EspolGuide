package espol.edu.ec.espolguide.viewModels;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PointF;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import espol.edu.ec.espolguide.utils.Util;
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
import java.util.Objects;
import java.util.Observable;
import java.util.Set;

/**
 * Created by galo on 14/04/18.
 */

public class MapViewModel extends Observable{
    public static final String NAMES_REQUEST_STARTED = "names_request_started";
    public static final String NAMES_REQUEST_SUCCEEDED = "names_request_succeeded";
    public static final String NAMES_REQUEST_FAILED_LOADING = "names_request_failed_loading";
    public static final String POI_INFO_REQUEST_STARTED = "poi_info_request_started";
    public static final String POI_INFO_REQUEST_SUCCEEDED = "poi_info_request_succeeded";
    public static final String POI_INFO_REQUEST_FAILED_LOADING = "poi_info_request_failed_loading";
    public static final String ROUTE_REQUEST_STARTED = "route_request_started";
    public static final String ROUTE_REQUEST_SUCCEEDED = "route_request_succeeded";
    public static final String ROUTE_REQUEST_FAILED = "route_request_failed";
    public static final String ADD_FAVORITES_REQUEST_STARTED = "add_favorites_request_started";
    public static final String ADD_FAVORITES_REQUEST_SUCCEEDED = "add_favorites_request_succeeded";
    public static final String REMOVE_FAVORITES_REQUEST_SUCCEEDED = "remove_favorites_request_succeeded";
    public static final String ADD_FAVORITES_REQUEST_FAILED_LOADING = "add_favorites_request_failed_loading";
    public static final String REQUEST_FAILED_HTTP = "request_failed_http";
    public static final String REQUEST_FAILED_CONNECTION = "request_failed_connection";

    public static final String MAP_CENTERING_REQUEST_STARTED = "map_centering_request_started";
    public static final String MAP_CENTERING_REQUEST_SUCCEEDED = "map_centering_request_succeeded";
    public static final String MAP_CENTERING_REQUEST_FAILED_LOADING = "map_centering_request_failed_loading";

    public static final String LOCATION_REQUEST_STARTED = "location_request_started";
    public static final String LOCATION_REQUEST_FAILED = "location_request_failed";
    public static final String LOCATION_REQUEST_SUCCEEDED_ON_CREATE = "location_request_succeeded_on_create";
    public static final String LOCATION_REQUEST_SUCCEEDED = "location_request_succeeded";

    final private String POIS_NAMES_WS = Constants.getAlternativeNamesURL();
    final private ArrayList<String> namesItems = new ArrayList<>();
    private SearchViewAdapter adapter;

    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    private final MapActivity activity;
    private String selectedRouteMode;

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

    /**
     * Method that returns the class permissionsManager attribute.
     *
     * This method is used for obtaining the permissionsManager class attribute.
     *
     * @author Galo Castillo
     * @return The method returns an instance of PermissionsManager used as class attribute.
     */
    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    public void setPermissionsManager(PermissionsManager permissionsManager) {
        this.permissionsManager = permissionsManager;
    }

    /**
     * Method that returns the class locationPlugin attribute.
     *
     * This method is used for obtaining the locationPlugin class attribute.
     *
     * @author Galo Castillo
     * @return The method returns an instance of LocationLayerPlugin used as class attribute.
     */
    public LocationLayerPlugin getLocationPlugin() {
        return locationPlugin;
    }

    public void setLocationPlugin(LocationLayerPlugin locationPlugin) {
        this.locationPlugin = locationPlugin;
    }

    /**
     * Method that returns the class locationEngine attribute.
     *
     * This method is used for obtaining the locationEngine class attribute.
     *
     * @author Galo Castillo
     * @return The method returns an instance of LocationEngine used as class attribute.
     */
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

    /**
     * Auxiliary class that helps to fill with items the search bars.
     *
     * This auxiliary class helps to fill with POI names the search bars used to locate a POI
     * and changing origin and destination places on route settings
     * The 'doInBackground' method calls the POI  names web service. Then, after parsing the
     * request response, it adds the received items to the 'nameItems' list.
     * Finally it instances a SearchViewApdater and a RouteAdapter in order to set them
     * on the listviews.
     *
     * @author Galo Castillo
     */
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
                        POIS_NAMES_WS, null, response -> {
                            Iterator<String> iter = response.keys();
                            while (iter.hasNext()) {
                                String identifier = iter.next();
                                if (identifier != null) {
                                    try {
                                        String blockString = "";
                                        JSONObject blockInfo = (JSONObject) response.get(identifier);
                                        String blockName = blockInfo.getString(Constants.BLOCKNAME_FIELD);
                                        String type = blockInfo.getString(Constants.TYPE_FIELD);
                                        String codeGtsi = blockInfo.getString(Constants.CODE_GTSI_FIELD);
                                        String codeInfra = blockInfo.getString(Constants.CODE_INFRA_FIELD);
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
                                        if(codeInfra.length() < 1){
                                            codeInfra = " ";
                                        }
                                        blockString = identifier + ";" + blockName + ";" +
                                                alternativeString + ";" + codeGtsi + ";" + codeInfra;
                                        namesItems.add(blockString);
                                    } catch (JSONException e) {
                                        setChanged();
                                        notifyObservers(NAMES_REQUEST_FAILED_LOADING);
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
                            setChanged();
                            notifyObservers(NAMES_REQUEST_SUCCEEDED);
                        }, error -> {
                            VolleyLog.d("tag", "Error: " + error.getMessage());
                            setChanged();
                            notifyObservers(REQUEST_FAILED_HTTP);
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
        if(this.selectedRouteMode.equals(Constants.CAR_ROUTE_MODE)){
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
        if(this.selectedRouteMode.equals(Constants.CAR_ROUTE_MODE)){
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
                        public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull retrofit2.Response<DirectionsResponse> response) {
                            try{
                                // You can get the generic HTTP info about the response
                                Log.d(getTAG(), "Response code: " + response.code());
                                if (response.body() == null) {
                                    Log.e(getTAG(), "No routes found, make sure you set the right user and access token.");
                                    return;
                                } else if (Objects.requireNonNull(response.body()).routes().size() < 1) {
                                    Log.e(getTAG(), "No routes found");
                                    return;
                                }
                                setCurrentRoute(Objects.requireNonNull(response.body()).routes().get(0));
                                removeMarkers();
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
                                String timeStr = Util.getTimeString(getCurrentRoute().duration(), activity);
                                activity.getViewHolder().timeTv.setText(timeStr);
                                setRouteZoom();
                                setChanged();
                                notifyObservers(ROUTE_REQUEST_SUCCEEDED);
                            }
                            catch (Exception e){
                                setChanged();
                                notifyObservers(ROUTE_REQUEST_FAILED);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable throwable) {
                            setChanged();
                            notifyObservers(ROUTE_REQUEST_FAILED);
                        }
                    });
        }
    }

    public void removeMarkers(){
        if(activity.getViewHolder().featureMarker != null){
            activity.getViewHolder().mapboxMap.removeMarker(activity.getViewHolder().featureMarker);
        }
        if(MapViewModel.this.getAdapter().getFeatureMarker() != null){
            activity.getViewHolder().mapboxMap.removeMarker(MapViewModel.this.getAdapter().getFeatureMarker());
        }
    }

    public void setMapOnClickListener(){
        activity.getViewHolder().mapView.getMapAsync(mapboxMap -> {
            activity.getViewHolder().setMapboxMap(mapboxMap);
            activity.getViewHolder().getMapboxMap().addOnMapClickListener(point -> {
                setChanged();
                notifyObservers(POI_INFO_REQUEST_STARTED);
                try {
                    final PointF pixel = activity.getViewHolder().mapboxMap.getProjection().toScreenLocation(point);
                    List<Feature> features = activity.getViewHolder().mapboxMap.queryRenderedFeatures(pixel);
                    if (features.size() > 0) {
                        Feature feature = features.get(0);
                        String blockName = "";
                        String academicUnit = "";
                        String description = "";
                        String codeGtsi = "";
                        String codeInfrastructure = "";
                        if (feature.properties() != null && hasEspolAttributes(feature)) {
                            removeMarkers();
                            activity.getViewHolder().poiRoute.setEnabled(true);
                            activity.getViewHolder().poiRoute.setClickable(true);
                            activity.setSelectedPoi("");
                            if (Objects.requireNonNull(feature.properties()).has(Constants.BLOCKNAME_FIELD)) {
                                blockName = feature.getStringProperty(Constants.BLOCKNAME_FIELD).toString();
                            }
                            if (Objects.requireNonNull(feature.properties()).has(Constants.ACADEMIC_UNIT_FIELD)) {
                                academicUnit = feature.getStringProperty(Constants.ACADEMIC_UNIT_FIELD).toString();
                            }
                            if (Objects.requireNonNull(feature.properties()).has(Constants.CODE_INFRA_FIELD)) {
                                codeInfrastructure = feature.getStringProperty(Constants.CODE_INFRA_FIELD).toString();
                            }
                            if (Objects.requireNonNull(feature.properties()).has(Constants.DESCRIPTION_FIELD)) {
                                description = feature.getStringProperty(Constants.DESCRIPTION_FIELD).toString();
                            }
                            if (Objects.requireNonNull(feature.properties()).has(Constants.CODE_GTSI_FIELD)) {
                                codeGtsi = feature.getStringProperty(Constants.CODE_GTSI_FIELD).toString();
                            }
                            if (codeGtsi.trim().length() > 0 || codeInfrastructure.trim().length() > 0) {
                                if(codeGtsi.trim().length() < 1){
                                    codeGtsi = " ";
                                }
                                if(codeInfrastructure.trim().length() < 1){
                                    codeInfrastructure = " ";
                                }
                                activity.setSelectedPoi(codeGtsi + "|" + codeInfrastructure);
                                setSelectedPoiCoords();
                                updateFavBtnColor(activity.getSelectedPoi());
                            } else {
                                activity.getViewHolder().poiRoute.setEnabled(false);
                                activity.getViewHolder().poiRoute.setClickable(false);
                            }
                            new PoiInfoViewModel(new PoiInfo(blockName, academicUnit, description,
                                    codeInfrastructure, activity, activity.getViewHolder().info)).show();
                            setChanged();
                            notifyObservers(POI_INFO_REQUEST_SUCCEEDED);
                        }
                    }
                } catch (Exception e) {
                    setChanged();
                    notifyObservers(POI_INFO_REQUEST_FAILED_LOADING);
                }

            });
        });
    }

    public void setSelectedPoiCoords(){
        if (!Constants.isNetworkAvailable(activity)){
            Toast.makeText(activity, activity.getResources().getString(R.string.failed_connection_msg),
                    Toast.LENGTH_LONG).show();
        } else {
            String[] codes = activity.getSelectedPoi().split("\\|");
            String codeGtsi = codes[0];
            String codeInfrastrucure = codes[1];
            JSONObject jsonBody = new JSONObject();
            try{
                jsonBody.put(Constants.CODE_GTSI_KEY, codeGtsi);
                jsonBody.put(Constants.CODE_INFRA_KEY, codeInfrastrucure);
            }
            catch (Exception ignored){ ;
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    COORDINATES_WS, jsonBody, response -> {
                        try {
                            if(response.length() > 0){
                                double lat = response.getDouble(Constants.LATITUDE_KEY);
                                double lon = response.getDouble(Constants.LONGITUDE_KEY);
                                LatLng point = new LatLng(lat, lon);
                                activity.setSelectedDestination(point);
                            }
                            else{
                                activity.setSelectedDestination(null);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(activity, activity.getResources().getString(R.string.loading_poi_info_error_msg),
                                    Toast.LENGTH_LONG).show();
                        } finally {
                            System.gc();
                        }
                    }, error -> {
                        VolleyLog.d("tag", "Error: " + error.getMessage());
                        Toast.makeText(activity, activity.getResources().getString(R.string.http_error_msg),
                                Toast.LENGTH_SHORT).show();
                    });
            AppController.getInstance(activity).addToRequestQueue(jsonObjReq);
        }
    }

    public void updateFavBtnColor(String selectedPoi){
        Integer colorInt;
        if(SessionHelper.isFavorite(activity, selectedPoi)){
            colorInt = ContextCompat.getColor(activity, R.color.fifth);
            System.out.println("======== NO EN ELSE");
        }
        else{
            colorInt = ContextCompat.getColor(activity, R.color.third);
            System.out.println("======== EN ELSE");
        }
        ImageViewCompat.setImageTintList(activity.getViewHolder().favBtn, ColorStateList.valueOf(colorInt));
    }

    /**
     * Method that sets the map view to fit the current route polyline on it.
     *
     * This method obtains all the current route coordinates. Then, these coordinates are
     * used to create bounds to display the map, in order to keep all the coordinates in the
     * screen. Finally, the map is centered on the center of mass of the route.
     *
     * @author Galo Castillo
     * @return The method returns nothing.
     */
    public void setRouteZoom(){
        LineString lineString = LineString.fromPolyline(Objects.requireNonNull(getCurrentRoute().geometry()), 6);
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

    /**
     * Method that checks if an Espol building has been selected on the map.
     *
     * This method checks if the feature of the region selected on the map, contains at least
     * one Espol's buildings attribute.
     *
     * @author Galo Castillo
     * @param feature The feature of the region selected on the map.
     * @return The method returns true if the feature contains at least one
     * Espol building attributes. Returns false if there are no Espol building attributes contained
     * in the feature's attributes.
     */
    public boolean hasEspolAttributes(Feature feature){
        return (Objects.requireNonNull(feature.properties()).has(Constants.CODE_GTSI_FIELD) ||
                Objects.requireNonNull(feature.properties()).has(Constants.BLOCKNAME_FIELD) ||
                Objects.requireNonNull(feature.properties()).has(Constants.ACADEMIC_UNIT_FIELD) ||
                Objects.requireNonNull(feature.properties()).has(Constants.CODE_INFRA_FIELD) ||
                Objects.requireNonNull(feature.properties()).has(Constants.DESCRIPTION_FIELD));
    }

    @SuppressWarnings( {"MissingPermission"})
    public void enableLocationPlugin() {
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

    /**
     * Method that sets the click listeners of the car and walking route buttons.
     *
     * This method sets the click listeners of the buttons used for updating the mode of the route,
     * which might be by car or walking. The buttons react if they have not been previously
     * selected. When the buttons listeners react, they automatically call the 'getRoute' method,
     * drawing the route on the map.
     *
     * @author Galo Castillo
     * @return The method returns nothing.
     */
    public void setRouteModeButtonsListeners(){
        activity.getViewHolder().walkBtn.setOnClickListener(v -> {
            if(!getSelectedRouteMode().equals(Constants.WALKING_ROUTE_MODE)){
                if(activity.getViewHolder().editOrigin.getText().toString().trim()
                        .equals(activity.getApplicationContext().getString(R.string.your_location))){
                    updateOriginLocation();
                }
                setSelectedRouteMode(Constants.WALKING_ROUTE_MODE);
                getRoute(activity.getOriginPosition(), activity.getDestinationPosition());
            }
        });

        activity.getViewHolder().carBtn.setOnClickListener(v -> {
            if(!getSelectedRouteMode().equals(Constants.CAR_ROUTE_MODE)){
                if(activity.getViewHolder().editOrigin.getText().toString().trim()
                        .equals(activity.getApplicationContext().getString(R.string.your_location))){
                    updateOriginLocation();
                }
                setSelectedRouteMode(Constants.CAR_ROUTE_MODE);
                getRoute(activity.getOriginPosition(), activity.getDestinationPosition());
            }

        });
    }

    /**
     * Method that retrieves user's location at the onCreate Activity's state.
     *
     * This method retrieves the user's at the very beginning of the application's launching
     * in order to avoid location's issues later, when drawing a route.
     *
     * @author Galo Castillo
     * @param onState is used to announce when the method is being called.
     * @return The method returns nothing.
     */
    public void getInitialPosition(String onState){
        setChanged();
        notifyObservers(LOCATION_REQUEST_STARTED);
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            activity.setOriginLocation(location);
                            if(onState.equals(Constants.ON_CREATE)){
                                setChanged();
                                notifyObservers(LOCATION_REQUEST_SUCCEEDED_ON_CREATE);
                            }
                            else{
                                setChanged();
                                notifyObservers(LOCATION_REQUEST_SUCCEEDED);
                            }
                        }
                        else{
                            setChanged();
                            notifyObservers(LOCATION_REQUEST_FAILED);
                        }
                    });
            mFusedLocationClient.getLastLocation()
                    .addOnFailureListener(activity, e -> {
                        setChanged();
                        notifyObservers(LOCATION_REQUEST_FAILED);
                    });
        }
    }

    /**
     * Method that instances a FavoriteUpdater class.
     *
     * This method instances a FavoriteUpdater class to add or remove as favorite
     * the POI related to the code passed as argument.
     *
     * @author Galo Castillo
     * @param selectedPoi The FavoriteUpdater URL request parameter.
     * @return The method returns nothing.
     */
    public void makeUpdateFavoriteRequest(String selectedPoi){
        if(selectedPoi.trim().length() > 0){
            setChanged();
            notifyObservers(ADD_FAVORITES_REQUEST_STARTED);
            new FavoriteUpdater().execute(selectedPoi);
        }
        else{
            setChanged();
            notifyObservers(ADD_FAVORITES_REQUEST_FAILED_LOADING);
        }
    }

    /**
     * Auxiliary class that handles the favorite POI addition or removal.
     *
     * This auxiliary class handles the favorites addition or removal of an specified POI
     * when requested. This class calls a the favorites updating web service to add or remove
     * the POI sent as parameter on the call. Moreover, this class updates the user's favorites
     * stored on the shared preferences.
     *
     * @author Galo Castillo
     */
    private class FavoriteUpdater extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String selectedPoi = strings[0];
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
                    String[] codes = selectedPoi.split("\\|");
                    String codeGtsi = codes[0];
                    String codeInfrastrucure = codes[1];
                    String url;
                    if(SessionHelper.isFavorite(activity, selectedPoi)){
                        url = Constants.getDeleteFavoriteURL();
                    }
                    else{
                        url = Constants.getFavoritesURL();
                    }
                    JSONObject jsonBody = new JSONObject();
                    try{
                        jsonBody.put(Constants.CODE_GTSI_KEY, codeGtsi);
                        jsonBody.put(Constants.CODE_INFRA_KEY, codeInfrastrucure);
                    }
                    catch (Exception ignored){ ;
                    }
                    String accessToken = SessionHelper.getAccessToken(activity);
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            url, jsonBody, response -> {
                                try{
                                    JSONArray codesGtsi = response.getJSONArray(Constants.CODES_GTSI_KEY);                                    JSONArray jsonArray = response.getJSONArray(Constants.CODES_GTSI_KEY);
                                    JSONArray codesInfra = response.getJSONArray(Constants.CODES_INFRA_KEY);
                                    Set<String> favoritesSet = new HashSet<>();
                                    if (codesGtsi != null && codesInfra != null &&
                                            codesGtsi.length() == codesInfra.length()) {
                                        int len = codesGtsi.length();
                                        for (int i=0; i<len; i++){
                                            String favGtsi = " ";
                                            String favInfra = " ";
                                            if(codesGtsi.get(i).toString().trim().length() > 0){
                                                favGtsi = codesGtsi.get(i).toString();
                                            }
                                            if(codesInfra.get(i).toString().trim().length() > 0){
                                                favInfra = codesInfra.get(i).toString();
                                            }
                                            favoritesSet.add(favGtsi + "|" + favInfra);
                                        }
                                    }
                                    SessionHelper.saveFavoritePois(activity, favoritesSet);
                                    updateFavBtnColor(selectedPoi);
                                    showFavUpdateFeedback(url);
                                }
                                catch (Exception e){
                                    setChanged();
                                    notifyObservers(ADD_FAVORITES_REQUEST_FAILED_LOADING);
                                }
                            }, error -> {
                                VolleyLog.d("tag", "Error: " + error.getMessage());
                                setChanged();
                                notifyObservers(REQUEST_FAILED_HTTP);
                            }) {
                        /**
                         * Passing some request headers
                         */
                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put(Constants.ACCESS_TOKEN_HEADER_KEY, accessToken);
                            return headers;
                        }
                    };
                    AppController.getInstance(activity).addToRequestQueue(jsonObjReq);
                }
            }
            return null;
        }

        public void showFavUpdateFeedback(String url){
            if(url == Constants.getDeleteFavoriteURL()){
                setChanged();
                notifyObservers(REMOVE_FAVORITES_REQUEST_SUCCEEDED);
            }
            else{
                setChanged();
                notifyObservers(ADD_FAVORITES_REQUEST_SUCCEEDED);
            }
        }
    }

    /**
     * Method that instances a MapCentering class.
     *
     * This method instances a MapCentering class to center and zoom the map over
     * the POI related to the code passed as argument.
     *
     * @author GaloCastillo
     * @param selectedPoi The MapCentering URL request parameter.
     * @return The method returns nothing.
     */
    public void centerMapOnResult(String selectedPoi){
        setChanged();
        notifyObservers(MAP_CENTERING_REQUEST_STARTED);
        new MapCentering().execute(selectedPoi);
    }

    /**
     * Auxiliary class that handles the map zoom and centering.
     *
     * This Auxiliary class handles the map zoom and centering of an specified POI when requested.
     * This class calls a the coordinates web service to obtain a POI's central coordinate.
     *
     * @author Galo Castillo
     */
    private class MapCentering extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String selectedPoi = strings[0];
            String[] codes = selectedPoi.split("\\|");
            String codeGtsi = " ";
            String codeInfra = " ";
            try{
                codeGtsi = codes[0].trim();
            }
            catch (Exception ignored){
            }
            try{
                codeInfra = codes[1].trim();
            }
            catch (Exception ignored){
            }
            if (!Constants.isNetworkAvailable(activity)) {
                setChanged();
                notifyObservers(REQUEST_FAILED_CONNECTION);
            }
            else if (codeGtsi.trim().length() > 0 || codeInfra.trim().length() > 0){
                JSONObject jsonBody = new JSONObject();
                try{
                    jsonBody.put(Constants.CODE_GTSI_KEY, codeGtsi);
                    jsonBody.put(Constants.CODE_INFRA_KEY, codeInfra);
                }
                catch (Exception ignored){
                }
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        COORDINATES_WS, jsonBody, response -> {
                            try {
                                double lat = response.getDouble(Constants.LATITUDE_KEY);
                                double lon = response.getDouble(Constants.LONGITUDE_KEY);
                                LatLng point = new LatLng(lat, lon);
                                activity.setSelectedDestination(point);
                                activity.getViewHolder().editDestination.setText(selectedPoi);
                                activity.getViewHolder().editSearch.setText(selectedPoi);
                                activity.getViewHolder().editSearch.clearFocus();
                                activity.getViewHolder().mapView.getMapAsync(mapboxMap -> {
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
                                });
                                setChanged();
                                notifyObservers(MAP_CENTERING_REQUEST_SUCCEEDED);
                            } catch (Exception e) {
                                e.printStackTrace();
                                setChanged();
                                notifyObservers(MAP_CENTERING_REQUEST_FAILED_LOADING);
                            } finally {
                                System.gc();
                                adapter.getPois().clear();
                            }
                        }, error -> {
                            setChanged();
                            notifyObservers(REQUEST_FAILED_HTTP);
                        });
                AppController.getInstance(activity).addToRequestQueue(jsonObjReq);
            }
        return null;
        }
    }

    /**
     * Method that updates the user's origin location.
     *
     * This method obtains user's location to update a route origin position.
     *
     * @author GaloCastillo
     * @return The method returns nothing.
     */
    public void updateOriginLocation(){
        initializeLocationEngine();
        activity.setSelectedOrigin(new LatLng(activity.getOriginLocation().getLatitude(),
                activity.getOriginLocation().getLongitude()));
        activity.setOriginPosition(Point.fromLngLat(activity.getSelectedOrigin().getLongitude(),
                activity.getSelectedOrigin().getLatitude()));
    }
}