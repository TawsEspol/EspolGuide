package espol.edu.ec.espolguide.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by galo on 14/04/18.
 */

public class Constants {
    public final static String IP_ = "192.168.0.8:8080";
    public final static String SERVER_URL = "http://" + IP_;
    public final static int CLOSE_ZOOM = 18;
    public final static int FAR_AWAY_ZOOM = 15;
    public final static String ORIGIN_ADAPTER = "origin";
    public final static String DESTINATION_ADAPTER = "destination";

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static String getAlternativeNamesURL(){
        return SERVER_URL + "/nombresAlternativo/";
    }

    public static String getBlockInfoURL(){
        return SERVER_URL + "/infoBloque/";
    }

}
