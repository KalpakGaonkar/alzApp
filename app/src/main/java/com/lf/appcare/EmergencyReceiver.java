package com.lf.appcare;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class EmergencyReceiver extends BroadcastReceiver {


    private DatabaseReference db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    public void onReceive(Context context, Intent intent) {

        KeyEvent ke = (KeyEvent)intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
        user = auth.getCurrentUser();
        String userUid = user.getUid();
        auth = FirebaseAuth.getInstance();

        if (ke .getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            System.out.println("I got volume up event");
        }else if (ke .getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            System.out.println("I got volume key down event");
        }

    }        // TODO: This method is called when the BroadcastReceiver is receiving

}
