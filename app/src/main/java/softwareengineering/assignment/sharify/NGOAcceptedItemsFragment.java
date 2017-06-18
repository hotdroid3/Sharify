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


/**
 * A simple {@link Fragment} subclass.

 * Use the {@link NGOAcceptedItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NGOAcceptedItemsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDataRef;
    private DatabaseReference mUserRef;
    private RecyclerView acceptedItemsRecyclerView;
    private AcceptedItemsRecyclerViewAdapter adapter;
    private int adapterPosition;
    private ProgressBar mProgressBar;
    private ArrayList<CharityItemInfo> acceptedItemsList = new ArrayList<CharityItemInfo>();
    private UserInfo userInfo;

    private static final int DISPLAY_NAME_LENGTH = 25;
    private static final int LENGTH_OF_NAME_SUBSTRING = 22;

    private static final int DISPLAY_DONATOR_LENGTH = 52;
    private static final int LENGTH_OF_DONATOR_NAME_SUBSTRING = 48;



    public NGOAcceptedItemsFragment() {
        // Required empty public constructor
    }


    public static NGOAcceptedItemsFragment newInstance() {
        NGOAcceptedItemsFragment fragment = new NGOAcceptedItemsFragment();
        //implement parcelable
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
        acceptedItemsList.clear();
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
                if(charityItemInfo.getItemCollectorUid().equals(userID))
                {
                    acceptedItemsList.add(charityItemInfo);
                    if(adapter != null)
                    {
                        //testing
                        //recyclerViewAdapter.notifyItemInserted(charityItemInfoArrayList.size()-1);
                        adapter.notifyDataSetChanged();
                        acceptedItemsRecyclerView.setVisibility(View.VISIBLE);
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
            if(updatedItem.getItemCollectorUid() == null)
            {
                for(CharityItemInfo charityItemInfo: acceptedItemsList)
                {
                    if(updatedItem.getItemUUID().equals(charityItemInfo.getItemUUID()))
                    {
                        index = acceptedItemsList.indexOf(charityItemInfo);
                    }
                }
                if(index != -1)
                {
                    acceptedItemsList.remove(index);
                    if(adapter != null)
                    {
//              recyclerViewAdapter.notifyItemRemoved(index);
//              recyclerViewAdapter.notifyItemRangeChanged(index, charityItemInfoArrayList.size());
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            else{
                if(updatedItem.getItemCollectorUid().equals(userID))
                {
                    if(isAccepted && !isCollected)
                    {
                        for(CharityItemInfo charityItemInfo: acceptedItemsList)
                        {
                            if(updatedItem.getItemUUID().equals(charityItemInfo.getItemUUID()))
                            {
                                index = acceptedItemsList.indexOf(charityItemInfo);
                            }
                        }
                        if(index != -1)
                        {
                            acceptedItemsList.set(index, updatedItem);
                            if(adapter != null)
                            {
                                //recyclerViewAdapter.notifyItemChanged(index);
                                adapter.notifyDataSetChanged();
                                acceptedItemsRecyclerView.setVisibility(View.VISIBLE);
                                mProgressBar.setVisibility(View.GONE);
                            }
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
        for(CharityItemInfo charityItemInfo: acceptedItemsList)
        {
            if(deletedItem.getItemUUID().equals(charityItemInfo.getItemUUID()))
            {
                index = acceptedItemsList.indexOf(charityItemInfo);
            }
        }
        if(index != -1)
        {
            acceptedItemsList.remove(index);
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
        View view = inflater.inflate(R.layout.fragment_accepted_items, container, false);
        acceptedItemsRecyclerView = (RecyclerView)view.findViewById(R.id.acceptedItemsRecyclerView);
        acceptedItemsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        acceptedItemsRecyclerView.setLayoutManager(linearLayoutManager);
        acceptedItemsList.clear();
        adapter = new AcceptedItemsRecyclerViewAdapter(getActivity(),acceptedItemsList);
        acceptedItemsRecyclerView.setAdapter(adapter);
        mProgressBar =(ProgressBar)view.findViewById(R.id.progressBar);

        return view;
    }


    public class AcceptedItemsRecyclerViewAdapter extends RecyclerView.Adapter<NGOAcceptedItemsFragment.AcceptedItemViewHolder> {
        private ArrayList<CharityItemInfo> acceptedItems;
        protected Context context;
        public AcceptedItemsRecyclerViewAdapter(Context context, ArrayList<CharityItemInfo> items) {
            this.acceptedItems = items;
            this.context = context;
        }
        @Override
        public NGOAcceptedItemsFragment.AcceptedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            NGOAcceptedItemsFragment.AcceptedItemViewHolder viewHolder = null;
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_ngo_accepted_items, parent, false);
            viewHolder = new NGOAcceptedItemsFragment.AcceptedItemViewHolder(layoutView, acceptedItems);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(NGOAcceptedItemsFragment.AcceptedItemViewHolder holder, int position) {


            String itemName = acceptedItems.get(position).getItemName();
            String itemDonator = acceptedItems.get(position).getItemDonatorName();
            if(itemName.length() > DISPLAY_NAME_LENGTH)
            {
                itemName = itemName.substring(0,LENGTH_OF_NAME_SUBSTRING);
                itemName = itemName + "...";
            }
            if(itemDonator.length() > DISPLAY_DONATOR_LENGTH)
            {
                itemDonator = itemDonator.substring(0,LENGTH_OF_DONATOR_NAME_SUBSTRING);
                itemDonator = itemDonator + "...";
            }
            holder.itemNameView.setText(itemName);
            holder.itemDonatorView.setText("Donated by: " + itemDonator);
            Picasso.with(getActivity()).load(acceptedItems.get(position).getImgUri()).fit().centerCrop().into(holder.itemPhotoView);
            //testing
            //adapterPosition = holder.getAdapterPosition();
        }
        @Override
        public int getItemCount() {
            return this.acceptedItems.size();
        }


    }


    public class AcceptedItemViewHolder extends RecyclerView.ViewHolder
    {
        private static final String TAG = "AcceptedItemViewHolder";

        public ImageView itemPhotoView;
        public TextView itemNameView;
        public TextView itemDonatorView;

        private ArrayList<CharityItemInfo> acceptedItems;

        public AcceptedItemViewHolder(final View itemView, final ArrayList<CharityItemInfo> items) {
            super(itemView);
            this.acceptedItems = items;
            itemPhotoView = (ImageView) itemView.findViewById(R.id.itemPicture);
            itemNameView = (TextView) itemView.findViewById(R.id.acceptedItemName);
            itemDonatorView = (TextView) itemView.findViewById(R.id.itemDonatorName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CharityItemInfo charityItemInfo;

//
                    Intent intent = new Intent(getActivity(), NGOViewAcceptedItemDetailsActivity.class);
                    try {
                        adapterPosition = getAdapterPosition();
                        charityItemInfo = acceptedItemsList.get(adapterPosition);
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
