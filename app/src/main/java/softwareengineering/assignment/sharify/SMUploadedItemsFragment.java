package softwareengineering.assignment.sharify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static softwareengineering.assignment.sharify.NGOAvailableItemsFragment.CHARITY_ITEM_INFO;


public class SMUploadedItemsFragment extends Fragment {



    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;
    private DatabaseReference mUserRef;
    private RecyclerView uploadedItemsRecyclerView;
    private SMUploadedItemsFragment.UploadedItemRecyclerViewAdapter adapter;
    private int adapterPosition;
    private ProgressBar mProgressBar;
    private ArrayList<CharityItemInfo> uploadedItemsList = new ArrayList<CharityItemInfo>();
    private UserInfo userInfo;

    private static final int DISPLAY_NAME_LENGTH = 25;
    private static final int LENGTH_OF_NAME_SUBSTRING = 22;
    private static final int DISPLAY_DONATOR_LENGTH = 52;
    private static final int LENGTH_OF_DONATOR_NAME_SUBSTRING = 48;

    public SMUploadedItemsFragment() {
        // Required empty public constructor
    }


    public static SMUploadedItemsFragment newInstance() {
        SMUploadedItemsFragment fragment = new SMUploadedItemsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mDataRef = mDatabase.getReference();
        mDataRef = mDataRef.child("Charity Items' Information");

        mUserRef = mDatabase.getReference();
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

    }


