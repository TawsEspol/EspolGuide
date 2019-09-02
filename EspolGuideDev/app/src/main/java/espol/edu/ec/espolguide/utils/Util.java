package espol.edu.ec.espolguide.utils;

import android.app.Activity;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import espol.edu.ec.espolguide.EventsActivity;
import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.models.Time;

/**
 * Class used to execute utilities tasks.
 *
 * This activity is used to execute various utilities tasks.
 *
 * @author Galo Castillo
 * @since apolo 0.2
 */

public class Util {

    /**
     * Method that closes the keyboard.
     *
     * This method closes the keyboard.
     *
     * @author Galo Castillo
     * @return The method returns nothing.
     * @param ctx is the current application context.
     */
    public static void closeKeyboard(Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    /**
     * Method that opens the keyboard.
     *
     * This method opens the keyboard.
     *
     * @author Galo Castillo
     * @return The method returns nothing.
     * @param ctx is the current application context.
     */
    public static void openKeyboard(Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * Method that is used to query if the drawer is open.
     *
     * This method is used to query if the main menu is open at the left of the screen.
     *
     * @author Galo Castillo
     * @return The method returns true if the drawer is open, otherwise returns false.
     * @param activity is the current application context activity.
     */
    public static boolean isDrawerOpen(Activity activity){
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        DrawerLayout drawerLayout = (DrawerLayout) viewGroup;
        return drawerLayout.isDrawerOpen(Gravity.LEFT);
    }

    /**
     * Method that opens the drawer.
     *
     * This method opens the main menu at the left of the screen.
     *
     * @author Galo Castillo
     * @return The method returns nothing.
     * @param activity is the current application context activity.
     */
    public static void openDrawer(Activity activity){
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        DrawerLayout drawerLayout = (DrawerLayout) viewGroup;
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    /**
     * Method that closes the drawer.
     *
     * This method closes the main menu at the left of the screen.
     *
     * @author Galo Castillo
     * @return The method returns nothing.
     * @param activity is the current application context activity.
     */
    public static void closeDrawer(Activity activity){
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        DrawerLayout drawerLayout = (DrawerLayout) viewGroup;
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    /**
     * Method that disables the drawer swipe gesture.
     *
     * This method disables the usage of a swipe gesture to open the main menu.
     *
     * @author Galo Castillo
     * @return The method returns nothing.
     * @param activity is the current application context activity.
     */
    public static void lockSwipeGesture(Activity activity){
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        DrawerLayout drawerLayout = (DrawerLayout) viewGroup;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * Method that enables the drawer swipe gesture.
     *
     * This method enables the usage of a swipe gesture to close and open the main menu.
     *
     * @author Galo Castillo
     * @return The method returns nothing.
     * @param activity is the current application context activity.
     */
    public static void allowSwipeGesture(Activity activity){
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        DrawerLayout drawerLayout = (DrawerLayout) viewGroup;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    /**
     * Method that builds the text used to show the route time duration.
     *
     * This method constructs the string used to be set on the time string TextView on the route
     * information view. From the parameters, this method transforms the route time duration
     * from seconds to minutes and hours. This method constructs the time string in a different
     * way depending if the route takes more than 59min or less than an hour.
     *
     * @author Galo Castillo
     * @return The method returns a string that will be used to be set as the route time duration text.
     * @param time is the double that stores the route time duration in seconds.
     * @param activity is the current application context activity.
     */
    public static String getTimeString(double time, Activity activity){
        String timeString = "";
        int minutes = (int) Math.round(time/60);
        String minuteUnit = activity.getApplicationContext().getString(R.string.minute_unit);
        if(minutes > 59){
            int hours;
            double fractionHour;
            int remainderMin;
            hours = minutes /60;
            fractionHour = minutes/60.0 - minutes/60;
            remainderMin = (int) Math.round(fractionHour*60);
            String hourUnit = activity.getApplicationContext().getString(R.string.hour_unit);
            timeString = Integer.toString(hours) + hourUnit + Integer.toString(remainderMin) + minuteUnit;
        }
        else{
            timeString = Integer.toString(minutes) + minuteUnit;
        }
        return timeString;
    }

    public static String choseName(String selectedPoi) {
        if (selectedPoi.contains("|")) {
            String[] codes = selectedPoi.split("\\|");
            try {
                String codeGtsi = codes[0].trim();
                if (codeGtsi.length() > 0) {
                    return codeGtsi;
                }
            } catch (Exception ignored) {
            }
            try {
                String codeInfra = codes[1].trim();
                if (codeInfra.length() > 0) {
                    return codeInfra;
                }
            } catch (Exception ignored) {
            }
            return "";
        }
        return selectedPoi;
    }

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    public static String getReminderTimeString(int timeUnit, int timeValue, String type){
        String reminderTimeString = "";
        HashMap<Integer, String> units = new HashMap<>();
        units.put(0, "minutos");
        units.put(1, "horas");
        units.put(2, "dias");

        String unitString = units.get(timeUnit);
        if(type == "reminders_list"){
            reminderTimeString = "Recordar " + timeValue + " " + unitString + " antes";
        }
        else if (type == "event_notification"){
            reminderTimeString = "En " + timeValue + " " + unitString + " comienza el evento en ESPOL";
        }
        return reminderTimeString;
    }

    public static void scheduleNotification(Context context, int notificationId, Time time, String eventTitle,
                                     int timeUnit, int timeValue) {
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;



        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(eventTitle)
                .setContentText(getReminderTimeString(timeUnit, timeValue, "event_notification"))
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
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name_, importance); //Create Notification Channel
            mNotificationManager.createNotificationChannel(channel);
            //  mNotificationManager.notify(notificationId, notification);
        }

        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar timeCal = Calendar.getInstance();
        timeCal.set(Calendar.HOUR_OF_DAY, time.getHour());
        timeCal.set(Calendar.MINUTE, time.getMinute());
        timeCal.set(Calendar.SECOND, time.getSecond());
        timeCal.set(Calendar.DAY_OF_MONTH, time.getDay());
        timeCal.set(Calendar.MONTH, time.getMonth() - 1);
        timeCal.set(Calendar.YEAR, time.getYear());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeCal.getTimeInMillis(), pendingIntent);

    }
}
