package espol.edu.ec.espolguide.utils;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by fabricio on 18/07/18.
 */

public class SubjectLayout extends LinearLayout {

    private String day;
    private String room;
    private Button route;


    public SubjectLayout(Context context) {
        super(context);
    }

    public SubjectLayout(Context context, String day, String room){
        super(context);
        this.day = day;
        this.room = room;
        this.route = new Button(context);


    }
}
