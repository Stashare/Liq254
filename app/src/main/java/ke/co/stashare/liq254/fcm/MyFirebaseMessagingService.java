package ke.co.stashare.liq254.fcm;

/**
 * Created by Ken Wainaina on 22/02/2017.
 */

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import ke.co.stashare.liq254.helper.Constants;
import ke.co.stashare.liq254.helper.NotificationHandler;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }


    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(JSONObject json) {

        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");

            //parsing json data
            String title = data.getString("title");
            String message = data.getString("message");
            String id = data.getString("id");

            //Creating a broadcast intent
            Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
            //Adding notification data to the intent
            pushNotification.putExtra("message", message);
            pushNotification.putExtra("name", title);
            pushNotification.putExtra("id", id);

            //We will create this class to handle notifications
            NotificationHandler notificationHandler = new NotificationHandler(getApplicationContext());

            //If the app is in foreground
            if (!NotificationHandler.isAppIsInBackground(getApplicationContext())) {
                //Sending a broadcast to the chatroom to add the new message
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            } else {
                //If app is in foreground displaying push notification
                notificationHandler.showNotificationMessage(title, message);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }



    }




}
