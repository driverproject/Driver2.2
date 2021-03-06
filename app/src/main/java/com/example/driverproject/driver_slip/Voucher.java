package com.example.driverproject.driver_slip;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Voucher extends AppCompatActivity {

    Toolbar toolbar;
    Button btn_get_sign, mClear, mGetSign, mCancel, submitButton;
    boolean btnFlag;
    File file;
    Dialog dialog;
    LinearLayout mContent;
    View view;
    signature mSignature;
    Bitmap bitmap, bitmap_sign;
    ImageView image, image_sign;
    TextView slipId, renterName, vehicleType, vehicleNumber, startReading, endReading, pickupAddress, dropAddress, totalDist, billtext;

    private Button btnChoose, btnUpload;
    private ImageView imageView;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseUser user;

    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference signRef;
    String formattedDate;
    String folder_random;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

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

        Integer stkm = Integer.parseInt(start_kms);
        Integer enkm = Integer.parseInt(end_kms);

        int bill;
        bill = (enkm - stkm) * 10;
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c);
        folder_random = UUID.randomUUID().toString();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        signRef = storageReference.child("images/" + user.getUid().toString() + "/" + formattedDate + "/" + folder_random + "/signimage");
        int startkmsnum = Integer.parseInt(start_kms);

        int endkmsnum = Integer.parseInt(end_kms);
        btnFlag=true;
        int totaltravel = (endkmsnum - startkmsnum);
        image = (ImageView) findViewById(R.id.signatureImage);
        // Setting ToolBar as ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
        textView4=(TextView)findViewById(R.id.textView4);

        textView5=(TextView)findViewById(R.id.textView5);

        textView6=(TextView)findViewById(R.id.textView6);

        textView7=(TextView)findViewById(R.id.textView7);

        textView8=(TextView)findViewById(R.id.textView8);

        textView9 = (TextView) findViewById(R.id.textView9);*/

        slipId = (TextView) findViewById(R.id.slipId);
        renterName = (TextView) findViewById(R.id.renterName);
        vehicleType = (TextView) findViewById(R.id.vehicleType);
        vehicleNumber = (TextView) findViewById(R.id.vehicleNumber);
        startReading = (TextView) findViewById(R.id.startReading);
        endReading = (TextView) findViewById(R.id.endReading);
        pickupAddress = (TextView) findViewById(R.id.pickupAddress);
        dropAddress = (TextView) findViewById(R.id.dropAddress);
        totalDist = (TextView) findViewById(R.id.totalDist);
        billtext = (TextView) findViewById(R.id.bill);


        btnUpload = (Button) findViewById(R.id.btnUpload);
        imageView = (ImageView) findViewById(R.id.TollSlipImage);
        image_sign = (ImageView) findViewById(R.id.signatureImage);
        btn_get_sign = (Button) findViewById(R.id.signature);
        submitButton = (Button) findViewById(R.id.submit_button);

        slipId.setText("Voucher Number: " + slipId);

        vehicleType.setText("Vehicle Type:  " + vehicle_Type);

        vehicleNumber.setText("Vehicle Number:  " + vehicle_Number);

        renterName.setText("Renter Name: " + rentersName);

        startReading.setText("Start KMS:  " + start_kms);

        endReading.setText("End KMS:  " + end_kms);

        totalDist.setText("Total travel:   " + Integer.toString(totaltravel) + " kms");

        pickupAddress.setText("PickUp Address: " + pickadd);

        dropAddress.setText("Drop Address: " + dropadd);

        billtext.setText("Amount Payable: Rs." + bill);

        dialog = new Dialog(Voucher.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        dialog.setCancelable(true);

        btn_get_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Function call for Digital Signature
                dialog_action();

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnFlag) {
                    chooseImage();
                } else {
                    uploadImage();
                }
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

    }

    private void submit() {

        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
        finish();

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                btnFlag = false;
                btnUpload.setText("UPLOAD");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if (filePath != null) {


            Bitmap bitmap = ((BitmapDrawable) image_sign.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data1 = baos.toByteArray();
            UploadTask uploadTask = signRef.putBytes(data1);


            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + user.getUid().toString() + "/" + formattedDate + "/" + folder_random + "/slipimage");
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(Voucher.this, "Uploaded Image and Signature", Toast.LENGTH_SHORT).show();
                            btnUpload.setText("CHOOSE");
                            btnFlag=true;

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Voucher.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    public void dialog_action() {

        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) dialog.findViewById(R.id.clear);
        mGetSign = (Button) dialog.findViewById(R.id.getsign);
        mGetSign.setEnabled(false);
        mCancel = (Button) dialog.findViewById(R.id.cancel);
        view = mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Bitmap b;
                Log.v("log_tag", "Panel Saved");
                view.setDrawingCacheEnabled(true);
                b = mSignature.save(view);
                image.setImageBitmap(b);
                dialog.dismiss();
                //Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                // Calling the same class
                //recreate();

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                dialog.dismiss();
                // Calling the same class

            }
        });
        dialog.show();
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public Bitmap save(View v) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                //FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);
                Toast.makeText(Voucher.this, "Passing Image", Toast.LENGTH_LONG).show();
                // Convert the output file to Image such as .png
                //bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                // mFileOutStream.flush();
                //mFileOutStream.close();

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
            return bitmap;

        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}