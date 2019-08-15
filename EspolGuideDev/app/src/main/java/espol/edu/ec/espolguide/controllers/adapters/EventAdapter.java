package espol.edu.ec.espolguide.controllers.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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

import java.util.ArrayList;
import java.util.List;

import espol.edu.ec.espolguide.EventInfoActivity;
import espol.edu.ec.espolguide.EventsActivity;
import espol.edu.ec.espolguide.MapActivity;
import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.SessionHelper;
import espol.edu.ec.espolguide.utils.Util;

import static android.app.Activity.RESULT_OK;

public class EventAdapter extends BaseAdapter{
    private final List<String> events;
    private ViewHolder viewHolder;
    private final Context mContext;
    private final LayoutInflater inflater;
    private AlertDialog reminderDialog;

    private class ViewHolder{
        private String eventId;
        private TextView eventId_tv;
        private String eventName;
        private TextView eventName_tv;
        private String place;
        private TextView place_tv;
        private String time;
        private TextView time_tv;
        private String date;
        private TextView date_tv;
        private String zoneArea;
        private TextView zoneArea_tv;
        private ImageButton goToBtn_btn;
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

        public ImageButton getGoToBtn_btn(){
            return this.goToBtn_btn;
        }

        public void setGoToBtn_btn(ImageButton goToBtn_btn){
            this.goToBtn_btn = goToBtn_btn;
        }
    }

    public EventAdapter(Context context, List<String> events){
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
            holder.date_tv = view.findViewById(R.id.date_tv);
            holder.eventName_tv = view.findViewById(R.id.event_name_tv);
            holder.place_tv = view.findViewById(R.id.place_tv);
            holder.time_tv = view.findViewById(R.id.time_tv);
            holder.zoneArea_tv = view.findViewById(R.id.event_zone_area_tv);
//            holder.goToBtn_btn = view.findViewById(R.id.goToBtn);
            holder.optionsBtn_btn = view.findViewById(R.id.optionsBtn);
            TextView reminderTime_tv = view.findViewById(R.id.reminderTime_tv);
            reminderTime_tv.setVisibility(View.GONE);
            holder.optionsBtn_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEventsMenu(view);
                }
            });
            view.setTag(holder);
        } else{
            holder = (ViewHolder) view.getTag();
        }

        String data = events.get(position);
        String[] parts = data.trim().split(";");
        String eventId = parts[0];
        String eventName = parts[1];
        String place = parts[2];
        String time = parts[3];
        String date = parts[4];
        String zoneArea = "";
        if(parts.length == 6){
            zoneArea = parts[5];
        }

        holder.setEventId(eventId);
        holder.setEventName(eventName);
        holder.setPlace(place);
        holder.setTime(time);

        holder.date_tv.setText(date);
        holder.eventId_tv.setText(eventId);
        holder.eventName_tv.setText(eventName);
        holder.place_tv.setText(place);
        holder.time_tv.setText(time);
        holder.zoneArea_tv.setText(zoneArea);
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

    public void goToBuilding(String codeGtsi){
        Intent mapIntent = new Intent(getActivity().getApplicationContext(), MapActivity.class);
        mapIntent.putExtra(Constants.SELECTED_OPTION, R.id.map_op);
        mapIntent.putExtra(Constants.SELECTED_GTSI_CODE, codeGtsi);
        getActivity().setResult(RESULT_OK, mapIntent);
        getActivity().finish();
    }

    public void showEventsMenu(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.event_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                LinearLayout eventLayout = (LinearLayout) ((ViewGroup) v.getParent()).getParent();
                String eventId = ((TextView) eventLayout.findViewById(R.id.event_id_tv)).getText().toString();
                String eventZoneArea = ((TextView) eventLayout.findViewById(R.id.event_zone_area_tv)).getText().toString();
                switch(id) {
                    case R.id.viewInformation:
                        Intent infoIntent = new Intent(getActivity(), EventInfoActivity.class);
                        infoIntent.putExtra("event_id", eventId);
                        infoIntent.putExtra("event_zone_area", eventZoneArea);
                        getActivity().startActivityForResult(infoIntent, Constants.EVENTS_INFO_REQUEST_CODE);
                        return true;
                    case R.id.locateBuilding:
                       /** Intent infoIntent = new Intent(getActivity(), EventInfoActivity.class);
                        infoIntent.putExtra("event_id", eventId);
                        getActivity().startActivity(infoIntent);*/
                        if(eventZoneArea.trim().length() > 0){
                            Intent mapIntent = new Intent(getActivity().getApplicationContext(), MapActivity.class);
                            mapIntent.putExtra(Constants.SELECTED_OPTION, R.id.map_op);
                            mapIntent.putExtra(Constants.SELECTED_GTSI_CODE, eventZoneArea);
                            getActivity().setResult(RESULT_OK, mapIntent);
                            getActivity().finish();
                        }
                        return true;
                    case R.id.scheduleReminder:
                        String eventName = ((TextView) eventLayout.findViewById(R.id.event_name_tv)).getText().toString();
                        String eventPlace = ((TextView) eventLayout.findViewById(R.id.place_tv)).getText().toString();
                        String eventDate = ((TextView) eventLayout.findViewById(R.id.date_tv)).getText().toString();
                        String eventTime = ((TextView) eventLayout.findViewById(R.id.time_tv)).getText().toString();

/**                        String partsDate[] = eventDate.trim().split("/");
                        int day = Integer.parseInt(partsDate[0]);
                        int month = Integer.parseInt(partsDate[1]);
                        int year = Integer.parseInt(partsDate[2]);

                        String partsTime[] = eventTime.trim().split(":");
                        int hour = Integer.parseInt(partsTime[0]);
                        int minute = Integer.parseInt(partsTime[1]);
*/

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                        View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_event_reminder, null);
                        mBuilder.setView(mView);
                        setReminderDialog(mBuilder.create());
                        fillTimesSpinner(mView);
                        getReminderDialog().show();
                        setDialogButtonsActions(mView, eventId, eventDate, eventTime, eventName);

                        //scheduleNotification(getApplicationContext(), 5,100);
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

    public void setDialogButtonsActions(View v, String eventId, String eventDate, String eventTime,
                                        String eventName){
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
                    String eventTs = eventDate + " " + eventTime + ":00";
           //         makeCreateReminderRequest(eventId, Integer.parseInt(reminderTimeValue.trim()),
             //               timeUnit, eventTs, eventName, userToken);
                }
            }
        });
    }

    public void makeCreateReminderRequest(String eventId, int value, int timeUnit,
                                          String eventTs, String eventTitle, String token){
        if (!Constants.isNetworkAvailable(mContext)){
            Toast.makeText(mContext, mContext.getResources().getString(R.string.failed_connection_msg),
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
            }
            catch (Exception ignored){ ;
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    Constants.getCreateReminderURL(), jsonBody, response -> {
                try {
                    if(response.length() > 0){

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
            AppController.getInstance(mContext).addToRequestQueue(jsonObjReq);
        }
    }
}
