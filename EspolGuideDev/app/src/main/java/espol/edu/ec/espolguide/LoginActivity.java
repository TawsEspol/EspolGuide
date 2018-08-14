package espol.edu.ec.espolguide;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import com.mapbox.mapboxsdk.Mapbox;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.SessionHelper;
import espol.edu.ec.espolguide.utils.Util;
import espol.edu.ec.espolguide.viewModels.LoginViewModel;

/**
 * Created by fabricio on 19/05/18.
 */

public class LoginActivity extends BaseActivity implements Observer {
    ViewHolder viewHolder;
    LoginViewModel viewModel;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleSignInClient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.access_token));

        FacebookSdk.sdkInitialize(getApplicationContext());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.start, contentFrameLayout);

        this.viewHolder = new ViewHolder(this);
        this.viewModel = new LoginViewModel(this);
        this.viewModel.addObserver(this);
        this.viewModel.checkSessions();
    }

    public class ViewHolder {
        public EditText username;
        public EditText password;
        public Button authBtn;
        public LoginButton fbAuthBtn;
        public SignInButton googlAuthBtn;
        public final Activity activity;
        private Activity ctx;
        NavigationView navigationView;

        public ViewHolder(Activity activity) {
            this.activity = activity;
            findViews();
            setAuthButtonListener();
            setFbLogin();
            setGoogleAuthButtonListener();
            checkToLinkStatus();
        }

        public void checkToLinkStatus(){
            Bundle bundle = getIntent().getExtras();
            if(Objects.requireNonNull(bundle).containsKey(Constants.TO_LINK_ACCOUNT)){
                Util.allowSwipeGesture(LoginActivity.this);
                this.fbAuthBtn.setVisibility(View.GONE);
                this.googlAuthBtn.setVisibility(View.GONE);
            }
            else{
                Util.lockSwipeGesture(LoginActivity.this);
            }
        }

        private void setFbLogin() {
            callbackManager = CallbackManager.Factory.create();

            fbAuthBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    setFacebookData(loginResult);
                    Intent intent;
                    intent = new Intent(activity, MapActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException e) {

                }

                private void setFacebookData(final LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        String firstName = response.getJSONObject().getString("name");
                                        String userID = response.getJSONObject().getString("id");
                                        String uri_photo = "https://graph.facebook.com/" + userID + "/picture?type=large";
                                        SessionHelper.saveFbName(getApplicationContext(), firstName);
                                        SessionHelper.saveFbPhoto(getApplicationContext(), uri_photo);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            GraphResponse gResponse = request.executeAndWait();
                        }
                    });
                    t.start();
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void findViews() {
            username = findViewById(R.id.username);
            password = findViewById(R.id.password);
            authBtn = findViewById(R.id.buttonAuth);
            fbAuthBtn = findViewById(R.id.fbAuthBtn);
            googlAuthBtn = findViewById(R.id.googlAuthbutton);
            navigationView = findViewById(R.id.navigation_view);
        }

        private void setAuthButtonListener() {
            this.authBtn.setOnClickListener(view -> viewModel.auth());
        }

        private void setGoogleAuthButtonListener(){
            this.googlAuthBtn.setOnClickListener(view -> viewModel.googleAuth(mGoogleSignInClient));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        //callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            viewModel.handleSignInResult(result,this.getGoogleApiClient());
        }
    }

    public ViewHolder getViewHolder() {
        return this.viewHolder;
    }

    public GoogleApiClient getGoogleApiClient(){
        return this.mGoogleSignInClient;
    }

    @Override
    public void update(Observable observable, Object arg) {
        String message = (String)arg;
        if (message.equals(LoginViewModel.AUTH_REQUEST_STARTED)) {
            System.out.println("STARTED");

        }
        else if (message.equals(LoginViewModel.AUTH_REQUEST_SUCCEED)) {
            Intent intent = new Intent(this, MapActivity.class);
            this.startActivity(intent);
            String espolUsername = this.getViewHolder().username.getText().toString().trim();
            SessionHelper.saveEspolSession(getApplicationContext(), espolUsername);
            viewModel.makeEgLoginRequest(espolUsername);
            this.viewHolder.username.setText("");
            this.viewHolder.password.setText("");
            this.finish();
        }

        else if (message.equals(LoginViewModel.GOOGL_AUTH_REQUEST_SUCCEED)) {
            Intent intent = new Intent(this, MapActivity.class);
            this.startActivity(intent);
            this.finish();
        }

        else if (message.equals(LoginViewModel.REQUEST_FAILED_CONNECTION)) {
            LoginActivity.this.runOnUiThread(() -> Toast.makeText(LoginActivity.this, getResources().getString(R.string.failed_connection_msg),
                    Toast.LENGTH_LONG).show());
        }
        else if (message.equals(LoginViewModel.GOOGL_AUTH_WRONG_CREDENTIALS)) {
            this.viewHolder.username.setText("");
            this.viewHolder.password.setText("");
            LoginActivity.this.runOnUiThread(() -> Toast.makeText(LoginActivity.this, getResources().getString(R.string.google_wrong_credentials_msg),
                    Toast.LENGTH_LONG).show());
        }
        else if (message.equals(LoginViewModel.AUTH_WRONG_CREDENTIALS)) {
            this.viewHolder.password.setText("");
            LoginActivity.this.runOnUiThread(() -> Toast.makeText(LoginActivity.this, getResources().getString(R.string.wrong_credentials_msg),
                    Toast.LENGTH_LONG).show());
        }
        else if (message.equals(LoginViewModel.REQUEST_FAILED_HTTP)) {
            LoginActivity.this.runOnUiThread(() -> Toast.makeText(LoginActivity.this, getResources().getString(R.string.http_error_msg),
                    Toast.LENGTH_SHORT).show());
        }
        else if (message.equals(LoginViewModel.FB_AUTHENTICATION)) {
            LoginActivity.this.runOnUiThread(() -> Toast.makeText(LoginActivity.this, getResources().getString(R.string.fb_auth),
                    Toast.LENGTH_SHORT).show());
        }
        else if (message.equals(LoginViewModel.GOOGLE_AUTHENTICATION)) {
            LoginActivity.this.runOnUiThread(() -> Toast.makeText(LoginActivity.this, getResources().getString(R.string.google_auth),
                    Toast.LENGTH_SHORT).show());
        }
        else if (message.equals(LoginViewModel.IS_ESPOL_LOGGED_IN)) {
            Bundle bundle = getIntent().getExtras();
            if(!Objects.requireNonNull(bundle).containsKey(Constants.TO_LINK_ACCOUNT)){
                Intent intent = new Intent(this, MapActivity.class);
                this.startActivity(intent);
                this.finish();
            }
        }
        else if (message.equals(LoginViewModel.EG_LOGIN_REQUEST_SUCCEED)) {
            viewModel.makeGetFavoritesRequest();
        }
        else if (message.equals(LoginViewModel.GET_FAVORITES_REQUEST_FAILED_LOADING)) {
            LoginActivity.this.runOnUiThread(() -> Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_on_loading_favorites),
                    Toast.LENGTH_SHORT).show());
        }
    }
}
