package ke.co.stashare.liq254.activities;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import ke.co.stashare.liq254.R;
import ke.co.stashare.liq254.helper.AppController;
import ke.co.stashare.liq254.helper.Constants;
import ke.co.stashare.liq254.helper.SharedPrefManager;
import ke.co.stashare.liq254.helper.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static ke.co.stashare.liq254.helper.Constants.REGISTRATION_TOKEN_SENT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Views
    private EditText editTextEmail;
    private EditText editTextName;

    private Button buttonEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        //Initiailizing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextName = (EditText) findViewById(R.id.editTextName);

        buttonEnter = (Button) findViewById(R.id.buttonEnter);

        buttonEnter.setOnClickListener(this);

        //If the user is already logged in
        //Starting chat room
        if(AppController.getInstance().isLoggedIn()){
            finish();
            startActivity(new Intent(this, ChatRoomActivity.class));
        }
    }


    //Method to register user
    private void registerUser() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Entering chat room");
        progressDialog.show();

        final String token = SharedPrefManager.getInstance(this).getDeviceToken();

        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        try {
                            JSONObject obj = new JSONObject(response);
                            int id = obj.getInt("id");
                            String name = obj.getString("name");
                            String email = obj.getString("email");
                            String message=obj.getString("message");

                            if(message.equalsIgnoreCase(Constants.USER_CREATED)|| message.equalsIgnoreCase(Constants.TOKEN_UPDATED) )

                            {
                                //Login user
                                AppController.getInstance().loginUser(id,name,email);

                                //Starting chat room we need to create this activity
                                startActivity(new Intent(MainActivity.this, ChatRoomActivity.class));
                                Intent registrationComplete = new Intent(REGISTRATION_TOKEN_SENT);

                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registrationComplete);

                            }else {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("token", token);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Checking if user is logged in
        if(AppController.getInstance().isLoggedIn()){
            finish();
            startActivity(new Intent(this, ChatRoomActivity.class));
        }
    }


    @Override
    public void onClick(View v) {
        registerUser();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
