package espol.edu.ec.espolguide.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by galo on 14/04/18.
 */

public class Constants {

    public final static String SERVER_URL = "http://espol-guide.espol.edu.ec";
    public final static int CLOSE_ZOOM = 18;
    public final static int FAR_AWAY_ZOOM = 16;
    public final static double ESPOL_CENTRAL_LAT = -2.14630;
    public final static double ESPOL_CENTRAL_LNG = -79.96575;

    public final static String FROM_ORIGIN = "from_origin";
    public final static String FROM_DESTINATION = "from_destination";
    public final static String WALKING_ROUTE_MODE = "walking_route_mode";
    public final static String CAR_ROUTE_MODE = "car_route_mode";

    public final static String NAMESPACE = "http://tempuri.org/";
    public final static String URL="https://ws.espol.edu.ec/saac/wsGuide.asmx";
    public final static String AUTH_METHOD_NAME = "autenticacion";
    public final static String AUTH_SOAP_ACTION = "http://tempuri.org/autenticacion";

    public final static String SUBJ_METHOD_NAME = "consultaCursoxUsuario";
    public final static String SUBJ_SOAP_ACTION = "http://tempuri.org/consultaCursoxUsuario";

    public final static String SOAP_HEADER = "GTSIAuthSoapHeader";
    public final static String USER_SOAP_HEADER = "taws";
    public final static String KEY_SOAP_HEADER = "jmMH2VY17PHPmUq2w7E2o7W4I2O9d16u";

    public static final Integer RC_SIGN_IN = 1;

    public final static String TO_LINK_ACCOUNT = "to_link_account";
    public final static String SELECTED_OPTION = "selected_option";

    public final static String CODE_INFRA_FIELD = "code_infra";
    public final static String CODE_GTSI_FIELD = "code_gtsi";
    public final static String BLOCKNAME_FIELD = "name_espol";
    public final static String ACADEMIC_UNIT_FIELD = "unity";
    public final static String DESCRIPTION_FIELD = "descriptio";

    public final static int ROUTE_ZOOM_PADDING_LEFT = 80;
    public final static int ROUTE_ZOOM_PADDING_TOP = 350;
    public final static int ROUTE_ZOOM_PADDING_RIGHT = 80;
    public final static int ROUTE_ZOOM_PADDING_BOTTOM = 0;

    public final static String ACCESS_TOKEN_KEY = "access-token";
    public final static String ACCESS_TOKEN_HEADER_KEY = "access-token";
    public final static String DATA_KEY = "data";
    public final static String USERNAME_KEY = "username";
    public final static String CODES_GTSI_KEY = "codes_gtsi";
    public final static String CODES_INFRA_KEY = "codes_infra";
    public final static String CODE_GTSI_KEY = "code_gtsi";
    public final static String CODE_INFRA_KEY = "code_infra";
    public final static String LATITUDE_KEY = "lat";
    public final static String LONGITUDE_KEY = "long";

    public final static String TYPE_FIELD = "type";
    public final static String ALTERNATIVE_NAMES_FIELD = "alternative_names";

    public final static String SELECTED_GTSI_CODE = "selected_gtsi_code";

    public final static String COLOR_FIRST = "#054A91";
    public final static String COLOR_SECOND = "#3E7CB1";
    public final static String COLOR_THIRD = "#81A4CD";
    public final static String COLOR_FOURTH = "#DBE4EE";
    public final static String COLOR_FIFTH = "#F17300";

    public final static int FAVORITES_REQUEST_CODE = 1;
    public final static int SUBJECTS_REQUEST_CODE = 2;

    public final static String ON_PERMISSION_RESULT = "on_permission_result";
    public final static String ON_CREATE = "on_create";
    public final static String ON_RESUME = "on_resume";

    public final static String USR_INFO_METHOD_NAME = "wsInfoUsuario";
    public final static String USR_INFO_SOAP_ACTION = "http://tempuri.org/wsInfoUsuario";

    public final static String MEDIA_NAMESPACE = "http://academico.espol.edu.ec/webservices/";
    public final static String MEDIA_URL="https://ws.espol.edu.ec/saac/wsMedia.asmx";
    public final static String USR_PHOTO_METHOD_NAME = "downloadFoto";
    public final static String USR_PHOTO_SOAP_ACTION = "http://academico.espol.edu.ec/webservices/downloadFoto";

    public static HashMap<String,HashMap<String,String>> getDaysTraductor(){
        HashMap<String,HashMap<String,String>> result = new HashMap<>();
        HashMap<String,String> english = new HashMap<>();
        english.put("LUNES","MONDAY");
        english.put("MARTES","TUESDAY");
        english.put("MIERCOLES","WEDNESDAY");
        english.put("JUEVES","THURSDAY");
        english.put("VIERNES","FRIDAY");
        english.put("SABADO","SATURDAY");
        english.put("DOMINGO","SUNDAY");
        HashMap<String,String> portuguese = new HashMap<>();
        portuguese.put("LUNES","SEGUNDA-FEIRA");
        portuguese.put("MARTES","TERÇA-FEIRA");
        portuguese.put("MIERCOLES","QUARTA-FEIRA");
        portuguese.put("JUEVES","QUINTA-FEIRA");
        portuguese.put("VIERNES","SEXTA-FEIRA");
        portuguese.put("SABADO","SABADO");
        portuguese.put("DOMINGO","DOMINGO");
        result.put("en",english);
        result.put("pt",portuguese);
        return result;
    }

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return Objects.requireNonNull(connectivityManager).getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static String getAlternativeNamesURL(){
        return SERVER_URL + "/alternativeNames/";
    }

    public static String getBlockPhotoURL(){
        return SERVER_URL + "/photoBlock/";
    }

    public static String getFavoritesURL(){
        return SERVER_URL + "/favorites/";
    }

    public static String getDeleteFavoriteURL(){
        return SERVER_URL + "/deleteFavorite/";
    }

    public static String getLoginURL(){
        return SERVER_URL + "/login/";
    }

    public static String getCoordinatesURL(){
        return SERVER_URL + "/coordinates/";
    }

    public static String getBuildingInfoURL(){
        return SERVER_URL + "/buildingInfo/";
    }


}
