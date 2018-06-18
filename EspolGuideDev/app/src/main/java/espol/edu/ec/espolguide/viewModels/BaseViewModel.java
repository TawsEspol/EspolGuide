package espol.edu.ec.espolguide.viewModels;

import java.util.Observable;

import espol.edu.ec.espolguide.BaseActivity;
import espol.edu.ec.espolguide.utils.SessionHelper;

/**
 * Created by galo on 15/06/18.
 */

public class BaseViewModel extends Observable{
    public static String EXTERNAL_USER_AUTHENTICATED = "external_user_authenticated";
    public static String ESPOL_USER_AUTHENTICATED = "espol_user_authenticated";

    BaseActivity activity;

    public BaseViewModel(BaseActivity activity){
        this.activity = activity;
    }

    public void verifyMenuItems(){
        if(!SessionHelper.isEspolLoggedIn(activity)){
            setChanged();
            notifyObservers(EXTERNAL_USER_AUTHENTICATED);
            activity.hideEspolUsersMenu();
        }
        else{
            setChanged();
            notifyObservers(ESPOL_USER_AUTHENTICATED);
            activity.hideExternalUsersMenu();
        }
    }
}
