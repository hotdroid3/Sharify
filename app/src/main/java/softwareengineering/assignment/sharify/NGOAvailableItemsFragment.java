package softwareengineering.assignment.sharify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.

 * Use the {@link NGOAvailableItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class NGOAvailableItemsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = null;
    private DatabaseReference mDataRef = null;
    private ArrayList<CharityItemInfo> charityItemInfoArrayList = new ArrayList<CharityItemInfo>();
    private RecyclerView availableItemsRecycler;
    private itemRecyclerViewAdapter recyclerViewAdapter = null;
    private ProgressBar mProgressBar;
    private int adapterPosition;
    private static final int DISPLAY_NAME_LENGTH= 13;
    private static final int LENGTH_OF_NAME_SUBSTRING = 11;

    public static final String CHARITY_ITEM_INFO = "CharityItemInfo";

    public NGOAvailableItemsFragment() {
        // Required empty public constructor
    }


    public static NGOAvailableItemsFragment newInstance() {
        NGOAvailableItemsFragment fragment = new NGOAvailableItemsFragment();
        //implement parcelable??
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDataRef = mDatabase.getReference();
        mDataRef = mDataRef.child("Charity Items' Information");
//        CharityItemInfo crazy = new CharityItemInfo();
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
//
//
//        mDataRef.child(crazy.getItemUUID()).setValue(crazy);



    }
    @Override
    public void onStart(){
        super.onStart();
        charityItemInfoArrayList.clear();
        ChildEventListener itemChildEventListener = new ChildEventListener() {
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
        mDataRef.addChildEventListener(itemChildEventListener);
    }


    private void addItem(DataSnapshot dataSnapshot)
    {

        CharityItemInfo charityItemInfo = dataSnapshot.getValue(CharityItemInfo.class);
        if(!(charityItemInfo.isAccepted()))
        {
            charityItemInfoArrayList.add(charityItemInfo);
            if(recyclerViewAdapter != null)
            {
                //testing
                //recyclerViewAdapter.notifyItemInserted(charityItemInfoArrayList.size()-1);
                recyclerViewAdapter.notifyDataSetChanged();
                availableItemsRecycler.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        }

    }

    private void updateItem(DataSnapshot dataSnapshot)
    {
        int index = -1;
        CharityItemInfo updatedItem = dataSnapshot.getValue(CharityItemInfo.class);
        if(!(updatedItem.isAccepted()))
        {
            for(CharityItemInfo charityItemInfo: charityItemInfoArrayList)
            {
                if(updatedItem.getItemUUID().equals(charityItemInfo.getItemUUID()))
                {
                    index = charityItemInfoArrayList.indexOf(charityItemInfo);
                }
            }
            if(index != -1)
            {
                charityItemInfoArrayList.set(index, updatedItem);
                if(recyclerViewAdapter != null)
                {
                    //recyclerViewAdapter.notifyItemChanged(index);
                    recyclerViewAdapter.notifyDataSetChanged();
                    availableItemsRecycler.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                }
            }

        }
    }

    private void removeItem(DataSnapshot dataSnapshot)
    {
        int index = -1;
        CharityItemInfo deletedItem = dataSnapshot.getValue(CharityItemInfo.class);
        for(CharityItemInfo charityItemInfo: charityItemInfoArrayList)
        {
            if(deletedItem.getItemUUID().equals(charityItemInfo.getItemUUID()))
            {
                index = charityItemInfoArrayList.indexOf(charityItemInfo);
            }
        }
        if(index != -1)
        {
            charityItemInfoArrayList.remove(index);
            if(recyclerViewAdapter != null)
            {
//              recyclerViewAdapter.notifyItemRemoved(index);
//              recyclerViewAdapter.notifyItemRangeChanged(index, charityItemInfoArrayList.size());
                recyclerViewAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_available_items, container, false);
        availableItemsRecycler = (RecyclerView) view.findViewById(R.id.availableItemsRecyclerView);
        availableItemsRecycler.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        availableItemsRecycler.setLayoutManager(gridLayoutManager);
        charityItemInfoArrayList.clear();
        recyclerViewAdapter = new itemRecyclerViewAdapter(getActivity(),charityItemInfoArrayList);
        availableItemsRecycler.setAdapter(recyclerViewAdapter);
        mProgressBar =(ProgressBar)view.findViewById(R.id.progressBar);
        return view;
    }

    public class itemRecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        private List<CharityItemInfo> itemsArrayList;
        protected Context context;
        public itemRecyclerViewAdapter(Context context, List<CharityItemInfo> items) {
            this.itemsArrayList = items;
            this.context = context;
        }
        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemViewHolder viewHolder = null;
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_view, parent, false);
            viewHolder = new ItemViewHolder(layoutView, itemsArrayList);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            String itemName = itemsArrayList.get(position).getItemName();
            if(itemName.length() > DISPLAY_NAME_LENGTH)
            {
                itemName = itemName.substring(0,LENGTH_OF_NAME_SUBSTRING);
                itemName = itemName + "...";
            }
            holder.itemNameView.setText(itemName);
            Picasso.with(getActivity()).load(itemsArrayList.get(position).getImgUri()).fit().centerCrop().into(holder.itemPhotoView);
            //testing
            adapterPosition = holder.getAdapterPosition();
        }
        @Override
        public int getItemCount() {
            return this.itemsArrayList.size();
        }


    }
    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "ItemViewHolder";

        public ImageView itemPhotoView;
        public TextView itemNameView;
        private List<CharityItemInfo> itemsList;

        public ItemViewHolder(final View itemView, final List<CharityItemInfo> items) {
            super(itemView);
            this.itemsList = items;
            itemPhotoView = (ImageView)itemView.findViewById(R.id.itemPhoto);
            itemNameView = (TextView) itemView.findViewById(R.id.itemName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CharityItemInfo charityItemInfo;

                    Intent intent = new Intent(getActivity(),NGOViewAvailableItemDetailsActivity.class);
                    try
                    {
                        charityItemInfo = charityItemInfoArrayList.get(adapterPosition);
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
