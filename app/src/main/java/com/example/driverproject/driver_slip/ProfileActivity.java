package com.example.driverproject.driver_slip;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private List<Vehicle> vehicleList;
    private DatabaseReference databaseReference;
    private Calendar calendar;
    private Button fetchButton;
    private int year, month, day;

    TextView textViewTest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fetchButton=(Button)findViewById(R.id.fetchButton);
        fetchButton.setOnClickListener(this);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        vehicleList = new ArrayList<>();
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user= firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Vehicle");

        dl=(DrawerLayout) findViewById(R.id.dl);
        abdt= new ActionBarDrawerToggle(this,dl,R.string.Open,R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id= menuItem.getItemId();

                if(id== R.id.myprofile)
                {
                    Toast.makeText(ProfileActivity.this,"Your Profile !" ,Toast.LENGTH_SHORT).show();
                }

                if(id == R.id.signout)
                {
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

                if(id == R.id.editprofile)
                {
                    startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
                }

                if(id == R.id.settings)
                {
                    Toast.makeText(ProfileActivity.this,"Settings !" ,Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });





    }

    public void floatButton(View view)
    {
        Toast.makeText(this,"New Slip ", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,Slip.class));
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                }
            };


    @Override
    public  boolean onOptionsItemSelected(MenuItem menuItem)
    {
        return abdt.onOptionsItemSelected(menuItem) || super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.fetchButton:
            {
                String user_id = firebaseAuth.getCurrentUser().getUid();
                Toast.makeText(this,user_id,Toast.LENGTH_SHORT).show();

                databaseReference.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for(DataSnapshot vehicleSnapshot : dataSnapshot.getChildren())
                        {
                            Vehicle v= vehicleSnapshot.getValue(Vehicle.class);
                            Toast.makeText(getApplicationContext(),v.getVehicle_Type(),Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(),v.getVehicle_Number(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }
        }
    }


}
