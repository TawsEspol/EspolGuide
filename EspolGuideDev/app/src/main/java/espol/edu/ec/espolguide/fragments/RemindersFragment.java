package espol.edu.ec.espolguide.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.controllers.adapters.ReminderAdapter;
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

    private class RemindersLoader extends AsyncTask<Void, Void, Void> {
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
    }

    public void loadReminders(){
        new RemindersLoader().execute();
    }
}