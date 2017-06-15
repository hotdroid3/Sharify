package softwareengineering.assignment.sharify;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chanan on 12/6/2017.
 */

public class UserInfo implements Parcelable{

    private String email;
    private String organizationName;
    private String organizationAddress;
    private String organizationContact;
    private String organizationType;



    public UserInfo()
    {

    }

    public UserInfo(String email, String organizationName, String organizationAddress, String organizationContact, String organizationType)
    {
        setEmail(email);
        setOrganizationName(organizationName);
        setOrganizationAddress(organizationAddress);
        setOrganizationContact(organizationContact);
        setOrganizationType(organizationType);
    }
    private UserInfo(Parcel parcelIn)
    {
        setEmail(parcelIn.readString());
        setOrganizationName(parcelIn.readString());
        setOrganizationAddress(parcelIn.readString());
        setOrganizationContact(parcelIn.readString());
        setOrganizationType(parcelIn.readString());
    }
    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeString(getEmail());
        parcel.writeString(getOrganizationName());
        parcel.writeString(getOrganizationAddress());
        parcel.writeString(getOrganizationContact());
        parcel.writeString(getOrganizationType());
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganizationName() {
        return this.organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationAddress() {
        return this.organizationAddress;
    }

    public void setOrganizationAddress(String organizationAddress) {
        this.organizationAddress = organizationAddress;
    }

    public String getOrganizationContact() {
        return this.organizationContact;
    }

    public void setOrganizationContact(String organizationContact) {
        this.organizationContact = organizationContact;
    }

    public String getOrganizationType()
    {
        return this.organizationType;
    }

    public void setOrganizationType(String organizationType)
    {
        this.organizationType = organizationType;
    }
    @Override
    public int describeContents()
    {
        return 0;
    }
    public static final Parcelable.Creator<UserInfo> CREATOR
            = new Parcelable.Creator<UserInfo>() {


        @Override
        public UserInfo createFromParcel(Parcel parcelIn) {
            return new UserInfo(parcelIn);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
