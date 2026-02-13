package clementechModel;

import java.io.Serializable;
import java.time.LocalDate;

public class Accessories extends Item implements Serializable{

    private static final long serialVersionUID = 1L;
    private String compatibility;
    private String accessoryType;

    public  Accessories(String itemId, String name, String brand, double purchasePrice, double SellingPrice
            , String compatibility, String accessoryType,int quantity,LocalDate dateOfPurchase,Inventory inventory)
    {
        super(itemId, name, brand, purchasePrice,SellingPrice, dateOfPurchase,quantity);
        this.compatibility = compatibility;
        this.accessoryType = accessoryType;
        super.sector = Sector.ACCESSORIES;
        inventory.addItemtoInventory(this);

    }

    public String getItemCompatibility()
    {
        return compatibility;
    }

    public String getAccessoryType()
    {
        return accessoryType;
    }

    @Override
    public String getItemInfo()
    {
        return super.getItemInfo()+" Compatability: "+this.getItemCompatibility()+" Accessory Type: "+this.getAccessoryType();
    }
}
