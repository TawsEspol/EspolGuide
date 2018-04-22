package com.example.galo.espolguide.viewModels;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.galo.espolguide.MapActivity;
import com.example.galo.espolguide.controllers.AppController;
import com.example.galo.espolguide.controllers.adapters.SearchViewAdapter;
import com.example.galo.espolguide.models.Block;
import com.example.galo.espolguide.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Observable;

public class MapViewModel extends Observable{
    public static String DRAW_REQUEST_STARTED = "draw_request_started";
    public static String DRAW_REQUEST_SUCCEED = "draw_request_succeed";
    public static String DRAW_REQUEST_FAILED_CONNECTION = "draw_request_failed_connection";
    public static String DRAW_REQUEST_FAILED_HTTP = "draw_request_failed_http";
    public static String DRAW_REQUEST_FAILED_LOADING = "draw_request_failed_loading";
    public static String NAMES_REQUEST_STARTED = "draw_request_started";
    public static String NAMES_REQUEST_FAILED_CONNECTION = "draw_request_failed_connection";
    public static String NAMES_REQUEST_FAILED_HTTP = "draw_request_failed_http";
    public static String NAMES_REQUEST_FAILED_LOADING = "draw_request_failed_loading";

    final private String GET_BLOCKS_SHAPES_WS = Constants.getBlocksShapesURL();
    final private String POIS_NAMES_WS = Constants.getAlternativeNamesURL();
    final private ArrayList<Marker> markerList = new ArrayList<>();
    final private ArrayList<String> namesItems = new ArrayList<>();
    private SearchViewAdapter adapter;
    private MapActivity activity;

    public MapViewModel(MapActivity activity) {
        this.activity = activity;
    }

    public void makelBocksShapesRequest(){
        setChanged();
        notifyObservers(DRAW_REQUEST_STARTED);
        new Drawer().execute(new DrawingTools(activity, activity.getViewHolder().mapView,
                                                        activity.getViewHolder().info));
    }

    public void makeNamesRequest(){
        setChanged();
        notifyObservers(NAMES_REQUEST_STARTED);
        new Nombres().execute(activity);
    }

    public ArrayList<Marker> getMarkerList(){
        return this.markerList;
    }

    private class DrawingTools {
        Context context;
        MapView map;
        View info;

        public DrawingTools(Context ctx, MapView map, View info){
            this.context = ctx;
            this.map = map;
            this.info = info;
        }
    }

    private class Drawer extends AsyncTask<DrawingTools, Void, Void> {
        DrawingTools actual ;
        @Override
        protected Void doInBackground(DrawingTools... dts) {
            actual = dts[0];
            if (!Constants.isNetworkAvailable(actual.context)) {
                setChanged();
                notifyObservers(DRAW_REQUEST_FAILED_CONNECTION);
            } else {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        GET_BLOCKS_SHAPES_WS, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray features = response.getJSONArray("features");
                            int totalFeatures = features.length();
                            for (int i = 0; i < totalFeatures; i++) {
                                JSONObject jsonObj = (JSONObject) features.get(i);
                                String identifier = jsonObj.getString("identificador");
                                JSONObject jsonObjGeometry = jsonObj.getJSONObject("geometry");
                                JSONArray coordinates = jsonObjGeometry.getJSONArray("coordinates").getJSONArray(0);
                                Block block = new Block(identifier);
                                block.buildPolygon(coordinates, actual.map, actual.context, actual.info);
                            }
                            setChanged();
                            notifyObservers(DRAW_REQUEST_SUCCEED);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            setChanged();
                            notifyObservers(DRAW_REQUEST_FAILED_LOADING);
                        } finally {
                            System.gc();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("tag", "Error: " + error.getMessage());
                        setChanged();
                        notifyObservers(DRAW_REQUEST_FAILED_HTTP);
                    }
                });
                AppController.getInstance(actual.context).addToRequestQueue(jsonObjReq);
            }
            return null;
        }
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
                        adapter = new SearchViewAdapter(activity, activity.getViewHolder().mapView, namesItems, activity.getViewHolder().editSearch,
                                getMarkerList());
                        adapter.setMapView(activity.getViewHolder().mapView);
                        activity.getViewHolder().searchPoiLv.setAdapter(adapter);
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
            return new ArrayList();
        }
    }
}