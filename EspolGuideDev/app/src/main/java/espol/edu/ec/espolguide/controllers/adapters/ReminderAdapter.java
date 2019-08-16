package espol.edu.ec.espolguide.controllers.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.List;

import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.SessionHelper;

import static android.app.Activity.RESULT_OK;

public class ReminderAdapter extends BaseAdapter {
    private final List<String> events;
    private ViewHolder viewHolder;
    private final Context mContext;
    private final LayoutInflater inflater;
    private AlertDialog reminderDialog;

    private class ViewHolder{
        private String eventId;
        private TextView eventId_tv;
        private TextView notificationId_tv;
        private String notificationId;
        private String eventName;
        private TextView eventName_tv;
        private String place;
        private TextView place_tv;
        private String time;
        private TextView time_tv;
        private String date;
        private TextView date_tv;
        private String reminderTime;
        private TextView reminderTime_tv;
        private ImageButton optionsBtn_btn;

        public ViewHolder(String eventName){
            this.eventName = eventName;
        }

        public ViewHolder(){

        }

        public String getEventId(){
            return this.eventId;
        }

        public void setEventId(String eventId){
            this.eventName = eventId;
        }

        public String getEventName(){
            return this.eventName;
        }

        public void setEventName(String eventName){
            this.eventName = eventName;
        }

        public String getPlace(){
            return this.place;
        }

        public void setPlace(String place){
            this.place = place;
        }

        public String getDate(){
            return this.date;
        }

        public void setDate(String date){
            this.date = date;
        }

        public String getTime(){
            return this.time;
        }

        public void setTime(String time){
            this.time = time;
        }

        public String getReminderTime(){ return this.reminderTime; }

        public void setReminderTime(String reminderTime){ this.reminderTime = reminderTime; }

        public String getNotificationId(){ return this.notificationId; }

        public void setNotificationId(String notificationId){ this.notificationId = notificationId; }
    }

    public ReminderAdapter(Context context, List<String> events){
        this.events = events;
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
    }

    public AlertDialog getReminderDialog(){
        return this.reminderDialog;
    }

    public void setReminderDialog(AlertDialog reminderDialog){
        this.reminderDialog = reminderDialog;
    }

    @Override
    public int getCount() {
        return this.events.size();
    }

    @Override
    public Object getItem(int position) {
        return this.events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.events_item, null);
            holder.eventId_tv = view.findViewById(R.id.event_id_tv);
            holder.notificationId_tv = view.findViewById(R.id.notification_id_tv);
            holder.date_tv = view.findViewById(R.id.date_tv);
            holder.eventName_tv = view.findViewById(R.id.event_name_tv);
            holder.place_tv = view.findViewById(R.id.place_tv);
            holder.time_tv = view.findViewById(R.id.time_tv);
            holder.reminderTime_tv = view.findViewById(R.id.reminderTime_tv);
//            holder.goToBtn_btn = view.findViewById(R.id.goToBtn);
            holder.optionsBtn_btn = view.findViewById(R.id.optionsBtn);
            holder.optionsBtn_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showRemindersMenu(view);
                }
            });
            view.setTag(holder);
        } else{
            holder = (ViewHolder) view.getTag();
        }

        String data = events.get(position);
        String[] parts = data.trim().split(";");
        String notificationId = parts[0];
        String eventName = parts[1];
        String place = parts[2];
        String time = parts[3];
        String date = parts[4];
        String reminderTime = parts[5];
//        String eventId = parts[6];
//        holder.setEventId(eventId);

        holder.setEventName(eventName);
        holder.setPlace(place);
        holder.setTime(time);
        holder.setReminderTime(reminderTime);
        holder.setNotificationId(notificationId);

        holder.date_tv.setText(date);
