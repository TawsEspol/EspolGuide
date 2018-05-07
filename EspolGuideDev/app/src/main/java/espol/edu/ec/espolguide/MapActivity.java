
package espol.edu.ec.espolguide;

import java.util.Observable;
import java.util.Observer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
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

import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.viewModels.MapViewModel;

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
        validateWritingPermission();
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

    public void onResume(){
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }
}
