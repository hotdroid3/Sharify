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


import static softwareengineering.assignment.sharify.AvailableItemsFragment.CHARITY_ITEM_INFO;

public class ViewAvailableItemDetailsActivity extends AppCompatActivity {

    private DatabaseReference mDataRef;
    private DatabaseReference mUserRef;
    private FirebaseDatabase mDatabase;
    private CharityItemInfo charityItemInfo;
    private FirebaseAuth mAuth;
    private UserInfo userInfo;
    private ImageView itemImage;
    private TextView itemName;
    private TextView itemDonator;
    private TextView itemDescription;
    private TextView itemManufacturedDate;
    private TextView itemExpiryDate;
    private TextView itemQuantity;
    private TextView itemCollectionDescription;
    private TextView itemContactDetails;
    private Button itemAccepted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_details);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mDataRef = mDataRef.child("Charity Items' Information");
        mUserRef = mDatabase.getReference();


        itemImage = (ImageView)findViewById(R.id.itemPhoto);
        itemName = (TextView)findViewById(R.id.viewItemName);
        itemDonator = (TextView)findViewById(R.id.itemDonator);
        itemDescription = (TextView)findViewById(R.id.viewItemDescription);
        itemManufacturedDate = (TextView)findViewById(R.id.viewManufactured);
        itemExpiryDate = (TextView)findViewById(R.id.viewExpiry);
        itemQuantity = (TextView)findViewById(R.id.viewQuantity);
        itemCollectionDescription = (TextView)findViewById(R.id.viewCollectionMethod);
        itemContactDetails = (TextView)findViewById(R.id.viewContact);
        itemAccepted = (Button)findViewById(R.id.acceptButton);

        Intent intent = getIntent();
        charityItemInfo = intent.getParcelableExtra(CHARITY_ITEM_INFO);
        final String charityItemInfoUUID = charityItemInfo.getItemUUID();

        mDataRef = mDataRef.child(charityItemInfoUUID);
        ValueEventListener itemInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CharityItemInfo charityItem = dataSnapshot.getValue(CharityItemInfo.class);
                updateView(charityItem);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewAvailableItemDetailsActivity.this, "Loading error!" ,Toast.LENGTH_LONG).show();
            }
        };
        mDataRef.addValueEventListener(itemInfoListener);


        mUserRef = mUserRef.child("Users Information");
        mUserRef = mUserRef.child(mAuth.getCurrentUser().getUid());
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo = dataSnapshot.getValue(UserInfo.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mUserRef.addValueEventListener(userListener);

        itemAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCollected();
                //Intent intent = new Intent(ViewAvailableItemDetailsActivity.this, NGOViewPagerActivity.class);
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
                if(userInfo!= null){
                    charityItemInfo.setAccepted(true);
                    charityItemInfo.setItemCollectorName(userInfo.getOrganizationName());
                    charityItemInfo.setItemCollectorUid(mAuth.getCurrentUser().getUid().toString());
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
            Picasso.with(ViewAvailableItemDetailsActivity.this).load(charityItemInfo.getImgUri()).fit().centerCrop().into(itemImage);
            itemName.setText(charityItemInfo.getItemName());
            itemDonator.setText(charityItemInfo.getItemDonatorName());
            itemDescription.setText(charityItemInfo.getItemDescription());
            itemManufacturedDate.setText(charityItemInfo.getItemManufacturedDate());
            itemExpiryDate.setText(charityItemInfo.getItemExpiryDate());
            itemQuantity.setText(String.valueOf(charityItemInfo.getItemQuantity()));
            itemCollectionDescription.setText(charityItemInfo.getItemCollectionDescription());
            itemContactDetails.setText(charityItemInfo.getContactDetails());

        }
    }
}
