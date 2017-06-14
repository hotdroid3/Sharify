package softwareengineering.assignment.sharify;


import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import android.support.v4.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPostFragment extends Fragment {
    private TextView mManSetDate;
    private DatePickerDialog.OnDateSetListener mManDateSetListener;
    private TextView mExpSetDate;
    private DatePickerDialog.OnDateSetListener mExpDateSetListener;
    private long addPost;
    private Button mSelectImage;
    private FirebaseStorage mStorage;
    private StorageReference mStorRef;
    private FirebaseAuth mAuth;
    public static final int GALLERY_INTENT=2;
    private static final String TAG ="UploadActivity";

    //Declare variables for item details
    private EditText itmName = null;
    private EditText itmBrand = null;
    private EditText itmDesc = null;
    private EditText itmManufactureDate=null;
    private EditText itmExpiryDate=null;
    private EditText itmQuantity=null;
    private Button UploadItem;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_post, container, false);

    }

    @Override
    public void onStart(){
        super.onStart();

        mStorage= FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mSelectImage=(Button) getView().findViewById(R.id.uploadImg_button);

        //Display DatePickerDialog for item's manufacture date
        mManSetDate=(TextView)getView().findViewById(R.id.item_ManufactureDate);
        mManSetDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Calendar itmManCal = Calendar.getInstance();
                int itmManYear= itmManCal.get(Calendar.YEAR);
                int itmManMonth= itmManCal.get(Calendar.MONTH);
                int itmManDay =itmManCal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog itmManDatePickerDialog= new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mManDateSetListener,
                        itmManYear,itmManMonth, itmManDay);
                itmManDatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                itmManDatePickerDialog.show();
            }
        });

        //Set item's manufacture date to TextView
        mManDateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day){
                String manDate= month +1  +"/" + day +"/" +year;
                mManSetDate.setText(manDate);
            }
        };

        //Display DatePickerDialog for item's expiry date
        mExpSetDate=(TextView)getView().findViewById(R.id.item_ExpiryDate);
        mExpSetDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Calendar itmManCal = Calendar.getInstance();
                int itmExpYear= itmManCal.get(Calendar.YEAR);
                int itmExpMonth= itmManCal.get(Calendar.MONTH);
                int itmExpDay =itmManCal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog itmManDatePickerDialog= new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mExpDateSetListener,
                        itmExpYear,itmExpMonth, itmExpDay);
                itmManDatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                itmManDatePickerDialog.show();
            }
        });

        //Set item's expiry date to TextView
        mExpDateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day){
                String expDate= month+1 +"/" + day +"/" +year;
                mExpSetDate.setText(expDate);
            }
        };


        //Open gallery to select image
        mSelectImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });
    }

    //Method to save item details
    private void uploadItemDetails(){
        String Name = itmName.getText().toString().trim();
        String Brand=itmBrand.getText().toString().trim();
        String Desc=itmDesc.getText().toString().trim();
        String Quanity=itmQuantity.getText().toString().trim();
        String ManufactureDate= mManSetDate.getText().toString().trim();
        String ExpiryDate=mExpSetDate.getText().toString().trim();

        UploadItem =(Button)getView().findViewById(R.id.uploadItm_button);

        if(isInputNull(Name, Brand, Desc, ManufactureDate, ExpiryDate)){
            onFailedSave();
        }else{
            UploadItem.isEnabled();
            final ProgressDialog progDialog = new ProgressDialog(getActivity(),
                    R.style.AppTheme_Dark_Dialog);
            progDialog.setIndeterminate(true);
            progDialog.setMessage("Item Uploaded");
            progDialog.show();

            final ItemInfo itemInfo=new ItemInfo(Name, Brand, Desc, ManufactureDate, ExpiryDate, Quanity);
            final Runnable saveItmDetails=new Runnable() {
                @Override
                public void run() {
                    //Store item details to storage
                    uploadItemDetails();
                }
            };
            new Thread(saveItmDetails).start();
            onSuccessfulSave();
        }
    }

    //Method to check whether the input is Null
    private Boolean isInputNull(String Name, String Brand, String Description, String ManufactureDate, String ExpiryDate){
        boolean inputNull=false;

        if (Name.isEmpty()){
            itmName.setError("Please fill in the item's name.");
        }
        if (Brand.isEmpty()){
            itmBrand.setError("Please fill in the item's brand.");
        }
        if(Description.isEmpty()){
            itmDesc.setError("Please fill in the item's description.");
        }
        if(ManufactureDate.isEmpty()){
            itmManufactureDate.setError("Please fill in the item's manufacture date.");
        }
        if(ExpiryDate.isEmpty()){
            itmExpiryDate.setError("Please fill in the item's expiry date.");
        }

        return inputNull;
    }

    public void onSuccessfulSave(){
        Toast.makeText(getActivity(), "Successfully upload item!", Toast.LENGTH_LONG).show();
        UploadItem.setEnabled(true);
    }

    public void onFailedSave(){
        Toast.makeText(getActivity(),"Did not upload item, please try again.", Toast.LENGTH_LONG).show();
        UploadItem.setEnabled(true);
    }

    //Method for upload image to cloud
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){
            final Uri uri=data.getData();
            Log.d(TAG, uri.toString());
            Toast.makeText(getActivity(), uri.toString(), Toast.LENGTH_LONG).show();

            final ProgressDialog progDialog = new ProgressDialog(getActivity(),
                    R.style.AppTheme_Dark_Dialog);

            progDialog.setIndeterminate(true);
            progDialog.setMessage("Uploading....");
            progDialog.show();
            mStorRef = mStorage.getReference().child("Photos").child(uri.getLastPathSegment());

            UploadTask uploadTask = mStorRef.putFile(uri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(getActivity(), "Upload failed!", Toast.LENGTH_LONG).show();
                    progDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                    Toast.makeText(getActivity(), "Upload Succeeded!", Toast.LENGTH_LONG).show();
                    progDialog.dismiss();
                }
            });

        }

    }

}
