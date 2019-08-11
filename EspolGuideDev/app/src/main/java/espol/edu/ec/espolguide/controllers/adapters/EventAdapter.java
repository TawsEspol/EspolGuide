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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import espol.edu.ec.espolguide.EventInfoActivity;
import espol.edu.ec.espolguide.EventsActivity;
import espol.edu.ec.espolguide.MapActivity;
import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.utils.Constants;
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
        holder.setEventId(eventId);
        holder.setEventName(eventName);
        holder.setPlace(place);
        holder.setTime(time);

        holder.date_tv.setText(date);
        holder.eventId_tv.setText(eventId);
        holder.eventName_tv.setText(eventName);
        holder.place_tv.setText(place);
        holder.time_tv.setText(time);
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
                switch(id) {
                    case R.id.viewInformation:
                        Intent infoIntent = new Intent(getActivity(), EventInfoActivity.class);
                        infoIntent.putExtra("event_id", eventId);
                        getActivity().startActivity(infoIntent);
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
                        setSpinnerButtonsActions(mView);

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

    public void setSpinnerButtonsActions(View v){
        Button scheduleBtn = v.findViewById(R.id.scheduleBtn);
        Button cancelBtn = v.findViewById(R.id.cancelScheduleBtn);
        AlertDialog reminderDialog = getReminderDialog();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reminderDialog.dismiss();
            }
        });
    }
}
