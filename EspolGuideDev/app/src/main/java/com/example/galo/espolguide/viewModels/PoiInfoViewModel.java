package com.example.galo.espolguide.viewModels;

import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.galo.espolguide.PoiInfo;
import com.example.galo.espolguide.controllers.AppController;
import com.example.galo.espolguide.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.galo.espolguide.utils.Constants.getBlockInfoURL;
import static com.example.galo.espolguide.utils.Constants.isNetworkAvailable;

public class PoiInfoViewModel {
    final String BLOCK_INFO_WS = getBlockInfoURL();
    private PoiInfo activity;

    public PoiInfoViewModel(PoiInfo activity) {
        this.activity = activity;
    }

    public void makePoiInfoRequest(){
        new Info().execute(activity);
    }

    private class Info extends AsyncTask<PoiInfo, Void, ArrayList> {
        @Override
        protected ArrayList doInBackground(PoiInfo... pois) {
            activity = pois[0];
            if (!isNetworkAvailable(activity.getCtx())) {
                Toast.makeText(activity.getCtx(), "Conexión no disponible", Toast.LENGTH_LONG).show();
            } else {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        BLOCK_INFO_WS + activity.getCode(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray features = response.getJSONArray("features");
                            int totalFeatures = features.length();
                            for (int i = 0; i < totalFeatures; i++) {
                                JSONObject jsonObj = (JSONObject) features.get(i);
                                JSONObject properties = jsonObj.getJSONObject("properties");
                                activity.setCode(properties.getString("codigo"));
                                activity.setAcademicUnit(properties.getString("unidad"));
                                activity.setDescription(properties.getString("descripcio"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity.getCtx(), "Error cargando datos...", Toast.LENGTH_LONG).show();
                        } finally {
                            System.gc();
                        }
                        show();
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("tag", "Error: " + error.getMessage());
                        Toast.makeText(activity.getCtx(), "Error HTTP", Toast.LENGTH_SHORT).show();
                    }
                });
                AppController.getInstance(activity.getCtx()).addToRequestQueue(jsonObjReq);
                return null;
            }
            return null;
        }
    }

    public void show() {
        ViewGroup nextChild = (ViewGroup) ((ViewGroup)activity.getView()).getChildAt(0);
        ViewGroup linear = (ViewGroup) nextChild.getChildAt(2);
        TextView code = (TextView) linear.getChildAt(0);
        code.setText(activity.getCode());
        TextView academicUnit = (TextView) linear.getChildAt(1);
        academicUnit.setText(activity.getacAdemicUnit());
        TextView description = (TextView) nextChild.getChildAt(3);
        description.setText(activity.getDescription());
        activity.getView().setVisibility(View.VISIBLE);
    }
}

