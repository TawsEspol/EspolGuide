package espol.edu.ec.espolguide.viewModels;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import espol.edu.ec.espolguide.PoiInfo;
import espol.edu.ec.espolguide.utils.Constants;

import java.io.InputStream;
import java.net.URL;
import java.util.Observable;

public class PoiInfoViewModel extends Observable {
    public static String POI_INFO_REQUEST_STARTED = "poi_info_request_started";
    public static String POI_INFO_REQUEST_SUCCEED = "poi_info_request_succeed";
    public static String POI_INFO_REQUEST_FAILED_CONNECTION = "poi_info_request_failed_connection";
    public static String POI_INFO_REQUEST_FAILED_HTTP = "poi_info_request_failed_http";
    public static String POI_INFO_REQUEST_FAILED_LOADING = "poi_info_request_failed_loading";
    public static String POI_PHOTO_REQUEST_STARTED = "poi_photo_request_started";
    public static String POI_PHOTO_REQUEST_SUCCEED = "poi_photo_request_succeed";
    public static String POI_PHOTO_REQUEST_FAILED_CONNECTION = "poi_photo_request_failed_connection";
    public static String POI_PHOTO_REQUEST_FAILED_HTTP = "poi_photo_request_failed_http";
    public static String POI_PHOTO_REQUEST_FAILED_LOADING = "poi_photo_request_failed_loading";

    private PoiInfo activity;

    public PoiInfoViewModel(PoiInfo activity) {
        this.activity = activity;
    }

    private Drawable LoadImage(String url)  {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "photoBlock");
            return d;
        } catch (Exception e) {
            setChanged();
            notifyObservers(POI_PHOTO_REQUEST_FAILED_HTTP);
            return null;
        }
    }

    public void show() {
        activity.getViewHolder().nameTv.setText(activity.getName());
        activity.getViewHolder().unityTv.setText(activity.getacAdemicUnit());
        //activity.getViewHolder().descriptionTv.setText(activity.getDescription());
        new Counter().execute(new PhotoData(activity.getCtx(), activity.getViewHolder().photo, activity.getCodeInfrastructure()));
        activity.getView().setVisibility(View.VISIBLE);
    }

    private class PhotoData{
        Context context;
        ImageView imgvw;
        String id;

        public PhotoData(Context ctx, ImageView imgvw, String id) {
            this.context = ctx;
            this.imgvw = imgvw;
            this.id = id;
        }
    }

    private class Counter extends AsyncTask<PhotoData, Void, Drawable> {
        Context context;
        ImageView iv;
        String identifier;
        @Override
        protected Drawable doInBackground(PhotoData... datas) {
            context = datas[0].context;
            identifier = datas[0].id;
            iv = datas[0].imgvw;
            Drawable d;
            if (!Constants.isNetworkAvailable(context)) {
                d = null;
                setChanged();
                notifyObservers(POI_PHOTO_REQUEST_FAILED_CONNECTION);
            }
            else {
                d = LoadImage(Constants.getBlockPhotoURL()+ identifier);
            }
            return d;
        }

        @Override
        protected void onPostExecute(Drawable d) {
            super.onPostExecute(d);
            iv.setImageDrawable(d);
        }
    }

}

