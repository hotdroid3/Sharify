package softwareengineering.assignment.sharify;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import android.Manifest.permission;

import java.util.Calendar;
import java.util.UUID;

;import static softwareengineering.assignment.sharify.NGOAvailableItemsFragment.CHARITY_ITEM_INFO;

public class SMAddPostActivity extends AppCompatActivity {


    private DatePickerDialog.OnDateSetListener mManDateSetListener;
    private DatePickerDialog.OnDateSetListener mExpDateSetListener;
    private CharityItemInfo charityItemInfo = new CharityItemInfo();


    private FirebaseStorage mStorage;
    private StorageReference mStorRef;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;

    private FirebaseAuth mAuth;

    public static final int GALLERY_INTENT = 2;
    private static final String TAG = "SMAddPostActivity";


    //Declare variables for item details
    private EditText itmName = null;
    private EditText itmDesc = null;
    private EditText itmManufactureDate = null;
    private EditText itmExpiryDate = null;
    private EditText itmQuantity = null;
    private EditText itmCollectionDescription = null;

    private UserInfo userInfo;
    private DatabaseReference mUserRef;

    private Button upLoadImgButton;
    private Button upLoadItemButton;

    private ImageView imageView;
    private Uri uri;
    private boolean isImgUploaded = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_add_post);

        mDatabase=FirebaseDatabase.getInstance();
        mDataRef=mDatabase.getReference();

        mStorage = FirebaseStorage.getInstance();
        mStorRef = mStorage.getReference();

        mAuth = FirebaseAuth.getInstance();



        itmName = (EditText) findViewById(R.id.item_Name);
        itmDesc = (EditText) findViewById(R.id.item_Description);
        itmManufactureDate = (EditText)findViewById(R.id.item_ManufactureDate);
        itmExpiryDate = (EditText)findViewById(R.id.item_ExpiryDate);
        itmQuantity = (EditText)findViewById(R.id.item_Quantity);
        itmCollectionDescription = (EditText)findViewById(R.id.itemCollectionDescription);

        upLoadImgButton = (Button) findViewById(R.id.uploadImg_button);
        upLoadItemButton = (Button)findViewById(R.id.upload_button);

        imageView=(ImageView)findViewById(R.id.previewImg);

        mUserRef = mDatabase.getReference();
        mUserRef = mUserRef.child("Users Information").child(mAuth.getCurrentUser().getUid());

        final ProgressDialog progDialog = new ProgressDialog(SMAddPostActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progDialog.setIndeterminate(true);
        progDialog.setMessage("Loading user data...");
        progDialog.show();
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo = dataSnapshot.getValue(UserInfo.class);
                progDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mUserRef.addValueEventListener(userListener);


        Intent intent = getIntent();
        CharityItemInfo charityItem = intent.getParcelableExtra(CHARITY_ITEM_INFO);

        if(charityItem != null)
        {
            this.isImgUploaded = true;
            final ProgressDialog pDialog = new ProgressDialog(SMAddPostActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            pDialog.setIndeterminate(true);
            pDialog.setMessage("Loading item data...");
            pDialog.show();

            final String charityItemInfoUUID = charityItem.getItemUUID();

            DatabaseReference databaseReference = mDatabase.getReference().child("Charity Items' Information").child(charityItemInfoUUID);

            ValueEventListener itemInfoListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    charityItemInfo = dataSnapshot.getValue(CharityItemInfo.class);
                    updateView(charityItemInfo);
                    pDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(SMAddPostActivity.this, "Loading error." ,Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                }
            };
            databaseReference.addListenerForSingleValueEvent(itemInfoListener);
        }



        itmManufactureDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar itmManCal = Calendar.getInstance();
                int itmManYear = itmManCal.get(Calendar.YEAR);
                int itmManMonth = itmManCal.get(Calendar.MONTH);
                int itmManDay = itmManCal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog itmManDatePickerDialog = new DatePickerDialog(
                        SMAddPostActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mManDateSetListener,
                        itmManYear, itmManMonth, itmManDay);
                itmManDatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                itmManDatePickerDialog.show();
            }
        });
        mManDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String manDate = month + 1 + "/" + day + "/" + year;
                //why +1?
                itmManufactureDate.setText(manDate);
            }
        };

        itmExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar itmManCal = Calendar.getInstance();
                int itmExpYear = itmManCal.get(Calendar.YEAR);
                int itmExpMonth = itmManCal.get(Calendar.MONTH);
                int itmExpDay = itmManCal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog itmManDatePickerDialog = new DatePickerDialog(
                        SMAddPostActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mExpDateSetListener,
                        itmExpYear, itmExpMonth, itmExpDay);
                itmManDatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                itmManDatePickerDialog.show();
            }
        });

        mExpDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String expDate = month + 1 + "/" + day + "/" + year;
                //why + 1?
                itmExpiryDate.setText(expDate);
            }
        };
        upLoadImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    // only for marshmallow and newer versions
                    if (!(checkSelfPermission(permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                        ActivityCompat.requestPermissions(SMAddPostActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                }
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_INTENT);

            }
        });

        upLoadItemButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                uploadItem();
            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == GALLERY_INTENT) && (resultCode == RESULT_OK) && (data != null) && (data.getData() != null)) {
            uri = data.getData();
            try
            {
                Picasso.with(SMAddPostActivity.this).load(uri).fit().centerCrop().into(imageView);
                imageView.setVisibility(View.VISIBLE);
            }
            catch (Exception e)
            {
                Toast.makeText(SMAddPostActivity.this, "Error loading image.",Toast.LENGTH_LONG).show();
            }

            uploadPicture();

        }
    }

    private void uploadPicture()
    {
        if (uri!=null){
            final ProgressDialog progDialog = new ProgressDialog(SMAddPostActivity.this,
                    R.style.AppTheme_Dark_Dialog);

            progDialog.setIndeterminate(true);
            progDialog.setMessage("Uploading....");
            progDialog.show();

            StorageReference storageReference = mStorage.getReference();
            storageReference = storageReference.child("Photos/" + uri.getLastPathSegment());

            UploadTask uploadTask = storageReference.putFile(uri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(SMAddPostActivity.this, "Failed uploading image.", Toast.LENGTH_LONG).show();
                    isImgUploaded = false;
                    progDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    isImgUploaded = true;
                    charityItemInfo.setImgUri(taskSnapshot.getDownloadUrl().toString());
                    //Toast.makeText(getApplicationContext(), "Upload Succeeded!", Toast.LENGTH_LONG).show();
                    progDialog.dismiss();
                }
            });
        }
    }

    private void uploadItem()
    {

        String name = itmName.getText().toString().trim();
        String desc = itmDesc.getText().toString().trim();
        final String mDate = itmManufactureDate.getText().toString().trim();
        String eDate = itmExpiryDate.getText().toString().trim();
        String qty = itmQuantity.getText().toString().trim();
        String coldesc = itmCollectionDescription.getText().toString().trim();

        if(isInputInvalid(name,desc,eDate,qty,coldesc))
        {
            onFailedSave();
        }
        else {
            upLoadItemButton.setEnabled(false);

            charityItemInfo.setItemName(name);
            charityItemInfo.setItemDescription(desc);
            if(mDate!=null)
            {
                charityItemInfo.setItemManufacturedDate(mDate);
            }
            charityItemInfo.setItemExpiryDate(eDate);
            charityItemInfo.setItemQuantity(Integer.parseInt(qty));
            charityItemInfo.setItemCollectionDescription(coldesc);
            charityItemInfo.setContactDetails(userInfo.getOrganizationContact());
            if(charityItemInfo.getItemUUID() == null)
            {
                charityItemInfo.setItemUUID(UUID.randomUUID().toString());
            }
            charityItemInfo.setItemDonatorName(userInfo.getOrganizationName());
            charityItemInfo.setItemDonatorUid(mAuth.getCurrentUser().getUid().toString());

            final ProgressDialog progDialog = new ProgressDialog(SMAddPostActivity.this,
                    R.style.AppTheme_Dark_Dialog);

            progDialog.setIndeterminate(true);
            progDialog.setMessage("Uploading....");
            progDialog.show();

            final Runnable uploadTask = new Runnable() {
                @Override
                public void run() {
                    mDataRef.child("Charity Items' Information").child(charityItemInfo.getItemUUID()).setValue(charityItemInfo);
                    progDialog.dismiss();
                }
            };
            new Thread(uploadTask).start();
            onSuccessfulSave();

        }

    }

    public void onSuccessfulSave() {
        Toast.makeText(SMAddPostActivity.this, "Successfully uploaded item.", Toast.LENGTH_LONG).show();
        upLoadItemButton.setEnabled(true);
        finish();
    }


    private boolean isInputInvalid(String name, String desc, String eDate, String qty, String coldesc)
    {
        boolean inputInvalid = false;

        if(name.isEmpty())
        {
            itmName.setError("Please enter an item name.");
            inputInvalid = true;
        }
        else{
            itmName.setError(null);
        }
        if(desc.isEmpty())
        {
            itmDesc.setError("Please fill in a short description of the item.");
            inputInvalid = true;
        }
        else
        {
            itmDesc.setError(null);
        }
        if(eDate.isEmpty())
        {
            itmExpiryDate.setError("Please select an expiry date.");
            inputInvalid = true;

        }
        else
        {
            itmExpiryDate.setText(null);
        }
        if(qty.isEmpty())
        {
            itmQuantity.setError("Please enter quantity.");
            inputInvalid = true;

        }
        else if(!isNumeric(qty))
        {
            itmQuantity.setError("Enter a number only.");
            inputInvalid = true;
        }
        else
        {
            itmQuantity.setError(null);
        }
        if(coldesc.isEmpty())
        {
            itmCollectionDescription.setError("Please explain how to collect the items.");
            inputInvalid = true;
        }
        else
        {
            itmCollectionDescription.setError(null);
        }
        if(!this.isImgUploaded)
        {
            inputInvalid = true;
        }

        return inputInvalid;
    }


    private boolean isNumeric(String s)
    {
        try
        {
            Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            return false;
        }
        catch (Exception e)
        {
            return false;
        }

        return true;

    }

    private void updateView(CharityItemInfo charityItemInfo)
    {
        if (charityItemInfo != null)
        {

            Picasso.with(SMAddPostActivity.this).load(charityItemInfo.getImgUri()).fit().centerCrop().into(imageView);
            imageView.setVisibility(View.VISIBLE);
            itmName.setText(charityItemInfo.getItemName());
            itmDesc.setText(charityItemInfo.getItemDescription());
            itmManufactureDate.setText(charityItemInfo.getItemManufacturedDate());
            itmExpiryDate.setText(charityItemInfo.getItemExpiryDate());
            itmQuantity.setText(String.valueOf(charityItemInfo.getItemQuantity()));
            itmCollectionDescription.setText(charityItemInfo.getItemCollectionDescription());
        }
    }


    public void onFailedSave() {
        Toast.makeText(SMAddPostActivity.this, "Item is not uploaded.Please try again.", Toast.LENGTH_LONG).show();
        upLoadItemButton.setEnabled(true);
    }
}
