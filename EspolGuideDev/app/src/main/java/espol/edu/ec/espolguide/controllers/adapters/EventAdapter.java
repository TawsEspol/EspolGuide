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

import java.util.ArrayList;
import java.util.List;

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

    private class ViewHolder{
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
            holder.date_tv = view.findViewById(R.id.date_tv);
            holder.eventName_tv = view.findViewById(R.id.event_name_tv);
            holder.place_tv = view.findViewById(R.id.place_tv);
            holder.time_tv = view.findViewById(R.id.time_tv);
//            holder.goToBtn_btn = view.findViewById(R.id.goToBtn);
            holder.optionsBtn_btn = view.findViewById(R.id.optionsBtn);
            view.setTag(holder);
        } else{
            holder = (ViewHolder) view.getTag();
        }

        String data = events.get(position);
        String[] parts = data.split(";");
        if(parts.length<2){
            String date = parts[0];
            holder.date_tv.setText(date);
            holder.eventName_tv.setVisibility(View.GONE);
            holder.place_tv.setVisibility(View.GONE);
            holder.time_tv.setVisibility(View.GONE);
            holder.optionsBtn_btn.setVisibility(View.GONE);
        }
        else{
            String eventName = parts[0];
            String place = parts[1];
            String time = parts[2];
            holder.eventName_tv.setText(eventName);
            holder.place_tv.setText(place);
            holder.time_tv.setText(time);
            holder.date_tv.setVisibility(View.GONE);
        }
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


}
