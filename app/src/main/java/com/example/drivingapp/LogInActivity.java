package com.example.drivingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LogInActivity extends AppCompatActivity {
    TextView signbtn;
    Button loginbtn;
    SignInButton gsign;
    EditText emailinp, passinp;
    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        signbtn = findViewById(R.id.signbtn);
        loginbtn = findViewById(R.id.loginbtn);
        emailinp = findViewById(R.id.emailinp);
        passinp = findViewById(R.id.passinp);
        gsign = findViewById(R.id.gsign);
        auth = FirebaseAuth.getInstance();

        signbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
                finish();
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUser();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("417005806745-et61pdcrp6c9u5glh7bgopdfit67842u.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        try {
                            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                            if (account != null) {
                                Log.d("SignUpActivity", "in firebase auth with google");
                                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                                auth.signInWithCredential(credential)
                                        .addOnCompleteListener(this, task -> {
                                            if (task.isSuccessful()) {
                                                startActivity(new Intent(LogInActivity.this, DashboardActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } catch (ApiException e) {
                            Toast.makeText(this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        gsign.setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            Log.d("SignUpActivity", "launching launcher");
            signInLauncher.launch(signInIntent);
        });
    }

    protected void verifyUser() {
        if(emailinp.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailinp.getText().toString()).matches()) {
            emailinp.setError("Empty or invalid email address");
        } else if(!passinp.getText().toString().matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")) {
            passinp.setError("Invalid password");
        } else {
            auth.signInWithEmailAndPassword(emailinp.getText().toString(), passinp.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        startActivity(new Intent(LogInActivity.this, DashboardActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LogInActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
