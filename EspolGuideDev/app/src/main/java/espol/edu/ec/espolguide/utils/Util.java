package espol.edu.ec.espolguide.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

import espol.edu.ec.espolguide.R;

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

    public static String choseName(String selectedPoi){
        if(selectedPoi.contains("|")) {
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
}
