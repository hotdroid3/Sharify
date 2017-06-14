package softwareengineering.assignment.sharify;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.

 * Use the {@link AvailableItems#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AvailableItems extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = null;
    private DatabaseReference mDataRef = null;
    private ArrayList<CharityItemInfo> charityItemInfoArrayList;

    private static final int DISPLAY_NAME_LENGTH= 13;
    private static final int LENGTH_OF_NAME_SUBSTRING = 11;

    public AvailableItems() {
        // Required empty public constructor
    }


    public static AvailableItems newInstance() {
        AvailableItems fragment = new AvailableItems();
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
//        crazy.setItemDonatorUid(mAuth.getCurrentUser().getUid());
//        crazy.setItemUUID(UUID.randomUUID().toString());
//        crazy.setItemName("Cauliflowerrrrrrrrrrrrrrrr");
//        crazy.setImgUri("https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/Choi_Sum_stalks.JPG/1200px-Choi_Sum_stalks.JPG");
//
//
//        mDataRef.child(crazy.getItemUUID()).setValue(crazy);
        charityItemInfoArrayList = new ArrayList<CharityItemInfo>();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    CharityItemInfo item = singleSnapshot.getValue(CharityItemInfo.class);
                    charityItemInfoArrayList.add(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDataRef.addListenerForSingleValueEvent(postListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_available_items, container, false);
        RecyclerView availableItemsRecycler = (RecyclerView) view.findViewById(R.id.availableItemsRecyclerView);
        availableItemsRecycler.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        availableItemsRecycler.setLayoutManager(gridLayoutManager);
        itemRecyclerViewAdapter recyclerViewAdapter = new itemRecyclerViewAdapter(getActivity(), charityItemInfoArrayList);
        availableItemsRecycler.setAdapter(recyclerViewAdapter);
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
                    Toast.makeText(getActivity(), "Selected " + itemNameView.getText().toString(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                    //supposed to start Entire Item View Fragment with parcelable;
                }
            });
        }
    }





}
