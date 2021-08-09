package com.aplicacion.elcatrachocarwash;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        Log.d("token", "Mi token es: " + token);
        guardartoken(token);

    }

    private void guardartoken(String token) {
        ///Aqui genero mi token como identificador unico
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("token");
        reference.child("Isabel").setValue(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);

        ///Envio de informacion desde Firebase a nuestra aplicacion android

        //Obtenemos el id de la persona que envia el mensaje
        String from = remoteMessage.getFrom();
        Log.e("TAG", "Mensaje recibido de: " +from);


       /* //Condicional, estamos haciendo uso de los valores que vienen por defecto, solo es el titulo y el cuerpo
        // de una notificacion sencilla
        //getNotification tiene dos valores, que ambos seran cargados de la plataforma de Firebase, si los valores no son nulos
        // signifca que podemos recuperar los TAG por defecto, desde Firebase, el titulo y el cuerpo.
        if(remoteMessage.getNotification()!=null){
            Log.e("TAG", "Titulo: "+remoteMessage.getNotification().getTitle());
            Log.e("TAG", "Cuerpo: "+remoteMessage.getNotification().getBody());
        }
        */

        if(remoteMessage.getData().size()>0){
           String titulo = remoteMessage.getData().get("titulo");
           String detalle = remoteMessage.getData().get("detalle");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                createNotification(titulo, detalle);
            }
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                createNotification2();
            }

        }


    }

    private void createNotification2() {
    }

    private void createNotification(String titulo, String detalle){
        String id="mensaje";
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(id, "nuevo", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(true);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        builder.setAutoCancel(true).setWhen(System.currentTimeMillis())
                .setContentTitle(titulo).setSmallIcon(R.drawable.carwash_redondo)
                .setContentText(detalle)
                .setColor(Color.GREEN)
                .setContentIntent(sendNotification())
                .setContentInfo("nuevo");
        Random random = new Random();
        int id_notification = random.nextInt(8000);

        assert notificationManager != null;
        notificationManager.notify(id_notification,builder.build());
    }

    public PendingIntent sendNotification(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("color", "rojo");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this,0,intent,0);
    }
}
