package espol.edu.ec.espolguide;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.Calendar;
import java.util.Observer;

import espol.edu.ec.espolguide.controllers.adapters.EventsPageAdapter;
import espol.edu.ec.espolguide.utils.AlarmReceiver;
import espol.edu.ec.espolguide.utils.Util;
import espol.edu.ec.espolguide.viewModels.EventsViewModel;
import espol.edu.ec.espolguide.viewModels.FavoritesViewModel;

/**
 * Activity for loading a list of Events.
 *
 * This activity is used to display the list of Events and their information.
 * It uses the Observator software design pattern.
 *
 * @author Galo Castillo
 */

public class EventsActivity extends BaseActivity implements Observer {
    private EventsViewModel viewModel;
    private ViewHolder viewHolder;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_events, contentFrameLayout);
        this.viewHolder = new ViewHolder();
        this.viewModel = new EventsViewModel(this);
        this.viewModel.addObserver(this);
        this.viewHolder.setTabs();
        this.viewHolder.setEventsPageAdapter();

        Util.lockSwipeGesture(this);
    }

    public class ViewHolder{
        public ListView eventsLv;
        public Toolbar eventsToolbar;

        public ViewHolder(){
            findViews();
            setActivityTitle();
            this.eventsToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
            setSupportActionBar(this.eventsToolbar);
           // registerForContextMenu(getEventsLv());
        }

        public ListView getEventsLv() {
            return eventsLv;
        }

        public void findViews(){
            eventsLv = findViewById(R.id.events_lv);
            eventsToolbar = findViewById(R.id.events_toolbar);

            tabLayout = findViewById(R.id.events_tab_layout);
            viewPager = findViewById(R.id.events_pager);
            toolbar = findViewById(R.id.events_toolbar);
        }

        public void setEventsLv(ListView eventsLv){
            this.eventsLv = eventsLv;
        }

        public void setActivityTitle(){
            String activityName = getApplicationContext().getString(R.string.events_menu_op);
            eventsToolbar.setTitle(activityName);
        }

        public void setTabs() {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.events_tab_name));
            tabLayout.addTab(tabLayout.newTab().setText(R.string.reminder_tab_name));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }

        public void setEventsPageAdapter(){
            EventsPageAdapter adapter = new EventsPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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

    public ViewHolder getViewHolder(){
        return this.viewHolder;
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

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.event_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch(id) {
                    case R.id.viewInformation:
                        finish();
                        return true;
                    case R.id.scheduleReminder:
                        scheduleNotification(getApplicationContext(), 5,100);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }



    public void scheduleNotification(Context context, long delay, int notificationId) {//delay is after how much time(in millis) from current time you want to schedule the notification
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Taller de Fundamentos de Scrum")
                .setContentText("En 1 hora comienza el evento en 11A-A01")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.mapbox_mylocation_icon_default)
                .setChannelId(CHANNEL_ID)
                .setLargeIcon(((BitmapDrawable) context.getResources().getDrawable(R.drawable.mapbox_mylocation_icon_default)).getBitmap())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        Intent intent = new Intent(context, EventsActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);

        Notification notification = builder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {       // For Oreo and greater than it, we required Notification Channel.
            CharSequence name_ = "My New Channel";                   // The user-visible name of the channel.
            NotificationManager mNotificationManager;
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name_, importance); //Create Notification Channel
            mNotificationManager.createNotificationChannel(channel);
            //  mNotificationManager.notify(notificationId, notification);
        }

        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar timeCal = Calendar.getInstance();
        timeCal.set(Calendar.HOUR_OF_DAY, 0);
        timeCal.set(Calendar.MINUTE, 30);
        timeCal.set(Calendar.SECOND, 0);
        timeCal.set(Calendar.DAY_OF_MONTH, 22);
        timeCal.set(Calendar.MONTH, 7-1);
        timeCal.set(Calendar.YEAR, 2019);

//        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //      alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeCal.getTimeInMillis(), pendingIntent);

    }

}
