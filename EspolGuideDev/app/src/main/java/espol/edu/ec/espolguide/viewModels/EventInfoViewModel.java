package espol.edu.ec.espolguide.viewModels;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import espol.edu.ec.espolguide.EventInfoActivity;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.utils.Constants;

public class EventInfoViewModel {
    public String eventId;
    public EventInfoActivity activity;
    private String eventZoneArea;

    public static final String EVENT_INFO_WS = Constants.getEspolEventInfoURL();

    public EventInfoViewModel(EventInfoActivity activity, String eventId){
        this.eventId = eventId;
        this.activity = activity;
    }

    public String getEventZoneArea(){
        return this.eventZoneArea;
    }

    public void setEventZoneArea(String eventZoneArea){
        this.eventZoneArea = eventZoneArea;
    }

    public void makeGetEventInfoRequest(){
        new EventInfoGetter().execute(this.activity);
    }

    private class EventInfoGetter extends AsyncTask<Context, Void, Void> {
        Context context;

        @Override
        protected Void doInBackground(Context... contexts) {
            context = contexts[0];
            if (!Constants.isNetworkAvailable(context)) {
              //  setChanged();
               // notifyObservers(REQUEST_FAILED_CONNECTION);
            }
            else {
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                            EVENT_INFO_WS + eventId, null, response -> {
                        try{
                            String eventName = response.getString("nombre");
                            String eventDescription= response.getString("descripcion");
                            String eventPlace = response.getString("lugar");
                            setEventZoneArea(response.getString("zona_area"));
                            String eventDate = response.getString("fecha");
                            String eventTime = response.getString("hora_inicio");
                            String eventInfoEmail = response.getString("email_responsable");
                            setDataOnScreen(eventName, eventDescription, eventPlace, getEventZoneArea(),
                                    eventDate, eventTime, eventInfoEmail);
                        }
                        catch (Exception e){
                           // setChanged();
                            //notifyObservers(GET_FAVORITES_REQUEST_FAILED_LOADING);
                        }
                    }, error -> {
                        VolleyLog.d("tag", "Error: " + error.getMessage());
                        //setChanged();
                        //notifyObservers(REQUEST_FAILED_HTTP);
                    });

                    AppController.getInstance(activity).addToRequestQueue(jsonObjReq);
                }
            return null;
        }
    }

    public void setDataOnScreen(String eventName, String eventDescription, String eventPlace,
                                String eventZoneArea, String eventDate, String eventTime, String infoEmail){
        String formatedDateTime = eventDate + " - " + eventTime.replace(':', 'h');
        String formatedPlace = eventPlace + "\n" + eventZoneArea;
        this.activity.getViewHolder().eventNameTv.setText(eventName);
        this.activity.getViewHolder().eventDescriptionTv.setText(eventDescription);
        this.activity.getViewHolder().eventTimeTv.setText(formatedDateTime);
        this.activity.getViewHolder().eventPlaceTv.setText(formatedPlace);
        this.activity.getViewHolder().eventMoreInfoTv.setText(infoEmail);
    }
}
