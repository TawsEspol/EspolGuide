package espol.edu.ec.espolguide.viewModels;

import android.content.Context;
import android.graphics.PointF;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
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
import retrofit2.Call;
import retrofit2.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Observable;


public class MapViewModel extends Observable{
    public static String NAMES_REQUEST_STARTED = "names_request_started";
    public static String NAMES_REQUEST_SUCCEEDED = "names_request_succeeded";
    public static String NAMES_REQUEST_FAILED_CONNECTION = "names_request_failed_connection";
    public static String NAMES_REQUEST_FAILED_HTTP = "names_request_failed_http";
    public static String NAMES_REQUEST_FAILED_LOADING = "names_request_failed_loading";

    public static String POI_INFO_REQUEST_STARTED = "poi_info_request_started";
    public static String POI_INFO_REQUEST_SUCCEEDED = "poi_info_request_succeeded";
    public static String POI_INFO_REQUEST_FAILED_LOADING = "poi_info_request_failed_loading";

    public static String ROUTE_REQUEST_STARTED = "route_request_started";
    public static String ROUTE_REQUEST_SUCCEEDED = "route_request_succeeded";
    public static String ROUTE_REQUEST_FAILED = "route_request_failed";

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
                notifyObservers(NAMES_REQUEST_FAILED_CONNECTION);
            }
            else {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        POIS_NAMES_WS, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Iterator<String> iter = response.keys();
                        while (iter.hasNext()) {
                            String identifier = iter.next();
                            Integer numIdentifier = Integer.getInteger(identifier.substring(6));
                            if (numIdentifier == null || numIdentifier <= 69) {
                                try {
                                    String blockString = "";
                                    JSONObject blockInfo = (JSONObject) response.get(identifier);
                                    String officialName = (String) blockInfo.getString("NombreOficial");
                                    JSONArray alternativeNames = blockInfo.getJSONArray("NombresAlternativos");
                                    int totalAlternatives = alternativeNames.length();
                                    String alternativeString = "";
                                    for (int i = 0; i < totalAlternatives; i++) {
                                        String alternative = (String) alternativeNames.get(i);
                                        alternativeString = alternativeString + "|" + alternative;
                                    }
                                    blockString = identifier +
                                            ";" + officialName + ";" + alternativeString;
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
                        notifyObservers(NAMES_REQUEST_FAILED_HTTP);
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

                                setNavigationMapRoute(new NavigationMapRoute(null, activity.getViewHolder().mapView, activity.getViewHolder().mapboxMap, R.style.NavigationMapRoute));
                            }
                            getNavigationMapRoute().addRoute(getCurrentRoute());
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
                                if (feature.properties() != null && feature.properties().has("code_gtsi")) {
                                    blockName = feature.getStringProperty("name").toString();
                                    academicUnit = feature.getStringProperty("unity").toString();
                                    description = feature.getStringProperty("descriptio").toString();
                                    new PoiInfoViewModel(new PoiInfo(blockName, academicUnit, description, activity,
                                            activity.getViewHolder().info)).show();
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

    @SuppressWarnings( {"MissingPermission"})
    public void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            // Create an instance of LOST location engine
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
        this.getLocationEngine().setPriority(LocationEnginePriority.LOW_POWER);
        this.getLocationEngine().activate();

        Location lastLocation = this.getLocationEngine().getLastLocation();
        if (lastLocation != null) {
            activity.setOriginLocation(lastLocation);
            this.setCameraPosition(lastLocation);
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

    public void setCameraPosition(Location location) {
        activity.getViewHolder().mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), Constants.FAR_AWAY_ZOOM));
    }
}