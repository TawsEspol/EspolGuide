package espol.edu.ec.espolguide.viewModels;

import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;

import espol.edu.ec.espolguide.FavoritesActivity;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.utils.Constants;

public class FavoritesViewModel extends Observable {
    public static String GET_FAVORITES_REQUEST_STARTED = "get_favorites_request_started";
    public static String GET_FAVORITES_REQUEST_SUCCEEDED = "get_favorites_request_succeeded";
    public static String GET_FAVORITES_REQUEST_FAILED_CONNECTION = "get_favorites_request_failed_connection";
    public static String GET_FAVORITES_REQUEST_FAILED_HTTP = "get_favorites_request_failed_http";
    public static String GET_FAVORITES_REQUEST_FAILED_LOADING = "get_favorites_request_failed_loading";
    public static String ADD_FAVORITES_REQUEST_STARTED = "add_favorites_request_started";
    public static String ADD_FAVORITES_REQUEST_SUCCEEDED = "add_favorites_request_succeeded";
    public static String ADD_FAVORITES_REQUEST_FAILED_CONNECTION = "add_favorites_request_failed_connection";
    public static String ADD_FAVORITES_REQUEST_FAILED_HTTP = "add_favorites_request_failed_http";
    public static String ADD_FAVORITES_REQUEST_FAILED_LOADING = "add_favorites_request_failed_loading";

    final private String ADD_FAVORITE_WS = Constants.getAddFavoriteURL();
    final private String GET_FAVORITES_WS = Constants.getGetFavoritesURL();

    private FavoritesActivity activity;
    private ArrayList<String> favoriteBlocks;

    public FavoritesViewModel(FavoritesActivity activity){
        this.activity = activity;
    }

    public void makeGetFavoritesRequest(){
        setChanged();
        notifyObservers(GET_FAVORITES_REQUEST_STARTED);
        new FavoritesGetter().execute();    }

    private class FavoritesGetter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if (!Constants.isNetworkAvailable(activity)) {
                setChanged();
                notifyObservers(GET_FAVORITES_REQUEST_FAILED_CONNECTION);
            }
            else {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        GET_FAVORITES_WS, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Iterator<String> iter = response.keys();
                        }
                        catch (Exception e){
                            setChanged();
                            notifyObservers(GET_FAVORITES_REQUEST_FAILED_LOADING);
                        }

                        setChanged();
                        notifyObservers(GET_FAVORITES_REQUEST_SUCCEEDED);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("tag", "Error: " + error.getMessage());
                        setChanged();
                        notifyObservers(GET_FAVORITES_REQUEST_FAILED_HTTP);
                    }
                });
                AppController.getInstance(activity).addToRequestQueue(jsonObjReq);
            }
        return null;
        }
    }

    private class FavoriteAdder extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            setChanged();
            notifyObservers(ADD_FAVORITES_REQUEST_STARTED);
            if (!Constants.isNetworkAvailable(activity)) {
                setChanged();
                notifyObservers(ADD_FAVORITES_REQUEST_FAILED_CONNECTION);
            }
            else {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        ADD_FAVORITE_WS, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Iterator<String> iter = response.keys();
                        }
                        catch (Exception e){
                            setChanged();
                            notifyObservers(ADD_FAVORITES_REQUEST_FAILED_LOADING);
                        }

                        setChanged();
                        notifyObservers(ADD_FAVORITES_REQUEST_SUCCEEDED);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("tag", "Error: " + error.getMessage());
                        setChanged();
                        notifyObservers(ADD_FAVORITES_REQUEST_FAILED_HTTP);
                    }
                });
                AppController.getInstance(activity).addToRequestQueue(jsonObjReq);
            }
            return null;
        }
    }
}
