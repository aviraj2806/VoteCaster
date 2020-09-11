package com.shweta.votecaster.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shweta.votecaster.R;
import com.shweta.votecaster.databse.UserDatabase;
import com.shweta.votecaster.databse.VoteDatabase;
import com.shweta.votecaster.databse.VoteEntity;
import com.shweta.votecaster.dialog.EditDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements EditDialog.OnEditProfile {

    private ScrollView svHome;
    private TextView txtName,txtMobile,txtEmail,txtEnroll,txtClass,txtYear;
    private ImageView imgHome,imgEdit,imgOut;
    private SharedPreferences sharedPreferences;
    private LinearLayout llCast,llResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        svHome = findViewById(R.id.svHome);
        txtName = findViewById(R.id.txtHomeName);
        txtMobile = findViewById(R.id.txtHomeMobile);
        txtEmail = findViewById(R.id.txtHomeEmail);
        txtEnroll = findViewById(R.id.txtHomeEnroll);
        txtClass = findViewById(R.id.txtHomeClass);
        txtYear = findViewById(R.id.txtHomeYear);
        imgHome = findViewById(R.id.imgHome);
        llCast = findViewById(R.id.llCastVote);
        llResult = findViewById(R.id.llResults);
        imgEdit = findViewById(R.id.imgHomeEdit);
        imgOut = findViewById(R.id.imgLogOut);
        sharedPreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);

        svHome.setVisibility(View.GONE);

        final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        txtName.setText(getSharedPrefData("name"));
        txtMobile.setText(getSharedPrefData("mobile"));
        txtEmail.setText(getSharedPrefData("email"));
        txtEnroll.setText("Enrol. No. : "+getSharedPrefData("enroll"));
        txtClass.setText("Class : "+getSharedPrefData("class"));
        txtYear.setText("Year : "+getSharedPrefData("year"));

        Picasso.get().load(getSharedPrefData("image")).error(R.drawable.avatar).into(imgHome, new Callback() {
            @Override
            public void onSuccess() {
                svHome.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this,R.anim.push_up_in));
                svHome.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }

            @Override
            public void onError(Exception e) {
                svHome.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this,R.anim.push_up_in));
                svHome.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
        });

        llCast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VoteEntity isVoted = Room.databaseBuilder(HomeActivity.this,VoteDatabase.class,"vote")
                        .allowMainThreadQueries().build().voteDao().isVoted(getSharedPrefData("mobile"));
                if (isVoted == null) {
                    Intent intent = new Intent(HomeActivity.this, VoteActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    finish();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
                    dialog.setCancelable(false);
                    dialog.setMessage("You have already casted a Vote!\nCome back next year.");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.create();
                    dialog.show();
                }

            }
        });

        llResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<VoteEntity> getVote = Room.databaseBuilder(HomeActivity.this, VoteDatabase.class,"vote")
                        .allowMainThreadQueries().build().voteDao().getAllVotes();
                if(getVote.isEmpty()){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
                    dialog.setCancelable(false);
                    dialog.setMessage("Please cast your vote before checking results.");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.create();
                    dialog.show();
                }else{
                    Intent intent = new Intent(HomeActivity.this, ResultActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    finish();
                }
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new EditDialog(getSharedPrefData("year"),getSharedPrefData("class"),HomeActivity.this);
                dialogFragment.show(getSupportFragmentManager(),"EditDialog");
            }
        });

        imgOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().putBoolean("isLoggedIn",false).apply();
                Intent intent = new Intent(HomeActivity.this,AuthActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public String getSharedPrefData(String key){
        return sharedPreferences.getString(key,"");
    }

    public void makeErrorToast(String text, EditText editText, String hint){
        View view = LayoutInflater.from(this).inflate(R.layout.toast,null);
        Toast toast = new Toast(this);
        TextView textView = view.findViewById(R.id.toast_text);
        textView.setText(text);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
        if(editText != null) {
            editText.setText(null);
            editText.setHint(hint);
            editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
            editText.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake));
            editText.clearFocus();
        }
    }

    @Override
    public void onEdit(String sClass, String year) {
        txtClass.setText("Class : "+sClass);
        txtYear.setText("Year : "+year);
        Room.databaseBuilder(HomeActivity.this, UserDatabase.class,"user")
                .allowMainThreadQueries().build().userDao().editUserClass(sClass,getSharedPrefData("mobile"));
        Room.databaseBuilder(HomeActivity.this, UserDatabase.class,"user")
                .allowMainThreadQueries().build().userDao().editUserYear(year,getSharedPrefData("mobile"));

        makeErrorToast("Edit Successful",null,"");
    }
}
