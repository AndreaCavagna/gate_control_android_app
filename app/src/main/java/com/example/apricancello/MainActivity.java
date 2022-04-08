package com.example.apricancello;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    MqttAndroidClient client;
    TextView subText;
    boolean pressed_button = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.setContentView(R.layout.activity_main);
        subText = (TextView)findViewById(R.id.mqtt_reply);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "wss://myhomeipdk.hopto.org:8883",clientId);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String payload_str = new String(message.getPayload());

                Button btn_apri = (Button) findViewById(R.id.apri);
                Button btn_apri_lungo = (Button) findViewById(R.id.aprilungo);
                TextView tv_mqtt_reply = (TextView) findViewById(R.id.mqtt_reply);
                TextView tv_mqtt_connection_gate = (TextView) findViewById(R.id.check_connection_gate);

                if (topic.equals("homeAssistant/casaBonate/cover/cancello/state")){
                    if (pressed_button) {
                        subText.setText(payload_str);



                        btn_apri.setTextColor(Color.WHITE);
                        btn_apri_lungo.setTextColor(Color.WHITE);
                        tv_mqtt_reply.setTextColor(Color.WHITE);

                        if (payload_str.equals("opening")) {
                            btn_apri.setBackgroundColor(Color.GREEN);
                            btn_apri_lungo.setBackgroundColor(Color.GREEN);
                            tv_mqtt_reply.setBackgroundColor(Color.GREEN);
                            tv_mqtt_reply.setText("Sto Aprendo!");

                        } else {
                            btn_apri.setBackgroundColor(Color.RED);
                            btn_apri_lungo.setBackgroundColor(Color.RED);
                            tv_mqtt_reply.setBackgroundColor(Color.RED);
                            tv_mqtt_reply.setText("Errore");
                        }
                    }

                }

                if (topic.equals("homeAssistant/casaBonate/cover/cancello/confirmOnline")) {
                    if (payload_str.equals("Yep!")) {

                        tv_mqtt_connection_gate.setTextColor(Color.GREEN);
                        tv_mqtt_connection_gate.setText("Cancello connessso!");

                        btn_apri.setEnabled(true);
                        btn_apri_lungo.setEnabled(true);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        View view = findViewById(android.R.id.content).getRootView();
        conn(view);
    }




    public void open(View v){

        String topic = "homeAssistant/casaBonate/cover/cancello/set";
        String message = "open";
        pressed_button = true;
        try {
            client.publish(topic, message.getBytes(),0,false);
            Toast.makeText(this,"Aperto",Toast.LENGTH_SHORT).show();
        } catch ( MqttException e) {
            e.printStackTrace();
        }
    }

    public void open_long(View v){

        String topic = "homeAssistant/casaBonate/cover/cancello/set";
        String message = "open";
        pressed_button = true;
        try {
            client.publish(topic, message.getBytes(),0,false);
            Toast.makeText(this,"Aperto",Toast.LENGTH_SHORT).show();
        } catch ( MqttException e) {
            e.printStackTrace();
        }
    }

    private void setSubscription(){

        try{

            client.subscribe("homeAssistant/casaBonate/cover/cancello/state",0);
            client.subscribe("homeAssistant/casaBonate/cover/cancello/confirmOnline",0);


        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    public void conn(View v){

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"connected!!",Toast.LENGTH_LONG).show();
                    setSubscription();
                    TextView tv_mqtt_connection = (TextView)findViewById(R.id.check_connection);
                    tv_mqtt_connection.setTextColor(Color.GREEN);
                    tv_mqtt_connection.setText("Server connessso!");

                    try {
                        client.publish("homeAssistant/casaBonate/cover/cancello/confirmOnline", "uThere?".getBytes(),0,false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"connection failed!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void disconn(View v){

        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"Disconnected!!",Toast.LENGTH_LONG).show();


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"Could not diconnect!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}