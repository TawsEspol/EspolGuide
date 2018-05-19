package espol.edu.ec.espolguide.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.Util;


// classes to calculate a route
//import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import retrofit2.Call;
import retrofit2.Callback;
//import retrofit2.Response;


// classes needed to add location layer
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;

import android.support.annotation.NonNull;

import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
//import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode;
//import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.android.location.LostLocationEngine;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

public class RouteAdapter extends BaseAdapter {
    final String BLOCK_INFO_WS = Constants.getBlockInfoURL();
    Context mContext;
    LayoutInflater inflater;
    private List<String> pois = null;
    private ArrayList<String> arraylist;
    private MapView mapView;
    private ViewHolder viewHolder;
    private View bar;
    private MapboxMap mapboxMap;
    Marker featureMarker;


    private LatLng selectedCoordinate;
    RouteAdapter routeAdapter;
    private String adapterType;

    public class ViewHolder {
        String id;
        TextView name;
        TextView alternativeName;

        public String getIdNumber(){
            String id_number = this.id.substring(6);
            System.out.println(id_number);
            return id_number;
        }

        public String getId(){
            return this.id;
        }
    }

    public String getAdapterType(){
        return this.adapterType;
    }

    public void setAdapterType(String adapterType){
        this.adapterType = adapterType;
    }

    public RouteAdapter getRouteAdapter(){
        return this.routeAdapter;
    }

    public void setRouteAdapter(RouteAdapter routeAdapter){
        this.routeAdapter = routeAdapter;
    }

    public LatLng getSelectedCoordinate(){
        return this.selectedCoordinate;
    }

    public void setSelectedCoordinate(LatLng selectedCoordinate){
        this.selectedCoordinate = selectedCoordinate;
    }

    public MapView getMapView(){
        return this.mapView;
    }

    public void setMapView(MapView mapView){
        this.mapView = mapView;
    }

    public Context getmContext(){
        return this.mContext;
    }

    public void setViewHolder(ViewHolder viewHolder){
        this.viewHolder = viewHolder;
    }

    public RouteAdapter(Context context, MapboxMap mapboxMap, List<String> pois, View bar,
                             Marker featureMarker, String adapterType) {
        this.bar = bar;
        mContext = context;
        this.pois = pois;
        inflater = LayoutInflater.from(mContext);
        this.mapboxMap = mapboxMap;
        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(pois);
        this.featureMarker = featureMarker;
        this.adapterType = adapterType;
    }

    @Override
    public int getCount() {
        return pois.size();
    }

    @Override
    public String getItem(int i) {
        return pois.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.searchview_item, null);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.alternativeName = (TextView) view.findViewById(R.id.alter_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String data = pois.get(i);
        String[] parts = data.split(";");
        String name1 = parts[1];
        String name2 = parts[2];
        holder.name.setText(name1);
        holder.alternativeName.setText(name2);
        holder.id = parts[0];
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Util.closeKeyboard(mContext);
                if (!Constants.isNetworkAvailable(getmContext())) {
                    Toast.makeText(getmContext(), mContext.getResources().getString(R.string.failed_connection_msg),
                            Toast.LENGTH_LONG).show();
                } else {

                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                            BLOCK_INFO_WS + holder.getIdNumber(), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray features = response.getJSONArray("features");
                                JSONObject jsonObj = (JSONObject) features.get(0);
                                JSONObject jsonObjGeometry = jsonObj.getJSONObject("geometry");
                                JSONArray coordinate1 = jsonObjGeometry.getJSONArray("coordinates").getJSONArray(0);
                                JSONArray pointCoord = coordinate1.getJSONArray(0);
                                System.out.println("=========ID: " + holder.getIdNumber() + " ========");
                                System.out.println("***********Name: " + name1 + " ********");
                                double lat = pointCoord.getDouble(0);
                                double lon = pointCoord.getDouble(1);
                                LatLng point = new LatLng(lat, lon);
                                setSelectedCoordinate(point);

                                System.out.println(point.toString() + " --------------------");
                                pois.clear();
                                mapView.getMapAsync(new OnMapReadyCallback() {
                                    @Override
                                    public void onMapReady(MapboxMap mapboxMap) {
                                        if (featureMarker != null) {
                                            mapboxMap.removeMarker(featureMarker);
                                        }
                                        featureMarker = mapboxMap.addMarker(new MarkerOptions()
                                                .position(point)
                                        );
                                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                                .target(point)
                                                .zoom(18)
                                                .build());
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getmContext(), mContext.getResources().getString(R.string.loading_poi_info_error_msg),
                                        Toast.LENGTH_LONG).show();
                            } finally {
                                System.gc();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d("tag", "Error: " + error.getMessage());
                            Toast.makeText(getmContext(), mContext.getResources().getString(R.string.http_error_msg),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    AppController.getInstance(getmContext()).addToRequestQueue(jsonObjReq);



                }
            }
        });
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        pois.clear();
        if (charText.length() == 0) {
            pois.clear();
        }
        else
        {
            for (String wp : arraylist)
            {
                if (wp.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    pois.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}