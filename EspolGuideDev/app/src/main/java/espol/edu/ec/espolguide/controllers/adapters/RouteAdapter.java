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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import espol.edu.ec.espolguide.MapActivity;
import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.Util;

public class RouteAdapter extends BaseAdapter {
    final String COORDINATES_WS = Constants.getCoordinatesURL();
    final Context mContext;
    final LayoutInflater inflater;
    private List<String> pois = null;
    private final ArrayList<String> arraylist;
    private LinearLayout layout;
    private ViewHolder viewHolder;
    private View bar;

    public class ViewHolder {
        String id;
        String codeGtsi;
        String codeInfra;
        TextView name;
        TextView alternativeName;

        public String getId(){
            return this.id;
        }

        public String getCodeGtsi(){
            return this.codeGtsi;
        }
    }

    public LinearLayout getLayout(){
        return this.layout;
    }

    public void setLayout(LinearLayout layout){
        this.layout = layout;
    }

    public Context getmContext(){
        return this.mContext;
    }

    public void setViewHolder(ViewHolder viewHolder){
        this.viewHolder = viewHolder;
    }

    public RouteAdapter(List<String> pois, MapActivity activity) {
        this.mContext = activity;
        this.pois = pois;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(pois);
        inflater = LayoutInflater.from(mContext);
    }

    public void setBar(View bar){
        this.bar = bar;
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
        try{
            holder.codeInfra = parts[4];
        }
        catch (Exception e){
            holder.codeInfra = " ";
        }

        view.setOnClickListener(arg0 -> {
            Util.closeKeyboard(mContext);
            if (!Constants.isNetworkAvailable(getmContext())) {
                Toast.makeText(getmContext(), mContext.getResources().getString(R.string.failed_connection_msg),
                        Toast.LENGTH_LONG).show();
            } else if (holder.getCodeGtsi().trim().length() > 0 ||
                    holder.codeInfra.trim().length() > 0){
                JSONObject jsonBody = new JSONObject();
                try{
                    jsonBody.put(Constants.CODE_GTSI_KEY, holder.codeGtsi);
                    jsonBody.put(Constants.CODE_INFRA_KEY, holder.codeInfra);
                }
                catch (Exception ignored){ ;
                }
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        COORDINATES_WS, jsonBody, response -> {
                    try {
                        double selectedLat = response.getDouble(Constants.LATITUDE_KEY);
                        double selectedLng = response.getDouble(Constants.LONGITUDE_KEY);
                        pois.clear();
                        MapActivity activity = (MapActivity) mContext;
                        if (activity.getSelectedEditText().equals(Constants.FROM_ORIGIN)) {
                            LatLng selectedOrigin = new LatLng(selectedLat, selectedLng);
                            activity.setSelectedOrigin(selectedOrigin);
                            activity.getViewHolder().editOrigin.setText(name1);
                            activity.setOriginPosition(Point.fromLngLat(activity.getSelectedOrigin().getLongitude(),
                                    activity.getSelectedOrigin().getLatitude()));
                        } else if (activity.getSelectedEditText().equals(Constants.FROM_DESTINATION)) {
                            LatLng selectedDestination = new LatLng(selectedLat, selectedLng);
                            activity.setSelectedDestination(selectedDestination);
                            activity.getViewHolder().editDestination.setText(name1);
                            activity.setDestinationPosition(Point.fromLngLat(activity.getSelectedDestination().getLongitude(),
                                    activity.getSelectedDestination().getLatitude()));
                            if (activity.getViewHolder().editOrigin.getText().toString().trim()
                                    .equals(activity.getApplicationContext().getString(R.string.your_location).trim())) {
                                activity.getViewModel().updateOriginLocation();
                            }
                        }
                        activity.getViewHolder().routeSearchLayout.setVisibility(View.GONE);
                        activity.getViewHolder().mapLayout.setVisibility(View.VISIBLE);
                        activity.getViewModel().removeMarkers();
                        activity.getViewModel().getRoute(activity.getOriginPosition(), activity.getDestinationPosition());
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