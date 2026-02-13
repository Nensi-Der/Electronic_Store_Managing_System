package clementechModel;

import java.io.Serializable;
//completed
import java.time.LocalDate;

public class Phone extends Item implements Serializable{

    private static final long serialVersionUID = 1L;
    private String model;
    private int storageGB;
    private boolean is5G;


    public Phone(String itemId, String name, String brand, double purchasePrice, double SellingPrice,
                 String model, int storageGB, boolean is5G,int quantity,LocalDate dateOfPurchase, Inventory inventory)
    {
        super(itemId, name, brand, purchasePrice, SellingPrice,dateOfPurchase,quantity);
        this.model = model;
        this.storageGB = storageGB;
        this.is5G = is5G;
        super.sector = Sector.PHONE;
        inventory.addItemtoInventory(this);
    }

    public String getPhoneModel()
    {
        return model;
    }
    public void setPhoneModel(String model)
    {
        this.model = model;
    }
    public int getStorageGB()
    {
        return this.storageGB;
    }
    public void setStorageGB(int storageGB)
    {
        this.storageGB = storageGB;
    }
    public boolean getIs5G()
    {
        return this.is5G;
    }
    public void setIs5G(boolean is5G)
    {
        this.is5G = is5G;
    }
    @Override
    public String getItemInfo()
    {
        if(!is5G)
            return super.getItemInfo()+ "Phone model: "+this.getPhoneModel()+" Phone storage"+this.getStorageGB()+" Not 5G.";
        else return
                super.getItemInfo()+"Phone model: "+this.getPhoneModel()+" Phone storage"+this.getStorageGB()+"5G phone";

    }


}
