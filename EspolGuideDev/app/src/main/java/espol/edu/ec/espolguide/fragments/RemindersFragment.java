package espol.edu.ec.espolguide.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.controllers.AppController;
import espol.edu.ec.espolguide.controllers.adapters.ReminderAdapter;
import espol.edu.ec.espolguide.utils.Constants;
import espol.edu.ec.espolguide.utils.SessionHelper;
import espol.edu.ec.espolguide.utils.User;

public class RemindersFragment  extends Fragment {
    public static final String LOAD_REMINDERS_STARTED = "load_reminders_started";
    public static final String LOAD_REMINDERS_SUCCEEDED = "load_reminders_succeeded";
    public static final String REMINDERS_NOT_FOUND = "reminders_not_found";
    public static final String LOAD_REMINDERS_FAILED = "load_reminders_failed";

    public static final String REQUEST_FAILED_CONNECTION = "request_failed_connection";
    public static final String REQUEST_FAILED_HTTP = "request_failed_http";

    private ArrayList<String> remindersList = new ArrayList<>();
    private ReminderAdapter reminderAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.events_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadReminders();
    }

/**    private class RemindersLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String reminder = "001;Charla 'Inteligencia Artificial en la Industria;11A-A103 - Auditorio de FIEC;11h00;25/06/2019;Recordar 1 hora antes";
            remindersList = new ArrayList<>();
            remindersList.add(reminder);
            reminderAdapter = new ReminderAdapter(getActivity(), remindersList);
            ListView remindersLv = (ListView) getView().findViewById(R.id.events_lv);
            remindersLv.setAdapter(reminderAdapter);
            return null;
        }
    }*/

    public void loadReminders(){
        new RemindersLoader().execute(this.getActivity());
    }


    private class RemindersLoader extends AsyncTask<Context, Void, ArrayList> {
        Context context;

        @Override
        protected ArrayList doInBackground(Context... contexts) {
            context = contexts[0];
            if (!Constants.isNetworkAvailable(context)) {
                //  setChanged();
                //  notifyObservers(REQUEST_FAILED_CONNECTION);
            } else {
                String userToken = SessionHelper.getAccessToken(getActivity());
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("token", userToken);
                } catch (Exception ignored) {
                    ;
                }
                remindersList = new ArrayList<>();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        Constants.getUserRemindersURL(), jsonBody, response -> {
                    try {
                        JSONArray notificationsArray = response.getJSONArray("notifications");
                        int notificationsAmount = notificationsArray.length();
                        for (int i = 0; i < notificationsAmount; i++) {
                            JSONObject notificationInfo = (JSONObject) notificationsArray.get(i);
                            String eventName = notificationInfo.getString("event_title");

                            String notificationDateTime = notificationInfo.getString("notification_ts");
                            String eventDateTime = notificationInfo.getString("event_ts");
                            int timeUnit = notificationInfo.getInt("time_unit");
                            int notificationId = notificationInfo.getInt("notification_id");
                            int timeValue = notificationInfo.getInt("value");

                            String eventDate = eventDateTime.split(" ")[0];
                            String eventTime = eventDateTime.split(" ")[1];

                            String reminder = notificationId + ";" + eventName + ";" + "11A-A103 - Auditorio de FIEC" +
                                    ";" + eventTime + ";" + eventDate + ";" + "Recordar 1 hora antes";
                            remindersList.add(reminder);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    reminderAdapter = new ReminderAdapter(getActivity(), remindersList);
                    ListView remindersLv = (ListView) getView().findViewById(R.id.events_lv);
                    if(reminderAdapter.getViewTypeCount() > 0){ remindersLv.setAdapter(reminderAdapter); }


//                    setChanged();
                    //                  notifyObservers(NAMES_REQUEST_SUCCEEDED);
                }, error -> {
                    VolleyLog.d("tag", "Error: " + error.getMessage());
                    //               setChanged();
                    //             notifyObservers(REQUEST_FAILED_HTTP);
                });
                AppController.getInstance(context).addToRequestQueue(jsonObjectRequest);
            }
            return remindersList;
        }
    }
}