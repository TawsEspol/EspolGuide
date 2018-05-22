package espol.edu.ec.espolguide.viewModels;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.sql.SQLOutput;
import java.util.Observable;

import espol.edu.ec.espolguide.Login;
import espol.edu.ec.espolguide.R;
import espol.edu.ec.espolguide.utils.Constants;

import android.view.View;
import android.widget.Toast;

/**
 * Created by fabricio on 19/05/18.
 */

public class LoginViewModel extends Observable {
    public static String AUTH_REQUEST_STARTED = "auth_request_started";
    public static String AUTH_REQUEST_SUCCEED = "authdraw_request_succeed";
    public static String AUTH_FAILED_CONNECTION = "auth_request_failed_connection";
    public static String AUTH_FAILED_HTTP = "auth_request_failed_http";
    public static String NAMESPACE = "http://tempuri.org/";
    public static String URL = "https://ws.espol.edu.ec/saac/wsGuide.asmx";
    public static String METHOD_NAME = "autenticacion";
    public static String SOAP_ACTION = "http://tempuri.org/autenticacion";

    private Login activity;

    public LoginViewModel(Login activity) {
        this.activity = activity;
    }

    public void auth() {
        setChanged();
        notifyObservers(AUTH_REQUEST_STARTED);
        new Auth().execute(new AuthScreen(activity, activity.getViewHolder().username,
                activity.getViewHolder().password));
    }

    //Enviar el view para coger el valor de los textfields
    private class AuthScreen {
        Context context;
        EditText usr;
        EditText pass;

        public AuthScreen(Context ctx, EditText usr, EditText pass) {
            this.context = ctx;
            this.usr = usr;
            this.pass = pass;
        }
    }

    private class Auth extends AsyncTask<AuthScreen, Void, Boolean> {
        Context ctx;
        Boolean result;

        @Override
        protected Boolean doInBackground(AuthScreen... auths) {
            ctx = auths[0].context;
            if (!Constants.isNetworkAvailable(ctx)) {
                setChanged();
                notifyObservers(AUTH_FAILED_CONNECTION);
            } else {
                System.out.println(auths[0].usr.getText());
                System.out.println(auths[0].pass.getText());
                SoapObject request = new SoapObject(Constants.NAMESPACE, Constants.AUTH_METHOD_NAME);
                request.addProperty("authUser", auths[0].usr.getText().toString());
                request.addProperty("authContrasenia", auths[0].pass.getText().toString());
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.headerOut = new Element[1];
                envelope.headerOut[0] = buildAuthHeader();
                envelope.setOutputSoapObject(request);
                HttpTransportSE transport = new HttpTransportSE(Constants.URL);

                try {
                    transport.call(Constants.AUTH_SOAP_ACTION, envelope);
                    SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                    result = Boolean.valueOf(response.toString());
                    return result;
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            return false;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                //@Todo AsyncTask finalizado
                activity.setContentView(R.layout.activity_map);
            } else {
                Toast message = Toast.makeText(activity.getApplicationContext(),
                        "Credenciales incorrectas", Toast.LENGTH_LONG);
                message.show();
            }
        }

        private Element buildAuthHeader() {
            Element h = new Element().createElement(NAMESPACE, Constants.SOAP_HEADER);
            Element user = new Element().createElement(NAMESPACE, "usuario");
            user.addChild(Node.TEXT, Constants.USER_SOAP_HEADER);
            h.addChild(Node.ELEMENT, user);
            Element key_ = new Element().createElement(NAMESPACE, "key");
            key_.addChild(Node.TEXT, Constants.KEY_SOAP_HEADER);
            h.addChild(Node.ELEMENT, key_);

            return h;
        }

    }
}
