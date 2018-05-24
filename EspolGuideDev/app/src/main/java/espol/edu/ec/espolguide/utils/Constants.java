package espol.edu.ec.espolguide.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by galo on 14/04/18.
 */

public class Constants {
    public final static String IP_ = "192.168.0.7:8080";
    public final static String SERVER_URL = "http://" + IP_;
    public final static int CLOSE_ZOOM = 18;
    public final static int FAR_AWAY_ZOOM = 15;
    public final static String FROM_ORIGIN = "from_origin";
    public final static String FROM_DESTINATION = "from_destination";
    public final static int REQUEST_CODE = 1;

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
