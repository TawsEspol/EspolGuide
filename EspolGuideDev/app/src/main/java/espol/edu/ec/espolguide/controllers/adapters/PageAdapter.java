package espol.edu.ec.espolguide.controllers.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import espol.edu.ec.espolguide.fragments.ExamsFragment;
import espol.edu.ec.espolguide.fragments.LecturesFragment;

/**
 * Created by fabricio on 07/07/18.
 */

public class PageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    Context ctx;
    LecturesFragment lecturesTab;
    ExamsFragment examsTab;

    public PageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        lecturesTab = new LecturesFragment();
        examsTab = new ExamsFragment();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return lecturesTab;
            case 1:
                return examsTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
