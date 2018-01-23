package espolguide.helpers.constants;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by fabricio on 23/01/18.
 */

public class Constantes {
    public final static String IP_LAB_SOFT = "172.19.66.151:8000";  //eduroam
    public final static String IP_GALO = "192.168.0.13:8000";
    public final static String IP_TAWS = "192.168.0.126:8000";
    public final static String IP_FAB = "192.168.0.112:8000";
    public final static String IP_FAB_CASAGALO = "192.168.0.15:8000";
    public final static String IP_LAB_SOFT_FAB = "172.19.15.215:8000";  //eduroam
    public final static String IP_TAWS_FAB = "192.168.0.124:8000";  //eduroam


    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
