package ke.co.stashare.liq254.fcm;

/**
 * Created by Ken Wainaina on 22/02/2017.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


import ke.co.stashare.liq254.helper.AppController;
import ke.co.stashare.liq254.helper.SharedPrefManager;
import ke.co.stashare.liq254.helper.URLs;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    //public static final String REGISTRATION_TOKEN_SENT = "RegistrationTokenSent";
    public static final String TOKEN_STORED = "token stored";
    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //calling the method store token and passing token
        storeToken(refreshedToken);
        //sendRegistrationToServer();
    }


    private void storeToken(String token) {
        //saving the token on shared preferences
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);
    }




     private void sendRegistrationToServer() {

         //Getting the user id from shared preferences
         //We are storing fcm token for the user in our mysql database
         final int id = AppController.getInstance().getUserId();
         final String token = SharedPrefManager.getInstance(this).getDeviceToken();
         StringRequest stringRequest = new StringRequest(Request.Method.PUT, URLs.URL_STORE_TOKEN + id,
                 new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {

                         /**
                             Intent registrationComplete = new Intent(REGISTRATION_TOKEN_SENT);
                             LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registrationComplete);
                          **/


                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError volleyError) {

                     }
                 }) {
             @Override
             protected Map<String, String> getParams() throws AuthFailureError {
                 Map<String, String> params = new HashMap<>();
                 params.put("token", token);
                 return params;
             }
         };
         AppController.getInstance().addToRequestQueue(stringRequest);
     }


}
