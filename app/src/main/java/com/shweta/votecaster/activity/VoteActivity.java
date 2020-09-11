package com.shweta.votecaster.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shweta.votecaster.R;
import com.shweta.votecaster.databse.VoteDatabase;
import com.shweta.votecaster.databse.VoteEntity;

public class VoteActivity extends AppCompatActivity {

    private TextView txtBack,txtName1,txtName2,txtName3;
    private CardView cv1,cv2,cv3;
    private ScrollView scVote;
    private SharedPreferences sharedPreferences;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        txtBack = findViewById(R.id.txtVoteBack);
        txtName1 = findViewById(R.id.txtVoteName1);
        txtName2 = findViewById(R.id.txtVoteName2);
        txtName3 = findViewById(R.id.txtVoteName3);
        cv1 = findViewById(R.id.cvVote1);
        cv2 = findViewById(R.id.cvVote2);
        cv3 = findViewById(R.id.cvVote3);
        scVote = findViewById(R.id.svVote);
        sharedPreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);

        scVote.setVisibility(View.GONE);
        txtBack.setVisibility(View.GONE);
        txtBack.startAnimation(AnimationUtils.loadAnimation(VoteActivity.this,R.anim.push_left_in));
        scVote.startAnimation(AnimationUtils.loadAnimation(VoteActivity.this,R.anim.push_up_in));
        scVote.setVisibility(View.VISIBLE);
        txtBack.setVisibility(View.VISIBLE);

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                castVote(cv1,txtName1,1);
            }
        });

        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                castVote(cv2,txtName2,2);
            }
        });

        cv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                castVote(cv3,txtName3,3);
            }
        });

        sharedPreferences.edit().putString("can1",txtName1.getText().toString()).apply();
        sharedPreferences.edit().putString("can2",txtName2.getText().toString()).apply();
        sharedPreferences.edit().putString("can3",txtName3.getText().toString()).apply();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(VoteActivity.this,HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void castVote(CardView cardView, final TextView textView, final int id){
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(VoteActivity.this);
                dialog.setCancelable(false);
                dialog.setMessage("Are you sure you want\nto cast vote for "+textView.getText().toString()+" ?\nThis can't be changed later.");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VoteEntity voteEntity = new VoteEntity(sharedPreferences.getString("mobile",""),
                                id,textView.getText().toString());
                        Room.databaseBuilder(VoteActivity.this, VoteDatabase.class,"vote")
                                .allowMainThreadQueries().build().voteDao().insertVote(voteEntity);

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        final DatabaseReference ref = firebaseDatabase.getReference(textView.getText().toString());
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.getValue() == null){
                                    ref.setValue(1);
                                    isFirst = false;
                                }else if(isFirst){
                                    long count = (long) dataSnapshot.getValue();
                                    ref.setValue(count+1);
                                    isFirst = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(sharedPreferences.getString("mobile",""),null,
                                "Your vote have been noted.\nThanks for voting "+textView.getText().toString()+".",null,null);

                        showSuccess();
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.create();
                dialog.show();
            }
        });
    }

    public void showSuccess(){

        ProgressDialog progressDialog = new ProgressDialog(VoteActivity.this);
        progressDialog.setMessage("Vote Successfully Casted,\nKeep voting!\nYou are beign redirected to Home Page");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onBackPressed();
            }
        },1000);
    }
}
