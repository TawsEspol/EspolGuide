package espol.edu.ec.espolguide.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Observable;
import java.util.Observer;

import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.utils.SessionHelper;
import espol.edu.ec.espolguide.utils.User;
import espol.edu.ec.espolguide.utils.assync.SubjectsSoapHelper;

/**
 * Created by fabricio on 07/07/18.
 */

public class LecturesFragment extends Fragment implements Observer {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_lectures, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String username = SessionHelper.getEspolUsername(getView().getContext());
        User user = new User(getView().getContext(), username, true, getView().findViewById(R.id.lectures_container) );
        new SubjectsSoapHelper().execute(user);
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}
