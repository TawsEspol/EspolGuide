package espol.edu.ec.espolguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import com.mapbox.mapboxsdk.Mapbox;

import java.util.Observable;
import java.util.Observer;

import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.SessionHelper;
import espol.edu.ec.espolguide.utils.Constants;
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

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
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
        public Activity activity;

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
            if(bundle.containsKey(Constants.TO_LINK_ACCOUNT)){
                this.fbAuthBtn.setVisibility(View.INVISIBLE);
            }
        }

        private void setFbLogin() {

            callbackManager = CallbackManager.Factory.create();

            fbAuthBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
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
            });
        }

        private void findViews() {
            username = (EditText) findViewById(R.id.username);
            password = (EditText) findViewById(R.id.password);
            authBtn = (Button) findViewById(R.id.buttonAuth);
            fbAuthBtn = (LoginButton) findViewById(R.id.fbAuthBtn);
            googlAuthBtn = (SignInButton) findViewById(R.id.googlAuthbutton);
        }

        private void setAuthButtonListener() {
            this.authBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewModel.auth();
                }
            });
        }

        private void setGoogleAuthButtonListener(){
            this.googlAuthBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewModel.googleAuth(mGoogleSignInClient);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            viewModel.handleSignInResult(result);
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
        if (message == viewModel.AUTH_REQUEST_STARTED) {

        }
        else if (message == viewModel.AUTH_REQUEST_SUCCEED) {
            Intent intent = new Intent(this, MapActivity.class);
            this.startActivity(intent);
            this.viewHolder.username.setText("");
            this.viewHolder.password.setText("");
            String espolUsername = this.getViewHolder().username.getText().toString().trim();
            SessionHelper.saveEspolSession(getApplicationContext(), espolUsername);
            this.finish();
        }

        else if (message == viewModel.GOOGL_AUTH_REQUEST_SUCCEED) {
            Intent intent = new Intent(this, MapActivity.class);
            this.startActivity(intent);
            this.finish();
        }

        else if (message == viewModel.AUTH_REQUEST_FAILED_CONNECTION || message == viewModel.GOOGL_AUTH_REQUEST_FAILED_CONNECTION) {
            LoginActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.failed_connection_msg),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        else if (message == viewModel.GOOGL_AUTH_WRONG_CREDENTIALS) {
            this.viewHolder.username.setText("");
            this.viewHolder.password.setText("");
            LoginActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.google_wrong_credentials_msg),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        else if (message == viewModel.AUTH_WRONG_CREDENTIALS) {
            this.viewHolder.username.setText("");
            this.viewHolder.password.setText("");
            LoginActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.wrong_credentials_msg),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        else if (message == viewModel.AUTH_REQUEST_FAILED_HTTP) {
            LoginActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.http_error_msg),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (message == viewModel.FB_AUTHENTICATION) {
            LoginActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.fb_auth),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (message == viewModel.GOOGLE_AUTHENTICATION) {
            LoginActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.google_auth),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (message == viewModel.IS_ESPOL_LOGGED_IN) {
            Bundle bundle = getIntent().getExtras();
            if(!bundle.containsKey(Constants.TO_LINK_ACCOUNT)){
                Intent intent = new Intent(this, MapActivity.class);
                this.startActivity(intent);
                this.finish();
            }
        }
        else if (message == viewModel.IS_NOT_LOGGED_IN) {

        }
    }
}
