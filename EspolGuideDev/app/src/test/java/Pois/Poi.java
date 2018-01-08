package Pois;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.galo.espolguide.R;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.Style;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;

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
    private ArrayList<String> nombres_alternativos;
    private String geo_json_string;

    public Poi(String codigo, String nombre, String unidad, int favoritos_count,
               String descripcion, ArrayList<String> nombres_alternativos, String geo_json_string){
        this.codigo = codigo;
        this.nombre = nombre;
        this.unidad = unidad;
        this.favoritos_count = favoritos_count;
        this.descripcion = descripcion;
        this.nombres_alternativos = nombres_alternativos;
        this.setGeo_json_string(geo_json_string);
    }

    public Poi(String codigo, String nombre, String unidad, int favoritos_count,
               String descripcion){
        this.codigo = codigo;
        this.nombre = nombre;
        this.unidad = unidad;
        this.favoritos_count = favoritos_count;
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

    public String getGeo_json_string() {
        return geo_json_string;
    }

    public void setGeo_json_string(String geo_json_string) {
        this.geo_json_string = geo_json_string;
    }

    public void construir_shape(MapView map, Context current_context){
        KmlDocument kmlDocument = new KmlDocument();
        String geoJsonString = this.getGeo_json_string();
        kmlDocument.parseGeoJSON(geoJsonString);
        Drawable defaultMarker = current_context.getResources().getDrawable(R.drawable.marker_default);
        Bitmap defaultBitmap = ((BitmapDrawable) defaultMarker).getBitmap();
        Style defaultStyle = new Style(defaultBitmap, 0x901010AA, 5f, 0x20AA1010);
        FolderOverlay geoJsonOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(map, defaultStyle, null, kmlDocument);
        map.getOverlays().add(geoJsonOverlay);
        map.invalidate();
    }
}
