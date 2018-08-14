package espol.edu.ec.espolguide;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import espol.edu.ec.espolguide.utils.Util;
import espol.edu.ec.espolguide.viewModels.PoiInfoViewModel;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

/**
 * Auxiliary class that helps to model and visualize the information of each POI.
 * Created by fabricio on 07/01/18.
 */

public class PoiInfo extends AppCompatActivity implements Observer {
    private Context ctx;
    private View view;
    private String code;
    private String name;
    private String academicUnit;
    private int favoritesCount;
    private String description;
    private String codeInfrastructure;
    private ArrayList<String> alternativeNames = new ArrayList<>();
    private final ViewHolder viewHolder;
    private final PoiInfoViewModel viewModel;

    public PoiInfo(String blockName, String academicUnit, String description, String codeInfrastructure,
                   Context ctx, View view){
        this.ctx = ctx;
        this.view = view;
        this.viewHolder = new ViewHolder();
        viewModel = new PoiInfoViewModel(this);
        viewModel.addObserver(this);
        this.name = blockName;
        this.academicUnit = academicUnit;
        this.description = description;
        this.codeInfrastructure = codeInfrastructure;
    }

    @Override
    public void update(Observable o, Object arg) {
        String message = (String)arg;
        if (message.equals(PoiInfoViewModel.POI_INFO_REQUEST_FAILED_CONNECTION)) {
            Activity activityTemp = (Activity) getCtx();
            activityTemp.runOnUiThread(() -> Toast.makeText(getCtx(), getResources().getString(R.string.failed_connection_msg),
                    Toast.LENGTH_LONG).show());
        }
        if (message.equals(PoiInfoViewModel.POI_INFO_REQUEST_FAILED_LOADING)) {
            Activity activityTemp = (Activity) getCtx();
            activityTemp.runOnUiThread(() -> Toast.makeText(getCtx(), getResources().getString(R.string.loading_poi_info_error_msg),
                    Toast.LENGTH_LONG).show());
        }
        if (message.equals(PoiInfoViewModel.POI_INFO_REQUEST_FAILED_HTTP)) {
            Activity activityTemp = (Activity) getCtx();
            activityTemp.runOnUiThread(() -> Toast.makeText(getCtx(), getResources().getString(R.string.http_error_msg),
                    Toast.LENGTH_SHORT).show());
        }
    }

    public class ViewHolder{
        public TextView nameTv;
        public TextView unityTv;
        public TextView descriptionTv;
        public ImageView photo;
        public Button poiRoute;

        public ViewHolder(){
            findViews();
            setPoiRouteListener();
        }

        public void findViews(){
            Activity parentActivity = (Activity) ctx;
            nameTv = parentActivity.findViewById(R.id.name_tv);
            unityTv = parentActivity.findViewById(R.id.unity_tv);
            photo = parentActivity.findViewById(R.id.flag);
            poiRoute = parentActivity.findViewById(R.id.poi_route_btn);
        }

        public void setPoiRouteListener(){
            this.poiRoute.setOnClickListener(v -> {
                MapActivity parentActivity = (MapActivity) ctx;
                if(parentActivity.getSelectedPoi().trim().length()>0){
                    parentActivity.getViewHolder().editDestination.setText(Util.choseName(parentActivity.getSelectedPoi()));
                    parentActivity.getViewHolder().closePoiInfo();
                    parentActivity.getViewHolder().drawRoute();
                }
            });
        }
    }

    public ViewHolder getViewHolder(){
        return this.viewHolder;
    }

    public Context getCtx(){
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public View getView(){
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getacAdemicUnit() {
        return academicUnit;
    }

    public void setAcademicUnit(String academicUnit) {
        this.academicUnit = academicUnit;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(int favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getAlternativeNames() {
        return alternativeNames;
    }

    public void setAlternativeNames(ArrayList<String> alternativeNames) {
        this.alternativeNames = alternativeNames;
    }

    public String getCodeInfrastructure(){
        return this.codeInfrastructure;
    }

    public void setCodeInfrastructure(String codeInfrastructure){
        this.codeInfrastructure = codeInfrastructure;
    }
}