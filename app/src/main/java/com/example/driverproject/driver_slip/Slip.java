package com.example.driverproject.driver_slip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Slip extends AppCompatActivity implements View.OnClickListener{

    int stkm, endkm;
    private TextView date_entry;
    DatabaseReference databaseVehicle;
    FirebaseAuth mauth;
    private Calendar calender;
    private boolean flag;
    private EditText vehicle, vehicleNumber, rName, startkms, endkms, padd, dadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slip);

        //new changes
        Intent receive = getIntent();
        Bundle bundle = receive.getExtras();
        String vehicle_Type = bundle.getString("VehicleType");
        String vehicle_Number = bundle.getString("VehicleNumber");
        String date_journey = bundle.getString("dateofjourney");
        String rentersName = bundle.getString("rname");
        String start_kms = bundle.getString("start");
        String end_kms = bundle.getString("end");
        String pickadd = bundle.getString("padd");
        String dropadd = bundle.getString("dadd");
        flag = false;
        long date = System.currentTimeMillis();

    //hello

        databaseVehicle= FirebaseDatabase.getInstance().getReference("Vehicle");

        vehicle=(EditText)findViewById(R.id.editTextVehicleType);

        vehicleNumber=(EditText)findViewById(R.id.editTextVehicleNumber);

        date_entry=(TextView) findViewById(R.id.editTextDate);

        rName = (EditText) findViewById(R.id.rentersName);

        startkms=(EditText)findViewById(R.id.editTextKMSS);

        endkms=(EditText)findViewById(R.id.editTextKMSP);

        padd = (EditText) findViewById(R.id.pickupAddress);

        dadd = (EditText) findViewById(R.id.dropAddress);


        if (!vehicle_Number.equals("")) {
            vehicle.setText(vehicle_Type);
            vehicleNumber.setText(vehicle_Number);
            startkms.setText(start_kms);
            endkms.setText(end_kms);
        }

        SimpleDateFormat sdf = new SimpleDateFormat(" MM/dd/yyyy");
        String dateString = sdf.format(date);

        date_entry.setText("Date:"+dateString);


        findViewById(R.id.buttonSave).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);

        mauth=FirebaseAuth.getInstance();

    }


    @Override
    protected void onPause() {

        super.onPause();
        if (!flag) {

        }
    }

    public void saveData()
    {


        String vehicle_Type=vehicle.getText().toString().trim();
        String vehicle_Number=vehicleNumber.getText().toString();
        String date_journey=date_entry.getText().toString();
        String start_kms=startkms.getText().toString();
        String end_kms=endkms.getText().toString();
        String p_add = padd.getText().toString();
        String d_add = dadd.getText().toString();
        String renter_name = rName.getText().toString();

        if(vehicle_Type.isEmpty())
        {
            vehicle.setError("Enter Vehicle Type");
            vehicle.requestFocus();
            return;
        }
        if(vehicle_Number.isEmpty())
        {
            vehicleNumber.setError("Enter Vehicle Number");
            vehicleNumber.requestFocus();
            return;
        }
        if(start_kms.isEmpty())
        {
            startkms.setError("Fill start kms");
            startkms.requestFocus();
            return;
        }
        if(end_kms.isEmpty())
        {
            endkms.setError("Fill end kms");
            endkms.requestFocus();
            return;
        }

        int s=Integer.parseInt(start_kms);
        int e=Integer.parseInt(end_kms);
        if(s>e)
        {

            startkms.setError("");
            endkms.setError("");


        }
        else
        {

            String user_id = mauth.getCurrentUser().getUid();
            String id = databaseVehicle.push().getKey();
            Vehicle v = new Vehicle( vehicle_Type, vehicle_Number, date_journey, start_kms, end_kms);
            databaseVehicle.child(user_id).push().setValue(v);
            Toast.makeText(this, "Added details", Toast.LENGTH_LONG).show();
            Bundle bundle = new Bundle();
            Intent intent = new Intent(this, Voucher.class);
            bundle.putString("VehicleType", vehicle_Type);
            bundle.putString("VehicleNumber", vehicle_Number);
            bundle.putString("Dateofjourney", date_journey);
            bundle.putString("start", start_kms);
            bundle.putString("end", end_kms);
            bundle.putString("rname", renter_name);
            bundle.putString("padd", p_add);
            bundle.putString("dadd", d_add);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.buttonSave:
                saveData();
                flag = true;
                break;
            case R.id.button6:
                saveData();
                break;

        }

    }

}
