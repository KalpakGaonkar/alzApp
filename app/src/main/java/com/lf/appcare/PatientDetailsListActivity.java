package com.lf.appcare;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientDetailsListActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference db;
    private List<PatientUser> patientList;
    private ListView patientListView;
    private CaregiverUser caregiver;
    private List<DataSnapshot> usersSnapshot = new ArrayList<>();
    private List<DataSnapshot> geofencesSnapshot = new ArrayList<>();
    private List<Geofence> geofenceList = new ArrayList<>();
    private List<String> emailList = new ArrayList<>();
    private FirebaseUser user;
    private AutoCompleteTextView textView;
    private String patientDocUid;
    private ArrayAdapter<PatientUser> arrayAdapterUser;
    private Map<String,String> patientNames = new HashMap<>();
    private List<String> patientUids = new ArrayList<>();
    private String caregiverUid;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_details_list);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.patient_list));
//        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        auth = FirebaseAuth.getInstance();



        db = FirebaseDatabase.getInstance().getReference();
        if (auth.getCurrentUser() == null)
        {
            startActivity(new Intent(PatientDetailsListActivity.this, StartupActivity.class));
            finish();
        }

        // Get the caregiver patient list from the DB
        user = auth.getCurrentUser();
        final String userUid = user.getUid();
