package espol.edu.ec.espolguide.models;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

import android.graphics.Color;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

/**
 * Created by galo on 07/01/18.
 */

public abstract class Poi implements View.OnClickListener {
    private String id;
    private String code;
    private String name;
    private String academicUnit;
    private int favoritesCount;
    private String description;
    private String type;
    private ArrayList<String> alternativeNames;
    private Poligono polygon;

    public Poi(String id){
        this. id = id;
    }
    public Poi(String id,String code, String name, String academicUnit, int favoritesCount,
               String description, ArrayList<String> alternativeNames){
        this.id = id;
        this.code = code;
        this.name = name;
        this.academicUnit = academicUnit;
        this.favoritesCount = favoritesCount;
        this.description = description;
        this.alternativeNames = alternativeNames;
    }

    public Poi(String id,String code, String name, String academicUnit, int favoritesCount,
               String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.academicUnit = academicUnit;
        this.favoritesCount = favoritesCount;
        this.description = description;
    }

    public Poi(String code, String description){
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getacAdemicUnit() {
        return academicUnit;
    }

    public void setAcademicUnit(String academicUnit) {
        this.academicUnit = academicUnit;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(int favoritesCount) { this.favoritesCount = favoritesCount; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getAlternativeNames() {
        return alternativeNames;
    }

    public void setAlternativeNames(ArrayList<String> alternativeNames) {
        this.alternativeNames = alternativeNames;
    }


    public void buildPolygon(JSONArray coordinates, MapView map, Context ctx, View info){
        ProgressDialog pDialog = new ProgressDialog(ctx);
        try{
            System.out.println("Poligono trazado.");
            ArrayList<GeoPoint> geoPoints = new ArrayList<>();
            for(int j=0; j<coordinates.length(); j++){
                JSONArray point_coord = coordinates.getJSONArray(j);
                double lat = point_coord.getDouble(0);
                double lon = point_coord.getDouble(1);
                GeoPoint geotest = new GeoPoint(lat, lon);
                geoPoints.add(geotest);
            }
            drawPolygon(map, geoPoints, ctx, info);
            geoPoints.clear();
        }catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ctx, "Error trazando bloque.", Toast.LENGTH_LONG).show();
            pDialog.dismiss();
        }
    }

    public void drawPolygon(MapView map, ArrayList<GeoPoint> geoPoints, Context ctx, View info){
        Poligono polygon = new Poligono(this.id,ctx, info);
        polygon.setFillColor(Color.argb(30, 0,0,220));
        polygon.setPoints(geoPoints);
        polygon.setStrokeColor(Color.BLUE);
        polygon.setStrokeWidth(0.7F);
        polygon.setTitle("A sample polygon");
        this.setPolygon(polygon);
        map.getOverlayManager().add(this.getPolygon());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Poligono getPolygon() {
        return polygon;
    }

    public void setPolygon(Poligono polygon) {
        this.polygon = polygon;
    }
    @Override
    public void onClick(View view) {
        view.setVisibility(View.VISIBLE);
    }
}