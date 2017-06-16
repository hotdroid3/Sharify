package softwareengineering.assignment.sharify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewAcceptedItemDetailsActivity extends AppCompatActivity {


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
    private TextView itemCollectionDescription;
    private TextView itemContactDetails;
    private Button mCancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_accepted_item_details);


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
        itemCollectionDescription = (TextView)findViewById(R.id.viewCollectionMethod);
        itemContactDetails = (TextView)findViewById(R.id.viewContact);
        mCancelButton = (Button)findViewById(R.id.cancelButton);



    }
}
