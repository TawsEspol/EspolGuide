package espol.edu.ec.espolguide.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by galo on 13/06/18.
 */

public class SessionHelper {
    private static final String ESPOL_USERNAME = "espol_username";

    public static boolean isEspolLoggedIn(Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        if(sharedPref.contains(ESPOL_USERNAME)){
            return true;
        }
        return false;
    }

    public static void saveEspolSession(Activity activity, String username){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ESPOL_USERNAME, username);
        editor.commit();
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
