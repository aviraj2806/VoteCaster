package com.shweta.votecaster.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shweta.votecaster.R;


public class EditDialog extends DialogFragment {

    public EditDialog(String year,String sClass, OnEditProfile onEditProfile) {
        this.year = year;
        this.sClass = sClass;
        this.onEditProfile = onEditProfile;
    }

    String year;
    String sClass;
    private OnEditProfile onEditProfile;
    private EditText etClass,etYear;
    private TextView txtBack,txtSave;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_dialog, container, false);

        etClass = view.findViewById(R.id.etEditClass);
        etYear = view.findViewById(R.id.etEditYear);
        txtBack = view.findViewById(R.id.txtEditBack);
        txtSave = view.findViewById(R.id.txtEditSave);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);

        etYear.setText(year);
        etClass.setText(sClass);

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eyear = etYear.getText().toString().trim();
                String eclass = etClass.getText().toString().trim();

                if(eyear.equals(sharedPreferences.getString("year","")) && eclass.equals(sharedPreferences.getString("class",""))){
                    makeErrorToast("Nothing to Save.",null,"");
                }else{
                    onEditProfile.onEdit(eclass,eyear);
                    dismiss();
                }
            }
        });

        return view;
    }

    public void makeErrorToast(String text, EditText editText, String hint){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.toast,null);
        Toast toast = new Toast(getActivity());
        TextView textView = view.findViewById(R.id.toast_text);
        textView.setText(text);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        if(editText != null) {
            editText.setText(null);
            editText.setHint(hint);
            editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
            editText.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.shake));
            editText.clearFocus();
        }
    }

    public interface OnEditProfile{
        void onEdit(String sClass,String year);
    }
}
