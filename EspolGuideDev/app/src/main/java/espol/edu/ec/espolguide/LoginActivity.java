package espol.edu.ec.espolguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;

import java.util.Observable;
import java.util.Observer;

import espol.edu.ec.espolguide.viewModels.LoginViewModel;

/**
 * Created by fabricio on 19/05/18.
 */

public class LoginActivity extends AppCompatActivity implements Observer {
    ViewHolder viewHolder;
    LoginViewModel viewModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.start);
        this.viewHolder = new ViewHolder();
        this.viewModel = new LoginViewModel(this);
        this.viewModel.addObserver(this);
    }

    public class ViewHolder{
        public EditText username;
        public EditText password;
        public Button authBtn;

        public ViewHolder(){
            findViews();
            setAuthButtonListener();
        }

        private void findViews(){
            username = (EditText) findViewById(R.id.username);
            password = (EditText) findViewById(R.id.password);
            authBtn = (Button) findViewById(R.id.buttonAuth);
        }

        private void setAuthButtonListener(){
            this.authBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewModel.auth();
                }
            });
        }
    }

    public ViewHolder getViewHolder(){
        return this.viewHolder;
    }

    @Override
    public void update(Observable o, Object arg) {
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
