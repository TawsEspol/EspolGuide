package espol.edu.ec.espolguide;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.SessionHelper;
import espol.edu.ec.espolguide.viewModels.EventInfoViewModel;

public class EventInfoActivity extends AppCompatActivity implements Observer {
    private ViewHolder viewHolder;
    private EventInfoViewModel viewModel;
    private String eventId;
    private String eventZoneArea;
    private AlertDialog reminderDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        this.viewHolder = new ViewHolder();
        Bundle bundle = getIntent().getExtras();
        if(bundle!= null && Objects.requireNonNull(bundle).containsKey("event_id")){
            this.eventId = bundle.getString("event_id");
        }
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = this.getLayoutInflater().inflate(R.layout.dialog_event_reminder, null);
        mBuilder.setView(mView);
        setReminderDialog(mBuilder.create());
        fillTimesSpinner(mView);

        this.viewModel = new EventInfoViewModel(this, this.eventId, mView);
        this.viewModel.makeGetEventInfoRequest();

        String eventName = getViewHolder().eventNameTv.getText().toString();

        System.out.println("============== TEST: " + getViewHolder().eventTimeTv.getText().toString());
//        String eventDate = getViewHolder().eventTimeTv.getText().toString().split("-")[1].trim().replace("h", ":");
  //      String eventTime = getViewHolder().eventTimeTv.getText().toString().split("-")[0].trim();


       // setDialogButtonsActions(mView, eventId, eventDate, eventTime, eventName);
        setClickListeners();
    }

    public void fillTimesSpinner(View v){
        Spinner spinner = (Spinner) v.findViewById(R.id.reminder_times_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.reminders_times_array, R.layout.reminder_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }

    public void setDialogButtonsActions(View v, String eventId, String eventDate, String eventTime,
                                        String eventName){
        Button scheduleBtn = v.findViewById(R.id.scheduleBtn);
        Button cancelBtn = v.findViewById(R.id.cancelScheduleBtn);
        ImageButton spinnerArrowBtn = v.findViewById(R.id.spinner_arrow_btn);
        Spinner spinner = (Spinner) v.findViewById(R.id.reminder_times_spinner);
        AlertDialog reminderDialog = getReminderDialog();

        spinnerArrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.performClick();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reminderDialog.dismiss();
            }
        });
        scheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText reminderTimeEdt = v.findViewById(R.id.reminder_time_et);
                String reminderTimeValue = reminderTimeEdt.getText().toString();
                if(reminderTimeValue.trim().length() > 0){
                    String userToken = SessionHelper.getAccessToken(EventInfoActivity.this);
                    int timeUnit = spinner.getSelectedItemPosition();
                    String eventTs = eventDate + " " + eventTime + ":00";
                    makeCreateReminderRequest(eventId, Integer.parseInt(reminderTimeValue.trim()),
                            timeUnit, eventTs, eventName, userToken);
                }
            }
        });
    }

    public void makeCreateReminderRequest(String eventId, int value, int timeUnit,
                                          String eventTs, String eventTitle, String token){
        if (!Constants.isNetworkAvailable(this)){
            Toast.makeText(this, this.getResources().getString(R.string.failed_connection_msg),
                    Toast.LENGTH_LONG).show();
        } else {
            JSONObject jsonBody = new JSONObject();
            try{

                jsonBody.put("event_id", eventId);
                jsonBody.put("value", value);
                jsonBody.put("time_unit", timeUnit);
                jsonBody.put("event_ts", eventTs);
                jsonBody.put("event_title", eventTitle);
                jsonBody.put("token", token);

                /**
                 * Volley automatically adds backslashes when sending slashes in JSON bodies. Then,
                 * it is necessary to remove them.
                 */
                jsonBody.toString().replace("\\\\","");
            }
            catch (Exception ignored){ ;
            }

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    Constants.getCreateReminderURL(), jsonBody, response -> {
                try {
                    if(response.length() > 0){
                        //getRemindersFragment().loadReminders();
                        Toast.makeText(this, "Recordatorio creado.",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{

                    }

                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(activity, activity.getResources().getString(R.string.loading_poi_info_error_msg),
                    //                           Toast.LENGTH_LONG).show();
                } finally {
                    System.gc();
                    getReminderDialog().dismiss();
                }
            }, error -> {
                VolleyLog.d("tag", "Error: " + error.getMessage());
                //            Toast.makeText(activity, activity.getResources().getString(R.string.http_error_msg),
                //                      Toast.LENGTH_SHORT).show();
            });
            AppController.getInstance(EventInfoActivity.this).addToRequestQueue(jsonObjReq);
        }
    }

    public AlertDialog getReminderDialog(){ return this.reminderDialog; }

    public void setReminderDialog(AlertDialog reminderDialog) { this.reminderDialog = reminderDialog; }

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

    public void setEventZoneArea(String eventZoneArea){ this.eventZoneArea = eventZoneArea; }

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

        viewHolder.remindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getReminderDialog().show();
            }
        });
    }
}
