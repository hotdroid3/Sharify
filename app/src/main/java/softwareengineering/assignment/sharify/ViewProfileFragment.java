package softwareengineering.assignment.sharify;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ViewProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;
    private TextView orgName;
    private TextView orgEmail;
    private TextView orgType;
    private TextView orgAddress;
    private TextView orgContact;
    private ImageButton editDetails;
    private UserInfo userInfo;
    private Button logout;

    public static final String USERINFO = "USER_INFO";
    public static final String CLASS_NAME = "CLASS_NAME";

    public ViewProfileFragment() {
        // Required empty public constructor
    }


    public static ViewProfileFragment newInstance() {
        ViewProfileFragment fragment = new ViewProfileFragment();
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
        final ProgressDialog progDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progDialog.setIndeterminate(true);
        progDialog.setMessage("Loading");
        progDialog.show();
        ValueEventListener userInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateView(dataSnapshot);
                progDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Loading error.", Toast.LENGTH_LONG).show();
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
        editDetails = (ImageButton)view.findViewById(R.id.editDetails);
        logout = (Button)view.findViewById(R.id.logout);



        editDetails.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra(USERINFO, userInfo);
                //intent.putExtra(CLASS_NAME, "NGOViewPagerActivity");
                startActivity(intent);
                //getActivity().finish();
                //doesn't allow user to pressback in editprofile profile activity;
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }




}
