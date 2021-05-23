package com.lf.appcare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewPatientDetails extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference db;
    private List<PatientUser> patientList;
    private ListView patientListView;
    private CaregiverUser caregiver;
    private List<DataSnapshot> usersSnapshot = new ArrayList<>();
    private String patientUid;
    private String patientDocUid, patientDocId;
    ImageView imageView;
    private List<String> emailList = new ArrayList<>();
    private FirebaseUser user;
    private AutoCompleteTextView textView;
    private ArrayAdapter<PatientUser> arrayAdapterUser;
    private String caregiverUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Updates");

        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        imageView = findViewById(R.id.imageView3);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TextView textView4 = (TextView) findViewById(R.id.textView4);
        textView4.setText("Doctor's Updates");
        textView4.setTextColor(Color.BLACK);
        TextView textView7 = (TextView) findViewById(R.id.textView7);
        textView7.setText("Alzheimer's Stage");
        textView4.setTextColor(Color.BLACK);
        TextView textView9 = (TextView) findViewById(R.id.textView9);
        textView9.setText("MRI Scan");
        textView4.setTextColor(Color.BLACK);
//        final String userUid = auth.getCurrentUser().getUid();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
//        final String userUid = auth.getCurrentUser().getUid();
//        DatabaseReference reff = db.child("users").child(userUid);

        final String userUid = auth.getCurrentUser().getUid();
        System.out.println("User id");
        System.out.println(userUid);
//        DatabaseReference reff = db.child("users");
//        reff.child(userUid).child(userUid).child()
//        Intent intent = getIntent();
        Intent intent = getIntent();
        patientDocUid = intent.getStringExtra("thisIsPatientId");
        System.out.println("Patient id");
        System.out.println(patientDocUid);

//        DatabaseReference refff = db.child("patientDocConnection").child(patientDocUid).child("patientDocId");
//        refff.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                patientDocId = dataSnapshot.getValue().toString();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        if(patientDocUid!=null){

        DatabaseReference ref = db.child("patients").child(patientDocUid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String updates = dataSnapshot.child("patientUpdates").getValue().toString();
                System.out.println("Patient Pid");
                TextView textView5 = (TextView) findViewById(R.id.textView5);
                textView5.setText(updates);
                System.out.println(patientUid);
                String alzStage = dataSnapshot.child("alzStage").getValue().toString();
                TextView textView8 = (TextView) findViewById(R.id.textView8);
                textView8.setText(alzStage);
                String link = dataSnapshot.child("imageUrl").getValue(String.class);

                Picasso.get().load(link).fit().centerInside().noFade().placeholder( R.drawable.progress_animation ).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });}
        else{return;}


    }
    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(ViewPatientDetails.this, PatientDetailsListActivity.class));
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home)
        {
            startActivity(new Intent(getApplicationContext(), MainActivityCaregiver.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}