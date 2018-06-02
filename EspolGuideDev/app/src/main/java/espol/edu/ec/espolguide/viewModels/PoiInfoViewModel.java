package espol.edu.ec.espolguide.viewModels;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import espol.edu.ec.espolguide.PoiInfo;
import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
            /*try {
                return Drawable.createFromStream((InputStream) new URL(Constants.getBlockPhoto()+"63").getContent(), "photoBlock");
            } catch (IOException e1) {
                setChanged();
                notifyObservers(POI_PHOTO_REQUEST_FAILED_CONNECTION);
                return null;
            }*/
            return null;
        }
    }

    public void show() {
        ViewGroup nextChild = (ViewGroup) ((ViewGroup)activity.getView()).getChildAt(0);
        ViewGroup linear = (ViewGroup) ((ViewGroup)activity.getView()).getChildAt(1);
        ImageView imageView = (ImageView) nextChild.getChildAt(1);
        TextView name = (TextView) ((ViewGroup)linear.getChildAt(0)).getChildAt(0);
        name.setText(activity.getName());
        TextView academicUnit = (TextView) ((ViewGroup)linear.getChildAt(1)).getChildAt(0);
        academicUnit.setText(activity.getacAdemicUnit());
        TextView description = (TextView) ((ViewGroup)linear.getChildAt(2)).getChildAt(0);
        description.setText(activity.getDescription());
        new Counter().execute(new PhotoData(activity.getCtx(),imageView,activity.getName()));
        activity.getView().setVisibility(View.VISIBLE);
    }

    private class PhotoData{
        Context context;
        ImageView imgvw;
        String id_;

        public PhotoData(Context ctx, ImageView imgvw, String id_) {
            this.context = ctx;
            this.imgvw = imgvw;
            this.id_ = id_;
        }
    }


    private class Counter extends AsyncTask<PhotoData, Void, Drawable> {
        Context context;
        ImageView iv;
        String identifier;
        @Override
        protected Drawable doInBackground(PhotoData... datas) {
            context = datas[0].context;
            identifier = datas[0].id_;
            iv = datas[0].imgvw;
            Drawable d;
            if (!Constants.isNetworkAvailable(context)) {
                setChanged();
                notifyObservers(POI_PHOTO_REQUEST_FAILED_CONNECTION);
                d = null;
            }
            else {
                d = LoadImage(Constants.getBlockPhoto()+identifier);
            }
            return d;
        }

        @Override
        protected void onPostExecute(Drawable d) {
            super.onPostExecute(d);
            /*
            Bitmap myBitmap = ((BitmapDrawable) d).getBitmap();

            // Llama al método encargado de cortar en forma cuadrada a la imagen.
            Bitmap croppedImage = ImageHelper.cropBitmapToSquare(myBitmap);

            // Llama al método encargado de redondear las esquinas de la imagen
            // previamente cortada. Recibe como parámetros el mapa de bits y el tamaño // de sus lados en pixeles.
            Bitmap roundedCornersImage = ImageHelper.getRoundedCornerBitmap(
                    croppedImage, 140);

            // Asigna el mapa de bits resultante a la vista ImageView que lo mostrará.
            iv.setImageBitmap(roundedCornersImage);*/
            iv.setImageDrawable(d);

        }
    }

}

