package com.example.galo.espolguide.controllers.adapters;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.galo.espolguide.R;
import com.example.galo.espolguide.controllers.AppController;
import com.example.galo.espolguide.utils.Constants;
import com.example.galo.espolguide.utils.Util;

/**
 * Created by fabricio on 14/01/18.
 */

public class SearchViewAdapter extends BaseAdapter {
    final String BLOCK_INFO_WS = Constants.getBlockInfoURL();
    Context mContext;
    LayoutInflater inflater;
    private List<String> pois = null;
    private ArrayList<String> arraylist;
    private MapView mapView;
    private ViewHolder viewHolder;
    private View bar;
    private MapView map;
    private ArrayList<Marker> markers;

    public class ViewHolder {
        String id;
        TextView name;
        TextView alternativeName;

        public String get_idNumber(){
            String id_number = this.id.substring(6);
            System.out.println(id_number);
            return id_number;
        }

        public String getId(){
            return this.id;
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

    public SearchViewAdapter(Context context, MapView map,List<String> pois,View bar,
                             ArrayList<Marker> markers) {
        this.bar = bar;
        mContext = context;
        this.pois = pois;
        inflater = LayoutInflater.from(mContext);
        this.map = map;
        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(pois);
        this.markers = new ArrayList<>();
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
        System.out.println(parts[0]);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Util.closeKeyboard(mContext);
                if (!Constants.isNetworkAvailable(getmContext())) {
                    Toast.makeText(getmContext(), "Conexión no disponible", Toast.LENGTH_LONG).show();
                } else {
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                            BLOCK_INFO_WS + holder.get_idNumber(), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray features = response.getJSONArray("features");
                                JSONObject jsonObj = (JSONObject) features.get(0);
                                JSONObject jsonObj_geometry = jsonObj.getJSONObject("geometry");
                                JSONArray coordenadas1 = jsonObj_geometry.getJSONArray("coordinates").getJSONArray(0);
                                JSONArray point_coord = coordenadas1.getJSONArray(0);
                                double lat = point_coord.getDouble(0);
                                double lon = point_coord.getDouble(1);
                                map.getController().setZoom(20);
                                Marker startMarker = new Marker(map);
                                startMarker.setPosition(new GeoPoint(lat,lon));
                                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                startMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener(){
                                    @Override
                                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                                        return false;
                                    }
                                });
                                map.getOverlays().add(startMarker);
                                for (Marker marker : markers){
                                    marker.remove(map);
                                }
                                markers.clear();
                                markers.add(startMarker);
                                TextView f = (TextView) bar;
                                f.setText("");
                                GeoPoint central_point = new GeoPoint(lat, lon);
                                IMapController map_controller = getMapView().getController();
                                map_controller.setZoom(22);
                                map_controller.setCenter(central_point);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getmContext(), "Error cargando datos...", Toast.LENGTH_LONG).show();
                            } finally {
                                System.gc();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d("tag", "Error: " + error.getMessage());
                            Toast.makeText(getmContext(), "Error HTTP", Toast.LENGTH_SHORT).show();
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
