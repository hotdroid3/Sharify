package softwareengineering.assignment.sharify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static softwareengineering.assignment.sharify.NGOAvailableItemsFragment.CHARITY_ITEM_INFO;

public class SMViewAcceptedItemDetailsActivity extends AppCompatActivity {


    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;

    private CharityItemInfo charityItemInfo;

    private ImageView itemImage;
    private TextView itemName;
    private TextView itemCollector;
    private TextView itemDescription;
    private TextView itemManufacturedDate;
    private TextView itemExpiryDate;
    private TextView itemQuantity;
    private TextView itemCollectionDescription;
    private TextView itemContactDetails;
    private Button mCollectedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_view_accepted_details);

        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mDataRef = mDataRef.child("Charity Items' Information");

        itemImage = (ImageView)findViewById(R.id.itemPhoto);
        itemName = (TextView)findViewById(R.id.viewItemName);
        itemCollector = (TextView)findViewById(R.id.itemCollector);
        itemDescription = (TextView)findViewById(R.id.viewItemDescription);
        itemManufacturedDate = (TextView)findViewById(R.id.viewManufactured);
        itemExpiryDate = (TextView)findViewById(R.id.viewExpiry);
        itemQuantity = (TextView)findViewById(R.id.viewQuantity);
        itemCollectionDescription = (TextView)findViewById(R.id.viewCollectionMethod);
        itemContactDetails = (TextView)findViewById(R.id.viewContact);
        mCollectedButton = (Button)findViewById(R.id.collectedButton);

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
                Toast.makeText(SMViewAcceptedItemDetailsActivity.this, "Loading error!" ,Toast.LENGTH_LONG).show();
            }
        };
        mDataRef.addValueEventListener(itemInfoListener);

        mCollectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCollected();
                //Intent intent = new Intent(NGOViewAvailableItemDetailsActivity.this, NGOViewPagerActivity.class);
                //startActivity(intent);
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

    private void updateCollected()
    {
        Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                if(charityItemInfo!= null)
                {
                    charityItemInfo.setCollected(true);
                    DatabaseReference databaseReference = mDatabase.getReference();
                    databaseReference = databaseReference.child("Charity Items' Information").child(charityItemInfo.getItemUUID());
                    databaseReference.setValue(charityItemInfo);
                }

            }
        };
        new Thread(updateTask).start();
    }


    private void updateView(CharityItemInfo charityItemInfo)
    {
        if (charityItemInfo != null)
        {
            Picasso.with(SMViewAcceptedItemDetailsActivity.this).load(charityItemInfo.getImgUri()).fit().centerCrop().into(itemImage);
            itemName.setText(charityItemInfo.getItemName());
            itemCollector.setText(charityItemInfo.getItemDonatorName());
            itemDescription.setText(charityItemInfo.getItemDescription());
            itemManufacturedDate.setText(charityItemInfo.getItemManufacturedDate());
            itemExpiryDate.setText(charityItemInfo.getItemExpiryDate());
            itemQuantity.setText(String.valueOf(charityItemInfo.getItemQuantity()));
            itemCollectionDescription.setText(charityItemInfo.getItemCollectionDescription());
            itemContactDetails.setText(charityItemInfo.getContactDetails());
        }
    }
}
