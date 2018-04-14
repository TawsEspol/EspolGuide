package com.example.galo.espolguide.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by galo on 14/04/18.
 */

public class Constants {
    public final static String IP_GALO = "192.168.0.13:8080";
    public final static String IP_TAWS = "192.168.0.126:8000";
    public final static String IP_FAB = "192.168.0.112:8000";

    public final static String SERVER_URL = "http://" + IP_GALO;
    public final static double ESPOL_CENTRAL_LONG = -79.96575;
    public final static double ESPOL_CENTRAL_LAT = -2.14630;
    public final static int START_ZOOM = 18;
    public final static int ZOOM_MAX = 20;

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static String getAlternativeNamesURL(){
        return SERVER_URL + "/nombresAlternativo/";
    }

    public static String getBlocksShapes(){
        return SERVER_URL + "/obtenerBloques/";
    }

    public static String getBlockInfo(){
        return SERVER_URL + "/infoBloque/";
    }

}
