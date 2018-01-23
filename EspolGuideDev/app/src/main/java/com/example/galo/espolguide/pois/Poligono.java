package com.example.galo.espolguide.pois;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.galo.espolguide.POI_vista;
import com.example.galo.espolguide.R;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;

/**
 * Created by fabricio on 22/01/18.
 */

public class Poligono extends Polygon {
    String id;
    Context context;
    View info;

    public Poligono (String id, Context ctx, View info){
        this.id = id;
        this.context= ctx;
        this.info = info;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        if (e.getAction() == MotionEvent.ACTION_DOWN && contains(e)) {
            //System.out.println("reconoce");
            System.out.println(this.id);
            POI_vista vista = new POI_vista(this.id, this.context, this.info);
            return true;
        }
        return super.onSingleTapUp(e, mapView);
    }
}
