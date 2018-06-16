package espol.edu.ec.espolguide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.signin.SignIn;
import com.google.android.gms.tasks.Task;
import com.mapbox.mapboxsdk.Mapbox;

import java.sql.SQLOutput;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.viewModels.LoginViewModel;
import espol.edu.ec.espolguide.viewModels.MapViewModel;

/**
 * Created by fabricio on 19/05/18.
 */

public class LoginActivity extends AppCompatActivity implements Observer {
    ViewHolder viewHolder;
    LoginViewModel viewModel;
    private CallbackManager callbackManager;
    GoogleApiClient mGoogleSignInClient;


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
        setContentView(R.layout.start);

        this.viewHolder = new ViewHolder(this);
        this.viewModel = new LoginViewModel(this);
        this.viewModel.addObserver(this);
    }




    public class ViewHolder {
        public EditText username;
        public EditText password;
        public Button authBtn;
        public LoginButton fbAuthBtn;
        public SignInButton googlAuthBtn;
        private Activity ctx;

        public ViewHolder(Activity ctx) {
            this.ctx = ctx;
            findViews();
            setAuthButtonListener();
            setFbLogin();
            setGoogleAuthButtonListener();
        }

        private void setFbLogin() {

            callbackManager = CallbackManager.Factory.create();
            fbAuthBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Intent intent;
                    intent = new Intent(ctx, MapActivity.class);
                    ctx.startActivity(intent);
                    ctx.finish();
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
                    viewModel.google_auth(mGoogleSignInClient);
                }
            });

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleSignInClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);


        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //con este account puedes manejar la data.
            //System.out.println(acct.getEmail());
            //System.out.println(acct.getIdToken());
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void updateUI(boolean t) {
        if (t) {
            System.out.println("SIRVIO");
            Intent intent;
            intent = new Intent(this, MapActivity.class);
            this.startActivity(intent);
            this.finish();
        } else {
            System.out.println("NO SIRVIO");
        }
    }


    public ViewHolder getViewHolder() {
        return this.viewHolder;
    }

    @Override
    public void update(Observable observable, Object arg) {
        String message = (String)arg;
        if (message == viewModel.AUTH_REQUEST_STARTED) {

        }
        if (message == viewModel.AUTH_REQUEST_SUCCEED) {
            Intent intent = new Intent(this, MapActivity.class);
            this.startActivity(intent);
            this.viewHolder.password.setText("");
            this.finish();
        }
        if (message == viewModel.AUTH_REQUEST_FAILED_CONNECTION) {
            LoginActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.failed_connection_msg),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        if (message == viewModel.AUTH_WRONG_CREDENTIALS) {
            this.viewHolder.username.setText("");
            this.viewHolder.password.setText("");
            LoginActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.wrong_credentials_msg),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        if (message == viewModel.AUTH_REQUEST_FAILED_HTTP) {
            LoginActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.http_error_msg),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
