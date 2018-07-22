package espol.edu.ec.espolguide;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import java.util.HashMap;
import java.util.Observer;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import espol.edu.ec.espolguide.controllers.adapters.PageAdapter;
import espol.edu.ec.espolguide.utils.SessionHelper;
import espol.edu.ec.espolguide.viewModels.SubjectsViewModel;


public class SubjectsActivity extends BaseActivity implements Observer {

    private SubjectsViewHolder viewHolder;
    private SubjectsViewModel viewModel;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_subjects, contentFrameLayout);
        this.viewModel = new SubjectsViewModel(this);
        this.viewHolder = new SubjectsViewHolder();
        this.viewHolder.setTabs();
        this.viewHolder.setSubjectsAdapter();
    }

    public SubjectsViewHolder getViewHolder() {
        return viewHolder;
    }

    public SubjectsViewModel getViewModel() {
        return viewModel;
    }

    public class SubjectsViewHolder{

        public SubjectsViewHolder() {
            findViews();
        }

        private void findViews() {
            tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            viewPager = (ViewPager) findViewById(R.id.pager);
        }

        public void setTabs() {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.lectures));
            tabLayout.addTab(tabLayout.newTab().setText(R.string.exams));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }

        public void setSubjectsAdapter(){
            adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }

    }

}

