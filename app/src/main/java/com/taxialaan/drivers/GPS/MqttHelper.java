package com.taxialaan.drivers.GPS;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.taxialaan.drivers.Api.request.Online;
import com.taxialaan.drivers.Helper.SharedHelper;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by wildan on 3/19/2017.
 */

public class MqttHelper {


  //  private final String serverUri = "tcp://45.89.139.219:1883";
    MqttAndroidClient mqttAndroidClient;
     private String serverUri;
     private String idUser;
    private Online online = new Online();
    private Gson g = new Gson();
    Context context;

    public MqttHelper(Context context) {

           serverUri = "tcp://" + SharedHelper.getKey(context, "ip");
           idUser = SharedHelper.getKey(context,"id");

        mqttAndroidClient = new MqttAndroidClient(context, serverUri, MqttClient.generateClientId());

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.i("Debug", "ConnectedMqtt");
            }

            @Override
            public void connectionLost(Throwable throwable) {

                Log.i("Debug", "connectionLostMqtt");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.i("Debug", mqttMessage.toString() + "mqtt");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                Log.i("Debug", "ConnectedMqtt");
            }
        });
        connect(context);
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect(Context context) {

        online.setStatus(0);
        online.setProvider_id(idUser);
        String str = g.toJson(online);
        //  String offline = "{status:0,provider_id:" + SharedHelper.getKey(context, "id") + "}";
        //  System.err.println("onlineStatus: " + str);
        String mobile = SharedHelper.getKey(context, "mobile");
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setWill("PROD/Provider/Status", str.getBytes(), 2, true);

        try {

            mqttAndroidClient.connect(mqttConnectOptions, context, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic(context);


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    publishMessage(mobile + " " + exception.getMessage(), "PROD/Provider/Error");
                    Log.i("Debug", "Failed to connect to: " + serverUri + exception.toString());

                }
            });


        } catch (MqttException ex) {
            Log.i("Debug", ex.getMessage() + "mqtt");
            // ex.printStackTrace();
            // publishMessage(mobile + " " + "MqttException" + " " + ex.getMessage(), "PROD/Provider/Error");

        }
    }

    private void subscribeToTopic(Context context) {


        try {
            String subscriptionTopic = "PROD/Request/";
            mqttAndroidClient.subscribe(subscriptionTopic + SharedHelper.getKey(context, "id"), 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("Debug", "Subscribed!mqtt");
                    String mobile = SharedHelper.getKey(context, "mobile");
                    publishMessage(mobile + " " + "onSuccess subscriptionTopic ", "PROD/Provider/Error");

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //  Log.i("Debug", "Subscribed fail,qtt!");
                    //  String mobile = SharedHelper.getKey(context, "mobile");
                    //   publishMessage(mobile + " " + "onFailure subscriptionTopic " + exception.getMessage(), "PROD/Provider/Error");
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribinmqttg");
            ex.printStackTrace();
        }
    }

    public void publishMessage(String publishMessage, String url) {

        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(publishMessage.getBytes());
            mqttAndroidClient.publish(url, message);
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void publishOnline() {

        online.setStatus(1);
        online.setProvider_id(idUser);
        String data = g.toJson(online);

        //   System.err.println("onlineStatus: " + data);
        try {
            mqttAndroidClient.publish("PROD/Provider/Status", data.getBytes(), 2, true);

        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }

    }
}

