package espol.edu.ec.espolguide.controllers.listeners;

import android.content.Context;
import android.view.View;

/**
 * Created by fabricio on 21/07/18.
 */

public class ToPoiListener implements View.OnClickListener {

    private String name;
    private Context context;

    public ToPoiListener(String name, Context ctx) {
        this.name = name;
        this.context = ctx;
    }

    @Override
    public void onClick(View arg0) {

        System.out.println("GO TO " + this.name);
    }

}


