package espol.edu.ec.espolguide.viewModels;

import android.content.Context;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import espol.edu.ec.espolguide.MapActivity;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.controllers.adapters.RouteAdapter;
import espol.edu.ec.espolguide.controllers.adapters.SearchViewAdapter;
import espol.edu.ec.espolguide.models.Block;
import espol.edu.ec.espolguide.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Observable;


public class MapViewModel extends Observable{
    public static String NAMES_REQUEST_STARTED = "names_request_started";
    public static String NAMES_REQUEST_FAILED_CONNECTION = "names_request_failed_connection";
    public static String NAMES_REQUEST_FAILED_HTTP = "names_request_failed_http";
    public static String NAMES_REQUEST_FAILED_LOADING = "names_request_failed_loading";

    final private String POIS_NAMES_WS = Constants.getAlternativeNamesURL();
    final private ArrayList<Marker> markerList = new ArrayList<>();
    final private ArrayList<String> namesItems = new ArrayList<>();
    private SearchViewAdapter adapter;
    private MapActivity activity;

    public MapViewModel(MapActivity activity) {
        this.activity = activity;
    }

    public void makeNamesRequest(){
        setChanged();
        notifyObservers(NAMES_REQUEST_STARTED);
        new Nombres().execute(activity);
    }

    public ArrayList<String> getNamesItems(){
        return this.namesItems;
    }

    public ArrayList<Marker> getMarkerList(){
        return this.markerList;
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







    private class BlockCoordinates extends AsyncTask<Context, Void, ArrayList> {
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
                        Constants.getBlockInfoURL(), null, new Response.Listener<JSONObject>() {
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

}