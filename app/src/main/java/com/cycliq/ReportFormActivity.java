package com.cycliq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.cycliq.CommonClasses.Constants;

//implementing onclicklistener
public class ReportFormActivity extends AppCompatActivity implements View.OnClickListener {

    //View Objects
    private Button btnSubmit;
    private EditText txtComments, txtSubject, txtCategory;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_form);

        getSupportActionBar().hide();

        setViews();

        setListener();

    }

    private void setListener() {

        btnSubmit.setOnClickListener(this);

    }

    private void setViews() {

//        txtSubject = (EditText) findViewById(R.id.txtSubject);
//
//        txtCategory = (EditText) findViewById(R.id.txtCategory);
//
//        txtComments = (EditText) findViewById(R.id.txtComments);

//        btnSubmit = (Button) findViewById(R.id.btnSubmit);
//
//        btnBack = (ImageButton) findViewById(R.id.btnBack);

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (view == btnSubmit) {

            finish();

        }

        if (view == btnBack) {

            finish();

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        System.exit(0);

    }
}