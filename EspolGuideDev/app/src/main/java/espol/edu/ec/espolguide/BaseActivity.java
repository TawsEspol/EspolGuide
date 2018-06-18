package espol.edu.ec.espolguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

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

        public ViewHolder(){
            findViews();
            setGreeting();
        }

        private void findViews(){
            navigationView = (NavigationView) findViewById(R.id.navigation_view);
            greetingTxt =(TextView) navigationView.getHeaderView(0).findViewById(R.id.greeting_tv);
        }

        private void setGreeting(){
            if(SessionHelper.isEspolLoggedIn(BaseActivity.this.getApplicationContext())){
                String espolUsername = SessionHelper.getEspolUsername(BaseActivity.this).trim();
                String startGreeting = getResources().getString(R.string.greeting) + ", ";
                String endGreeting = "!";
                String greeting = startGreeting + espolUsername + endGreeting;
                Spannable sb = new SpannableString(greeting);

                final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
                sb.setSpan(bss, startGreeting.length(), startGreeting.length() + espolUsername.length(),
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                greetingTxt.setText(sb);
            }
            else{
                String greeting = getResources().getString(R.string.greeting) + "!";
                greetingTxt.setText(greeting);
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
                Menu navMenu = getBaseViewHolder().navigationView.getMenu();
                Util.closeDrawer(BaseActivity.this);
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
                        break;

                    case R.id.favorites_op:
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
            restartAllItems(viewHolder.navigationView.getMenu());
            int id = -1;
            try{
                id = bundle.getInt(Constants.SELECTED_OPTION);
                viewHolder.navigationView.getMenu().findItem(id).setChecked(true);
                viewHolder.navigationView.getMenu().findItem(id).setEnabled(false);
            }catch (Exception e){
                e.getStackTrace();
            }
        }
        else{
            viewHolder.navigationView.getMenu().findItem(R.id.map_op);
        }
    }

    public void restartAllItems(Menu navMenu){
        unCheckAllItems(navMenu);
        enableAllItems(navMenu);
    }

    public void unCheckAllItems(Menu navMenu){
        for(int i = 0; i<navMenu.size(); i++){
            navMenu.getItem(i).setChecked(false);
        }
    }

    public void enableAllItems(Menu navMenu){
        for(int i = 0; i<navMenu.size(); i++){
            navMenu.getItem(i).setEnabled(true);
        }
    }
}