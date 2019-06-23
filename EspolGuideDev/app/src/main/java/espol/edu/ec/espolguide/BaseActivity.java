package espol.edu.ec.espolguide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import de.hdodenhof.circleimageview.CircleImageView;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.SessionHelper;
import espol.edu.ec.espolguide.utils.Util;
import espol.edu.ec.espolguide.viewModels.BaseViewModel;

/**
 * Created by galo on 15/06/18.
 */

public class BaseActivity extends AppCompatActivity implements Observer {
    private BaseViewModel viewModel;
    private ViewHolder viewHolder;
    private static GoogleApiClient client;

    public class ViewHolder {
        public NavigationView navigationView;
        public TextView greetingTxt;
        public CircleImageView imgView;

        public ViewHolder(){
            findViews();
            setUserInfo();
        }

        private void findViews(){
            navigationView = findViewById(R.id.navigation_view);
            greetingTxt = navigationView.getHeaderView(0).findViewById(R.id.greeting_tv);
            imgView = navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        }

        private void setUserInfo(){
            String startGreeting = getResources().getString(R.string.greeting) + ", ";
            String name  = "";
            String endGreeting = "!";
            if(SessionHelper.isEspolLoggedIn(BaseActivity.this.getApplicationContext())){
                name = SessionHelper.getEspolName(BaseActivity.this).trim();
                String espolPhoto = SessionHelper.getEspolPhoto(BaseActivity.this);
                byte[] imageAsBytes = Base64.decode(espolPhoto.getBytes(), Base64.DEFAULT);
                Bitmap imgBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                imgView.setImageBitmap(imgBitmap);
            }else{
                String photo = "";
                if(SessionHelper.isFacebookLoggedIn(getApplicationContext())){
                    name = SessionHelper.getFbName(BaseActivity.this).split(" ")[0].trim();
                    photo = SessionHelper.getFbPhoto(BaseActivity.this);
                }
                else {
                    name = SessionHelper.getGoogleName(getApplicationContext());
                    photo = SessionHelper.getGooglePhoto(BaseActivity.this);
                }
                try {
                    Picasso.with(getApplicationContext())
                            .load(photo)
                            .into(imgView);
                }catch(Exception e){
                    imgView.setImageResource(R.drawable.profileblank);
                }
            }

            String greeting = startGreeting + name + endGreeting;
            Spannable sb = new SpannableString(greeting);
            final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
            sb.setSpan(bss, startGreeting.length(), startGreeting.length() + name.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            greetingTxt.setText(sb);

        }

    }

    public static GoogleApiClient getClient() {
        return client;
    }

    public static void setClient(GoogleApiClient client) {
        BaseActivity.client = client;
    }

    public void GoogleLogout(GoogleApiClient mGoogleSignInClient){
        if (mGoogleSignInClient != null) {
            if (mGoogleSignInClient.isConnected()) {
                com.google.android.gms.auth.api.Auth.GoogleSignInApi.signOut(mGoogleSignInClient);
                mGoogleSignInClient.disconnect();
                setClient(null);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.viewHolder = new ViewHolder();
        this.viewModel = new BaseViewModel(this);
        this.viewModel.addObserver(this);
        this.viewModel.verifyMenuItems();
        //this.handleSelectedOptionUI();

        viewHolder.navigationView.setNavigationItemSelectedListener(item -> {
            if(item.isChecked()){
                Util.closeDrawer(BaseActivity.this);
                return true;
            }
            switch (item.getItemId()) {
                case R.id.map_op:
                    showMapLayoutView();
                    break;

                case R.id.courses_op:
                    Intent subjIntent = new Intent(getApplicationContext(), SubjectsActivity.class);
                    subjIntent.putExtra(Constants.SELECTED_OPTION, R.id.courses_op);
                    startActivityForResult(subjIntent, Constants.SUBJECTS_REQUEST_CODE);
                    break;

                case R.id.events_op:
                    Intent eventsIntent = new Intent(getApplicationContext(), EventsActivity.class);
                    eventsIntent.putExtra(Constants.SELECTED_OPTION, R.id.events_op);
                    startActivityForResult(eventsIntent, Constants.EVENTS_REQUEST_CODE);
                    break;

                case R.id.favorites_op:
                    Intent favIntent = new Intent(getApplicationContext(), FavoritesActivity.class);
                    favIntent.putExtra(Constants.SELECTED_OPTION, R.id.favorites_op);
                    startActivityForResult(favIntent, Constants.FAVORITES_REQUEST_CODE);
                    break;

                case R.id.link_op:
                    Intent linkIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    linkIntent.putExtra(Constants.TO_LINK_ACCOUNT, Constants.TO_LINK_ACCOUNT);
                    linkIntent.putExtra(Constants.SELECTED_OPTION, R.id.link_op);
                    startActivity(linkIntent);
                    break;

                case R.id.logout_op:
                    if (getClient()!=null){
                        GoogleLogout(getClient());
                        SessionHelper.clear(getApplicationContext());
                    }
                    SessionHelper.logout(getApplicationContext());
                    SessionHelper.fbLogout();
                    Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(logoutIntent);
                    finish();
                    break;
            }
            return false;
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        String message = (String)arg;
    }

    public ViewHolder getBaseViewHolder() {
        return this.viewHolder;
    }

    public void setBaseViewHolder(ViewHolder viewHolder){
        this.viewHolder = viewHolder;
    }

    public BaseViewModel getBaseViewModel() {
        return this.viewModel;
    }

    public void setBaseViewModel(BaseViewModel viewModel){
        this.viewModel = viewModel;
    }

    public void hideEspolUsersMenu() {
        Menu navMenu = this.getBaseViewHolder().navigationView.getMenu();
        navMenu.findItem(R.id.courses_op).setVisible(false);
        navMenu.findItem(R.id.favorites_op).setVisible(false);
    }

    public void hideExternalUsersMenu() {
        Menu navMenu = this.getBaseViewHolder().navigationView.getMenu();
        navMenu.findItem(R.id.link_op).setVisible(false);
    }

    public void handleSelectedOptionUI(){
        Bundle bundle = getIntent().getExtras();
        if(Objects.requireNonNull(bundle).containsKey(Constants.SELECTED_OPTION)){
            unCheckAllItems(viewHolder.navigationView.getMenu());
            int id = -1;
            try{
                id = bundle.getInt(Constants.SELECTED_OPTION);
                viewHolder.navigationView.getMenu().findItem(id).setChecked(true);
            }catch (Exception e){
                e.getStackTrace();
            }
        }
        else{
            viewHolder.navigationView.getMenu().findItem(R.id.map_op);
        }
    }

    public void unCheckAllItems(Menu navMenu){
        for(int i = 0; i<navMenu.size(); i++){
            navMenu.getItem(i).setChecked(false);
        }
    }

    public void showMapLayoutView(){

    }
}