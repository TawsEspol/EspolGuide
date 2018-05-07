package espol.edu.ec.espolguide.models;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import espol.edu.ec.espolguide.PoiInfo;

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
            System.out.println(this.id);
            PoiInfo view = new PoiInfo(this.id, this.context, this.info);
            return true;
        }
        return super.onSingleTapUp(e, mapView);
    }
}