//
//        DatabaseReference reff = db.child("geofences").child(userUid);
//        reff.addValueEventListener(new ValueEventListener()
//        {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot)
//            {
//                geofenceList = new ArrayList<>();
//                patientUids = new ArrayList<>();
//                geofencesSnapshot = Lists.newArrayList (dataSnapshot.getChildren().iterator());
//
//                for (DataSnapshot geofenceSnapshot : geofencesSnapshot)
//                {
//                    Geofence geofence = geofenceSnapshot.getValue(Geofence.class);
//                    geofenceList.add(geofence);
//                    patientUids.add(geofenceSnapshot.getKey());
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError)
//            {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });

        DatabaseReference ref = db.child("users").child(userUid);
        ref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                caregiver = dataSnapshot.getValue(CaregiverUser.class);
                if (user == null)
                    return;
                patientList = caregiver.getPatientList();
                System.out.println("List of pa");

                System.out.println(patientList);
                GetPatientNames(patientUids);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        ref = db.child("users");
        ref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                emailList = new ArrayList<>();
                System.out.println("READING FROM DB");
                usersSnapshot = Lists.newArrayList (dataSnapshot.getChildren().iterator());
                System.out.println("User snapshot");
                System.out.println(usersSnapshot);

                for (DataSnapshot userSnapshot: usersSnapshot)
                {
                    AppCareUser user = userSnapshot.getValue(AppCareUser.class);
                    if (user.getUserType().equals(AppCareUser.PATIENT)) { emailList.add (user.getEmail());
                        System.out.println(user.getUid());
                        patientUids.add(user.getUid());
                        System.out.println(patientUids);}
                }
                GetPatientNames(patientUids);
                System.out.println("patientuids");
                System.out.println(patientUids);
                System.out.println("Email list: " + Arrays.toString(emailList.toArray()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        // Set lister when clicking an item from the patient list
        patientListView = findViewById(R.id.patientListView);
        patientListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id)
            {
                System.out.println("This pos");
                System.out.println(patientUids.get(position));
                PatientUser patient = patientList.get(position);
                System.out.println("patientlist");
                System.out.println(patient);



                DatabaseReference reff = db.child("patientDocConnection").child(patientUids.get(position));
                reff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        patientDocUid = dataSnapshot.child("patientDocId").getValue().toString();
                        System.out.println("Doc id");
                        System.out.println(patientDocUid);
                        Intent intent1 = new Intent(PatientDetailsListActivity.this, ViewPatientDetails.class);
                        intent1.putExtra("thisIsPatientId", patientDocUid);
                        startActivity(intent1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


//                finish();
//                Intent intent = new Intent(PatientDetailsListActivity.this, ViewPatientDetails.class);
//                intent.putExtra("patientUid", patientUids.get(position));

//                startActivity(new Intent(PatientDetailsListActivity.this, ViewPatientDetails.class));
//                intent.putExtra("patientUid", patientUids.get(position));
//                startActivity(intent);
//                finish();
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
//                builder.setMessage(getString(R.string.remove_patient_message, patient.getFirstName()))
//                        .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id)
//                            {
//                                int response = caregiver.RemovePatient(patient);
//                                Toast.makeText(getApplicationContext(), R.string.patient_removed,
//                                        Toast.LENGTH_SHORT)
//                                        .show();
//                            }
//                        })
//                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
//                        {
//                            public void onClick(DialogInterface dialog, int id)
//                            {
//                            }
//                        });
//                // Create the AlertDialog object
//                AlertDialog dialog = builder.create();
//                dialog.show();
//
//                // Configure the buttons
//                Button posButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
//                Button negButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
//                posButton.setTextColor(getResources().getColor(R.color.colorPrimary));
//                negButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });

        // Get list of patient emails from the database


        SharedPreferences myPreferences
                = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        caregiverUid = myPreferences.getString("UID", "");

        // "Add patient" button
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                final Dialog dialog = new Dialog(view.getContext());
//                dialog.setContentView(R.layout.dialog_add_patient);
//                dialog.setTitle(R.string.connect_to_patient_dialog);
//
//                Button searchButton = dialog.findViewById(R.id.searchButton);
//                searchButton.setOnClickListener(new View.OnClickListener()
//                {
//                    @Override
//                    public void onClick(View view)
//                    {
//                        int response;
//                        String email = textView.getText().toString();
//                        if (!emailList.contains(email))
//                        {
//                            Toast.makeText(getApplicationContext(), R.string.add_patient_not_found,
//                                    Toast.LENGTH_SHORT)
//                                    .show();
//                            // ADD ERROR MESSAGE
//                            System.out.println("EMAIL NOT FOUND");
//                        }
//                        else
//                        {
//                            for (DataSnapshot userSnapshot : usersSnapshot)
//                            {
//                                Map<String,Object> user = (Map<String,Object>) userSnapshot.getValue();
//                                System.out.println(user.toString());
//                                if (user.get("userType").equals(AppCareUser.PATIENT) && user.get("email").equals(email))
//                                {
//                                    PatientUser patient = userSnapshot.getValue(PatientUser.class);
//                                    response = caregiver.AddPatient(patient);
//
//                                    Toast.makeText(getApplicationContext(), R.string.add_patient_success,
//                                            Toast.LENGTH_SHORT)
//                                            .show();
//                                    break;
//                                }
//                            }
//                            // CHECK RESPONSE
//                            dialog.cancel();
//                        }
//                    }
//                });
//
//                Button cancelButton = dialog.findViewById(R.id.cancelButton);
//                cancelButton.setOnClickListener(new View.OnClickListener()
//                {
//                    @Override
//                    public void onClick(View view)
//                    {
//                        dialog.cancel();
//                    }
//                });
//
//
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_dropdown_item_1line, emailList);
//                textView = dialog.findViewById(R.id.patientEmail);
//                textView.setThreshold(1);
//                textView.setAdapter(adapter);
//
//                dialog.show();
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), MainActivityCaregiver.class);
            startActivity(intent);
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }


    private void GetPatientNames(final List<String> patientUids)
    {
        if (!patientUids.isEmpty())
        {
            DatabaseReference ref = db.child("users");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    List<DataSnapshot> usersSnapshot = Lists.newArrayList(dataSnapshot.getChildren().iterator());
                    for (DataSnapshot userSnapshot : usersSnapshot)
                    {
                        AppCareUser user = userSnapshot.getValue(AppCareUser.class);
                        String userUid = user.getUid();
                        if (user.getUserType().equals(AppCareUser.PATIENT) && patientUids.contains(userUid))
                        {
                            patientNames.put(userUid, user.getFirstName());
                            System.out.println("New patient found: " + user.getFirstName());
                            System.out.println("New patient id: "+userUid);
                        }
                    }

                    ListPatients();
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
        // If there are no patient uids
        // empty the hashmap
        else
        {
            patientNames = new HashMap<>();
            ListPatients();
        }
    }

    private void ListPatients ()
    {
        if (patientList == null)
        {
            System.out.println("NO PATIENTLIST REFERENCE");
            patientListView.setVisibility(View.GONE);
        }
        else if (patientList.isEmpty())
        {
            System.out.println("EMPTY PATIENT LIST");
            patientListView.setVisibility(View.GONE);
        }
        else
        {
            System.out.println("THERE ARE " + patientList.size() + " PATIENTS");
            patientListView.setVisibility(View.VISIBLE);
            arrayAdapterUser = new ArrayAdapter<PatientUser>(this, android.R.layout.simple_list_item_2, android.R.id.text1, patientList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent)
                {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = view.findViewById(android.R.id.text1);
                    TextView text2 = view.findViewById(android.R.id.text2);

                    text1.setText(patientList.get(position).getFirstName());
                    text2.setText(patientList.get(position).getEmail());
                    return view;
                }
            };
            patientListView.setAdapter(arrayAdapterUser);
        }
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(PatientDetailsListActivity.this, MainActivityCaregiver.class));
        finish();
    }
}