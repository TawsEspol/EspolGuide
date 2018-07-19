package espol.edu.ec.espolguide.utils;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by fabricio on 14/07/18.
 */

public class User{
    private Context context;
    private String usr;
    private Boolean type;
    private LinearLayout layout;

    public User(Context ctx, String usr, Boolean type, LinearLayout layout) {
        this.context = ctx;
        this.usr = usr;
        this.type = type;
        this.layout = layout;
    }

    public Context getContext() {
        return context;
    }

    public String getUsr() {
        return usr;
    }


    public Boolean getType() {
        return type;
    }

    public LinearLayout getLayout() {
        return layout;
    }
}

