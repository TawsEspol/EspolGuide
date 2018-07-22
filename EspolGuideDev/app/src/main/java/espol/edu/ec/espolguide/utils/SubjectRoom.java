package espol.edu.ec.espolguide.utils;

import android.content.Context;
/**
 * Created by fabricio on 14/07/18.
 */

public class SubjectRoom extends android.support.v7.widget.AppCompatButton {
    private String day;
    private String place;

    public SubjectRoom(Context ctx, String day, String place){
        super(ctx);
        this.day = day;
        this.place = place;
    }

    public String toString(){
        return (this.day +" | "+this.place);
    }


}

