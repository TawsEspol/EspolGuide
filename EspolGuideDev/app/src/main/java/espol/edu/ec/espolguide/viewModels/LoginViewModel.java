package espol.edu.ec.espolguide.viewModels;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.EditText;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.Vector;

import espol.edu.ec.espolguide.BaseActivity;
import espol.edu.ec.espolguide.LoginActivity;
import espol.edu.ec.espolguide.MapActivity;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.SessionHelper;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

/**
 * Created by fabricio on 19/05/18.
 */

public class LoginViewModel extends Observable {
    public static final String AUTH_REQUEST_STARTED = "auth_request_started";
    public static final String AUTH_REQUEST_SUCCEED = "auth_request_succeed";
    public static final String AUTH_WRONG_CREDENTIALS = "auth_wrong_credentials";
    public static final String NAMESPACE = "http://tempuri.org/";
    public static final String GOOGL_AUTH_REQUEST_STARTED = "google_auth_request_started";
    public static final String GOOGL_AUTH_REQUEST_SUCCEED = "google_auth_request_succeed";
    public static final String GOOGL_AUTH_WRONG_CREDENTIALS = "google_auth_wrong_credentials";
    public static final String FB_AUTHENTICATION = "facebook_authentication";
    public static final String GOOGLE_AUTHENTICATION = "google_authentication";
    public static final String IS_ESPOL_LOGGED_IN = "is_espol_logged_in";
    public static final String IS_NOT_LOGGED_IN = "is_not_logged_in";
    public static final String EG_LOGIN_REQUEST_STARTED = "eg_login_request_started";
    public static final String EG_LOGIN_REQUEST_SUCCEED = "eg_login_request_succeed";
    public static final String REQUEST_FAILED_CONNECTION = "request_failed_connection";
    public static final String REQUEST_FAILED_HTTP = "request_failed_http";
    public static final String GET_FAVORITES_REQUEST_STARTED = "get_favorites_request_started";
    public static final String GET_FAVORITES_REQUEST_SUCCEEDED = "get_favorites_request_succeeded";
    public static final String GET_FAVORITES_REQUEST_FAILED_LOADING = "get_favorites_request_failed_loading";
    public static final String NAME_REQUEST_STARTED = "name_request_started";
    public static final String NAME_REQUEST_SUCCEED = "name_request_succeed";
    public static final String PHOTO_REQUEST_STARTED = "photo_request_started";
    public static final String PHOTO_REQUEST_SUCCEED = "photo_request_succeed";

    private final String EG_LOGIN_WS = Constants.getLoginURL();
    private final String FAVORITES_WS = Constants.getFavoritesURL();

    private final LoginActivity activity;

    public LoginViewModel(LoginActivity activity) {
        this.activity = activity;
    }

    public void auth() {
        setChanged();
        notifyObservers(AUTH_REQUEST_STARTED);
        new Auth().execute(new AuthScreen(activity, activity.getViewHolder().username,
                activity.getViewHolder().password));
    }

    public void googleAuth(GoogleApiClient mGoogleSignInClient) {
        setChanged();
        notifyObservers(GOOGL_AUTH_REQUEST_STARTED);
        googleSignIn(mGoogleSignInClient);
    }

    private void googleSignIn(GoogleApiClient mGoogleSignInClient) {
        if (!Constants.isNetworkAvailable(activity.getApplicationContext())) {
            setChanged();
            notifyObservers(REQUEST_FAILED_CONNECTION);
        }else{
            Intent signInIntent = com.google.android.gms.auth.api.Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
            activity.startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
        }
    }

    public void handleFbSession(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn){
            setChanged();
            notifyObservers(FB_AUTHENTICATION);
            Intent intent;
            intent = new Intent(activity, MapActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    public void handleSignInResult(GoogleSignInResult result, GoogleApiClient googleClient) {
        if (result.isSuccess()) {
            googleClient.connect();
            BaseActivity.setClient(googleClient);
            setChanged();
            notifyObservers(GOOGL_AUTH_REQUEST_SUCCEED);
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //con este account puedes manejar la data.
            if (acct != null) {
                String personName = acct.getDisplayName();
                System.out.println(personName);
                String personGivenName = acct.getGivenName();
                SessionHelper.saveGoogleName(activity.getApplicationContext(),personGivenName);

                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                try {
                    SessionHelper.saveGooglePhoto(activity.getApplicationContext(), personPhoto.toString());
                }catch(Exception e){
                    e.printStackTrace();
                }
                updateUI();

            }
        } else {
            setChanged();
            notifyObservers(GOOGL_AUTH_WRONG_CREDENTIALS);
            // Signed out, show unauthenticated UI.
        }
    }

    private void updateUI() {
        Intent intent;
        intent = new Intent(activity, MapActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
    public void handleGoogleSession(GoogleApiClient mGoogleSignInClient) {
        OptionalPendingResult<GoogleSignInResult> opr = com.google.android.gms.auth.api.Auth.GoogleSignInApi.silentSignIn(mGoogleSignInClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            setChanged();
            notifyObservers(GOOGLE_AUTHENTICATION);
            GoogleSignInResult result = opr.get();
            handleSignInResult(result,mGoogleSignInClient);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult,mGoogleSignInClient);
                }
            });
        }
    }
    private class AuthScreen {
        final Context context;
        final EditText usr;
        final EditText pass;

