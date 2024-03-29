package espol.edu.ec.espolguide;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import java.util.Observer;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;


import espol.edu.ec.espolguide.controllers.adapters.PageAdapter;
import espol.edu.ec.espolguide.utils.Util;
import espol.edu.ec.espolguide.viewModels.SubjectsViewModel;


public class SubjectsActivity extends BaseActivity implements Observer {

    private SubjectsViewHolder viewHolder;
    private SubjectsViewModel viewModel;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_subjects, contentFrameLayout);
        this.viewModel = new SubjectsViewModel(this);
        this.viewHolder = new SubjectsViewHolder();
        this.viewHolder.setTabs();
        this.viewHolder.setSubjectsAdapter();
        Util.lockSwipeGesture(this);
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
            tabLayout = findViewById(R.id.tab_layout);
            viewPager = findViewById(R.id.pager);
            toolbar = findViewById(R.id.toolbar);
            setActivityTitle();
            toolbar.setNavigationIcon(R.drawable.ic_left_arrow);
            setSupportActionBar(toolbar);

        }

        private void setActivityTitle() {
            String activityName = getApplicationContext().getString(R.string.courses_menu_op);
            toolbar.setTitle(activityName);
        }

        public void setTabs() {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.lectures));
            tabLayout.addTab(tabLayout.newTab().setText(R.string.exams));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }

        public void setSubjectsAdapter(){
            PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}


