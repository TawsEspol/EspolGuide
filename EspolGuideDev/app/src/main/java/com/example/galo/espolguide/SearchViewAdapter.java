package com.example.galo.espolguide;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by fabricio on 14/01/18.
 */

public class SearchViewAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<String> pois_lista = null;
    private ArrayList<String> arraylist;

    public class ViewHolder {
        String id;
        TextView nombre;
        TextView nombre_alternativo;
    }

    public SearchViewAdapter(Context context, List<String> pois_lista) {
        mContext = context;
        this.pois_lista = pois_lista;
        inflater = LayoutInflater.from(mContext);

        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(pois_lista);
    }


    @Override
    public int getCount() {
        return pois_lista.size();
    }

    @Override
    public String getItem(int i) {
        return pois_lista.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.searchview_item, null);
            // Locate the TextViews in listview_item.xml
            holder.nombre = (TextView) view.findViewById(R.id.name);
            holder.nombre_alternativo = (TextView) view.findViewById(R.id.alter_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String data = pois_lista.get(i);
        String[] parts = data.split(";");
        final String name1 = parts[1]; // 004
        final String name2 = parts[2];
        // Set the results into TextViews
        holder.nombre.setText(name1);
        holder.nombre_alternativo.setText(name2);
        holder.id = parts[0];
        System.out.println(holder.id);

        // Listen for ListView Item Click
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Send single item click data to SingleItemView Class
                Intent intent = new Intent(mContext, SingleItemView.class);
                //intent.putExtra("id",name1);
                // Pass all data rank
                intent.putExtra("name",name1);
                // Pass all data country
                intent.putExtra("alter_name",name2);
                // Pass all data population
                // Pass all data flag
                // Start SingleItemView Class
                System.out.println(name1);
                mContext.startActivity(intent);
            }
        });

        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        pois_lista.clear();
        if (charText.length() == 0) {
            //pois_lista.addAll(arraylist);
            pois_lista.clear();
        }
        else
        {
            for (String wp : arraylist)
            {
                if (wp.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    pois_lista.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
