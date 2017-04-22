package com.example.android.gdgnjproject4_quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ResultsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_activity);

        Intent intentExtras = getIntent();
        Bundle bundle = intentExtras.getExtras();
        if (bundle != null) {
            String result = bundle.getString("results");
            if (result != null) {
                TextView tv = (TextView)findViewById(R.id.results);
                if (tv != null) {
                    tv.setText(result);
                }
            }
        }


    }
}
