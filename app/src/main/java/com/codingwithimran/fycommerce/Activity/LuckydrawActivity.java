package com.codingwithimran.fycommerce.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codingwithimran.fycommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LuckydrawActivity extends AppCompatActivity {
    EditText name, email;
    Button submit;
    FirebaseFirestore database;
    FirebaseAuth auth;
    String u_name, user_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luckydraw);
        Toolbar toolbar = findViewById(R.id.luckydraw_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        name = findViewById(R.id.add_name);
        email = findViewById(R.id.add_email);
        submit = findViewById(R.id.submit);

        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        u_name = name.getText().toString();
        user_email = email.getText().toString();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> obj = new HashMap<>();
                obj.put("my_name", name.getText().toString());
                obj.put("my_email", email.getText().toString());
                database.collection("Luck Draw").add(obj).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LuckydrawActivity.this, "Data has been submitted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



    }
}