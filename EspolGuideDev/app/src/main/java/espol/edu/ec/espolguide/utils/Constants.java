package espol.edu.ec.espolguide.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by galo on 14/04/18.
 */

public class Constants {

    public final static String IP_ = "";

    public final static String SERVER_URL = "http://" + IP_;
    public final static int CLOSE_ZOOM = 18;
    public final static int FAR_AWAY_ZOOM = 16;
    public final static double ESPOL_CENTRAL_LAT = -2.14630;
    public final static double ESPOL_CENTRAL_LNG = -79.96575;
    public final static String FROM_ORIGIN = "from_origin";
    public final static String FROM_DESTINATION = "from_destination";
    public final static int REQUEST_CODE = 1;

    public final static String NAMESPACE = "http://tempuri.org/";
    public final static String URL="https://ws.espol.edu.ec/saac/wsGuide.asmx";
    public final static String AUTH_METHOD_NAME = "autenticacion";
    public final static String AUTH_SOAP_ACTION = "http://tempuri.org/autenticacion";

    public final static String SOAP_HEADER = "GTSIAuthSoapHeader";
    public final static String USER_SOAP_HEADER = "taws";
    public final static String KEY_SOAP_HEADER = "jmMH2VY17PHPmUq2w7E2o7W4I2O9d16u";

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

    public static String getBlockPhoto(){
        return SERVER_URL + "/photoBlock/";
    }

}
