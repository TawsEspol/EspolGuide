package espol.edu.ec.espolguide.controllers.listeners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import espol.edu.ec.espolguide.MapActivity;
import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.utils.Constants;

import static android.app.Activity.RESULT_OK;

/**
 * Created by fabricio on 21/07/18.
 */

public class ToPoiListener implements View.OnClickListener {

    private String codeGtsi;
    private Context context;

    public ToPoiListener(String codeGtsi, Context ctx) {
        this.codeGtsi = codeGtsi;
        System.out.println(codeGtsi);
        this.context = ctx;
    }

    @Override
    public void onClick(View arg0) {
        goToBuilding(this.codeGtsi);
    }

    public Activity getActivity(){
        Activity activity = (Activity) this.context;
        return activity;
    }
    
    public void goToBuilding(String codeGtsi){
        Intent mapIntent = new Intent(this.context, MapActivity.class);
        mapIntent.putExtra(Constants.SELECTED_OPTION, R.id.map_op);
        mapIntent.putExtra(Constants.SELECTED_GTSI_CODE, codeGtsi);
        getActivity().setResult(RESULT_OK, mapIntent);
        getActivity().finish();
    }
}


