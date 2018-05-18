package espol.edu.ec.espolguide.viewModels;

import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import espol.edu.ec.espolguide.PoiInfo;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;

import static espol.edu.ec.espolguide.utils.Constants.getBlockInfoURL;
import static espol.edu.ec.espolguide.utils.Constants.isNetworkAvailable;

public class PoiInfoViewModel extends Observable {
    public static String POI_INFO_REQUEST_STARTED = "poi_info_request_started";
    public static String POI_INFO_REQUEST_SUCCEED = "poi_info_request_succeed";
    public static String POI_INFO_REQUEST_FAILED_CONNECTION = "poi_info_request_failed_connection";
    public static String POI_INFO_REQUEST_FAILED_HTTP = "poi_info_request_failed_http";
    public static String POI_INFO_REQUEST_FAILED_LOADING = "poi_info_request_failed_loading";

    private PoiInfo activity;

    public PoiInfoViewModel(PoiInfo activity) {
        this.activity = activity;
    }

    public void show() {
        ViewGroup nextChild = (ViewGroup) ((ViewGroup)activity.getView()).getChildAt(0);
        ViewGroup linear = (ViewGroup) nextChild.getChildAt(2);
        TextView name = (TextView) linear.getChildAt(0);
        name.setText(activity.getName());
        TextView academicUnit = (TextView) linear.getChildAt(1);
        academicUnit.setText(activity.getacAdemicUnit());
        TextView description = (TextView) nextChild.getChildAt(3);
        description.setText(activity.getDescription());
        activity.getView().setVisibility(View.VISIBLE);
    }
}