        public AuthScreen(Context ctx, EditText usr, EditText pass) {
            this.context = ctx;
            this.usr = usr;
            this.pass = pass;
        }
    }

    private class Auth extends AsyncTask<AuthScreen, Void, Boolean> {
        Context ctx;
        Boolean result;
        String username;
        @Override
        protected Boolean doInBackground(AuthScreen... auths) {
            ctx = auths[0].context;
            if (!Constants.isNetworkAvailable(ctx)) {
                setChanged();
                notifyObservers(REQUEST_FAILED_CONNECTION);
            } else {
                try {
                SoapObject request = new SoapObject(Constants.NAMESPACE, Constants.AUTH_METHOD_NAME);
                username = auths[0].usr.getText().toString();
                request.addProperty("authUser", username );
                request.addProperty("authContrasenia", auths[0].pass.getText().toString());
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.headerOut = new Element[1];
                envelope.headerOut[0] = buildAuthHeader();
                envelope.setOutputSoapObject(request);
                HttpTransportSE transport = new HttpTransportSE(Constants.URL);
                transport.call(Constants.AUTH_SOAP_ACTION, envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = Boolean.valueOf(response.toString());
                return result;
                } catch (Exception e) {
                    setChanged();
                    notifyObservers(REQUEST_FAILED_HTTP);
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                new UserInfoSoapHelper().execute(username);
            } else {
                setChanged();
                notifyObservers(AUTH_WRONG_CREDENTIALS);
            }
        }

        private Element buildAuthHeader() {
            Element h = new Element().createElement(NAMESPACE, Constants.SOAP_HEADER);
            Element user = new Element().createElement(NAMESPACE, "usuario");
            user.addChild(Node.TEXT, Constants.USER_SOAP_HEADER);
            h.addChild(Node.ELEMENT, user);
            Element key_ = new Element().createElement(NAMESPACE, "key");
            key_.addChild(Node.TEXT, Constants.KEY_SOAP_HEADER);
            h.addChild(Node.ELEMENT, key_);
            return h;
        }
    }

    public void checkSessions(){
        if(SessionHelper.isEspolLoggedIn(activity.getApplicationContext())){
            setChanged();
            notifyObservers(IS_ESPOL_LOGGED_IN);
        }
        else{
            handleFbSession();
            handleGoogleSession(activity.getGoogleApiClient());
        }
    }

    public void makeEgLoginRequest(String username){
        setChanged();
        notifyObservers(EG_LOGIN_REQUEST_STARTED);
        new EgLogin().execute(username);
    }

    private class EgLogin extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String username = strings[0];
            if (!Constants.isNetworkAvailable(activity)) {
                setChanged();
                notifyObservers(REQUEST_FAILED_CONNECTION);
            } else {
                JSONObject jsonBody = new JSONObject();
                JSONObject data = new JSONObject();
                try{
                    data.put(Constants.USERNAME_KEY, username);
                    jsonBody.put(Constants.DATA_KEY, data);
                }
                catch (Exception ignored){ ;
                }
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        EG_LOGIN_WS, jsonBody, response -> {
                            String accessToken;
                            try {
                                if(response.has(Constants.ACCESS_TOKEN_KEY)){
                                    accessToken = response.getString(Constants.ACCESS_TOKEN_KEY);
                                    System.out.println(accessToken);
                                    SessionHelper.saveAccessToken(activity, accessToken);
                                    setChanged();
                                    notifyObservers(EG_LOGIN_REQUEST_SUCCEED);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                            VolleyLog.d("tag", "Error: " + error.getMessage());
                            setChanged();
                            notifyObservers(REQUEST_FAILED_HTTP);
                        });
                AppController.getInstance(activity).addToRequestQueue(jsonObjReq);
            }
            return null;
        }
    }

    public void makeGetFavoritesRequest(){
        setChanged();
        notifyObservers(GET_FAVORITES_REQUEST_STARTED);
        new FavoritesGetter().execute();
    }

