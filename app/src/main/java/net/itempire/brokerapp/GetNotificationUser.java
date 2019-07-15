package net.itempire.brokerapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GetNotificationUser extends Service {
    private DatabaseReference mDatabase;
    SharedPreferences sharedPreferencesFB_user;
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("@servicebind", "onBind:" );
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferencesFB_user = getSharedPreferences("FBDetailsUSER",MODE_PRIVATE);

        if (sharedPreferencesFB_user.getString("id","").compareTo("")==0)
        {
            SharedPreferences.Editor editor = sharedPreferencesFB_user.edit();
            editor.putString("id",Long.toString(System.currentTimeMillis()));
            editor.commit();
            editor.apply();
        }
        notificationChecking();

        Log.e("@servicestart", "onBind:0" );
        return START_STICKY;
    }
    /*@Override
    public void onDestroy() {
        Log.e("@servicedestroy", "onBind:1" );
        mDatabase.child("User").child(sharedPreferences.getString("id", "")).child("Driver Data").child("userStatus").setValue("offline");
        super.onDestroy();
    }*/

   /* @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (sharedPreferences.getString("id", "") != null) {
            Log.e("@servicetask", "onTaskRemoved:" );
            mDatabase.child("User").child(sharedPreferences.getString("id", "")).child("Driver Data").child("userStatus").setValue("offline");
        }
        Log.e("@servicetask", "onBind:2" );
        super.onTaskRemoved(rootIntent);
    }*/

    public void notificationChecking() {
        mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("Notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("@@@FirstData", "onDataChange:===" + dataSnapshot);
                if (dataSnapshot.getValue() != null) {
                    String Fbridedetails = (String) dataSnapshot.getValue().toString();
                   /* String Notification_tetx = "Your Service provider accepted your request";*/

                    if (Fbridedetails.equals("0")) {
                        Log.e("@@@NULLDatasnap", "onDataChange:==");
                    } else if(Fbridedetails.equals("1")) {
                        Log.e("@@@DataSnapshot", "onDataChange:===" + Fbridedetails);
                        String Notification_tetx = "Your Service provider accepted your request";
                        Intent intent = new Intent(getApplicationContext(), SP_Detail_Activity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        intent.setAction("Notification.getFromBarUser");
                        intent.putExtra("openingNotiFromBarUser", "1");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            CharSequence name = "chanelname";
                            String description = "channelDiscription";
                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                            NotificationChannel channel = new NotificationChannel("1", name, importance);
                            channel.setDescription(description);
                            // Register the channel with the system; you can't change the importance
                            // or other notification behaviors after this
                            NotificationManager notificationManager = getSystemService(NotificationManager.class);
                            notificationManager.createNotificationChannel(channel);
                        }

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "1")
                                .setSmallIcon(R.drawable.green)
                                .setContentTitle("Broker App")
                                .setContentText(Notification_tetx)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        ;

                        int PROGRESS_MAX = 0;
                        int PROGRESS_CURRENT = 0;
                        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, true);


                        NotificationManagerCompat notificationManager1 = NotificationManagerCompat.from(getApplicationContext());
                        notificationManager1.notify(1, builder.build());

                       // mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("Notification").setValue("0");
                    }else if(Fbridedetails.equals("2")){
                        Log.e("@@@@", "onDataChange:==");
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

