package com.chrisvanry.weightlosscontest.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisvanry.weightlosscontest.R;
import com.chrisvanry.weightlosscontest.data.Competition;
import com.chrisvanry.weightlosscontest.data.User;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;

import java.util.ArrayList;
import java.util.List;

// TODO max member count - disable join button

public class CompDetails extends AppCompatActivity {

    private static final String TAG = "CompDetailsActivity";

    private User currentUser;
    private String currentUserId;
    private Competition currentComp;
    private String compDetailsId;
    private String currentCompName;
    private String currentCompLength;
    private String currentCompMemberCount;
    private TextView textViewCurrentComp;
    private ProgressBar progressBar;
    private Button buttonJoinComp;
    private Button buttonRecordWeight;

    // Firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;;

    // MP Android Chart
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comp_details);

        // Receive string from previous activity
        Intent intent = getIntent();
        compDetailsId = intent.getStringExtra("comp_id");
        Log.d(TAG, "Comp ID received: " + compDetailsId);

        // Nav buttons
        Button buttonHome = findViewById(R.id.buttonHome);
        Button buttonSettings = findViewById(R.id.buttonSettings);
        Button buttonLogout = findViewById(R.id.buttonLogout);

        textViewCurrentComp = findViewById(R.id.textViewCurrentComp);
        lineChart = findViewById(R.id.lineChart);
        buttonJoinComp = findViewById(R.id.buttonJoinComp);
        buttonRecordWeight = findViewById(R.id.buttonRecordWeight);

        // progress bar
        progressBar = findViewById(R.id.progress);

        // Firebase stuff
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        currentUserId = user.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        // show progress bar while loading
        progressBar.setVisibility(View.VISIBLE);

        myRef.addValueEventListener(new ValueEventListener() {
            // Reads database once on creation and each time a change is made
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get current user and comp ID and name
                currentUser = getUserData(dataSnapshot);
                currentComp = getCompData(dataSnapshot);
                currentCompName = currentComp.getName();
                currentCompLength = currentComp.getLength();
                currentCompMemberCount = currentComp.getMemberCount();

                Log.d(TAG, "currentCompName: " + currentCompName);
                Log.d(TAG, "currentCompLength: " + currentCompLength);
                Log.d(TAG, "currentCompMemberCount: " + currentCompMemberCount);

                // array list of comp member userIDs
                ArrayList<String> compMemberIds = getCompMembers(dataSnapshot);

                // array list of entries and names
                ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

                // iterate userIDs and retrieve name and entry data, write to line date set
                int i = 0;
                int colorArray[] = {Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, Color.DKGRAY, Color.MAGENTA, Color.YELLOW};
                for (String compMemberId : compMemberIds) {

                    // comp member full name
                    User compMember = getUserData(dataSnapshot, compMemberId);
                    String compMemberName = compMember.getFirstName() + " " + compMember.getLastName();

                    // get member entries
                    ArrayList<Entry> compMemberEntries = getCompEntries(dataSnapshot, compMemberId);

                    // write to line data set
                    LineDataSet lineDataSet = new LineDataSet(compMemberEntries, compMemberName);

                    // set color for each
                    lineDataSet.setCircleColors(colorArray[i]);
                    lineDataSet.setColor(colorArray[i]);
                    i++;

                    lineDataSet.setLineWidth(2);
                    lineDataSet.setValueTextSize(10);

                    // write sets to array
                    lineDataSets.add(lineDataSet);

                }

                // Display comp name
                progressBar.setVisibility(View.GONE);
                textViewCurrentComp.setText(currentCompName);

                // display chart
                showChart(lineDataSets);

                // Show or hide record weight button
                String userCompId = currentUser.getCompetitionId();
                String notEnrolled = "not enrolled";
                // User comp enrollment status
                if (userCompId.equals(notEnrolled)){
                    // If user not enrolled, show join button
                    buttonJoinComp.setVisibility(View.VISIBLE);
                } else if (userCompId.equals(compDetailsId)){
                    // If user enrolled in this comp, show record weight button
                    buttonRecordWeight.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // OnClick listener for home button
        buttonHome.setOnClickListener(v -> {
            // direct to home activity
            Intent newIntent = new Intent(getApplicationContext(), Home.class);
            startActivity(newIntent);
            finish();
        });

        // OnClick listener for settings button
        buttonSettings.setOnClickListener(v -> {
            // direct to comp list activity
            Intent newIntent = new Intent(getApplicationContext(), Settings.class);
            startActivity(newIntent);
        });

        // OnClick listener for logout button
        buttonLogout.setOnClickListener(v -> {
            // Logout and redirect to login screen
            FirebaseAuth.getInstance().signOut();
            Intent newIntent = new Intent(getApplicationContext(), Login.class);
            startActivity(newIntent);
            finish();
        });

        // OnClick listener for record weight button
        buttonRecordWeight.setOnClickListener(v -> {
            // direct to record weight activity
            Intent newIntent = new Intent(getApplicationContext(), RecordWeight.class);
            startActivity(newIntent);
        });

        // OnClick listener for join comp button
        buttonJoinComp.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            // Join button clicked
                            joinCurrentComp(currentUserId, compDetailsId, currentCompMemberCount);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            // Cancel button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Join Competition");
            builder.setMessage("Join " + currentCompName + "?").setPositiveButton("Join", dialogClickListener)
                    .setNegativeButton("Cancel", dialogClickListener).show();
        });

    }

    // get user data for current user
    private User getUserData(DataSnapshot dataSnapshot) {

        User currentUser = new User();

        currentUser.setFirstName(dataSnapshot.child("Users").child(currentUserId).child("firstName").getValue().toString());
        currentUser.setLastName(dataSnapshot.child("Users").child(currentUserId).child("lastName").getValue().toString());
        currentUser.setEmail(dataSnapshot.child("Users").child(currentUserId).child("email").getValue().toString());
        currentUser.setCompetitionId(dataSnapshot.child("Users").child(currentUserId).child("competitionId").getValue().toString());

        // display all the information
        Log.d(TAG, "showData User: First name: " + currentUser.getFirstName());
        Log.d(TAG, "showData User: Last name: " + currentUser.getLastName());
        Log.d(TAG, "showData User: Email: " + currentUser.getEmail());
        Log.d(TAG, "showData User: Comp ID: " + currentUser.getCompetitionId());

        return currentUser;
    }

    // overloaded user object function - to get user data for each comp member
    private User getUserData(DataSnapshot dataSnapshot, String userId) {

        User currentUser = new User();

        currentUser.setFirstName(dataSnapshot.child("Users").child(userId).child("firstName").getValue().toString());
        currentUser.setLastName(dataSnapshot.child("Users").child(userId).child("lastName").getValue().toString());
        currentUser.setEmail(dataSnapshot.child("Users").child(userId).child("email").getValue().toString());
        currentUser.setCompetitionId(dataSnapshot.child("Users").child(userId).child("competitionId").getValue().toString());

        // display all the information
        Log.d(TAG, "showData User: First name: " + currentUser.getFirstName());
        Log.d(TAG, "showData User: Last name: " + currentUser.getLastName());
        Log.d(TAG, "showData User: Email: " + currentUser.getEmail());
        Log.d(TAG, "showData User: Comp ID: " + currentUser.getCompetitionId());

        return currentUser;
    }

    private Competition getCompData(DataSnapshot dataSnapshot) {

        Competition currentComp = new Competition();

        currentComp.setOwnerId(dataSnapshot.child("Competitions").child(compDetailsId).child("ownerId").getValue().toString());
        currentComp.setName(dataSnapshot.child("Competitions").child(compDetailsId).child("name").getValue().toString());
        currentComp.setStartDate(dataSnapshot.child("Competitions").child(compDetailsId).child("startDate").getValue().toString());
        currentComp.setLength(dataSnapshot.child("Competitions").child(compDetailsId).child("length").getValue().toString());
        currentComp.setMemberCount(dataSnapshot.child("Competitions").child(compDetailsId).child("memberCount").getValue().toString());
        currentComp.setCompetitionId(dataSnapshot.child("Competitions").child(compDetailsId).child("competitionId").getValue().toString());


        // display all the information
        Log.d(TAG, "showData Comp: Owner ID: " + currentComp.getOwnerId());
        Log.d(TAG, "showData Comp: Comp name: " + currentComp.getName());
        Log.d(TAG, "showData Comp: Start date: " + currentComp.getStartDate());
        Log.d(TAG, "showData Comp: Length: " + currentComp.getLength());
        Log.d(TAG, "showData Comp: memberCount: " + currentComp.getMemberCount());
        Log.d(TAG, "showData Comp: Comp ID: " + currentComp.getCompetitionId());

        return currentComp;
    }

    private void joinCurrentComp(String userID, String compDetailsId, String currentCompMemberCount) {

        // Create first weight entry with 0 value
        String week1 = "1";
        myRef.child("Entries").child(compDetailsId).child(userID).child(week1).setValue("0");

        // Increase comp member count and update value
        int count = Integer.parseInt(currentCompMemberCount);
        count++;
        currentCompMemberCount = Integer.toString(count);
        myRef.child("Competitions").child(compDetailsId).child("memberCount").setValue(currentCompMemberCount);

        // write compID to user branch
        myRef.child("Users").child(userID).child("competitionId").setValue(compDetailsId).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    toastMessage("Competition enrollment successful");

                    // Redirect to home screen
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                    finish();

                } else {
                    toastMessage("Competition enrollment failed - try again");
                }
            }
        });

    }

    private ArrayList<String> getCompMembers(DataSnapshot dataSnapshot) {

        ArrayList<String> compMembers = new ArrayList<>();

        for (DataSnapshot snapshot : dataSnapshot.child("Entries").child(compDetailsId).getChildren()) {

            // get key (user ID)
            String compMemberId = snapshot.getKey();
            Log.d(TAG, "memberID: " + compMemberId);
            compMembers.add(compMemberId);

        }
        Log.d(TAG, "compMembers: " + compMembers.toString());
        return compMembers;
    }

    private ArrayList<Entry> getCompEntries(DataSnapshot dataSnapshot, String singleUserId) {

        ArrayList<Entry> compEntries = new ArrayList<>();

        // variables used for weight entry
        String weekNum;
        String weight;

        DataSnapshot parentSnapshot = dataSnapshot.child("Entries").child(compDetailsId).child(singleUserId);

        for (DataSnapshot childSnapshot : dataSnapshot.child("Entries").child(compDetailsId).child(singleUserId).getChildren()) {

            // get week number and weight
            weekNum = childSnapshot.getKey();
            weight = parentSnapshot.child(weekNum).getValue(String.class);
            Log.d(TAG, "user: " + singleUserId + "| week: " + weekNum + " | weight: " + weight);

            // write to array
            float xFloat = Float.parseFloat(weekNum);
            float yFloat = Float.parseFloat(weight);
            Log.d(TAG, "xFloat: " + xFloat);
            Log.d(TAG, "yFloat: " + yFloat);

            // add float values to array
            compEntries.add(new Entry(xFloat, yFloat));
        }

        Log.d(TAG, "user: " + singleUserId + "array: " + compEntries.toString());
        return compEntries;
    }

    private void showChart(ArrayList<ILineDataSet> lineDataSets) {

        int compLength = Integer.parseInt(currentCompLength);

        LineData lineData = new LineData(lineDataSets);
        lineChart.setData(lineData);

        // format description
        Description description = new Description();
        description.setText("Weight (lbs)");
        description.setTextColor(Color.BLACK);
        description.setTextSize(12);
        lineChart.setDescription(description);

        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(true);
        lineChart.setBorderWidth(2);

        // legend
        Legend legend = lineChart.getLegend();
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(12f);
        legend.setFormSize(10f);
        legend.setXEntrySpace(15f);
        legend.setFormToTextSpace(8f);

        XAxis xAxis = lineChart.getXAxis();
        YAxis yAxisLeft = lineChart.getAxisLeft();
        YAxis yAxisRight = lineChart.getAxisRight();

        xAxis.setAxisMaximum(compLength);
        xAxis.setAxisMinimum(1);
        xAxis.setLabelCount(compLength, true);
        yAxisLeft.setDrawLabels(false);

        lineChart.invalidate();
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}