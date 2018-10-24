package com.example.driverproject.driver_slip;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    public ArrayList<Vehicle> myDataset;
    public ArrayList<DataSnapshot> dslist;
    private int year, month, day;

    public static final int REQUEST_PERM_WRITE_STORAGE = 102;

    //    RecyclerView recyclerView;
//    ProfileAdapter profileAdapter;
    Context context;
    private Button btnCalendar;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2 + 1, arg3);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
//
//        vehicleList = new ArrayList<>();
//        recyclerView= (RecyclerView)findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        profileAdapter = new ProfileAdapter(this,vehicleList);
//        recyclerView.setAdapter(profileAdapter);

        myDataset = new ArrayList<>();
        dslist = new ArrayList<>();
        context = ProfileActivity.this;

//        fetchButton = (Button) findViewById(R.id.fetchButton);
//        fetchButton.setOnClickListener(this);

        btnCalendar = (Button) findViewById(R.id.btnCalender);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Vehicle/" + user.getUid().toString());
        showDate(year, month + 1, day);

        dl = (DrawerLayout) findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.myprofile) {
                    Toast.makeText(ProfileActivity.this, "Your Profile !", Toast.LENGTH_SHORT).show();
                }

                if (id == R.id.signout) {
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

                if (id == R.id.editprofile) {
                    startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
                }

                if (id == R.id.settings) {
                    Toast.makeText(ProfileActivity.this, "Settings !", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });


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

    public void floatButton(View view) {
        Toast.makeText(this, "New Slip ", Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        Intent intent = new Intent(getApplicationContext(), Slip.class);
        bundle.putString("VehicleType", "");
        bundle.putString("VehicleNumber", "");
        bundle.putString("dateofjourney", "");
        bundle.putString("start", "");
        bundle.putString("end", "");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showDate(int year, int month, int day) {
        String date = String.format("%02d", month) + "/" + String.format("%02d", day) + "/" + String.format("%02d", year);
        //String date = month+"/"+day+"/"+year;
        btnCalendar.setText(date);

        test(date);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return abdt.onOptionsItemSelected(menuItem) || super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.fetchButton: {
//
//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot vehicleSnapshot : dataSnapshot.getChildren()) {
//
//                            Vehicle v = vehicleSnapshot.getValue(Vehicle.class);
//                            Toast.makeText(ProfileActivity.this, v.getVehicle_Type(), Toast.LENGTH_SHORT).show();
//                            vehicleList.add(v);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
        }
    }

    public void test(final String date) {


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);

//        myDataset.add("one");
//        myDataset.add("two");
//        myDataset.add("three");
//        myDataset.add("four");
//        myDataset.add("five");
//        myDataset.add("six");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myDataset.clear();
                dslist.clear();
                Log.d("EXACTLY", dataSnapshot.getChildrenCount() + "");

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //Log.d("EXACTLY", ds.getValue().toString());
                    dslist.add(ds);
                    Vehicle v = ds.getValue(Vehicle.class);
//                    ds.getRef().removeValue();
                    Log.d("EXACTLY", date + " " + v.getDate_journey());
                    if (("Date: " + date).equals(v.getDate_journey())) {
                        Log.d("EXACTLY", v.getVehicle_Type());
                        myDataset.add(v);
                    }
                }
                mAdapter = new MyAdapter(myDataset);
                recyclerView.setAdapter(mAdapter);
                recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, final int position) {
                        TextView txt = (TextView) view.findViewById(R.id.textViewS);

                        Log.d("NAVIN", "" + txt.getText());
                        Button ebtn = (Button) view.findViewById(R.id.buttonE);
                        ebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dslist.get(position).getRef().removeValue();

                                Toast.makeText(getApplicationContext(), "kahitari", Toast.LENGTH_LONG).show();
                                Log.d("EXACTLY", "hotNahir");
                                Vehicle v = myDataset.get(position);
                                Bundle bundle = new Bundle();
                                Intent intent = new Intent(getApplicationContext(), Slip.class);
                                bundle.putString("VehicleType", v.getVehicle_Type());
                                bundle.putString("VehicleNumber", v.getVehicle_Number());
                                bundle.putString("dateofjourney", v.getDate_journey());
                                bundle.putString("start", v.getStart_kms());
                                bundle.putString("end", v.getEnd_kms());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });

                        Button dbtn = (Button) view.findViewById(R.id.buttonD);
                        dbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Vehicle v = myDataset.get(position);
                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                        PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(ProfileActivity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERM_WRITE_STORAGE);
                                } else {
                                    // PDF Generation
                                    Toast.makeText(ProfileActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();
                                    Document doc = new Document();
                                    try {
                                        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF";
                                        //Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
                                        File dir = new File(path);
                                        if (!dir.exists()) {
                                            dir.mkdirs();
                                        }
                                        Log.d("PDFCreator", "PDF Path: " + path);

                                        //pdf of name invoice + vehicleType + vehicleNumber
                                        File file = new File(dir, "invoice" + v.getVehicle_Type() + v.getVehicle_Number() + ".pdf");
                                        FileOutputStream fout = new FileOutputStream(file);

                                        PdfWriter.getInstance(doc, fout);
                                        doc.open();

                                        //Toast.makeText(MainActivity.this, "Pressed", Toast.LENGTH_LONG).show();

                                        Paragraph p = new Paragraph("Prime Travels");

                                        p.setAlignment(Paragraph.ALIGN_CENTER);
                                        doc.add(p);

                                        LineSeparator lineSeparator = new LineSeparator();
                                        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 100));
                                        doc.add(new Chunk(lineSeparator));

                                        Paragraph title = new Paragraph("Trip Details");
                                        title.setAlignment(Paragraph.ALIGN_CENTER);
                                        doc.add(title);

                                        Paragraph date = new Paragraph("Receipt Date: " + Calendar.getInstance().getTime());
                                        date.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(date);

                                        Paragraph driverName = new Paragraph("Driver Name: " + "Ramesh");
                                        driverName.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(driverName);

                                        Paragraph vehicleType = new Paragraph("Vehicle Type: " + "Innova");
                                        vehicleType.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(vehicleType);

                                        Paragraph renterName = new Paragraph("Renter's Name: " + "Parag");
                                        renterName.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(renterName);

                                        Paragraph pickAdd = new Paragraph("Address: " + "PICT, Dhankawadi, Pune- 411027");
                                        pickAdd.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(pickAdd);

                                        Paragraph vehicleNo = new Paragraph("Vehicle Number: " + "MH12AE9110");
                                        vehicleNo.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(vehicleNo);

                                        //Section End
                                        doc.add(new Chunk(lineSeparator));

                                        Paragraph stkm = new Paragraph("Starting Reading(Km): " + "350");
                                        stkm.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(stkm);

                                        Paragraph endkm = new Paragraph("End Reading(Km): + " + "400");
                                        endkm.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(endkm);

                                        Paragraph total = new Paragraph("Total kilometers Travelled(Km): " + "50");
                                        total.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(total);

                                        Paragraph rate = new Paragraph("Rate(Rs.): " + "10");
                                        rate.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(rate);

                                        Paragraph taxes = new Paragraph("taxes(10%): " + "50");
                                        rate.setAlignment(Paragraph.ALIGN_LEFT);
                                        doc.add(rate);

                                        Paragraph bill = new Paragraph(("Total Amount to Pay(Rs.): " + "550"));
                                        bill.setAlignment(Paragraph.ALIGN_CENTER);
                                        doc.add(bill);

                                        //Section End
                                        doc.add(new Chunk(lineSeparator));

                                        Paragraph documents = new Paragraph("Uploaded Documents");
                                        documents.setAlignment(Paragraph.ALIGN_CENTER);
                                        doc.add(documents);


                                        PdfPTable table = new PdfPTable(3);
                                        table.setWidthPercentage(100);


                                        String vname = v.getVehicle_Type();

                                        if (vname.equals("innova")) {
                                            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                                            Bitmap bitmap1 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.imgsign);
                                            bitmap1.compress(Bitmap.CompressFormat.JPEG, 10, stream1);
                                            Image myimg1 = Image.getInstance(stream1.toByteArray());
                                            table.addCell(myimg1);
                                        } else {
                                            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                                            Bitmap bitmap1 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.img_sign);
                                            bitmap1.compress(Bitmap.CompressFormat.JPEG, 10, stream2);
                                            Image myimg1 = Image.getInstance(stream2.toByteArray());
                                            table.addCell(myimg1);

                                        }
                                        ByteArrayOutputStream stream3 = new ByteArrayOutputStream();

                                        Bitmap bitmap2 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.imgtoll);
                                        bitmap2.compress(Bitmap.CompressFormat.JPEG, 10, stream3);
                                        Image myimg2 = Image.getInstance(stream3.toByteArray());
                                        table.addCell(myimg2);

                                        ByteArrayOutputStream stream4 = new ByteArrayOutputStream();
                                        Bitmap bitmap3 = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.driver1);
                                        bitmap3.compress(Bitmap.CompressFormat.JPEG, 10, stream4);
                                        Image myimg3 = Image.getInstance(stream4.toByteArray());
                                        table.addCell(myimg3);

                                        table.addCell("Sign");
                                        table.addCell("Toll 1");
                                        table.addCell("Toll 2");
                                        doc.add(table);
                                        doc.add(new Chunk(lineSeparator));


                                        //Toast.makeText(this, "Created...", Toast.LENGTH_SHORT).show();
                                    } catch (DocumentException de) {
                                        // de.printStackTrace();
                                        Log.e("PDFCreator", "Document Exception: " + de);
                                    } catch (IOException ioe) {
                                        //ioe.printStackTrace();
                                        Log.e("PDFCreator", "IO Exception: " + ioe);
                                    } finally {
                                        doc.close();
                                    }

                                }
                            }
                        });
                        switch (view.getId()) {
                            case R.id.textViewS: {

                            }
                        }
//                        Vehicle vehicle = myDataset.get(position);
//                        Toast.makeText(getApplicationContext(), vehicle.getVehicle_Type() + " is selected!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }

                }));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private ArrayList<Vehicle> mDataset;

        public MyAdapter(ArrayList<Vehicle> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_rv, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Vehicle v = mDataset.get(position);
            holder.cardText.setText(v.getVehicle_Type());

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView cardText;
            public Button dbtn, ebtn;

            public MyViewHolder(View v) {
                super(v);
                cardText = (TextView) v.findViewById(R.id.textViewS);
                dbtn = (Button) v.findViewById(R.id.buttonD);
                ebtn = (Button) v.findViewById(R.id.buttonE);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.buttonD: {
                                int pos = getAdapterPosition();
                                Toast.makeText(itemView.getContext(), "hehe", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
            }
        }
    }
}
