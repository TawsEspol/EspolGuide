package com.example.galo.espolguide;

/**
 * Created by fabricio on 14/01/18.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SingleItemView extends Activity {
    // Declare Variables
    TextView txt_name;
    TextView txt_alter_name;
    String name;
    String alter_name;
    String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singleitemview);
        // Retrieve data from MainActivity on item click event
        Intent i = getIntent();
        // Get the results of rank
        name = i.getStringExtra("name");
        // Get the results of country
        alter_name = i.getStringExtra("alter_name");
        // Locate the TextViews in singleitemview.xml
        txt_name = (TextView) findViewById(R.id.name);
        txt_alter_name = (TextView) findViewById(R.id.alter_name);

        // Load the results into the TextViews
        txt_name.setText(name);
        txt_alter_name.setText(alter_name);
    }
}