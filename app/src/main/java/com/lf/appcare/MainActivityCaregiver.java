package com.lf.appcare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class MainActivityCaregiver extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Button signOut, patientList, reminderList, geofenceList, patientDetailsList;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private List<PatientUser> patientList1;
    private Button randomButton;
    private String flag;
    private String count;
    private int patient = 0;
    private String patientUid, patientDocUid;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_caregiver);
//        NotificationScheduler.showNotification(getApplicationContext(), MainActivityCaregiver.class,
//                "Hello", "Welcome uesr");

        // Location permission for the geofencing feature
        getLocationPermission ();

        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        setSupportActionBar(toolbar);


        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        // Subscribe for notifications
        FirebaseMessaging.getInstance().subscribeToTopic("caregiver");



        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userUid1 = user.getUid();
        System.out.println("curruserid");
        System.out.println(userUid1);


        TextView textView10 = (TextView) findViewById(R.id.textView10);
        textView10.setText("Caregiver ID:");
        TextView textView11 = (TextView) findViewById(R.id.textView11);
        textView11.setText(userUid1);


        DatabaseReference reff = db.child("users").child(userUid1).child("patientCount");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                db.child("users").child(auth.getCurrentUser().getUid()).child("patientCount").setValue("no");
                count = dataSnapshot.getValue().toString();
                if(count=="no"){return;}
                else{
                    DatabaseReference refff = db.child("patientDocConnection").child(userUid1).child("patientDocId");
                    refff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            patientDocUid = dataSnapshot.getValue().toString();
                            System.out.println("this is patientdocuid");
                            System.out.println(patientDocUid);
//                            Intent intent1 = new Intent(MainActivityCaregiver.this, ViewPatientDetails.class);
//                            intent1.putExtra("thisIsPatientId", patientDocUid);
//                            startActivity(intent1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                System.out.println("this is patient uid");
                System.out.println(patientUid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//


        authListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null)
                {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivityCaregiver.this, StartupActivity.class));
                    finish();
                }
                else
                {
                    String userUid = user.getUid();
                    DatabaseReference ref = db.child("users").child(userUid);
                    ref.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            CaregiverUser user = dataSnapshot.getValue(CaregiverUser.class);
                            if (user == null)
                                return;

                            String userType = user.getUserType();
                            patientList1 = user.getPatientList();
                            System.out.println("This is patient list");
                            System.out.println(patientList1);

                            System.out.println("User type: " + userType +
                                    "\nUser name: " + user.getFirstName() +
                                    "\nUID: " + user.getUid() +
                                    "\nPatientID " + user.getPatientList());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                }
            }
        };

        patientList = findViewById(R.id.connectToPatientButton);
        reminderList = findViewById(R.id.remindersButton);
        geofenceList = findViewById(R.id.geofencesButton);
        patientDetailsList = findViewById(R.id.patientDetailsListButton);
        signOut =  findViewById(R.id.sign_out);
//        randomButton = findViewById(R.id.random_button);

        DatabaseReference ref = db.child("z");
        ref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                flag = dataSnapshot.getValue().toString();
                System.out.println("Flag status");
                System.out.println(flag);

                if("enter".equals(flag)){
                    NotificationScheduler.showNotification(getApplicationContext(), MainActivityCaregiver.class,
                            "Transition ENTER", "Patient has entered geofence");
                    db.child("z").setValue("empty");

                }
                if("exit".equals(flag)){
                    NotificationScheduler.showNotification(getApplicationContext(), MainActivityCaregiver.class,
                            "Transition EXIT", "Patient has exited geofence");
                    db.child("z").setValue("empty");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        patientList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivityCaregiver.this, PatientListActivity.class));
                finish();
            }
        });

        reminderList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivityCaregiver.this, ReminderListCaregiverActivity.class));
                finish();
            }
        });

        geofenceList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivityCaregiver.this, GeofenceListActivity.class));
                finish();
            }
        });

        patientDetailsList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(count=="no"){
                    Toast.makeText(MainActivityCaregiver.this, "You have not added a patient", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    Intent intent1 = new Intent(MainActivityCaregiver.this, ViewPatientDetails.class);
                    intent1.putExtra("thisIsThePatientId", patientDocUid);
                    startActivity(intent1);
                    finish();}
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(MainActivityCaregiver.this);
            }
        });




    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
    }


//    private

    private void getLocationPermission ()
    {
        System.out.println("Getting location permissions");
        String [] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        else
        {
            System.out.println("Location permissions already granted");
        }
    }
}