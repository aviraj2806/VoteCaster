package com.shweta.votecaster.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shweta.votecaster.R;
import com.shweta.votecaster.databse.VoteDatabase;
import com.shweta.votecaster.databse.VoteEntity;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private TextView txtBack, txtRes;
    private BarChart chart;
    private ScrollView svRes;
    private SharedPreferences sharedPreferences;
    private boolean isCan1 = false;
    private boolean isCan2 = false;
    private boolean isCan3 = false;
    private boolean isFirstTime = true;
    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    final long vote[] = new long[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txtBack = findViewById(R.id.txtResBack);
        txtRes = findViewById(R.id.txtRes);
        chart = findViewById(R.id.barchartResults);
        svRes = findViewById(R.id.svRes);
        database = FirebaseDatabase.getInstance();
        sharedPreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);

        svRes.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(ResultActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        final DatabaseReference ref1 = database.getReference(sharedPreferences.getString("can1",""));
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    Log.d("hello","1can0");
                    vote[0] = 0;
                }else {
                    Log.d("hello","1can"+dataSnapshot.getValue());
                    vote[0] = (long) dataSnapshot.getValue();
                }
                isCan1 = true;
                loadData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference ref2 = database.getReference(sharedPreferences.getString("can2",""));
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    vote[1] = 0;
                    Log.d("hello","2can0");
                }else {
                    Log.d("hello","2can"+dataSnapshot.getValue());
                    vote[1] = (long) dataSnapshot.getValue();
                }
                isCan2 = true;
                loadData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference ref3 = database.getReference(sharedPreferences.getString("can3",""));
        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    vote[2] = 0;
                    Log.d("hello","3can0");
                }else {
                    Log.d("hello","3can"+dataSnapshot.getValue());
                    vote[2] = (long) dataSnapshot.getValue();
                }
                isCan3 = true;
                loadData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ResultActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }

    public void loadData(){
        if(isCan1 && isCan2 && isCan3 & isFirstTime){
            isFirstTime = false;
            long max = vote[0];
            long winner_id = 1;
            for (int i = 0; i < 3; i++) {
                if (max < vote[i]) {
                    max = vote[i];
                    winner_id = i;
                }
            }

            int max_count = 0;
            boolean isTie = false;
            for(int i=0;i<3;i++){
                if(max == vote[i]){
                    max_count++;
                }
            }
            if(max_count>1) {
                isTie = true;
            }

            if (vote[0] == 0 && vote[1] == 0 && vote[2] == 0) {
                txtRes.setText("No votes yet.");
            } else if(isTie){
                txtRes.setText("It's a tie.");
            }else {
                if (winner_id == 0) {
                    txtRes.setText(sharedPreferences.getString("can1","") + " won the elections with " + vote[0] + " Vote.");
                } else if (winner_id == 1) {
                    txtRes.setText(sharedPreferences.getString("can2","") + " won the elections with " + vote[1] + " Vote.");
                } else if (winner_id == 2) {
                    txtRes.setText(sharedPreferences.getString("can3","") + " won the elections with " + vote[2] + " Vote.");
                }
            }

            chart.getXAxis().setLabelsToSkip(0);
            chart.getAxisRight().setEnabled(false);
            chart.setDescription("");

            ArrayList noOfVotes = new ArrayList();
            noOfVotes.add(new BarEntry(vote[0], 0));
            noOfVotes.add(new BarEntry(vote[1], 1));
            noOfVotes.add(new BarEntry(vote[2], 2));

            ArrayList candidates = new ArrayList();
            candidates.add(sharedPreferences.getString("can1", "1"));
            candidates.add(sharedPreferences.getString("can2", "2"));
            candidates.add(sharedPreferences.getString("can3", "3"));

            BarDataSet bardataset = new BarDataSet(noOfVotes, "No. of votes");
            chart.animateY(1000);
            BarData data = new BarData(candidates, bardataset);
            bardataset.setColor(getResources().getColor(R.color.colorAccent));
            chart.setData(data);

            progressDialog.dismiss();
            svRes.startAnimation(AnimationUtils.loadAnimation(ResultActivity.this, R.anim.push_up_in));
            svRes.setVisibility(View.VISIBLE);
        }
    }
}
