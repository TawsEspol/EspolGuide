package com.example.galo.espolguide.pois;

import android.view.MotionEvent;
import android.view.View;

import org.osmdroid.views.overlay.Polygon;

/**
 * Created by fabricio on 22/01/18.
 */

public class Poligono extends Polygon implements View.OnClickListener{


    @Override
    public void onClick(View view) {
        System.out.println("holi");
    }
}
