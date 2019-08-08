package espol.edu.ec.espolguide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class EventInfoActivity extends AppCompatActivity {
    private ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        this.viewHolder = new ViewHolder();
    }

    public class ViewHolder{
        public Toolbar eventInfoToolbar;

        public ViewHolder(){
            findViews();
            setActivityTitle();
            this.eventInfoToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
            setSupportActionBar(this.eventInfoToolbar);
        }

        public void findViews(){
            eventInfoToolbar = findViewById(R.id.event_info_toolbar);
        }

        public void setActivityTitle(){
            String activityName = getApplicationContext().getString(R.string.events_menu_op);
            eventInfoToolbar.setTitle(activityName);
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
