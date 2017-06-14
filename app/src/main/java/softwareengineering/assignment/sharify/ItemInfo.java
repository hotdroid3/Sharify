package softwareengineering.assignment.sharify;

/**
 * Created by Raeka on 6/13/2017.
 */

public class ItemInfo {
    private String Name;
    private String Brand;
    private String Description;
    private String ManufactureDate;
    private String ExpiryDate;
    private String Quant;

    public ItemInfo(String Name, String Brand, String Description, String ManufactureDate,String ExpiryDate, String Quantity){
        setName(Name);
        setBrand(Brand);
        setDesc(Description);
        setManufactureDate(ManufactureDate);
        setExpireDate(ExpiryDate);
//        setQuantity(Quant);
    }
    public String getName(){
        return this.Name;
    }
    public void setName(String Name){
        this.Name=Name;
    }
    public String getBrand(){
        return this.Brand;
    }
    public void setBrand(String Brand){
        this.Brand=Brand;
    }
    public String getDescription(){
        return this.Description;
    }
    public void setDesc(String Description){
        this.Description= Description;
    }
    public String getManufactureDate(){
        return this.ManufactureDate;
    }
    public void setManufactureDate(String ManufactureDate){
        this.ManufactureDate=ManufactureDate;
    }
    public String getExpiryDate(){
        return this.ExpiryDate;
    }
    public void setExpireDate(String ExpiryDate){
        this.ExpiryDate=ExpiryDate;
    }
    public String getQuantity(){
        return this.Quant;
    }
//    public void setQuantity(){
//        this.Quant= Quant;
//    }
}

