package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationPage extends AppCompatActivity {
    EditText editemail, editpass;
    Button register;
    FirebaseAuth auth;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        editemail = findViewById(R.id.email);
        editpass = findViewById(R.id.password);
        register = findViewById(R.id.register);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckValidation();
            }
        });
    }

    private void CheckValidation() {
        String mail = editemail.getText().toString();
        String password = editpass.getText().toString();

        if (mail.isEmpty()) {
            editemail.setError("Cant be Null");
        } else if (password.isEmpty()) {
            editpass.setError("Cant Be Null");
        } else if (password.length() < 8) {
            editpass.setError("Password is too short");
        } else {
            AddUser(mail, password);
        }
    }

    private void AddUser(String mail, String password) {
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();

        auth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(RegistrationPage.this, "User registered Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrationPage.this, MainActivity.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(RegistrationPage.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}