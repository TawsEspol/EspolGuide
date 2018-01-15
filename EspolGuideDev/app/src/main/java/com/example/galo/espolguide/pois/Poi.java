package com.example.galo.espolguide.pois;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;

/**
 * Created by galo on 07/01/18.
 */

public abstract class Poi {
    private String codigo;
    private String nombre;
    private String unidad;
    private int favoritos_count;
    private String descripcion;
    private String tipo;
    private ArrayList<String> nombres_alternativos;
    private Polygon poligono;


    public Poi(String codigo, String nombre, String unidad, int favoritos_count,
               String descripcion, ArrayList<String> nombres_alternativos){
        this.codigo = codigo;
        this.nombre = nombre;
        this.unidad = unidad;
        this.favoritos_count = favoritos_count;
        this.descripcion = descripcion;
        this.nombres_alternativos = nombres_alternativos;
    }

    public Poi(String codigo, String nombre, String unidad, int favoritos_count,
               String descripcion){
        this.codigo = codigo;
        this.nombre = nombre;
        this.unidad = unidad;
        this.favoritos_count = favoritos_count;
        this.descripcion = descripcion;
    }

    public Poi(String codigo){
        this.codigo = codigo;
    }

    public Poi(String codigo, String descripcion){
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public int getFavoritos_count() {
        return favoritos_count;
    }

    public void setFavoritos_count(int favoritos_count) {
        this.favoritos_count = favoritos_count;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ArrayList<String> getNombres_alternativos() {
        return nombres_alternativos;
    }

    public void setNombres_alternativos(ArrayList<String> nombres_alternativos) {
        this.nombres_alternativos = nombres_alternativos;
    }


    public void construir_poligono(JSONArray coordenadas, MapView map, Context ctx){
        ProgressDialog pDialog = new ProgressDialog(ctx);
        try{
            System.out.println("ENTRAAAA");
            ArrayList<GeoPoint> geoPoints = new ArrayList<>();
            for(int j=0; j<coordenadas.length(); j++){
                JSONArray point_coord = coordenadas.getJSONArray(j);
                double lat = point_coord.getDouble(0);
                double lon = point_coord.getDouble(1);
                GeoPoint geotest = new GeoPoint(lat, lon);
                geoPoints.add(geotest);
            }
            mapear_poligono(map, geoPoints);
            geoPoints.clear();
        }catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ctx, "Error , try again ! ", Toast.LENGTH_LONG).show();
            pDialog.dismiss();
        }
    }

    public void mapear_poligono(MapView map, ArrayList<GeoPoint> geoPoints){
        Polygon polygon = new Polygon();
        polygon.setFillColor(Color.argb(30, 0,0,220));
        polygon.setPoints(geoPoints);
        polygon.setStrokeColor(Color.BLUE);
        polygon.setStrokeWidth(0.7F);
        polygon.setTitle("A sample polygon");
        this.setPoligono(polygon);
        map.getOverlayManager().add(this.getPoligono());
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Polygon getPoligono() {
        return poligono;
    }

    public void setPoligono(Polygon poligono) {
        this.poligono = poligono;
    }
}
