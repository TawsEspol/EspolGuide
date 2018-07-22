package espol.edu.ec.espolguide.controllers.listeners;

import android.view.View;

/**
 * Created by fabricio on 21/07/18.
 */

public class ToPOIListener  implements View.OnClickListener {

    private String name;

    public ToPOIListener(String name) {
        this.name = name;
    }

    @Override
    public void onClick(View arg0) {

        System.out.println("GO TO " + this.name);
    }

}