//        holder.eventId_tv.setText(eventId);
        holder.eventName_tv.setText(eventName);
        holder.place_tv.setText(place);
        holder.time_tv.setText(time);
        holder.reminderTime_tv.setText(reminderTime);
        holder.notificationId_tv.setText(notificationId);

        holder.reminderTime_tv.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public Activity getActivity(){
        return (Activity) this.mContext;
    }

    public void showRemindersMenu(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.reminder_menu, popup.getMenu());
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_event_reminder, null);
        mBuilder.setView(mView);
        setReminderDialog(mBuilder.create());
        fillTimesSpinner(mView);

        LinearLayout eventLayout = (LinearLayout) ((ViewGroup) v.getParent()).getParent();
        String eventId = ((TextView) eventLayout.findViewById(R.id.event_id_tv)).getText().toString();
        String notificationId = ((TextView) eventLayout.findViewById(R.id.notification_id_tv)).getText().toString();
        String userToken = SessionHelper.getAccessToken(getActivity());

        setDialogButtonsActions(mView, notificationId);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                switch(id) {
                    case R.id.viewInformation:
//                        Intent infoIntent = new Intent(getActivity(), EventInfoActivity.class);
//                        infoIntent.putExtra("event_id", eventId);
//                        getActivity().startActivityForResult(infoIntent, Constants.EVENTS_INFO_REQUEST_CODE);
                        return true;
                    case R.id.updateReminder:
                        getReminderDialog().show();
                        return true;
                    case R.id.removeReminder:
                        makeRemoveReminderRequest(Integer.parseInt(notificationId), userToken,
                                eventLayout);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    public void fillTimesSpinner(View v){
        Spinner spinner = (Spinner) v.findViewById(R.id.reminder_times_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.reminders_times_array, R.layout.reminder_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }

    public void setDialogButtonsActions(View v, String notificationId){
        Button scheduleBtn = v.findViewById(R.id.scheduleBtn);
        Button cancelBtn = v.findViewById(R.id.cancelScheduleBtn);
        AlertDialog reminderDialog = getReminderDialog();
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
                    Spinner spinner = (Spinner) v.findViewById(R.id.reminder_times_spinner);
                    String userToken = SessionHelper.getAccessToken(mContext);
                    int timeUnit = spinner.getSelectedItemPosition();
                    makeUpdateReminderRequest(Integer.parseInt(notificationId.trim()),
                            Integer.parseInt(reminderTimeValue.trim()), timeUnit,
                            userToken);
                }
            }
        });
    }


    public void makeRemoveReminderRequest(int notificationId, String token, LinearLayout eventLayout){
        if (!Constants.isNetworkAvailable(mContext)){
            Toast.makeText(mContext, mContext.getResources().getString(R.string.failed_connection_msg),
                    Toast.LENGTH_LONG).show();
        } else {
            JSONObject jsonBody = new JSONObject();
            try{
                System.out.println();
                jsonBody.put("notification_id", notificationId);
                jsonBody.put("token", token);
            }
            catch (Exception ignored){ ;
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    Constants.getRemoveReminderURL(), jsonBody, response -> {
                try {
                    if(response.length() > 0){
                        eventLayout.removeAllViews();
                    }
                    else{

                    }

                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(activity, activity.getResources().getString(R.string.loading_poi_info_error_msg),
                    //                           Toast.LENGTH_LONG).show();
                } finally {
                    System.gc();
                }
            }, error -> {
                VolleyLog.d("tag", "Error: " + error.getMessage());
                //            Toast.makeText(activity, activity.getResources().getString(R.string.http_error_msg),
                //                      Toast.LENGTH_SHORT).show();
            });
            AppController.getInstance(mContext).addToRequestQueue(jsonObjReq);
        }
    }

    public void makeUpdateReminderRequest(int notificationId, int value, int timeUnit,
                                          String token){
        if (!Constants.isNetworkAvailable(mContext)){
            Toast.makeText(mContext, mContext.getResources().getString(R.string.failed_connection_msg),
                    Toast.LENGTH_LONG).show();
        } else {
            JSONObject jsonBody = new JSONObject();
            try{
                jsonBody.put("notification_id", notificationId);
                jsonBody.put("value", value);
                jsonBody.put("time_unit", timeUnit);
                jsonBody.put("token", token);
            }
            catch (Exception ignored){ ;
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    Constants.getUpdateReminderURL(), jsonBody, response -> {
                try {
                    if(response.length() > 0){
                        getReminderDialog().dismiss();
                    }
                    else{

                    }

                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(activity, activity.getResources().getString(R.string.loading_poi_info_error_msg),
                    //                           Toast.LENGTH_LONG).show();
                } finally {
                    System.gc();
                }
            }, error -> {
                VolleyLog.d("tag", "Error: " + error.getMessage());
                //            Toast.makeText(activity, activity.getResources().getString(R.string.http_error_msg),
                //                      Toast.LENGTH_SHORT).show();
            });
            AppController.getInstance(mContext).addToRequestQueue(jsonObjReq);
        }
    }
}
