package espol.edu.ec.espolguide.viewModels;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import espol.edu.ec.espolguide.PoiInfo;
import espol.edu.ec.espolguide.utils.Constants;

import java.io.InputStream;
import java.net.URL;
import java.util.Observable;

/**
 * Created by galo on 14/04/18.
 */

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
        //new Counter().execute(new PhotoData(activity.getCtx(), activity.getViewHolder().photo, activity.getCodeInfrastructure()));
        System.out.println(Constants.getBlockPhotoURL()+activity.getCodeInfrastructure());
        String url = Constants.getBlockPhotoURL() + activity.getCodeInfrastructure();
        ImageView img = activity.getViewHolder().photo;
        //Picasso.with(activity.getCtx()).load(url).into(img);


        Picasso.with(activity.getCtx()).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                img.setImageDrawable(new BitmapDrawable(bitmap));
                activity.getView().setVisibility(View.VISIBLE);
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
        Context context;
        ImageView imgvw;
        String id;

        public PhotoData(Context ctx, ImageView imgvw, String id) {
            this.context = ctx;
            this.imgvw = imgvw;
            this.id = id;
        }
    }

    private class Counter extends AsyncTask<PhotoData, Void, Void> {
        Context context;
        ImageView iv;
        String url = Constants.getBlockPhotoURL() + activity.getCodeInfrastructure();

        @Override
        protected Void doInBackground(PhotoData... datas) {
            Picasso.with(activity.getCtx()).load(url).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    activity.getView().setVisibility(View.VISIBLE);
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
            return null;
        }


    }

}

