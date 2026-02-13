package clementechModel;

import java.io.Serializable;
import java.time.LocalDate;

public class Household extends Item implements Serializable{
    private static final long serialVersionUID = 1L;
    private int powerWatts;
    private String energyRating;

    public Household(String itemId, String name, String brand, double purchasePrice, double sellingPrice,
                     LocalDate purchaseDate, int quantity ,int powerWatts, String energyRating,Inventory inventory)
    {
        super(itemId,name, brand, purchasePrice,sellingPrice,purchaseDate, quantity);
        this.powerWatts = powerWatts;
        this.energyRating = energyRating;
        inventory.addItemtoInventory(this);
    }
    public int getPowerWatts()
    {
        return this.powerWatts;
    }
    public void setPowerWatts(int powerWatts)
    {
        this.powerWatts = powerWatts;
    }
    public String getEnergyRating()
    {
        return this.energyRating;
    }
    public void setEnergyRating(String energyRating)
    {
        this.energyRating = energyRating;
    }
    @Override
    public String getItemInfo()
    {
        return super.getItemInfo()+" Power in Watts: "+this.getPowerWatts()+" Energy Rating: "+this.getEnergyRating();
    }

}
