package clementechModel;

import java.io.Serializable;
import java.time.LocalDate;
//completed
public class TV extends Item implements Serializable{

    private static final long serialVersionUID = 1L;
    private int screenSizeInches;
    private boolean isSmartTV;
    private String resolution;


    public TV(String itemId, String name, String brand, double purchasePrice, double sellingPrice,
              int screenSizeInches, boolean isSmartTV, String resolution,int quantity,LocalDate dateOfPurchase,
              Inventory inventory)
    {
        super(itemId, name, brand, purchasePrice, sellingPrice,dateOfPurchase, quantity);
        this.screenSizeInches = screenSizeInches;
        this.isSmartTV = isSmartTV;
        this.resolution = resolution;
        super.sector = Sector.TV;
        inventory.addItemtoInventory(this);
    }

    public int getScreenSizaInches()
    {
        return this.screenSizeInches;
    }
    public void setScreenSizeInches(int screenSize)
    {
        this.screenSizeInches = screenSize;
    }
    public boolean getIsSmartTV()
    {
        return isSmartTV;
    }
    public void setIsSmartTV(boolean isSmartTV)
    {
        this.isSmartTV = isSmartTV;
    }
    public String getResolution()
    {
        return this.resolution;
    }
    public void setResolution(String resolution)
    {
        this.resolution = resolution;
    }

    @Override
    public String getItemInfo()
    {
        if(isSmartTV)
            return super.getItemInfo()+" Screen Size:"+this.getScreenSizaInches()+"Resolution: "+this.getResolution()+" Is smart TV";
        else
            return super.getItemInfo()+" Screen Size:"+this.getScreenSizaInches()+"Resolution: "+this.getResolution()+" Is not smart TV";
    }



}