    @Override
    public void onStart(){
        super.onStart();
        uploadedItemsList.clear();
        ChildEventListener acceptedItemsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addItem(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateItem(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeItem(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading items!", Toast.LENGTH_LONG).show();
            }
        };
        mDataRef.addChildEventListener(acceptedItemsListener);
    }


    private void addItem(DataSnapshot dataSnapshot)
    {

        CharityItemInfo charityItemInfo = dataSnapshot.getValue(CharityItemInfo.class);

        boolean isAccepted = charityItemInfo.isAccepted();
        boolean isCollected = charityItemInfo.isCollected();

        if(userInfo != null)
        {
            String userID = mAuth.getCurrentUser().getUid().toString();

            if(!isAccepted && !isCollected)
            {
                if(charityItemInfo.getItemDonatorUid().equals(userID))
                {
                    uploadedItemsList.add(charityItemInfo);
                    if(adapter != null)
                    {
                        //testing
                        //recyclerViewAdapter.notifyItemInserted(charityItemInfoArrayList.size()-1);
                        adapter.notifyDataSetChanged();
                        uploadedItemsRecyclerView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        }


    }

    private void updateItem(DataSnapshot dataSnapshot)
    {
        int index = -1;

        CharityItemInfo updatedItem = dataSnapshot.getValue(CharityItemInfo.class);

        boolean isAccepted = updatedItem.isAccepted();
        boolean isCollected = updatedItem.isCollected();

        if(userInfo != null)
        {
            String userID = mAuth.getCurrentUser().getUid().toString();

            if(updatedItem.getItemDonatorUid().equals(userID))
            {
                if(!isAccepted && !isCollected)
                {
                    for(CharityItemInfo charityItemInfo: uploadedItemsList)
                    {
                        if(updatedItem.getItemUUID().equals(charityItemInfo.getItemUUID()))
                        {
                            index = uploadedItemsList.indexOf(charityItemInfo);
                        }
                    }
                    if(index != -1)
                    {
                        uploadedItemsList.set(index, updatedItem);
                        if(adapter != null)
                        {
                            //recyclerViewAdapter.notifyItemChanged(index);
                            adapter.notifyDataSetChanged();
                            uploadedItemsRecyclerView.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }

                }
            }



        }

    }

    private void removeItem(DataSnapshot dataSnapshot)
    {
        int index = -1;

        CharityItemInfo deletedItem = dataSnapshot.getValue(CharityItemInfo.class);

        for(CharityItemInfo charityItemInfo: uploadedItemsList)
        {
            if(deletedItem.getItemUUID().equals(charityItemInfo.getItemUUID()))
            {
                index = uploadedItemsList.indexOf(charityItemInfo);
            }
        }
        if(index != -1)
        {
            uploadedItemsList.remove(index);
            if(adapter != null)
            {
//              recyclerViewAdapter.notifyItemRemoved(index);
//              recyclerViewAdapter.notifyItemRangeChanged(index, charityItemInfoArrayList.size());
                adapter.notifyDataSetChanged();
            }
        }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sm_uploaded_items,container,false);

        uploadedItemsRecyclerView = (RecyclerView)view.findViewById(R.id.uploadedRecyclerView);
        uploadedItemsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        uploadedItemsRecyclerView.setLayoutManager(linearLayoutManager);
        uploadedItemsList.clear();
        adapter = new SMUploadedItemsFragment.UploadedItemRecyclerViewAdapter(getActivity(),uploadedItemsList);
        uploadedItemsRecyclerView.setAdapter(adapter);
        mProgressBar = (ProgressBar)view.findViewById(R.id.progressBar);

        return view;
    }


    public class UploadedItemRecyclerViewAdapter extends RecyclerView.Adapter<SMUploadedItemsFragment.UploadedItemViewHolder> {

        private ArrayList<CharityItemInfo> uploadedItems;
        protected Context context;

        public UploadedItemRecyclerViewAdapter(Context context, ArrayList<CharityItemInfo> items) {
            this.uploadedItems = items;
            this.context = context;
        }
        @Override
        public SMUploadedItemsFragment.UploadedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            SMUploadedItemsFragment.UploadedItemViewHolder viewHolder = null;
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_sm_available_items_cardview, parent, false);
            viewHolder = new SMUploadedItemsFragment.UploadedItemViewHolder(layoutView, uploadedItems);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(SMUploadedItemsFragment.UploadedItemViewHolder holder, int position) {

            String itemName = uploadedItems.get(position).getItemName();
            String itemDesc = uploadedItems.get(position).getItemDescription();

            if(itemName.length() > DISPLAY_NAME_LENGTH)
            {
                itemName = itemName.substring(0,LENGTH_OF_NAME_SUBSTRING);
                itemName = itemName + "...";
            }
            if(itemDesc.length() > DISPLAY_DONATOR_LENGTH)
            {
                itemDesc = itemDesc.substring(0,LENGTH_OF_DONATOR_NAME_SUBSTRING);
                itemDesc = itemDesc + "...";
            }
            holder.itemNameView.setText(itemName);
            holder.itemDescription.setText("Description: " + itemDesc);
            Picasso.with(getActivity()).load(uploadedItems.get(position).getImgUri()).fit().centerCrop().into(holder.itemPhotoView);
            //testing
            adapterPosition = holder.getAdapterPosition();
        }
        @Override
        public int getItemCount() {
            return this.uploadedItems.size();
        }


    }



    public class UploadedItemViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "UploadedItemViewHolder";

        public ImageView itemPhotoView;
        public TextView itemNameView;
        public TextView itemDescription;

        private ArrayList<CharityItemInfo> uploadedItems;

        public UploadedItemViewHolder(final View itemView, final ArrayList<CharityItemInfo> items) {
            super(itemView);
            this.uploadedItems = items;

            itemPhotoView = (ImageView)itemView.findViewById(R.id.itemPicture);
            itemNameView = (TextView) itemView.findViewById(R.id.itemName);
            itemDescription = (TextView)itemView.findViewById(R.id.itemDesc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CharityItemInfo charityItemInfo;

                    Intent intent = new Intent(getActivity(),SMViewUploadedItemDetailsActivity.class);
                    try
                    {
                        charityItemInfo = uploadedItemsList.get(adapterPosition);
                        Toast.makeText(getActivity(), "Selected " + itemNameView.getText().toString(), Toast.LENGTH_LONG).show();
                        intent.putExtra(CHARITY_ITEM_INFO, charityItemInfo);
                        startActivity(intent);

                    }catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(getActivity(),"Error, no more items available! ", Toast.LENGTH_LONG).show();
                    }

                    //getActivity().finish();
                }
            });
        }
    }

}
