package espol.edu.ec.espolguide.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.List;
import java.util.Set;

/**
 * Created by galo on 13/06/18.
 */

public class SessionHelper {
    private static final String ESPOL_USERNAME = "espol_username";
    private static final String FAVORITES = "favorites";
    private static final String ACCESS_TOKEN = "access_token";

    public static boolean isEspolLoggedIn(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if(sharedPref.contains(ESPOL_USERNAME)){
            return true;
        }
        return false;
    }

    public static void saveEspolSession(Context context, String username){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ESPOL_USERNAME, username);
        editor.commit();
    }

    public static void logout(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    public static String getEspolUsername(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(ESPOL_USERNAME, "");
    }

    public static void saveEspolClasses(){

    }

    public static void saveAccessToken(Context context, String accessToken){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    public static boolean hasAccessToken(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if(sharedPref.contains(ACCESS_TOKEN)){
            return true;
        }
        return false;
    }

    public static String getAccessToken(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(ACCESS_TOKEN, "");
    }

    public static void saveFavoritePois(Context context, Set<String> favorites){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(FAVORITES, favorites);
        editor.commit();
    }

    public static boolean isFacebookLoggedIn(Activity activity){
        return true;
    }

    public static boolean isGoogleLoggedIn(Activity activity){
        return true;
    }
}
