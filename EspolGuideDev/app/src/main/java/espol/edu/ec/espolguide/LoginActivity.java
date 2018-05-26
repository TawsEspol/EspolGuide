package espol.edu.ec.espolguide;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.mapbox.mapboxsdk.Mapbox;

import java.util.Observable;
import java.util.Observer;

import espol.edu.ec.espolguide.viewModels.LoginViewModel;
import espol.edu.ec.espolguide.viewModels.MapViewModel;

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
    public void update(Observable observable, Object o) {

    }
}
