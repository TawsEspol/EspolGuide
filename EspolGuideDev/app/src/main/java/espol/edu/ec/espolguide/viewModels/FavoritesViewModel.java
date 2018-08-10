package espol.edu.ec.espolguide.viewModels;

import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import espol.edu.ec.espolguide.FavoritesActivity;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.controllers.adapters.FavoriteAdapter;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.SessionHelper;

/**
 * Created by galo on 04/07/18.
 */

public class FavoritesViewModel extends Observable {
    public static final String LOAD_FAVORITES_STARTED = "load_favorites_started";
    public static final String LOAD_FAVORITES_SUCCEEDED = "load_favorites_succeeded";
    public static final String FAVORITES_NOT_FOUND = "favorites_not_found";
    public static final String LOAD_FAVORITES_FAILED = "load_favorites_failed";

    public static final String REQUEST_FAILED_CONNECTION = "request_failed_connection";
    public static final String REQUEST_FAILED_HTTP = "request_failed_http";
    public static final String GET_FAVORITES_REQUEST_STARTED = "get_favorites_request_started";
    public static final String GET_FAVORITES_REQUEST_SUCCEEDED = "get_favorites_request_succeeded";
    public static final String GET_FAVORITES_REQUEST_FAILED_LOADING = "get_favorites_request_failed_loading";

    private final String FAVORITES_WS = Constants.getFavoritesURL();
    private ArrayList<String> favoritePlaces = new ArrayList<>();
    private FavoriteAdapter favoriteAdapter;
    private final FavoritesActivity activity;

    public FavoritesViewModel(FavoritesActivity activity){
        this.activity = activity;
    }

    public void makeGetFavoritesRequest(){
        setChanged();
        notifyObservers(GET_FAVORITES_REQUEST_STARTED);
        new FavoritesGetter().execute();
    }

    private class FavoritesGetter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if(!SessionHelper.hasAccessToken(activity)){
                setChanged();
                notifyObservers(GET_FAVORITES_REQUEST_FAILED_LOADING);
            }
            else{
                String accessToken = SessionHelper.getAccessToken(activity);
                if (!Constants.isNetworkAvailable(activity)) {
                    setChanged();
                    notifyObservers(REQUEST_FAILED_CONNECTION);
                }
                else {
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                            FAVORITES_WS, null, response -> {
                                try{
                                    JSONArray jsonArray = response.getJSONArray(Constants.CODES_GTSI_KEY);
                                    ArrayList<String> favoritesList = new ArrayList<>();
                                    if (jsonArray != null) {
                                        int len = jsonArray.length();
                                        for (int i=0;i<len;i++){
                                            favoritesList.add(jsonArray.get(i).toString());
                                        }
                                    }
                                    Set<String> favoritesSet = new HashSet<>();
                                    favoritesSet.addAll(favoritesList);
                                    SessionHelper.saveFavoritePois(activity, favoritesSet);
                                    setChanged();
                                    notifyObservers(GET_FAVORITES_REQUEST_SUCCEEDED);
                                }
                                catch (Exception e){
                                    setChanged();
                                    notifyObservers(GET_FAVORITES_REQUEST_FAILED_LOADING);
                                }
                            }, error -> {
                                VolleyLog.d("tag", "Error: " + error.getMessage());
                                setChanged();
                                notifyObservers(REQUEST_FAILED_HTTP);
                            }){
                        /**
                         * Passing some request headers
                         */
                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put(Constants.ACCESS_TOKEN_HEADER_KEY, accessToken);
                            return headers;
                        }

                    };
                    AppController.getInstance(activity).addToRequestQueue(jsonObjReq);
                }
            }
            return null;
        }
    }

    public void loadFavorites(){
        setChanged();
        notifyObservers(LOAD_FAVORITES_STARTED);
        new FavoritesLoader().execute();    }

    private class FavoritesLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if(!SessionHelper.hasFavorites(activity)){
                setChanged();
                notifyObservers(FAVORITES_NOT_FOUND);
            }
            else{
                Set<String> favoritesSet = SessionHelper.getFavoritePois(activity);
                favoritePlaces.addAll(favoritesSet);
                favoriteAdapter = new FavoriteAdapter(activity, favoritePlaces);
                activity.getViewHolder().favoritesLv.setAdapter(favoriteAdapter);
                setChanged();
                notifyObservers(LOAD_FAVORITES_SUCCEEDED);
            }
            return null;
        }
    }

    public ArrayList<String> getFavoritePlaces(){
        return this.favoritePlaces;
    }

    public void setFavoritePlaces(ArrayList<String> favoritePlaces){
        this.favoritePlaces = favoritePlaces;
    }

    public FavoriteAdapter getFavoriteAdapter() {
        return this.favoriteAdapter;
    }

    public void setFavoriteAdapter(FavoriteAdapter favoriteAdapter){
        this.favoriteAdapter = favoriteAdapter;
    }
}
