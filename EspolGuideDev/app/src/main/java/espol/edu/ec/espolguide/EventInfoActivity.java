package espol.edu.ec.espolguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.viewModels.EventInfoViewModel;

public class EventInfoActivity extends AppCompatActivity implements Observer {
    private ViewHolder viewHolder;
    private EventInfoViewModel viewModel;
    private String eventId;
    private String eventZoneArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        this.viewHolder = new ViewHolder();
        Bundle bundle = getIntent().getExtras();
        if(bundle!= null && Objects.requireNonNull(bundle).containsKey("event_id")){
            this.eventId = bundle.getString("event_id");
        }
        this.viewModel = new EventInfoViewModel(this, this.eventId);
        this.viewModel.makeGetEventInfoRequest();
        this.eventZoneArea = this.viewModel.getEventZoneArea();
        setClickListeners();
    }

    @Override
    public void update(Observable observable, Object o) {

    }

    public String getEventId(){
        return this.eventId;
    }

    public void setEventId(String eventId){
        this.eventId = eventId;
    }

    public class ViewHolder{
        public Toolbar eventInfoToolbar;
        public TextView eventNameTv;
        public TextView eventDescriptionTv;
        public TextView eventTimeTv;
        public TextView eventPlaceTv;
        public TextView eventMoreInfoTv;
        public Button locateBuildingBtn;
        public Button remindBtn;

        public ViewHolder(){
            findViews();
            setActivityTitle();
            this.eventInfoToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
            setSupportActionBar(this.eventInfoToolbar);
        }

        public void findViews(){
            eventInfoToolbar = findViewById(R.id.event_info_toolbar);
            eventNameTv = findViewById(R.id.event_name_tv);
            eventDescriptionTv = findViewById(R.id.event_description_tv);
            eventTimeTv = findViewById(R.id.event_time_tv);
            eventPlaceTv = findViewById(R.id.event_place_tv);
            eventMoreInfoTv = findViewById(R.id.event_info_tv);
            locateBuildingBtn = findViewById(R.id.place_btn);
            remindBtn = findViewById(R.id.reminder_btn);
        }

        public void setActivityTitle(){
            String activityName = getApplicationContext().getString(R.string.events_menu_op);
            eventInfoToolbar.setTitle(activityName);
        }
    }

    public ViewHolder getViewHolder(){
        return this.viewHolder;
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

    public String getEventZoneArea(){
        return this.eventZoneArea;
    }

    public void setClickListeners(){
        viewHolder.locateBuildingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getEventZoneArea() != null && getEventZoneArea().trim().length() > 0){
                    Intent eventIntent = new Intent(getApplicationContext(), EventsActivity.class);
                    eventIntent.putExtra(Constants.SELECTED_OPTION, R.id.events_op);
                    eventIntent.putExtra(Constants.SELECTED_GTSI_CODE, getEventZoneArea().trim());
                    setResult(RESULT_OK, eventIntent);
                    finish();
                }
            }
        });
    }
}
