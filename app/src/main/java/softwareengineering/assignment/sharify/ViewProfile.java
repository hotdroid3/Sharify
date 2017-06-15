package softwareengineering.assignment.sharify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ViewProfile extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;
    private TextView orgName;
    private TextView orgEmail;
    private TextView orgType;
    private TextView orgAddress;
    private TextView orgContact;
    private Button editDetails;
    private UserInfo userInfo;


    public static final String USERINFO = "USER_INFO";

    public ViewProfile() {
        // Required empty public constructor
    }


    public static ViewProfile newInstance() {
        ViewProfile fragment = new ViewProfile();
        //implement parcelable?
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mDataRef = mDataRef.child("Users Information").child(mAuth.getCurrentUser().getUid());


    }


    @Override
    public void onResume()
    {
        super.onResume();
        ValueEventListener userInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateView(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Loading error!", Toast.LENGTH_LONG).show();
            }
        };
        mDataRef.addValueEventListener(userInfoListener);

    }

    private void updateView(DataSnapshot dataSnapshot)
    {
        userInfo = dataSnapshot.getValue(UserInfo.class);
        if(userInfo!= null)
        {
            orgName.setText(userInfo.getOrganizationName());
            orgEmail.setText(userInfo.getEmail());
            orgType.setText(userInfo.getOrganizationType());
            orgAddress.setText(userInfo.getOrganizationAddress());
            orgContact.setText(userInfo.getOrganizationContact());

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        orgName = (TextView)view.findViewById(R.id.viewName);
        orgEmail = (TextView)view.findViewById(R.id.viewEmail);
        orgType = (TextView)view.findViewById(R.id.viewType);
        orgAddress = (TextView)view.findViewById(R.id.viewAddress);
        orgContact = (TextView)view.findViewById(R.id.viewContact);
        editDetails = (Button)view.findViewById(R.id.editDetails);



        editDetails.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra(USERINFO, userInfo);
                startActivity(intent);
                //getActivity().finish();
                //doesn't allow user to pressback in edit profile activity;
            }
        });

        return view;
    }




}
