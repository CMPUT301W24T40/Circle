package com.example.circleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.circleapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This class handles notification sending from the organizer to the attendees
 * of their event.
 */
public class SendNotificationActivity extends AppCompatActivity {
    EditText notifBody, notifTitle;
    Button sendNotifButton;
    ArrayList<String> tokens;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);
        tokens = getIntent().getStringArrayListExtra("tokens");
        setReferences();
    }

    /**
     * Sets the references to the button and edit texts on the layout
     * Clicking the sendNotif button sends the notification out to attendees.
     */
    private void setReferences() {
        notifBody = findViewById(R.id.notif_body);
        notifTitle = findViewById(R.id.notif_title);
        sendNotifButton = findViewById(R.id.send_notif_button);
        sendNotifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification(notifTitle.getText().toString(), notifBody.getText().toString());
                finish();
            }
        });
    }

    /**
     * This method creates a JSONobject that is the notification message
     * to be sent out to attendees
     * @param title
     *          The title of the notification
     * @param body
     *          The body of the notification, what's in it
     */
    private void sendNotification(String title, String body) {
        for (String token : tokens) {
            Log.d("what token", token.toString());
            JSONObject jsonNotif = new JSONObject();
            JSONObject wholeObject = new JSONObject();
            JSONObject dataObject = new JSONObject();
            try {
                jsonNotif.put("title", title);
                jsonNotif.put("body", body);
                dataObject.put("something", "some data");
                wholeObject.put("to", token);
                wholeObject.put("notification", jsonNotif);
                wholeObject.put("data", dataObject);
            } catch (JSONException e) {
                Log.d("log", e.toString());
            }
            callApi(wholeObject);
        }
    }

    /**
     * This method calls the API OkhttpClient used to be able to
     * send the message to the attendee
     * @param jsonObject
     *          This is the jsonObject that will be the notification
     */
    private void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAAp1BsAy0:APA91bGlFWtvgx2m1hIWJV7fXGIe8T-7xOnCmnSWhLEahwI3PHJTAjlpXR6hcW_kYywGIQsKIIBuCddAmLRKXLq2hRylGTFBMKeqSb0_W137391G0m8QRc4jIhuE4ZY0er0mIcIulurD")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });

    }
}