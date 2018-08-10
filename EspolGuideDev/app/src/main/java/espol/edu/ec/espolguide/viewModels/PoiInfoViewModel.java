package espol.edu.ec.espolguide.viewModels;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import espol.edu.ec.espolguide.PoiInfo;
import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.utils.Constants;

import java.util.Observable;

/**
 * Created by galo on 14/04/18.
 */

public class PoiInfoViewModel extends Observable {
    public static final String POI_INFO_REQUEST_STARTED = "poi_info_request_started";
    public static final String POI_INFO_REQUEST_SUCCEED = "poi_info_request_succeed";
    public static final String POI_INFO_REQUEST_FAILED_CONNECTION = "poi_info_request_failed_connection";
    public static final String POI_INFO_REQUEST_FAILED_HTTP = "poi_info_request_failed_http";
    public static final String POI_INFO_REQUEST_FAILED_LOADING = "poi_info_request_failed_loading";
    public static String POI_PHOTO_REQUEST_STARTED = "poi_photo_request_started";
    public static String POI_PHOTO_REQUEST_SUCCEED = "poi_photo_request_succeed";
    public static String POI_PHOTO_REQUEST_FAILED_CONNECTION = "poi_photo_request_failed_connection";
    public static final String POI_PHOTO_REQUEST_FAILED_HTTP = "poi_photo_request_failed_http";
    public static String POI_PHOTO_REQUEST_FAILED_LOADING = "poi_photo_request_failed_loading";

    private final PoiInfo activity;

    public PoiInfoViewModel(PoiInfo activity) {
        this.activity = activity;
    }

    public void show() {
        activity.getViewHolder().nameTv.setText(activity.getName());
        activity.getViewHolder().unityTv.setText(activity.getacAdemicUnit());
        String url = Constants.getBlockPhotoURL() + activity.getCodeInfrastructure();
        ImageView img = activity.getViewHolder().photo;
        img.setImageResource(R.drawable.nophoto);
        activity.getView().setVisibility(View.VISIBLE);


        Picasso.with(activity.getCtx()).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                img.setImageDrawable(new BitmapDrawable(bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                setChanged();
                notifyObservers(POI_PHOTO_REQUEST_FAILED_HTTP);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                //Log.(TAG, "Getting ready to get the image");
                //Here you should place a loading gif in the ImageView to
                //while image is being obtained.
            }
        });

    }

    private class PhotoData{
        final Context context;
        final ImageView imgvw;
        final String id;

        public PhotoData(Context ctx, ImageView imgvw, String id) {
            this.context = ctx;
            this.imgvw = imgvw;
            this.id = id;
        }
    }



}

