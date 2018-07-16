package espol.edu.ec.espolguide.controllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import espol.edu.ec.espolguide.MapActivity;
import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.utils.Constants;

/**
 * Created by galo on 12/07/18.
 */

public class FavoriteAdapter extends BaseAdapter {
    private List<String> favoritePlaces;
    private ViewHolder viewHolder;
    private Context mContext;
    private LayoutInflater inflater;

    private class ViewHolder{
        private String codeGtsi;
        private TextView codeGtsi_tv;

        public ViewHolder(String codeGtsi){
            this.codeGtsi = codeGtsi;
        }

        public ViewHolder(){

        }

        public String getCodeGtsi(){
            return this.codeGtsi;
        }

        public void setCodeGtsi(String codeGtsi){
            this.codeGtsi = codeGtsi;
        }
    }

    public FavoriteAdapter(Context context, List<String> favoritePlaces){
        this.favoritePlaces = new ArrayList<>();
        this.favoritePlaces.addAll(favoritePlaces);
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return this.favoritePlaces.size();
    }

    @Override
    public Object getItem(int position) {
        return this.favoritePlaces.get(position);
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
            view = inflater.inflate(R.layout.favorites_item, null);
            holder.codeGtsi_tv = (TextView) view.findViewById(R.id.favorite_gtsi_tv);
            view.setTag(holder);
        } else{
            holder = (ViewHolder) view.getTag();
        }
        holder.setCodeGtsi(favoritePlaces.get(position));
        holder.codeGtsi_tv.setText(holder.getCodeGtsi());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBuilding(holder.getCodeGtsi());
            }
        });
        return view;
    }

    public Activity getActivity(){
        return (Activity) this.mContext;
    }

    public void goToBuilding(String codeGtsi){
        Intent mapIntent = new Intent(getActivity().getApplicationContext(), MapActivity.class);
        mapIntent.putExtra(Constants.SELECTED_OPTION, R.id.map_op);
        mapIntent.putExtra(Constants.SELECTED_GTSI_CODE, codeGtsi);
        mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(mapIntent);
        getActivity().finish();
    }
}
