package espol.edu.ec.espolguide.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by galo on 13/06/18.
 */

public class SessionHelper {
    private static final String ESPOL_USERNAME = "espol_username";

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

    public static boolean isFacebookLoggedIn(Activity activity){
        return true;
    }

    public static boolean isGoogleLoggedIn(Activity activity){
        return true;
    }
}
