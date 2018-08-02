package espol.edu.ec.espolguide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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

    public class ViewHolder {
        public NavigationView navigationView;
        public TextView greetingTxt;
        public CircleImageView imgView;

        public ViewHolder(){
            findViews();
            setUserInfo();
        }

        private void findViews(){
            navigationView = (NavigationView) findViewById(R.id.navigation_view);
            greetingTxt =(TextView) navigationView.getHeaderView(0).findViewById(R.id.greeting_tv);
            imgView = navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        }

        private void setUserInfo(){
            if(SessionHelper.isEspolLoggedIn(BaseActivity.this.getApplicationContext())){
                String espolName = SessionHelper.getEspolName(BaseActivity.this).trim();
                String espolPhoto = SessionHelper.getEspolPhoto(BaseActivity.this);
                String startGreeting = getResources().getString(R.string.greeting) + ", ";
                String endGreeting = "!";
                String greeting = startGreeting + espolName + endGreeting;
                Spannable sb = new SpannableString(greeting);

                final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
                sb.setSpan(bss, startGreeting.length(), startGreeting.length() + espolName.length(),
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                greetingTxt.setText(sb);

                byte[] imageAsBytes = Base64.decode(espolPhoto.getBytes(), Base64.DEFAULT);
                Bitmap imgBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                imgView.setImageBitmap(imgBitmap);
            }
            else{
                String greeting = getResources().getString(R.string.greeting) + "!";
                greetingTxt.setText(greeting);
                imgView.setImageResource(R.drawable.nophoto);
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
        this.handleSelectedOptionUI();

        viewHolder.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if(item.isChecked()){
                    Util.closeDrawer(BaseActivity.this);
                    return true;
                }
                switch (item.getItemId()) {
                    case R.id.map_op:
                        Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
                        mapIntent.putExtra(Constants.SELECTED_OPTION, R.id.map_op);
                        startActivity(mapIntent);
                        finish();
                        break;

                    case R.id.events_op:
                        break;

                    case R.id.courses_op:
                        Intent subjIntent = new Intent(getApplicationContext(), SubjectsActivity.class);
                        subjIntent.putExtra(Constants.SELECTED_OPTION, R.id.courses_op);
                        startActivityForResult(subjIntent, Constants.SUBJECTS_REQUEST_CODE);
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
                        SessionHelper.logout(getApplicationContext());
                        Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(logoutIntent);
                        finish();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void update(Observable o, Object arg) {
        String message = (String)arg;
        if (message == viewModel.EXTERNAL_USER_AUTHENTICATED) {

        }
        if (message == viewModel.ESPOL_USER_AUTHENTICATED) {

        }
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
        if(bundle.containsKey(Constants.SELECTED_OPTION)){
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
}