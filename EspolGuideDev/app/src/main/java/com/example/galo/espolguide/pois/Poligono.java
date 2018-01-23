package com.example.galo.espolguide.pois;

import android.view.MotionEvent;
import android.view.View;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;

/**
 * Created by fabricio on 22/01/18.
 */

public class Poligono extends Polygon {
    String id;

    public Poligono (String id){
        this.id = id;
    }
    //AQUIIIII PERRITO AQUI DEBES LLAMAR A LA VISTA.
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        if (e.getAction() == MotionEvent.ACTION_DOWN && contains(e)) {
            System.out.println("reconoce");
            System.out.println(this.id);
            // YOUR CODE HERE
            return true;
        }
        return super.onSingleTapUp(e, mapView);
    }
}
