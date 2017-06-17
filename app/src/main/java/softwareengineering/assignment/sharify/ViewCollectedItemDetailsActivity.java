package softwareengineering.assignment.sharify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import static softwareengineering.assignment.sharify.AvailableItemsFragment.CHARITY_ITEM_INFO;

public class ViewCollectedItemDetailsActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;

    private CharityItemInfo charityItemInfo;

    private ImageView itemImage;
    private TextView itemName;
    private TextView itemDonator;
    private TextView itemDescription;
    private TextView itemManufacturedDate;
    private TextView itemExpiryDate;
    private TextView itemQuantity;
    private TextView itemContactDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_collected_item_details);

        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mDataRef = mDataRef.child("Charity Items' Information");

        itemImage = (ImageView)findViewById(R.id.itemPhoto);
        itemName = (TextView)findViewById(R.id.viewItemName);
        itemDonator = (TextView)findViewById(R.id.itemDonator);
        itemDescription = (TextView)findViewById(R.id.viewItemDescription);
        itemManufacturedDate = (TextView)findViewById(R.id.viewManufactured);
        itemExpiryDate = (TextView)findViewById(R.id.viewExpiry);
        itemQuantity = (TextView)findViewById(R.id.viewQuantity);
        itemContactDetails = (TextView)findViewById(R.id.viewContact);

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
                Toast.makeText(ViewCollectedItemDetailsActivity.this, "Loading error!" ,Toast.LENGTH_LONG).show();
            }
        };
        mDataRef.addValueEventListener(itemInfoListener);

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
            Picasso.with(ViewCollectedItemDetailsActivity.this).load(charityItemInfo.getImgUri()).fit().centerCrop().into(itemImage);
            itemName.setText(charityItemInfo.getItemName());
            itemDonator.setText(charityItemInfo.getItemDonatorName());
            itemDescription.setText(charityItemInfo.getItemDescription());
            itemManufacturedDate.setText(charityItemInfo.getItemManufacturedDate());
            itemExpiryDate.setText(charityItemInfo.getItemExpiryDate());
            itemQuantity.setText(String.valueOf(charityItemInfo.getItemQuantity()));
            itemContactDetails.setText(charityItemInfo.getContactDetails());
        }
    }
}
