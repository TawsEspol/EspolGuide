package espol.edu.ec.espolguide.models;

import android.widget.ImageView;

/**
 * Created by galo on 07/01/18.
 */

public class Block extends Poi {
    private ImageView photo;

    public Block(String id, String code, String name, String academicUnit, int favoritesCount,
                 String description, ImageView photo){
        super(id, code, name, academicUnit, favoritesCount, description);
        this.setPhoto(photo);
    }

    public Block(String id, String code, String name, String academicUnit, int favoritesCount,
                 String descripcion){
        super(id, code, name, academicUnit, favoritesCount, descripcion);
    }

    public Block(String code){
        super(code);
    }

    public Block(String code, String description){
        super(code, description);
    }

    public ImageView getPhoto() {
        return photo;
    }

    public void setPhoto(ImageView photo) {
        this.photo = photo;
    }
}
