package com.example.galo.espolguide;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
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
import com.example.galo.espolguide.pois.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static espolguide.helpers.constants.Constantes.IP_COMSOC;
import static espolguide.helpers.constants.Constantes.IP_LAB_SOFT_FAB;
import static espolguide.helpers.constants.Constantes.IP_TAWS_FAB;

/**
 * Created by fabricio on 14/01/18.
 */

public class SearchViewAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<String> pois_lista = null;
    private ArrayList<String> arraylist;
    private MapView mapView;
    private ViewHolder viewHolder;

    public class ViewHolder {
        String id;
        TextView nombre;
        TextView nombre_alternativo;

        public String get_idNumber(){
            String id_number = this.id.substring(6);
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

    public SearchViewAdapter(Context context, List<String> pois_lista) {
        mContext = context;
        this.pois_lista = pois_lista;
        inflater = LayoutInflater.from(mContext);

        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(pois_lista);
    }


    @Override
    public int getCount() {
        return pois_lista.size();
    }

    @Override
    public String getItem(int i) {
        return pois_lista.get(i);
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
            // Locate the TextViews in listview_item.xml
            holder.nombre = (TextView) view.findViewById(R.id.name);
            holder.nombre_alternativo = (TextView) view.findViewById(R.id.alter_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String data = pois_lista.get(i);
        String[] parts = data.split(";");
        String name1 = parts[1]; // 004
        String name2 = parts[2];

        // Set the results into TextViews
        holder.nombre.setText(name1);
        holder.nombre_alternativo.setText(name2);
        holder.id = parts[0];

        // Listen for ListView Item Click
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String info_poi_ws = "http://" + IP_TAWS_FAB + "/infoBloque/";
                if (!isNetworkAvailable(getmContext())) {
                    Toast.makeText(getmContext(), "Conexión no disponible", Toast.LENGTH_LONG).show();
                } else {
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                            info_poi_ws + holder.get_idNumber(), null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray features = response.getJSONArray("features");
                                response = null;
                                JSONObject jsonObj = (JSONObject) features.get(0);
                                JSONObject jsonObj_geometry = jsonObj.getJSONObject("geometry");
                                JSONArray coordenadas1 = jsonObj_geometry.getJSONArray("coordinates").getJSONArray(0);
                                JSONArray point_coord = coordenadas1.getJSONArray(0);
                                double lat = point_coord.getDouble(0);
                                double lon = point_coord.getDouble(1);

                                GeoPoint central_point = new GeoPoint(lat, lon);
                                IMapController map_controller = getMapView().getController();
                                map_controller.setZoom(19);
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

                /**
                // Send single item click data to SingleItemView Class
                Intent intent = new Intent(mContext, SingleItemView.class);
                //intent.putExtra("id",name1);
                // Pass all data rank
                intent.putExtra("name",name1);
                // Pass all data country
                intent.putExtra("alter_name",name2);
                // Pass all data population
                // Pass all data flag
                // Start SingleItemView Class
                System.out.println(name1);
                mContext.startActivity(intent);**/
            }
        });

        return view;
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        pois_lista.clear();
        if (charText.length() == 0) {
            //pois_lista.addAll(arraylist);
            pois_lista.clear();
        }
        else
        {
            for (String wp : arraylist)
            {
                if (wp.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    pois_lista.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
