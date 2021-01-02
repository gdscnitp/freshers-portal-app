package com.dscnitp.freshersportal.Student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dscnitp.freshersportal.R;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        update = (Button) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity();
            }
        });

    }

    public void openActivity() {
        Intent intent = new Intent(this, ProfileFragment.class);
        TextInputEditText name= (TextInputEditText) findViewById(R.id.name);
        String text = name.getText().toString();
        TextInputEditText roll= (TextInputEditText) findViewById(R.id.roll);
        String text1 = roll.getText().toString();
        TextInputEditText branch= (TextInputEditText) findViewById(R.id.branch);
        String text2 = branch.getText().toString();
        TextInputEditText year= (TextInputEditText) findViewById(R.id.year);
        String text3 = year.getText().toString();

        intent.putExtra("mytext", text);
        intent.putExtra("mytext1",text1);
        intent.putExtra("mytext2",text2);
        intent.putExtra("mytext3",text3);

        startActivity(intent);
    }
}

