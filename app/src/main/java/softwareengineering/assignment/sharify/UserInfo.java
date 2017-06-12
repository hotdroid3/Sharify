package softwareengineering.assignment.sharify;

/**
 * Created by Chanan on 12/6/2017.
 */

public class UserInfo{

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
}
