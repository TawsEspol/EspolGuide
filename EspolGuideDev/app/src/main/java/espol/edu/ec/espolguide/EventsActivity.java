package espol.edu.ec.espolguide;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.Observer;

import espol.edu.ec.espolguide.utils.Util;
import espol.edu.ec.espolguide.viewModels.EventsViewModel;
import espol.edu.ec.espolguide.viewModels.FavoritesViewModel;

/**
 * Activity for loading a list of Events.
 *
 * This activity is used to display the list of Events and their information.
 * It uses the Observator software design pattern.
 *
 * @author Galo Castillo
 */

public class EventsActivity extends BaseActivity implements Observer {
    private EventsViewModel viewModel;
    private ViewHolder viewHolder;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_events, contentFrameLayout);
        this.viewHolder = new ViewHolder();
        this.viewModel = new EventsViewModel(this);
        this.viewModel.addObserver(this);
        //this.viewModel.loadFavorites();
        Util.lockSwipeGesture(this);
    }

    public class ViewHolder{
        public ListView eventsLv;
        public Toolbar eventsToolbar;

        public ViewHolder(){
            findViews();
            setActivityTitle();
            this.eventsToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
            setSupportActionBar(this.eventsToolbar);
        }

        public void findViews(){
            eventsLv = findViewById(R.id.events_lv);
            eventsToolbar = findViewById(R.id.events_toolbar);
        }

        public void setActivityTitle(){
            String activityName = getApplicationContext().getString(R.string.events_menu_op);
            eventsToolbar.setTitle(activityName);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
