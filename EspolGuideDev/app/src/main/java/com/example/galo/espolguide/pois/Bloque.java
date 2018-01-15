package com.example.galo.espolguide.pois;

import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by galo on 07/01/18.
 */

public class Bloque extends Poi {
    private ImageView foto_iv;

    public Bloque(String id,String codigo, String nombre, String unidad, int favoritos_count,
                  String descripcion, ImageView foto_iv){
        super(id, codigo, nombre, unidad, favoritos_count, descripcion);
        this.setFoto_iv(foto_iv);
    }

    public Bloque(String id, String codigo, String nombre, String unidad, int favoritos_count,
                  String descripcion){
        super(id,codigo, nombre, unidad, favoritos_count, descripcion);
    }

    public Bloque(String codigo){
        super(codigo);
    }

    public Bloque(String codigo, String descripcion){
        super(codigo, descripcion);
    }

    public ImageView getFoto_iv() {
        return foto_iv;
    }

    public void setFoto_iv(ImageView foto_iv) {
        this.foto_iv = foto_iv;
    }


}
