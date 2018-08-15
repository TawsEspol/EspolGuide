package espol.edu.ec.espolguide.controllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabricio on 11/08/18.
 */

public class SubjectsAdapter extends BaseAdapter {
    private List<LinearLayout> subjects;
    private ViewHolder viewHolder;
    private Context mContext;
    private LayoutInflater inflater;

    private class ViewHolder{
        private String codeGtsi;
        private TextView codeGtsi_tv;
        private TextView subjectName;

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

        public TextView getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(TextView subjectName) {
            this.subjectName = subjectName;
        }
    }

    public SubjectsAdapter(Context context, List<LinearLayout> subjects){
        this.subjects = new ArrayList<>();
        this.subjects.addAll(subjects);
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.subjects.size();
    }

    @Override
    public Object getItem(int position) {
        return this.subjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        return subjects.get(position);
    }

    public Activity getActivity(){
        return (Activity) this.mContext;
    }

}
