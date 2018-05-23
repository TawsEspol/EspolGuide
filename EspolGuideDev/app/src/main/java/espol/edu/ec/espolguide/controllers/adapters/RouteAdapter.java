package espol.edu.ec.espolguide.controllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.SearchResultsActivity;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.Util;


public class RouteAdapter extends BaseAdapter {
    final String BLOCK_INFO_WS = Constants.getBlockInfoURL();
    Context mContext;
    LayoutInflater inflater;
    private List<String> pois = null;
    private ArrayList<String> arraylist;
    private LinearLayout layout;
    private ViewHolder viewHolder;
    private View bar;

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

    public RouteAdapter(List<String> pois, SearchResultsActivity activity) {
        this.mContext = activity;
        this.pois = pois;
        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(pois);
    }

    public void setInflater(SearchResultsActivity searchResultsActivity){
        this.inflater = LayoutInflater.from(searchResultsActivity);
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

                                SearchResultsActivity activity = (SearchResultsActivity) mContext;
                                JSONArray features = response.getJSONArray("features");
                                JSONObject jsonObj = (JSONObject) features.get(0);
                                JSONObject jsonObjGeometry = jsonObj.getJSONObject("geometry");
                                JSONArray coordinate1 = jsonObjGeometry.getJSONArray("coordinates").getJSONArray(0);
                                JSONArray pointCoord = coordinate1.getJSONArray(0);
                                System.out.println("=========ID: " + holder.getIdNumber() + " ========");
                                System.out.println("***********Name: " + name1 + " ********");
                                pois.clear();
                                double selectedLat = pointCoord.getDouble(0);
                                double selectedLng = pointCoord.getDouble(1);
                                Intent intent = new Intent();
                                intent.putExtra("selectedLat", selectedLat);
                                intent.putExtra("selectedLng", selectedLng);
                                intent.putExtra("officialName", name1);
                                intent.putExtra("from", activity.getFrom());
                                activity.setResult(Activity.RESULT_OK, intent);
                                activity.finish();
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