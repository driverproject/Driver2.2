package com.example.driverproject.driver_slip;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    EditText email_id,pass;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email_id=findViewById(R.id.editText);
        pass=findViewById(R.id.editText2);
        findViewById(R.id.button_1).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        progressBar=(ProgressBar)findViewById(R.id.progressBar2);
        findViewById(R.id.textView3).setOnClickListener(this);


    }

    private void register()
    {
        String email=email_id.getText().toString().trim();
        String password=pass.getText().toString().trim();
        if(email.isEmpty())
        {
            email_id.setError("Email required");
            email_id.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            email_id.setError("Enter a valid email");
            return;
        }
        if(password.isEmpty())
        {
            pass.setError("Password Required");
            pass.requestFocus();
            return;
        }
        if(password.length()<6)
        {
            pass.setError("Password should have length greater than 6");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(),"Registered",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "Already Registered", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_1:
                register();
                break;
            case R.id.textView3:
                finish();
                startActivity(new Intent(this,MainActivity.class));
                break;
        }
    }
}
