package softwareengineering.assignment.sharify;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static softwareengineering.assignment.sharify.ViewProfileFragment.CLASS_NAME;
import static softwareengineering.assignment.sharify.ViewProfileFragment.USERINFO;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private UserInfo userInfo;
    private EditText orgName = null;
    private EditText orgAddress = null;
    private EditText orgContact = null;
    private AppCompatButton saveDetails = null;
    private FirebaseDatabase mDatabase = null;
    private DatabaseReference mDataRef = null;
    private DatabaseReference charityItemRef = null;
    private FirebaseAuth mAuth = null;
    private RadioButton mNGO;
    private RadioButton mSuper;
    private RadioGroup radioGroup;
    private ArrayList<CharityItemInfo> charityItemInfoArrayList = new ArrayList<CharityItemInfo>();
    private String organizationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();


        orgName = (EditText)findViewById(R.id.organization_name);
        orgAddress = (EditText)findViewById(R.id.organization_address);
        orgContact = (EditText) findViewById(R.id.organization_contact);
        saveDetails = (AppCompatButton)findViewById(R.id.saveDetails);
        mNGO = (RadioButton)findViewById(R.id.NGO_input);
        mSuper = (RadioButton)findViewById(R.id.supermarket_input);
        radioGroup = (RadioGroup)findViewById(R.id.orgType);

        saveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });


    }
    @Override
    public void onResume()
    {
        super.onResume();
        Intent i = getIntent();
        UserInfo userInfo = i.getParcelableExtra(USERINFO);
        if(userInfo != null)
        {
            //testing
            this.userInfo = userInfo;
            this.organizationType = userInfo.getOrganizationType();
            updateView(userInfo);
        }
        String callingActivity = i.getStringExtra(CLASS_NAME);
        if(callingActivity != null)
        {
            this.organizationType = null;
        }

    }

    private void updateView(UserInfo userInfo)
    {
        orgName.setText(userInfo.getOrganizationName());
        orgAddress.setText(userInfo.getOrganizationAddress());
        orgContact.setText(userInfo.getOrganizationContact());
        radioGroup.setVisibility(View.GONE);
//        Intent intent = getIntent();
//        String callingActivity = intent.getStringExtra(CLASS_NAME);
//        if(userInfo.getOrganizationType().equals("Non-governmental Organization"))
//        {
//            mNGO.setChecked(true);
//            this.organizationType = "Non-governmental Organization";
//        }
//        else
//        {
//            mSuper.setChecked(true);
//            this.organizationType = "Supermarket";
//        }

//        if(callingActivity == null)
//        {
//
//            mNGO.setEnabled(false);
//            mSuper.setEnabled(false);
//            mNGO.setVisibility(View.GONE);
//            mSuper.setVisibility(View.GONE);

//        }

    }

    public void onRadioItemClick(View view)
    {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId())
        {
            case R.id.NGO_input:
                if(checked)
                {
                    this.organizationType = "Non-governmental Organization";
                }
                break;
            case R.id.supermarket_input:
                if(checked)
                {
                    this.organizationType = "Supermarket";
                }
                break;
        }
    }

    private void saveUserInfo()
    {
        Log.d(TAG, "Saving user details!");

        String email = mAuth.getCurrentUser().getEmail().toString().trim();
        String name = orgName.getText().toString().trim();
        String address = orgAddress.getText().toString().trim();
        String contact = orgContact.getText().toString().trim();

        if(isInputNull(name, address,contact))
        {
            onFailedSave();
        }
        else{

            saveDetails.setEnabled(false);

            final ProgressDialog progDialog = new ProgressDialog(EditProfileActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progDialog.setIndeterminate(true);
            progDialog.setMessage("Signing in");
            progDialog.show();

            userInfo = new UserInfo(email,name,address,contact,this.organizationType);

            Runnable saveDetails = new Runnable() {
                @Override
                public void run() {
                    mDataRef.child("Users Information").child(mAuth.getCurrentUser().getUid()).setValue(userInfo);
                    progDialog.dismiss();

                }
            };
            new Thread(saveDetails).start();
            onSuccessfulSave();
        }
    }

    private boolean isInputNull(String name, String address, String contact)
    {
        boolean inputNull = false;

        if(name.isEmpty())
        {
            orgName.setError("Please fill in your organizational name.");
            inputNull = true;
        }
        else{
            orgName.setError(null);
        }
        if(address.isEmpty())
        {
            orgAddress.setError("Please fill in your address.");
            inputNull = true;
        }
        else
        {
            orgName.setError(null);
        }
        if(contact.isEmpty())
        {
            orgContact.setError("Please fill in your contact.");
            inputNull = true;
        }
        else if(!isNumeric(contact))
        {
            orgContact.setError("Contact number format example: 0122729823");
            inputNull = true;
        }
        else
        {
            orgContact.setError(null);
        }
        if(this.organizationType == null)
        {
            Toast.makeText(EditProfileActivity.this, "Please choose your type of organization.", Toast.LENGTH_LONG).show();
            inputNull = true;
        }

        return inputNull;
    }

    private void onSuccessfulSave()
    {
        Toast.makeText(EditProfileActivity.this, "Successfully saved details!", Toast.LENGTH_LONG).show();
        saveDetails.setEnabled(true);
        //check user account type before starting activity
        Intent receivedIntent = getIntent();
        String callingActivity = receivedIntent.getStringExtra(CLASS_NAME);

        if(callingActivity != null)
        {
            if(callingActivity.equals("LoginActivity") || callingActivity.equals("SignUpActivity"))
            {
                if(userInfo.getOrganizationType().equals("Non-governmental Organization"))
                {
                    Intent intent = new Intent(EditProfileActivity.this, NGOViewPagerActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(userInfo.getOrganizationType().equals("Supermarket"))
                {
                    Intent intent = new Intent(EditProfileActivity.this, SMViewPagerActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
        else {
            final ProgressDialog progDialog = new ProgressDialog(EditProfileActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progDialog.setIndeterminate(true);
            progDialog.setMessage("Updating charity items...");
            progDialog.show();

            charityItemRef = mDatabase.getReference().child("Charity Items' Information");
            final ValueEventListener updateItemsListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    updateItemsList(dataSnapshot);
                    progDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            charityItemRef.addListenerForSingleValueEvent(updateItemsListener);
            finish();
        }

    }

    private void updateItemsList(DataSnapshot dataSnapshot)
    {
        charityItemInfoArrayList.clear();
        for(DataSnapshot child: dataSnapshot.getChildren())
        {
            charityItemInfoArrayList.add(child.getValue(CharityItemInfo.class));
        }
        for(CharityItemInfo charityItemInfo: charityItemInfoArrayList)
        {
            if(charityItemInfo.getItemCollectorUid() != null)
            {
                if(charityItemInfo.getItemCollectorUid().equals(mAuth.getCurrentUser().getUid()))
                {
                    int index = charityItemInfoArrayList.indexOf(charityItemInfo);
                    charityItemInfo.setItemCollectorName(userInfo.getOrganizationName());
                    charityItemInfoArrayList.set(index,charityItemInfo);
                }
            }
            else if(charityItemInfo.getItemDonatorUid().equals(mAuth.getCurrentUser().getUid()))
            {
                int index = charityItemInfoArrayList.indexOf(charityItemInfo);
                charityItemInfo.setItemDonatorName(userInfo.getOrganizationName());
                charityItemInfo.setContactDetails(userInfo.getOrganizationContact());
                charityItemInfoArrayList.set(index, charityItemInfo);
            }

        }
        for(CharityItemInfo charityItemInfo: charityItemInfoArrayList)
        {
            DatabaseReference updateRef = mDatabase.getReference().child("Charity Items' Information");
            updateRef = updateRef.child(charityItemInfo.getItemUUID());
            updateRef.setValue(charityItemInfo);
        }

    }



    private void onFailedSave()
    {
        Toast.makeText(EditProfileActivity.this,"User data is not saved. Please try again.", Toast.LENGTH_LONG).show();
        saveDetails.setEnabled(true);
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
}
