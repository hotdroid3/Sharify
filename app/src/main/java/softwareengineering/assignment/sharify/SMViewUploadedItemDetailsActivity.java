package softwareengineering.assignment.sharify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static softwareengineering.assignment.sharify.NGOAvailableItemsFragment.CHARITY_ITEM_INFO;
import static softwareengineering.assignment.sharify.ViewProfileFragment.CLASS_NAME;

public class SMViewUploadedItemDetailsActivity extends AppCompatActivity {

    private DatabaseReference mDataRef;
    private DatabaseReference mUserRef;
    private FirebaseDatabase mDatabase;
    private CharityItemInfo charityItemInfo;
    private FirebaseAuth mAuth;
    private UserInfo userInfo;


    private ImageView itemImage;
    private TextView itemName;
    private TextView itemDescription;
    private TextView itemManufacturedDate;
    private TextView itemExpiryDate;
    private TextView itemQuantity;
    private TextView itemCollectionDescription;
    private TextView itemContactDetails;
    private Button editButton;
    private Button deleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smview_uploaded_details);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mDataRef = mDataRef.child("Charity Items' Information");
        mUserRef = mDatabase.getReference();

        itemImage = (ImageView)findViewById(R.id.itemPhoto);
        itemName = (TextView)findViewById(R.id.viewItemName);
        itemDescription = (TextView)findViewById(R.id.viewItemDescription);
        itemManufacturedDate = (TextView)findViewById(R.id.viewManufactured);
        itemExpiryDate = (TextView)findViewById(R.id.viewExpiry);
        itemQuantity = (TextView)findViewById(R.id.viewQuantity);
        itemCollectionDescription = (TextView)findViewById(R.id.viewCollectionMethod);
        itemContactDetails = (TextView)findViewById(R.id.viewContact);
        editButton = (Button)findViewById(R.id.editButton);
        deleteButton = (Button)findViewById(R.id.deleteButton);


        Intent intent = getIntent();
        charityItemInfo = intent.getParcelableExtra(CHARITY_ITEM_INFO);
        final String charityItemInfoUUID = charityItemInfo.getItemUUID();

        mDataRef = mDataRef.child(charityItemInfoUUID);
        ValueEventListener itemInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                charityItemInfo = dataSnapshot.getValue(CharityItemInfo.class);
                updateView(charityItemInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SMViewUploadedItemDetailsActivity.this, "Loading error!" ,Toast.LENGTH_LONG).show();
            }
        };
        mDataRef.addValueEventListener(itemInfoListener);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start addpost activity with parcelable do not finish()
                Intent intent = new Intent(SMViewUploadedItemDetailsActivity.this, SMAddPostActivity.class);
                intent.putExtra(CHARITY_ITEM_INFO, charityItemInfo);
                //intent.putExtra(CLASS_NAME, "SMViewUploadedItemDetailsActivity");
                startActivity(intent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(charityItemInfo);
                finish();
            }
        });
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        updateView(charityItemInfo);

    }
    private void updateView(CharityItemInfo charityItemInfo)
    {
        if (charityItemInfo != null)
        {
            Picasso.with(SMViewUploadedItemDetailsActivity.this).load(charityItemInfo.getImgUri()).fit().centerCrop().into(itemImage);
            itemName.setText(charityItemInfo.getItemName());
            itemDescription.setText(charityItemInfo.getItemDescription());
            itemManufacturedDate.setText(charityItemInfo.getItemManufacturedDate());
            itemExpiryDate.setText(charityItemInfo.getItemExpiryDate());
            itemQuantity.setText(String.valueOf(charityItemInfo.getItemQuantity()));
            itemCollectionDescription.setText(charityItemInfo.getItemCollectionDescription());
            itemContactDetails.setText(charityItemInfo.getContactDetails());

        }
    }

    private void deleteItem(final CharityItemInfo charityItemInfo)
    {
        Runnable deleteTask = new Runnable() {
            @Override
            public void run() {
                if(charityItemInfo != null)
                {
                    DatabaseReference databaseReference = mDatabase.getReference();
                    databaseReference = databaseReference.child("Charity Items' Information").child(charityItemInfo.getItemUUID());
                    databaseReference.setValue(null);
                }

            }
        };

        new Thread(deleteTask).start();

    }
}
