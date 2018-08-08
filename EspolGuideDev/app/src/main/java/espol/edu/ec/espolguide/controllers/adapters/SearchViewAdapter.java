package espol.edu.ec.espolguide.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import espol.edu.ec.espolguide.MapActivity;
import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.Util;
import java.io.Serializable;

/**
 * Created by fabricio on 14/01/18.
 */

public class SearchViewAdapter extends BaseAdapter {
    final String COORDINATES_WS = Constants.getCoordinatesURL();
    Context mContext;
    LayoutInflater inflater;
    private List<String> pois = null;
    private ArrayList<String> arraylist;
    private MapView mapView;
    private ViewHolder viewHolder;
    private View bar;
    private MapboxMap mapboxMap;
    Marker featureMarker;

    public class ViewHolder {
        String id;
        String codeGtsi;
        TextView name;
        TextView alternativeName;

        public String getIdNumber(){
            return this.id;
        }

        public String getId(){
            return this.id;
        }

        public String getCodeGtsi(){
            return this.codeGtsi;
        }
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

    public SearchViewAdapter(Context context, MapboxMap mapboxMap, List<String> pois, View bar,
                             Marker featureMarker) {
        this.bar = bar;
        mContext = context;
        this.pois = pois;
        inflater = LayoutInflater.from(mContext);
        this.mapboxMap = mapboxMap;
        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(pois);
        this.featureMarker = featureMarker;
    }

    public List<String> getPois() {
        return this.pois;
    }

    public Marker getFeatureMarker(){
        return this.featureMarker;
    }

    public ArrayList<String> getArraylist(){
        return this.arraylist;
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
            holder.name = view.findViewById(R.id.name);
            holder.alternativeName = view.findViewById(R.id.alter_name);
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

        holder.codeGtsi = parts[3];

        view.setOnClickListener(arg0 -> {
            Util.closeKeyboard(mContext);
            if (!Constants.isNetworkAvailable(getmContext())) {
                Toast.makeText(getmContext(), mContext.getResources().getString(R.string.failed_connection_msg),
                        Toast.LENGTH_LONG).show();
            } else if (holder.getCodeGtsi().trim().length() > 0){
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        COORDINATES_WS + holder.getCodeGtsi(), null, response -> {
                    MapActivity mapActivity = (MapActivity) mContext;
                    try {
                        if (response.length() > 0) {
                            double lat = response.getDouble(Constants.LATITUDE_KEY);
                            double lon = response.getDouble(Constants.LONGITUDE_KEY);
                            LatLng point = new LatLng(lat, lon);
                            mapActivity.setSelectedDestination(point);
                            mapActivity.getViewHolder().editDestination.setText(name1);
                            TextView f = (TextView) bar;
                            f.setText(name1);
                            pois.clear();
                            mapActivity.getViewHolder().editSearch.clearFocus();
                            mapView.getMapAsync(mapboxMap -> {
                                if (featureMarker != null) {
                                    mapboxMap.removeMarker(featureMarker);
                                }
                                featureMarker = mapboxMap.addMarker(new MarkerOptions()
                                        .position(point)
                                );
                                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                        .target(point)
                                        .zoom(Constants.CLOSE_ZOOM)
                                        .build());
                            });
                            mapActivity.getViewHolder().routeBtn.setVisibility(View.VISIBLE);
                        } else {
                            mapActivity.setSelectedDestination(null);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getmContext(), mContext.getResources().getString(R.string.loading_poi_info_error_msg),
                                Toast.LENGTH_LONG).show();
                    } finally {
                        System.gc();
                    }
                }, error -> {
                    VolleyLog.d("tag", "Error: " + error.getMessage());
                    Toast.makeText(getmContext(), mContext.getResources().getString(R.string.http_error_msg),
                            Toast.LENGTH_SHORT).show();
                });
                AppController.getInstance(getmContext()).addToRequestQueue(jsonObjReq);
            }
            else{
                Toast.makeText(getmContext(), mContext.getResources().getString(R.string.loading_poi_info_error_msg),
                        Toast.LENGTH_LONG).show();
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