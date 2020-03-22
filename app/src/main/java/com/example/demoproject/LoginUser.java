package com.example.demoproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import FireBaseObjects.FirebaseRegisterUser;

public class LoginUser extends AppCompatActivity {
    static final String LOG_TAG = LoginUser.class.getSimpleName();

    EditText etUsername, etPassword;
    Button btnLogin;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private DatabaseReference reference;
    String uname, pswd;
    ProgressBar pgBar;
    TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

//        if (firebaseAuth != null) {
//            finish();
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        pgBar = findViewById(R.id.pgBar);
        pgBar.setVisibility(View.GONE);
        tvMessage = findViewById(R.id.tvMessage);
        tvMessage.setVisibility(View.GONE);

        reference = FirebaseDatabase.getInstance().getReference();
        //   ifWrongCredential(false);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }


    public void loginUser() {
        if (etUsername.getText().toString().trim().isEmpty()) {
            Toast.makeText(LoginUser.this, "Please enter user name", Toast.LENGTH_SHORT).show();
        } else if (etPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(LoginUser.this, "Please enter Password", Toast.LENGTH_SHORT).show();
        } else {
            uname = etUsername.getText().toString().trim();
            pswd = etPassword.getText().toString().trim();
            final Query query = reference.child("register_users");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Log.d(LOG_TAG, "CHECKPOINT 3");
                    for (DataSnapshot user : dataSnapshot.getChildren()) {

                        FirebaseRegisterUser usersBean = user.getValue(FirebaseRegisterUser.class);
                        if (usersBean.getRegister_user_name().equals(uname) && usersBean.getRegister_password().equals(pswd)) {
                            ifWrongCredential(true);
                            Log.d(LOG_TAG, "PROGRESSBAR 1");
                            Intent intent = new Intent(LoginUser.this, MainActivity.class);
                            startActivity(intent);
                            Log.d(LOG_TAG, "USER NAME" + uname);
                            Log.d(LOG_TAG, "USER PASSWORD" + pswd);
                            Toast.makeText(LoginUser.this, "success", Toast.LENGTH_SHORT).show();
                        } else {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ifWrongCredential(false);
                                    Log.d(LOG_TAG, "PROGRESSBAR 2");
                                }
                            }, 2000);


                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(LoginUser.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(LOG_TAG, "WRONG CREDENTIALS");
                }
            });


            Log.d(LOG_TAG, "CHECKPOINT 8");

        }
    }

    public void ifWrongCredential(boolean authorised) {
        pgBar.setVisibility(View.VISIBLE);
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText("Please Wait...");

        Log.d(LOG_TAG, "PROGRESSBAR 3");

        if (authorised) {
            pgBar.setVisibility(View.GONE);
            tvMessage.setVisibility(View.GONE);
            Log.d(LOG_TAG, "PROGRESSBAR 4");
        } else {
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pgBar.setVisibility(View.VISIBLE);
                    tvMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText("Wrong Username and Password");
                    Log.d(LOG_TAG, "PROGRESSBAR 5");
                }
            });

//                }
//            }, 2000);
//            Log.d(LOG_TAG, "PROGRESSBAR 6");

        }

    }
}
