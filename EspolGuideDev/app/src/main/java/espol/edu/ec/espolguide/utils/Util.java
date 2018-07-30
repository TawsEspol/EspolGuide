package espol.edu.ec.espolguide.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import espol.edu.ec.espolguide.R;

public class Util {

    public static void closeKeyboard(Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static void openKeyboard(Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static boolean isDrawerOpen(Activity activity){
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        DrawerLayout drawerLayout = (DrawerLayout) viewGroup;
        return drawerLayout.isDrawerOpen(Gravity.LEFT);
    }

    public static void openDrawer(Activity activity){
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        DrawerLayout drawerLayout = (DrawerLayout) viewGroup;
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    public static void closeDrawer(Activity activity){
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        DrawerLayout drawerLayout = (DrawerLayout) viewGroup;
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    public static void lockSwipeGesture(Activity activity){
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        DrawerLayout drawerLayout = (DrawerLayout) viewGroup;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public static void allowSwipeGesture(Activity activity){
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        DrawerLayout drawerLayout = (DrawerLayout) viewGroup;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public static String getTimeString(double time){
        String timeString = "";
        int minutes = (int) Math.round(time/60);
        if(minutes > 59){
            int hours;
            double fractionHour;
            int remainderMin;
            hours = (int) minutes/60;
            fractionHour = minutes/60.0 - minutes/60;
            remainderMin = (int) Math.round(fractionHour*60);
            timeString = Integer.toString(hours) + Constants.HOUR_UNIT + Integer.toString(remainderMin);
        }
        else{
            timeString = Integer.toString(minutes) + Constants.MINUTE_UNIT;
        }
        return timeString;
    }
}
