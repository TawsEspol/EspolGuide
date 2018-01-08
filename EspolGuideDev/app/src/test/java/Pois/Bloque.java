package Pois;

import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by galo on 07/01/18.
 */

public class Bloque extends Poi {
    private ImageView foto_iv;

    public Bloque(String codigo, String nombre, String unidad, int favoritos_count,
                  String descripcion, ArrayList<String> nombres_alternativos,
                  String geo_json_string, ImageView foto_iv){
        super(codigo, nombre, unidad, favoritos_count, descripcion, nombres_alternativos,
                geo_json_string);
        this.setFoto_iv(foto_iv);
    }

    public ImageView getFoto_iv() {
        return foto_iv;
    }

    public void setFoto_iv(ImageView foto_iv) {
        this.foto_iv = foto_iv;
    }
}
