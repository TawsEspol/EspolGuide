package espol.edu.ec.espolguide.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import espol.edu.ec.espolguide.EventsActivity;
import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.controllers.adapters.EventAdapter;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.SessionHelper;

public class EventsFragment extends Fragment {
    public static final String LOAD_EVENTS_STARTED = "load_events_started";
    public static final String LOAD_EVENTS_SUCCEEDED = "load_events_succeeded";
    public static final String EVENTS_NOT_FOUND = "events_not_found";
    public static final String LOAD_EVENTS_FAILED = "load_events_failed";

    public static final String REQUEST_FAILED_CONNECTION = "request_failed_connection";
    public static final String REQUEST_FAILED_HTTP = "request_failed_http";
    public static final String GET_EVENTS_REQUEST_STARTED = "get_events_request_started";
    public static final String GET_EVENTS_REQUEST_SUCCEEDED = "get_events_request_succeeded";
    public static final String GET_EVENTS_REQUEST_FAILED_LOADING = "get_events_request_failed_loading";

    final private String ESPOL_EVENTS_WS = Constants.getEspolEventsURL();


    private ArrayList<String> eventsList = new ArrayList<>();
    private EventAdapter eventAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.events_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadEvents();
    }

    private class EventsLoader extends AsyncTask<Context, Void, ArrayList> {
        Context context;
        @Override
        protected ArrayList doInBackground(Context... contexts) {
            context = contexts[0];
            if (!Constants.isNetworkAvailable(context)) {
              //  setChanged();
              //  notifyObservers(REQUEST_FAILED_CONNECTION);
            }
            else {
                eventsList = new ArrayList<>();
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                        ESPOL_EVENTS_WS, null, response -> {
                    int datesAmount = response.length();
                    for (int i=0; i<datesAmount; i++ ) {
                        try {
                            JSONObject dateInfo = (JSONObject) response.get(i);
                            String eventDate = dateInfo.getString("fecha");
                            JSONArray eventsArray = (JSONArray) dateInfo.getJSONArray("eventos");
                            int eventsAmount = eventsArray.length();
                            for(int j=0; j<eventsAmount; j++){
                                JSONObject eventInfo = (JSONObject) eventsArray.get(j);
                                String eventZone = eventInfo.getString("zona_area");
                              //  if(eventZone.trim().length() < 1){ continue;}

                                String eventId = eventInfo.getString("id");
                                String eventName = eventInfo.getString("nombre");
                                String eventPlace = eventInfo.getString("lugar");
                                String eventTime = eventInfo.getString("hora_inicio");

                                String eventString = eventId + ";" + eventName + ";" + eventPlace +
                                        ";" + eventTime + ";" + eventDate;
                                eventsList.add(eventString);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    eventAdapter = new EventAdapter(getActivity(), eventsList);
                    ListView eventsLv = (ListView) getView().findViewById(R.id.events_lv);
                    eventsLv.setAdapter(eventAdapter);

//                    setChanged();
  //                  notifyObservers(NAMES_REQUEST_SUCCEEDED);
                }, error -> {
                    VolleyLog.d("tag", "Error: " + error.getMessage());
     //               setChanged();
       //             notifyObservers(REQUEST_FAILED_HTTP);
                });
                AppController.getInstance(context).addToRequestQueue(jsonArrayRequest);
            }
            return eventsList;
        }
    }

    public void loadEvents(){
        new EventsLoader().execute(getActivity());
    }
}