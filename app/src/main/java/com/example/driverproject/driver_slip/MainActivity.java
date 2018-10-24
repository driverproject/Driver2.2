package com.example.driverproject.driver_slip;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    EditText emailText;
    EditText passText;
    ProgressBar progressBar;
    ImageView imageView;
    Bitmap bitmap;
    private void userLogin()
    {

        String email=emailText.getText().toString().trim();
        String password=passText.getText().toString().trim();
        if(email.isEmpty())
        {
            emailText.setError("Email required");
            emailText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailText.setError("Enter a valid email");
            return;
        }
        if(password.isEmpty())
        {
            passText.setError("Password Required");
            passText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful())
                {
                    finish();
                    startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                }
                else
                {
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.button).setOnClickListener(this);
        FirebaseApp.initializeApp(MainActivity.this);
        mAuth=FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }

        emailText=(EditText)findViewById(R.id.editText);
        passText=(EditText)findViewById(R.id.editText2);
        progressBar=(ProgressBar)findViewById(R.id.progressBar3);
        imageView=(ImageView)findViewById(R.id.imageView);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.d1);
        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(),bitmap);
        roundedBitmapDrawable.setCircular(true);
        imageView.setImageDrawable(roundedBitmapDrawable);


    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button:
                userLogin();
                break;

        }

    }
}