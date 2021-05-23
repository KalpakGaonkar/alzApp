package com.lf.appcare;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationManager;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.TextView;

public class MainActivityPatient extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;


    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private LatLng currentPosition;
    LocationManager locationManager;
    private DatabaseReference db;
    private Button signOut, createReminder, removeReminder, emergency;
    private PatientUser patient;
    private FirebaseUser user;
    public String userUid;
    Double currLat, currLng;
    public float radius;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    String patientUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_patient);

        // Location permission for the geofencing feature
        getLocationPermission();
//        getDeviceLocation();
//        if(currentPosition.latitude!=0 && currentPosition.longitude!=0){
//        GeofenceHandler.getGeofencingRequest("GEOFENCE_ID", currentPosition, radius);}
//        user = auth.getCurrentUser();
//        userUid = user.getUid();

//        getDeviceLocation();
        db = FirebaseDatabase.getInstance().getReference();

//        db.child("users").child(patient.getUid()).child("locCoordinates").setValue(currentPosition);

        Toolbar toolbar =  findViewById(R.id.toolbar);

        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);


//        // Enable a receiver
//        Context context = getApplicationContext();
//        ComponentName receiver = new ComponentName(context, EmergencyReceiver.class);
//        PackageManager pm = context.getPackageManager();
//
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println("user id");
        System.out.println(user.getUid());
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText("Patient ID:");
        TextView textView3 = (TextView) findViewById(R.id.textView3);
        textView3.setText(user.getUid());



        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivityPatient.this, StartupActivity.class));
                    finish();
                }
            }
        };

        db = FirebaseDatabase.getInstance().getReference();
        db.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                patient = dataSnapshot.getValue(PatientUser.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        db.child("geofences").child(patient.getCaregiverUid()).child(patient.getUid()).child("radius").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                radius = (float) dataSnapshot.getValue();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        createReminder =  findViewById(R.id.menuCreateReminder);
        removeReminder =  findViewById(R.id.menuRemoveReminder);
        signOut = findViewById(R.id.menuSignOut);
        emergency = findViewById(R.id.emergencyButton);

        createReminder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View view)
            {
                Intent intent = new Intent(getApplicationContext(), CreateReminderNameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        removeReminder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View view)
            {
                Intent intent = new Intent(getApplicationContext(), ReminderListPatientActivity.class);
                startActivity(intent);
                finish();
            }
        });


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(MainActivityPatient.this);
            }
        });

        emergency.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View view)
            {
                db = FirebaseDatabase.getInstance().getReference();

                System.out.println("patient uid: " + patient.getUid());
                System.out.println("caregiver uid: " + patient.getCaregiverUid());
                if (!patient.getCaregiverUid().isEmpty())
                {
                    db.child("emergencyRequest").child(patient.getUid()).child(patient.getCaregiverUid()).child("emergencyType").setValue("emergencyButton");
                    Toast.makeText(getApplicationContext(), R.string.emergencyMessage, Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(), R.string.emergencyMessageError, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onResume() {
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
//            Toast.makeText(this, "Location permissions granted", Toast.LENGTH_SHORT).show();

//            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//            try
//            {
//                final Task location = mFusedLocationProviderClient.getLastLocation();
//                location.addOnCompleteListener(new OnCompleteListener()
//                {
//                    @Override
//                    public void onComplete(@NonNull Task task)
//                    {
//                        if (task.isSuccessful())
//                        {
//                            System.out.println("onComplete: found location");
//                            if (task.getResult() != null)
//                            {
//                                Location currentLocation = (Location) task.getResult();
//                                currentPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//
//                            }
//                            else
//                            {
//                                System.out.println("onComplete: current location is null");
//                            }
//                        }
//                        else
//                        {
//                            System.out.println("onComplete: current location is null");
//                            Toast.makeText(MainActivityPatient.this, R.string.get_location_error, Toast.LENGTH_LONG).show();
//                        }
//
//                    }
//                });
//            }
//            catch (SecurityException e){
//                System.out.println("getDeviceLocation: SecurityException: " + e.getMessage() );
//            }
//            catch (NullPointerException e){
//                System.out.println("getDeviceLocation: NullPointerException: " + e.getMessage() );
//            }
        }
    }
//    private void getDeviceLocation()
//    {
//
//
//        System.out.println("Getting device current location");
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//        try
//        {
//            final Task location = mFusedLocationProviderClient.getLastLocation();
//            location.addOnCompleteListener(new OnCompleteListener()
//            {
//                @Override
//                public void onComplete(@NonNull Task task)
//                {
//                    if (task.isSuccessful())
//                    {
//                        System.out.println("onComplete: found location");
//                        if (task.getResult() != null)
//                        {
//                            Location currentLocation = (Location) task.getResult();
//                            currentPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//
//                            System.out.println(currentPosition);
//                            currLat = currentPosition.latitude;
//                            currLng = currentPosition.longitude;
////                            db.child("users").child(user.getUid()).child("lat").setValue(currentPosition.latitude);
////                            db.child("users").child(user.getUid()).child("lng").setValue(currentPosition.longitude);
//                        }
//                        else
//                        {
//                            System.out.println("onComplete: current location is null");
//                        }
//                    }
//                    else
//                    {
//                        System.out.println("onComplete: current location is null");
//                        Toast.makeText(MainActivityPatient.this, R.string.get_location_error, Toast.LENGTH_LONG).show();
//                    }
//
//                }
//            });
//        }
//        catch (SecurityException e){
//            System.out.println("getDeviceLocation: SecurityException: " + e.getMessage() );
//        }
//        catch (NullPointerException e){
//            System.out.println("getDeviceLocation: NullPointerException: " + e.getMessage() );
//        }
//    }

}