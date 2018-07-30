package espol.edu.ec.espolguide;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;

import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;

import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.Util;
import espol.edu.ec.espolguide.viewModels.MapViewModel;

import android.location.Location;

import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.permissions.PermissionsListener;

/**
 * Created by galo on 29/12/17.
 */


/**
 * Activity for loading Map layout resources.
 *
 * This activity is used to display the Map layout resources. It uses the Observator
 * software design pattern.
 *
 * @autho Galo Castillo
 * @since apolo 0.1
 */
public class MapActivity extends BaseActivity implements Observer, LocationEngineListener, PermissionsListener {
    ViewHolder viewHolder;
    MapViewModel viewModel;

    private LatLng selectedOrigin;
    private LatLng selectedDestination;
    private String selectedEditText;

    private Location originLocation;

    private Point originPosition;
    private Point destinationPosition;

    private String selectedPoi = "";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_map, contentFrameLayout);

        this.viewHolder = new ViewHolder();
        this.viewModel = new MapViewModel(this);
        this.viewModel.setMapOnClickListener();
        this.viewModel.addObserver(this);
        this.viewModel.makeNamesRequest();
        this.viewModel.setSelectedRouteMode(Constants.WALKING_ROUTE_MODE);
        this.viewModel.setRouteModeButtonsListeners();
        this.disableMenuOption();
        this.viewModel.getInitialPosition();
        this.viewHolder.setFavBtnListener();
    }

    public class ViewHolder{
        public ListView searchPoiLv;
        public EditText editSearch;
        public MapView mapView;
        public LinearLayout info;
        public Button closePoiInfoBtn;
        public Marker featureMarker;
        public MapboxMap mapboxMap;
        public ImageButton favBtn;

        public EditText editOrigin;
        public EditText editDestination;
        public ListView originLv;
        public ListView destinationLv;
        public LinearLayout routeBox;
        public Button routeBtn;
        public ImageButton backBtn;

        public FrameLayout mapLayout;
        public FrameLayout routeSearchLayout;
        public ListView routesLv;
        public EditText editSearchRoutes;

        public ImageButton walkBtn;
        public ImageButton carBtn;

        public ImageView drawerBtn;
        public Button poiRoute;

        public ViewHolder(){
            findViews();
            setBackButtonListener();
            setClosePoiButtonListener();
            setDrawRouteButtonListener();
            setEditTextListeners();
            setDrawerBtnListener();
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
            routeBox = (LinearLayout) findViewById(R.id.route_box);
            routeBtn = (Button) findViewById(R.id.route_btn);
            mapLayout = (FrameLayout) findViewById(R.id.map_layout);
            routeSearchLayout = (FrameLayout) findViewById(R.id.routes_search_layout);
            routesLv = (ListView) findViewById(R.id.listroutes);
            editSearchRoutes = (EditText) findViewById(R.id.search_routes);
            backBtn = (ImageButton) findViewById(R.id.back_button);
            walkBtn = (ImageButton) findViewById(R.id.walk_button);
            carBtn = (ImageButton) findViewById(R.id.car_button);

            drawerBtn = (ImageView) findViewById(R.id.drawerBtn);
            favBtn = (ImageButton) findViewById(R.id.favoriteBtn);
            poiRoute = (Button) findViewById(R.id.poi_route_btn);
        }

        private void setDrawerBtnListener(){
            drawerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.openDrawer(MapActivity.this);
                }
            });
        }

        private void setFavBtnListener(){
            this.favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.makeUpdateFavoriteRequest(selectedPoi);
                }
            });
        }

        public MapboxMap getMapboxMap(){
            return this.mapboxMap;
        }

        public void setMapboxMap(MapboxMap mapboxMap){
            this.mapboxMap = mapboxMap;
        }

        private void setBackButtonListener(){
            this.backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMapLayoutView();
                }
            });
        }

        private void setClosePoiButtonListener(){
            this.closePoiInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closePoiInfo();
                }
            });
        }

        public void closePoiInfo(){
            mapboxMap.getUiSettings().setAllGesturesEnabled(true);
            final LinearLayout info = (LinearLayout) findViewById(R.id.overlay);
            info.setVisibility(View.GONE);
            ImageView photo = (ImageView) findViewById(R.id.flag);
            //photo.setImageResource(android.R.color.transparent);
            if(!isRouteModeViewDisplayed()){
                getViewHolder().editSearch.setVisibility(View.VISIBLE);
            }
        }

        private void setDrawRouteButtonListener(){
            this.routeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawRoute();
                }
            });
        }

        public void drawRoute(){
            try{
                editOrigin.setText(getResources().getString(R.string.your_location));
                viewModel.enableLocationPlugin();
                setSelectedOrigin(new LatLng(getOriginLocation().getLatitude(), getOriginLocation().getLongitude()));
                setOriginPosition(Point.fromLngLat(getSelectedOrigin().getLongitude(), getSelectedOrigin().getLatitude()));
                setDestinationPosition(Point.fromLngLat(getSelectedDestination().getLongitude(), getSelectedDestination().getLatitude()));
                viewModel.getRoute(getOriginPosition(), getDestinationPosition());
                editSearch.setVisibility(View.GONE);
                routeBox.setVisibility(View.VISIBLE);
                drawerBtn.setVisibility(View.GONE);
                routeBtn.setVisibility(View.GONE);
                editDestination.clearFocus();
                editOrigin.clearFocus();
            }
            catch(Exception e){
                if (!Constants.isNetworkAvailable(MapActivity.this)) {
                    MapActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MapActivity.this, getResources().getString(R.string.failed_connection_msg),
                                    Toast.LENGTH_LONG).show();

                        }
                    });
                }

                else if(getSelectedDestination() == null){
                    MapActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MapActivity.this, getResources().getString(R.string.error_on_calculating_route),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{
                    MapActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MapActivity.this, getResources().getString(R.string.error_on_getting_location),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
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
                        viewHolder.editSearchRoutes.setText(text);
                        viewHolder.editSearchRoutes.setHint(R.string.search_origin);
                        viewHolder.editOrigin.setFocusable(false);
                        viewHolder.editSearchRoutes.requestFocus();
                        viewHolder.editSearchRoutes.setSelection(text.length());
                        setSelectedEditText(Constants.FROM_ORIGIN);
                        viewHolder.mapLayout.setVisibility(View.GONE);
                        viewHolder.routeSearchLayout.setVisibility(View.VISIBLE);
                        Util.openKeyboard(MapActivity.this);
                    }
                    return false;
                }
            });

            this.editDestination.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(MotionEvent.ACTION_UP == event.getAction()){
                        String text = editDestination.getText().toString().trim();
                        viewHolder.editSearchRoutes.setText(text);
                        viewHolder.editSearchRoutes.setHint(R.string.search_destination);
                        viewHolder.editDestination.setFocusable(false);
                        viewHolder.editSearchRoutes.requestFocus();
                        viewHolder.editSearchRoutes.setSelection(text.length());
                        setSelectedEditText(Constants.FROM_DESTINATION);
                        viewHolder.mapLayout.setVisibility(View.GONE);
                        viewHolder.routeSearchLayout.setVisibility(View.VISIBLE);
                        Util.openKeyboard(MapActivity.this);
                    }
                    return false;
                }
            });

        }
    }

    @Override
    public void update(Observable o, Object arg) {
        String message = (String)arg;
        if (message == viewModel.NAMES_REQUEST_STARTED) {

        }
        if (message == viewModel.REQUEST_FAILED_CONNECTION) {
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
        if (message == viewModel.REQUEST_FAILED_HTTP) {
            MapActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MapActivity.this, getResources().getString(R.string.http_error_msg),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        if (message == viewModel.POI_INFO_REQUEST_STARTED) {

        }
        if (message == viewModel.POI_INFO_REQUEST_SUCCEEDED) {
            getViewHolder().editSearch.setVisibility(View.GONE);
        }
        if (message == viewModel.POI_INFO_REQUEST_FAILED_LOADING) {
            MapActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MapActivity.this, getResources().getString(R.string.loading_poi_info_error_msg),
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        if (message == viewModel.ROUTE_REQUEST_STARTED) {

        }
        if (message == viewModel.ROUTE_REQUEST_SUCCEEDED) {

        }
        if (message == viewModel.ROUTE_REQUEST_FAILED) {
            MapActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MapActivity.this, getResources().getString(R.string.error_on_calculating_route),
                            Toast.LENGTH_LONG).show();
                }
            });
            showMapLayoutView();
        }
        if (message == viewModel.ADD_FAVORITES_REQUEST_STARTED) {

        }
        if (message == viewModel.ADD_FAVORITES_REQUEST_SUCCEEDED) {
            /**
             *
             */
        }
        if (message == viewModel.ADD_FAVORITES_REQUEST_FAILED_LOADING) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        viewModel.getPermissionsManager().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            viewModel.getInitialPosition();
        } else {
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onConnected() {
        viewModel.getLocationEngine().requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            setOriginLocation(location);
            viewModel.getLocationEngine().removeLocationEngineListener(this);
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        if (viewModel.getLocationEngine() != null) {
            viewModel.getLocationEngine().requestLocationUpdates();
        }
        if (viewModel.getLocationPlugin() != null) {
            viewModel.getLocationPlugin().onStart();
        }
        viewHolder.mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (viewModel.getLocationEngine() != null) {
            viewModel.getLocationEngine().removeLocationUpdates();
        }
        if (viewModel.getLocationPlugin() != null) {
            viewModel.getLocationPlugin().onStop();
        }
        viewHolder.mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewHolder.mapView.onDestroy();
        if (viewModel.getLocationEngine() != null) {
            viewModel.getLocationEngine().deactivate();
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
        Util.closeDrawer(this);
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
    public void onBackPressed() {
        if(Util.isDrawerOpen(this)){
            Util.closeDrawer(this);
        }
        else if(isPoiInfoDisplayed()){
            viewHolder.closePoiInfo();
            viewModel.removeMarkers();
        }
        else if (isUpdateRouteViewDisplayed()){
            hideUpdateRouteView();
            showRouteModeView();
        }
        else if (isRouteModeViewDisplayed()){
            hideRouteModeView();
            showMapLayoutView();
        }
        else if(isRouteBtnDisplayed()){
            viewHolder.routeBtn.setVisibility(View.GONE);
            viewModel.removeMarkers();
            showMapLayoutView();
        }
        else if(isSearchResultsDisplayed()){
            getViewHolder().searchPoiLv.setVisibility(View.GONE);
            getViewHolder().routesLv.setVisibility(View.GONE);
        }
        else if(isMapLayoutViewDisplayed() && !isPoiInfoDisplayed() && !isRouteBtnDisplayed() &&
                !isRouteModeViewDisplayed() && !isUpdateRouteViewDisplayed() &&
                !Util.isDrawerOpen(this)){
            finish();
        }
    }

    public boolean isSearchResultsDisplayed(){
        return getViewHolder().searchPoiLv.getVisibility() == View.VISIBLE ||
                getViewHolder().routesLv.getVisibility() == View.VISIBLE;
    }

    public boolean isPoiInfoDisplayed(){
        return viewHolder.info.getVisibility() == View.VISIBLE;
    }

    public void hidePoiInfo(){
        getViewHolder().info.setVisibility(View.GONE);
    }

    public void showPoiInfo(){
        getViewHolder().info.setVisibility(View.VISIBLE);
    }

    public boolean isRouteBtnDisplayed(){
        return viewHolder.routeBtn.getVisibility() == View.VISIBLE;
    }

    public void hideRouteBtn(){
        getViewHolder().routeBtn.setVisibility(View.GONE);
    }

    public void showRouteBtn(){
        getViewHolder().routeBtn.setVisibility(View.VISIBLE);
    }

    public boolean isMapLayoutViewDisplayed(){
        return this.viewHolder.mapLayout.getVisibility() == View.VISIBLE;
    }

    public void hideMapLayoutView(){
        getViewHolder().mapLayout.setVisibility(View.GONE);
    }

    public void showMapLayoutView(){
        viewModel.removeMarkers();
        this.viewHolder.drawerBtn.setVisibility(View.VISIBLE);
        this.viewHolder.editSearch.setText("");
        this.viewHolder.editDestination.setText("");
        this.viewHolder.editOrigin.setText("");
        hidePoiInfo();
        hideRouteBtn();
        hideRouteModeView();
        hideUpdateRouteView();
        if (this.viewHolder.featureMarker != null){
            this.viewHolder.mapboxMap.removeMarker(this.viewHolder.featureMarker);
        }
        if (this.viewModel.getAdapter().getFeatureMarker() != null){
            this.viewHolder.mapboxMap.removeMarker(this.viewModel.getAdapter().getFeatureMarker());
        }
        if (viewModel.getNavigationMapRoute() != null) {
            viewModel.getNavigationMapRoute().removeRoute();
        }
        this.viewHolder.editSearch.setVisibility(View.VISIBLE);
        this.viewModel.setSelectedRouteMode(Constants.WALKING_ROUTE_MODE);
        this.viewHolder.mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(Constants.ESPOL_CENTRAL_LAT, Constants.ESPOL_CENTRAL_LNG))
                        .zoom(Constants.FAR_AWAY_ZOOM)
                        .build());
            }
        });
    }

    public boolean isUpdateRouteViewDisplayed(){
        return viewHolder.mapLayout.getVisibility() == View.GONE &&
                viewHolder.routeSearchLayout.getVisibility() == View.VISIBLE;
    }

    public void hideUpdateRouteView(){
        this.viewHolder.editSearchRoutes.setText("");
        this.viewHolder.routeSearchLayout.setVisibility(View.GONE);
    }

    public void showRouteModeView(){
        this.viewHolder.mapLayout.setVisibility(View.VISIBLE);
        viewModel.removeMarkers();
    }

    public boolean isRouteModeViewDisplayed(){
        return viewHolder.routeBox.getVisibility() == View.VISIBLE;
    }

    public void hideRouteModeView(){
        viewHolder.routeBox.setVisibility(View.GONE);
    }


    public LatLng getSelectedOrigin() {
        return selectedOrigin;
    }

    public void setSelectedOrigin(LatLng selectedOrigin) {
        this.selectedOrigin = selectedOrigin;
    }

    public LatLng getSelectedDestination() {
        return selectedDestination;
    }

    public void setSelectedDestination(LatLng selectedDestination) {
        this.selectedDestination = selectedDestination;
    }

    public String getSelectedEditText() {
        return selectedEditText;
    }

    public void setSelectedEditText(String selectedEditText) {
        this.selectedEditText = selectedEditText;
    }

    public Location getOriginLocation() {
        return originLocation;
    }

    public void setOriginLocation(Location originLocation) {
        this.originLocation = originLocation;
    }

    public MapViewModel getViewModel() {
        return this.viewModel;
    }

    public void setViewModel(MapViewModel viewModel){
        this.viewModel = viewModel;
    }

    public Point getOriginPosition(){
        return this.originPosition;
    }

    public Point getDestinationPosition(){
        return this.destinationPosition;
    }

    public void setOriginPosition(Point originPosition){
        this.originPosition = originPosition;
    }

    public void setDestinationPosition(Point destinationPosition){ this.destinationPosition = destinationPosition; }

    public ViewHolder getViewHolder(){
        return this.viewHolder;
    }

    public void disableMenuOption(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        try{
            navigationView.getMenu().findItem(R.id.map_op).setChecked(true);
        }catch (Exception e){
            e.getStackTrace();
        }
    }

    public void setSelectedPoi(String selectedPoi){
        this.selectedPoi = selectedPoi;
    }

    public String getSelectedPoi(){
        return this.selectedPoi;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String codeGtsi = "";
        if (requestCode == Constants.SUBJECTS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if(data.getExtras().containsKey(Constants.SELECTED_GTSI_CODE)){
                    codeGtsi = data.getExtras().getString(Constants.SELECTED_GTSI_CODE);
                    viewModel.centerMapOnResult(codeGtsi);
                    if (viewModel.getNavigationMapRoute() != null) {
                        viewModel.getNavigationMapRoute().removeRoute();
                    }
                }
            }
            else if (resultCode == RESULT_CANCELED) {
                // Write the code if there's no result
            }
        }

        else if(requestCode == Constants.FAVORITES_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                if(data.getExtras().containsKey(Constants.SELECTED_GTSI_CODE)){
                    codeGtsi = data.getExtras().getString(Constants.SELECTED_GTSI_CODE);
                    viewModel.centerMapOnResult(codeGtsi);
                    if (viewModel.getNavigationMapRoute() != null) {
                        viewModel.getNavigationMapRoute().removeRoute();
                    }
                }
            }
            else if (resultCode == RESULT_CANCELED) {
                // Write the code if there's no result
            }
        }
    }
}
