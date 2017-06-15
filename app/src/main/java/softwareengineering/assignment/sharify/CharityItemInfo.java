package softwareengineering.assignment.sharify;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chanan on 12/6/2017.
 */

public class CharityItemInfo implements Parcelable{

    private String itemUUID;
    private String imgUri;
    private String itemName;
    private String itemDescription;
    private String itemManufacturedDate;
    private String itemExpiryDate;
    private int itemQuantity;
    private String itemCollectionDescription;
    private String contactDetails;
    private boolean isAccepted;
    private boolean isCollected;
    private String itemDonatorName;
    private String itemCollectorName;

    public CharityItemInfo()
    {

    }

    public CharityItemInfo(String itemUUID, String imgUri, String itemName, String itemDescription, String itemManufacturedDate, String itemExpiryDate, int itemQuantity, String itemCollectionDescription, String contactDetails, boolean isAccepted, boolean isCollected, String itemDonatorName, String itemCollectorName) {
        setItemUUID(itemUUID);
        setImgUri(imgUri);
        setItemName(itemName);
        setItemDescription(itemDescription);
        setItemManufacturedDate(itemManufacturedDate);
        setItemExpiryDate(itemExpiryDate);
        setItemQuantity(itemQuantity);
        setItemCollectionDescription(itemCollectionDescription);
        setContactDetails(contactDetails);
        setAccepted(isAccepted);
        setCollected(isCollected);
        setItemDonatorName(itemDonatorName);
        setItemCollectorName(itemCollectorName);
    }
    private CharityItemInfo(Parcel parcelIn)
    {
        setItemUUID(parcelIn.readString());
        setImgUri(parcelIn.readString());
        setItemName(parcelIn.readString());
        setItemDescription(parcelIn.readString());
        setItemManufacturedDate(parcelIn.readString());
        setItemExpiryDate(parcelIn.readString());
        setItemQuantity(parcelIn.readInt());
        setItemCollectionDescription(parcelIn.readString());
        setContactDetails(parcelIn.readString());
        setAccepted(Boolean.valueOf(parcelIn.readString()));
        setCollected(Boolean.valueOf(parcelIn.readString()));
        setItemDonatorName(parcelIn.readString());
        setItemCollectorName(parcelIn.readString());
    }
    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeString(getItemUUID());
        parcel.writeString(getImgUri());
        parcel.writeString(getItemName());
        parcel.writeString(getItemDescription());
        parcel.writeString(getItemManufacturedDate());
        parcel.writeString(getItemExpiryDate());
        parcel.writeInt(getItemQuantity());
        parcel.writeString(getItemCollectionDescription());
        parcel.writeString(getContactDetails());
        parcel.writeString(Boolean.toString(isAccepted()));
        parcel.writeString(Boolean.toString(isCollected()));
        parcel.writeString(getItemDonatorName());
        parcel.writeString(getItemCollectorName());
    }

    public String getItemUUID() {
        return this.itemUUID;
    }

    public void setItemUUID(String itemUUID) {
        this.itemUUID = itemUUID;
    }

    public String getImgUri() {
        return this.imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return this.itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemManufacturedDate() {
        return this.itemManufacturedDate;
    }

    public void setItemManufacturedDate(String itemManufacturedDate) {
        this.itemManufacturedDate = itemManufacturedDate;
    }

    public String getItemExpiryDate() {
        return this.itemExpiryDate;
    }

    public void setItemExpiryDate(String itemExpiryDate) {
        this.itemExpiryDate = itemExpiryDate;
    }

    public int getItemQuantity() {
        return this.itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemCollectionDescription() {
        return this.itemCollectionDescription;
    }

    public void setItemCollectionDescription(String itemCollectionDescription) {
        this.itemCollectionDescription = itemCollectionDescription;
    }

    public String getContactDetails() {
        return this.contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public boolean isAccepted() {
        return this.isAccepted;
    }

    public void setAccepted(boolean accepted) {
        this.isAccepted = accepted;
    }

    public boolean isCollected() {
        return this.isCollected;
    }

    public void setCollected(boolean collected) {
        this.isCollected = collected;
    }

    public String getItemDonatorName() {
        return this.itemDonatorName;
    }

    public void setItemDonatorName(String itemDonatorName) {
        this.itemDonatorName = itemDonatorName;
    }

    public String getItemCollectorName() {
        return this.itemCollectorName;
    }

    public void setItemCollectorName(String itemCollectorName) {
        this.itemCollectorName = itemCollectorName;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator<CharityItemInfo> CREATOR
            = new Parcelable.Creator<CharityItemInfo>() {


        @Override
        public CharityItemInfo createFromParcel(Parcel parcelIn) {
            return new CharityItemInfo(parcelIn);
        }

        @Override
        public CharityItemInfo[] newArray(int size) {
            return new CharityItemInfo[size];
        }
    };
}
