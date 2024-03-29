package espol.edu.ec.espolguide.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.utils.SessionHelper;
import espol.edu.ec.espolguide.utils.User;
import espol.edu.ec.espolguide.utils.assync.SubjectsSoapHelper;

/**
 * Created by fabricio on 07/07/18.
 */

public class ExamsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subjects_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String username = SessionHelper.getEspolUsername(getView().getContext());
        User user = new User(getView().getContext(), username, false, getView().findViewById(R.id.subjects_container) );
        new SubjectsSoapHelper().execute(user);
    }
}
