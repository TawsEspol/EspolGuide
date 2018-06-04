package espol.edu.ec.espolguide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.mapbox.mapboxsdk.Mapbox;

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


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        FacebookSdk.sdkInitialize(getApplicationContext());
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
        private Activity ctx;

        public ViewHolder(Activity ctx) {
            this.ctx = ctx;
            findViews();
            setAuthButtonListener();
            setFbLogin();
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
        }

        private void setAuthButtonListener() {
            this.authBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewModel.auth();
                }
            });
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public ViewHolder getViewHolder() {
        return this.viewHolder;
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}
