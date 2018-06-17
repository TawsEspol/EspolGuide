package espol.edu.ec.espolguide.viewModels;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.EditText;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.util.Observable;

import espol.edu.ec.espolguide.LoginActivity;
import espol.edu.ec.espolguide.MapActivity;
import espol.edu.ec.espolguide.utils.Constants;

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
    public static String AUTH_REQUEST_STARTED = "auth_request_started";
    public static String AUTH_REQUEST_SUCCEED = "auth_request_succeed";
    public static String AUTH_REQUEST_FAILED_CONNECTION = "auth_request_failed_connection";
    public static String AUTH_REQUEST_FAILED_HTTP = "auth_request_failed_http";
    public static String AUTH_WRONG_CREDENTIALS = "auth_wrong_credentials";
    public static String NAMESPACE = "http://tempuri.org/";
    public static String GOOGL_AUTH_REQUEST_STARTED = "google_auth_request_started";
    public static String GOOGL_AUTH_REQUEST_SUCCEED = "google_auth_request_succeed";
    public static String GOOGL_AUTH_REQUEST_FAILED_CONNECTION = "google_auth_request_failed_connection";
    // the following method willbe used for the backend server authentication
    public static String GOOGL_AUTH_REQUEST_FAILED_HTTP = "google_auth_request_failed_http";
    public static String GOOGL_AUTH_WRONG_CREDENTIALS = "google_auth_wrong_credentials";
    public static String FB_AUTHENTICATION = "facebook_authentication";
    public static String GOOGL_AUTHENTICATION = "google_authentication";


    private LoginActivity activity;

    public LoginViewModel(LoginActivity activity) {
        this.activity = activity;
    }

    public void auth() {
        setChanged();
        notifyObservers(AUTH_REQUEST_STARTED);
        new Auth().execute(new AuthScreen(activity, activity.getViewHolder().username,
                activity.getViewHolder().password));
    }

    public void google_auth(GoogleApiClient mGoogleSignInClient) {
        setChanged();
        notifyObservers(GOOGL_AUTH_REQUEST_STARTED);
        signIn(mGoogleSignInClient);
    }

    private void signIn(GoogleApiClient mGoogleSignInClient) {
        if (!Constants.isNetworkAvailable(activity.getApplicationContext())) {
            setChanged();
            notifyObservers(GOOGL_AUTH_REQUEST_FAILED_CONNECTION);
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

    public void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            setChanged();
            notifyObservers(GOOGL_AUTH_REQUEST_SUCCEED);
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //con este account puedes manejar la data.
            //System.out.println(acct.getEmail());
            //System.out.println(acct.getIdToken());
            updateUI();
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
            notifyObservers(GOOGL_AUTHENTICATION);
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    private class AuthScreen {
        Context context;
        EditText usr;
        EditText pass;

        public AuthScreen(Context ctx, EditText usr, EditText pass) {
            this.context = ctx;
            this.usr = usr;
            this.pass = pass;
        }
    }

    private class Auth extends AsyncTask<AuthScreen, Void, Boolean> {
        Context ctx;
        Boolean result;

        @Override
        protected Boolean doInBackground(AuthScreen... auths) {
            ctx = auths[0].context;
            if (!Constants.isNetworkAvailable(ctx)) {
                setChanged();
                notifyObservers(AUTH_REQUEST_FAILED_CONNECTION);
            } else {
                try {
                SoapObject request = new SoapObject(Constants.NAMESPACE, Constants.AUTH_METHOD_NAME);
                request.addProperty("authUser", auths[0].usr.getText().toString());
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
                    notifyObservers(AUTH_REQUEST_FAILED_HTTP);
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                setChanged();
                notifyObservers(AUTH_REQUEST_SUCCEED);
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
}
