package espol.edu.ec.espolguide.controllers.adapters;

import java.util.List;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

import espol.edu.ec.espolguide.R;


/**
 * Created by fabricio on 07/07/18.
 */

public class SubjectAdapter extends ArrayAdapter<String>{

        private final LayoutInflater mInflater;
        private final Context mContext;
        private final List<Subject> items;
        private final int mResource;

        public SubjectAdapter(@NonNull Context context, @LayoutRes int resource,
                              @NonNull List objects) {
            super(context, resource, 0, objects);

            mContext = context;
            mInflater = LayoutInflater.from(context);
            mResource = resource;
            items = objects;
        }
        @Override
        public View getDropDownView(int position, @Nullable View convertView,
                                    @NonNull ViewGroup parent) {
            return createItemView(position, convertView, parent);
        }

        @Override
        public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createItemView(position, convertView, parent);
        }

        private View createItemView(int position, View convertView, ViewGroup parent){
            final View view = mInflater.inflate(mResource, parent, false);

            TextView offTypeTv = (TextView) view.findViewById(R.id.offer_type_txt);
            TextView numOffersTv = (TextView) view.findViewById(R.id.num_offers_txt);
            TextView maxDiscTV = (TextView) view.findViewById(R.id.max_discount_txt);

            Subject offerData = items.get(position);

            offTypeTv.setText(offerData.getDay());
            numOffersTv.setText(offerData.getRoom());
            maxDiscTV.setText("Ver ruta");

            return view;
        }


    /**
     * Holder para el Adapter del Spinner
     * @author danielme.com
     *
     */
    private static class SocialNetworkHolder {

        private ImageView icono;

        private TextView textView;

        public ImageView getIcono() {
            return icono;
        }

        public void setIcono(ImageView icono) {
            this.icono = icono;
        }

        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }

    }

    private class Subject{

        private String day;
        private String room;

        public Subject(String day, String room){
            this.day = day;
            this.room = room;
        }

        public String getDay(){
            return this.day;
        }

        public String getRoom(){
            return this.room;
        }

    }
}