    private class FavoritesGetter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if(!SessionHelper.hasAccessToken(activity)){
                setChanged();
                notifyObservers(GET_FAVORITES_REQUEST_FAILED_LOADING);
            }
            else{
                String accessToken = SessionHelper.getAccessToken(activity);
                if (!Constants.isNetworkAvailable(activity)) {
                    setChanged();
                    notifyObservers(REQUEST_FAILED_CONNECTION);
                }
                else {
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                            FAVORITES_WS, null, response -> {
                                try{
                                    JSONArray jsonArray = response.getJSONArray(Constants.CODES_GTSI_KEY);
                                    ArrayList<String> favoritesList = new ArrayList<>();
                                    if (jsonArray != null) {
                                        int len = jsonArray.length();
                                        for (int i=0;i<len;i++){
                                            favoritesList.add(jsonArray.get(i).toString());
                                        }
                                    }
                                    Set<String> favoritesSet = new HashSet<>();
                                    favoritesSet.addAll(favoritesList);
                                    SessionHelper.saveFavoritePois(activity, favoritesSet);
                                    setChanged();
                                    notifyObservers(GET_FAVORITES_REQUEST_SUCCEEDED);
                                }
                                catch (Exception e){
                                    setChanged();
                                    notifyObservers(GET_FAVORITES_REQUEST_FAILED_LOADING);
                                }
                            }, error -> {
                                System.out.println("======================== ERROR EN RESPONSE ========================");

                                VolleyLog.d("tag", "Error: " + error.getMessage());
                                setChanged();
                                notifyObservers(REQUEST_FAILED_HTTP);
                            }){
                        /**
                         * Passing some request headers
                         */
                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put(Constants.ACCESS_TOKEN_HEADER_KEY, accessToken);
                            return headers;
                        }

                    };
                    AppController.getInstance(activity).addToRequestQueue(jsonObjReq);
                }
            }
            return null;
        }
    }

    private class UserInfoSoapHelper extends AsyncTask<String, Void, Void> {
        Context ctx;
        String studentNumber;
        @Override
        protected Void doInBackground(String... users) {
            ctx = activity.getApplicationContext();
            if (!Constants.isNetworkAvailable(ctx)) {
                setChanged();
                notifyObservers(REQUEST_FAILED_CONNECTION);
            } else {
                setChanged();
                notifyObservers(NAME_REQUEST_STARTED);
                try {
                    SoapObject request = new SoapObject(Constants.NAMESPACE, Constants.USR_INFO_METHOD_NAME);
                    request.addProperty("user", users[0]);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE transport = new HttpTransportSE(Constants.URL);
                    transport.call(Constants.USR_INFO_SOAP_ACTION, envelope);
                    SoapObject response = (SoapObject) envelope.getResponse();
                    SoapObject userInfo = (SoapObject) ((SoapObject) ((SoapObject) response.getProperty("diffgram")).getProperty("NewDataSet")).getProperty("ESTUDIANTE");
                    String name = userInfo.getPropertyAsString("NOMBRES").split(" ")[0].toLowerCase();
                    studentNumber = userInfo.getPropertyAsString("MATRICULA");
                    name = WordUtils.capitalize(name);
                    SessionHelper.saveEspolName(ctx, name);
                    SessionHelper.saveEspolUserIdNumber(ctx, studentNumber);
                } catch (Exception e) {
                    setChanged();
                    notifyObservers(REQUEST_FAILED_HTTP);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setChanged();
            notifyObservers(NAME_REQUEST_SUCCEED);
            new PhotoSoapHelper().execute(studentNumber);
        }
    }

    public class PhotoSoapHelper extends AsyncTask<String, Void,String>{

        private Context ctx;

        @Override
        protected String doInBackground(String... data){
            ctx = activity.getApplicationContext();
            if (!Constants.isNetworkAvailable(ctx)) {
                setChanged();
                notifyObservers(REQUEST_FAILED_CONNECTION);
            } else {
                setChanged();
                notifyObservers(PHOTO_REQUEST_STARTED);

                try {
                    SoapObject request = new SoapObject(Constants.MEDIA_NAMESPACE, Constants.USR_PHOTO_METHOD_NAME);
                    request.addProperty("name", data[0]);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE transport = new HttpTransportSE(Constants.MEDIA_URL);
                    transport.call(Constants.USR_PHOTO_SOAP_ACTION, envelope);
                    SoapPrimitive result = (SoapPrimitive) (((Vector) envelope.getResponse()).get(0));
                    return result.toString();
                } catch (Exception e) {
                    setChanged();
                    notifyObservers(PHOTO_REQUEST_SUCCEED);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            SessionHelper.saveEspolUserPhoto(ctx,s);
            setChanged();
            notifyObservers(AUTH_REQUEST_SUCCEED);
        }
    }
}
