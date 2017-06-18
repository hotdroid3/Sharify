package softwareengineering.assignment.sharify;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import java.util.UUID;

import static softwareengineering.assignment.sharify.NGOAvailableItemsFragment.CHARITY_ITEM_INFO;


/**
 * A simple {@link Fragment} subclass.

 * Use the {@link SMAcceptedItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SMAcceptedItemsFragment extends Fragment {


    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;
    private DatabaseReference mUserRef;
    private RecyclerView SMAcceptedItemsRecyclerView;
    private SMAcceptedItemsFragment.SMAcceptedItemsRecyclerViewAdapter adapter;
    private int adapterPosition;
    private ProgressBar mProgressBar;
    private ArrayList<CharityItemInfo> SMacceptedItemsList = new ArrayList<CharityItemInfo>();
    private UserInfo userInfo;

    private static final int DISPLAY_NAME_LENGTH = 25;
    private static final int LENGTH_OF_NAME_SUBSTRING = 22;

    private static final int DISPLAY_DONATOR_LENGTH = 52;
    private static final int LENGTH_OF_DONATOR_NAME_SUBSTRING = 48;


    public SMAcceptedItemsFragment() {
        // Required empty public constructor
    }


    public static SMAcceptedItemsFragment newInstance() {
        SMAcceptedItemsFragment fragment = new SMAcceptedItemsFragment();
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




//        CharityItemInfo crazy = new CharityItemInfo();
//        crazy.setItemQuantity(10);
//        crazy.setItemDonatorName("Tesco");
//        crazy.setItemDescription("Tons of cauliflower about to go bad");
//        crazy.setItemManufacturedDate("19/09/2017");
//        crazy.setItemExpiryDate("20/09/2017");
//        crazy.setItemCollectionDescription("Collect from main branch at Sunway Pyramid Tesco");
//        crazy.setContactDetails("0129290192");
//        crazy.setItemUUID(UUID.randomUUID().toString());
//        crazy.setAccepted(false);
//        crazy.setCollected(false);
//        crazy.setItemName("Cauliflowerrrrrrrrrrrrrrrr");
//        crazy.setImgUri("https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/Choi_Sum_stalks.JPG/1200px-Choi_Sum_stalks.JPG");
//        crazy.setItemDonatorUid(mAuth.getCurrentUser().getUid().toString());
//
//
//
//        mDataRef.child(crazy.getItemUUID()).setValue(crazy);






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
        SMacceptedItemsList.clear();
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

            if(isAccepted && !isCollected)
            {
                if(charityItemInfo.getItemDonatorUid().equals(userID))
                {
                    SMacceptedItemsList.add(charityItemInfo);
                    if(adapter != null)
                    {
                        //testing
                        //recyclerViewAdapter.notifyItemInserted(charityItemInfoArrayList.size()-1);
                        adapter.notifyDataSetChanged();
                        SMAcceptedItemsRecyclerView.setVisibility(View.VISIBLE);
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
                if(isAccepted && !isCollected)
                {
                    for(CharityItemInfo charityItemInfo: SMacceptedItemsList)
                    {
                        if(updatedItem.getItemUUID().equals(charityItemInfo.getItemUUID()))
                        {
                            index = SMacceptedItemsList.indexOf(charityItemInfo);
                        }
                    }
                    if(index != -1)
                    {
                        SMacceptedItemsList.set(index, updatedItem);
                        if(adapter != null)
                        {
                            //recyclerViewAdapter.notifyItemChanged(index);
                            adapter.notifyDataSetChanged();
                            SMAcceptedItemsRecyclerView.setVisibility(View.VISIBLE);
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

        for(CharityItemInfo charityItemInfo: SMacceptedItemsList)
        {
            if(deletedItem.getItemUUID().equals(charityItemInfo.getItemUUID()))
            {
                index = SMacceptedItemsList.indexOf(charityItemInfo);
            }
        }
        if(index != -1)
        {
            SMacceptedItemsList.remove(index);
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

        View view = inflater.inflate(R.layout.fragment_smaccepted_items, container, false);

        SMAcceptedItemsRecyclerView = (RecyclerView)view.findViewById(R.id.collectedItemsRecyclerView);
        SMAcceptedItemsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        SMAcceptedItemsRecyclerView.setLayoutManager(linearLayoutManager);
        SMacceptedItemsList.clear();
        adapter = new SMAcceptedItemsFragment.SMAcceptedItemsRecyclerViewAdapter(getActivity(),SMacceptedItemsList);
        SMAcceptedItemsRecyclerView.setAdapter(adapter);
        mProgressBar =(ProgressBar)view.findViewById(R.id.progressBar);

        return view;
    }


    public class SMAcceptedItemsRecyclerViewAdapter extends RecyclerView.Adapter<SMAcceptedItemsFragment.SMAcceptedItemViewHolder> {

        private ArrayList<CharityItemInfo> acceptedItems;
        protected Context context;

        public SMAcceptedItemsRecyclerViewAdapter(Context context, ArrayList<CharityItemInfo> items) {
            this.acceptedItems = items;
            this.context = context;
        }
        @Override
        public SMAcceptedItemsFragment.SMAcceptedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            SMAcceptedItemsFragment.SMAcceptedItemViewHolder viewHolder = null;
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_sm_collected_items_cardview, parent, false);
            viewHolder = new SMAcceptedItemsFragment.SMAcceptedItemViewHolder(layoutView, acceptedItems);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(SMAcceptedItemsFragment.SMAcceptedItemViewHolder holder, int position) {


            String itemName = acceptedItems.get(position).getItemName();
            String itemCollector = acceptedItems.get(position).getItemCollectorName();

            if(itemName.length() > DISPLAY_NAME_LENGTH)
            {
                itemName = itemName.substring(0,LENGTH_OF_NAME_SUBSTRING);
                itemName = itemName + "...";
            }
            if(itemCollector.length() > DISPLAY_DONATOR_LENGTH)
            {
                itemCollector = itemCollector.substring(0,LENGTH_OF_DONATOR_NAME_SUBSTRING);
                itemCollector = itemCollector + "...";
            }
            holder.itemNameView.setText(itemName);
            holder.itemCollectorView.setText("Accepted by: " + itemCollector);
            Picasso.with(getActivity()).load(acceptedItems.get(position).getImgUri()).fit().centerCrop().into(holder.itemPhotoView);
            //testing
            adapterPosition = holder.getAdapterPosition();
        }
        @Override
        public int getItemCount() {
            return this.acceptedItems.size();
        }


    }


    public class SMAcceptedItemViewHolder extends RecyclerView.ViewHolder
    {
        private static final String TAG = "SMAcceptedItemViewHolder";

        public ImageView itemPhotoView;
        public TextView itemNameView;
        public TextView itemCollectorView;

        private ArrayList<CharityItemInfo> acceptedItems;

        public SMAcceptedItemViewHolder(final View itemView, final ArrayList<CharityItemInfo> items) {
            super(itemView);
            this.acceptedItems = items;

            itemPhotoView = (ImageView) itemView.findViewById(R.id.itemPicture);
            itemNameView = (TextView) itemView.findViewById(R.id.acceptedItemName);
            itemCollectorView = (TextView) itemView.findViewById(R.id.itemCollectorName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CharityItemInfo charityItemInfo;

                    Intent intent = new Intent(getActivity(), SMViewAcceptedItemDetailsActivity.class);
                    try {
                        charityItemInfo = SMacceptedItemsList.get(adapterPosition);
                        Toast.makeText(getActivity(), "Selected " + itemNameView.getText().toString(), Toast.LENGTH_LONG).show();
                        intent.putExtra(CHARITY_ITEM_INFO, charityItemInfo);
                        startActivity(intent);

                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getActivity(), "Error, no more items available! ", Toast.LENGTH_LONG).show();
                    }

                    //getActivity().finish();
                }
            });
        }
    }

}
