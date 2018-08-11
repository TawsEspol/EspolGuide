package espol.edu.ec.espolguide.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by galo on 13/06/18.
 */

public class SessionHelper {
    private static final String ESPOL_USERNAME = "espol_username";
    private static final String FAVORITES = "favorites";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String ESPOL_NAME = "espol_name";
    private static final String ESPOL_ID = "espol_id";
    private static final String ESPOL_PHOTO = "espol_photo";
    private static final String FB_USERNAME = "fb_name";
    private static final String FB_PHOTO = "fb_photo";
    private static final String GOOGLE_NAME = "googl_name";
    private static final String GOOGLE_PHOTO = "googl_photo";
    private static final String GOOGLE_SESSION = "googl_session";

    public static boolean isEspolLoggedIn(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.contains(ESPOL_USERNAME);
    }

    public static void saveEspolSession(Context context, String username){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ESPOL_USERNAME, username);
        editor.commit();
    }

    public static String getEspolUsername(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(ESPOL_USERNAME, "");
    }

    public static void saveAccessToken(Context context, String accessToken){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    public static boolean hasAccessToken(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.contains(ACCESS_TOKEN);
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

    public static Set getFavoritePois(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getStringSet(FAVORITES, new HashSet<>());
    }

    public static boolean hasFavorites(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return isEspolLoggedIn(context) && sharedPref.contains(FAVORITES);
    }

    public static boolean isFavorite(Context context, String codeGtsi){
        if(isEspolLoggedIn(context) && hasFavorites(context)){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPref.getStringSet(FAVORITES, new HashSet<>()).contains(codeGtsi);
        }
        return false;
    }

    public static boolean isFacebookLoggedIn(Context context){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        return  isLoggedIn;
    }
    public static boolean isGoogleLoggedIn(Activity activity){
        return true;
    }

    public static void saveEspolName(Context ctx, String name) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ESPOL_NAME, name);
        editor.commit();
    }

    public static void saveEspolUserIdNumber(Context ctx, String studentNumber) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ESPOL_ID, studentNumber);
        editor.commit();
    }

    public static void saveEspolUserPhoto(Context ctx, String photoString) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ESPOL_PHOTO, photoString);
        editor.commit();
    }

    public static String getEspolName(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(ESPOL_NAME, "");
    }

    public static String getEspolPhoto(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(ESPOL_PHOTO, "");
    }
    public static String getFbPhoto(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(FB_PHOTO, "");
    }

    public static void saveFbPhoto(Context ctx, String uri_photo) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(FB_PHOTO, uri_photo);
        editor.commit();
    }

    public static String getFbName(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(FB_USERNAME, "");
    }

    public static void saveFbName(Context ctx, String name) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(FB_USERNAME, name);
        editor.commit();
    }

    public static String getGoogleName(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(GOOGLE_NAME, "");
    }

    public static void saveGoogleName(Context ctx,String name) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(GOOGLE_NAME, name);
        editor.commit();
    }

    public static String getGooglePhoto(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(GOOGLE_PHOTO, "");
    }

    public static void saveGooglePhoto(Context ctx, String photoAsString) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(GOOGLE_PHOTO, photoAsString);
        editor.commit();
    }
    public static void logout(Context context){
        if (isFacebookLoggedIn(context)){
            LoginManager.getInstance().logOut();
        }
        clear(context);
    }

    public static void clear(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    public static void fbLogout(Context context){
        LoginManager.getInstance().logOut();
        clear(context);
    }

}
