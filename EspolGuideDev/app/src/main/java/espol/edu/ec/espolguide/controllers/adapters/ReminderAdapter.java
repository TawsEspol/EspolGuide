package espol.edu.ec.espolguide.controllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;

import espol.edu.ec.espolguide.MapActivity;
import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.utils.Constants;

import static android.app.Activity.RESULT_OK;

public class ReminderAdapter extends BaseAdapter {
    private final List<String> events;
    private ViewHolder viewHolder;
    private final Context mContext;
    private final LayoutInflater inflater;

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
    }

    public ReminderAdapter(Context context, List<String> events){
        this.events = events;
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
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
        String eventId = parts[0];
        String eventName = parts[1];
        String place = parts[2];
        String time = parts[3];
        String date = parts[4];
        String reminderTime = parts[5];
        holder.setEventId(eventId);
        holder.setEventName(eventName);
        holder.setPlace(place);
        holder.setTime(time);
        holder.setReminderTime(reminderTime);

        holder.date_tv.setText(date);
        holder.eventId_tv.setText(eventId);
        holder.eventName_tv.setText(eventName);
        holder.place_tv.setText(place);
        holder.time_tv.setText(time);
        if(reminderTime==null){
            System.out.println("==================== ES LA VARIABLE");
        }
        if(holder.reminderTime_tv == null){
            System.out.println("=================== ES LA CAJA");
        }
        holder.reminderTime_tv.setText(date + " - " + reminderTime);

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
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch(id) {
                    case R.id.viewInformation:
                        return true;
                    case R.id.updateReminder:
                        return true;
                    case R.id.removeReminder:
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

}
