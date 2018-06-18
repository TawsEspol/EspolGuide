package espol.edu.ec.espolguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class BaseActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    public static Activity activity;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.navigationView = (NavigationView) findViewById(R.id.navigation_view);
        activity = this;

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
  //      drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    //    actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
      //          R.string.drawer_open, R.string.drawer_closed);
      //  drawerLayout.setDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.map_op:
                        Intent anIntent = new Intent(getApplicationContext(), MapActivity.class);
                        startActivity(anIntent);
//                        drawerLayout.closeDrawers();
                        break;

                    case R.id.events_op:
                        Intent otherIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(otherIntent);
  //                      drawerLayout.closeDrawers();
                        break;

                    case R.id.courses_op:
//                        drawerLayout.closeDrawers();
                        break;

                    case R.id.favorites_op:
//                        drawerLayout.closeDrawers();
                        break;

                    case R.id.link_op:
                        //                      drawerLayout.closeDrawers();
                        break;

                    case R.id.logout_op:
                        //                      drawerLayout.closeDrawers();

                        View item2 = (View) findViewById(R.id.link_op);
                        item2.setVisibility(View.INVISIBLE);
                        break;
                }
                return false;
            }
        });

    }

    public void setNavigationView(NavigationView navigationView){
        this.navigationView = navigationView;
    }

    public NavigationView getNavigationView() {
        return this.navigationView;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //actionBarDrawerToggle.syncState();
    }

}