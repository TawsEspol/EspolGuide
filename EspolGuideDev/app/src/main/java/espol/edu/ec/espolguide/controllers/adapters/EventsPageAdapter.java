package espol.edu.ec.espolguide.controllers.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import espol.edu.ec.espolguide.fragments.EventsFragment;
import espol.edu.ec.espolguide.fragments.RemindersFragment;

public class EventsPageAdapter extends FragmentStatePagerAdapter {
    final int mNumOfTabs;
    Context ctx;
    final EventsFragment eventsTab;
    final RemindersFragment remindersTab;

    public EventsPageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        eventsTab = new EventsFragment();
        remindersTab = new RemindersFragment();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return eventsTab;
            case 1:
                return remindersTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

