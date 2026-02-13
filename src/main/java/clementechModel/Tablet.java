package clementechModel;

import java.io.Serializable;
import java.time.LocalDate;

public class Tablet extends Item implements Serializable{

    private static final long serialVersionUID = 1L;
    private String model;
    private double screenSize;
    private int storageGB;
    private int ramGB;
    private boolean hasCellular;
    private String operatingSystem;
    private double batteryLifeHours;
    private boolean supportsPen;


    public Tablet(String itemId, String name, String brand, double purchasePrice, double sellingPrice
            ,int quantity,LocalDate purchaseDate, String model, double screenSize, int storageGB, int ramGB,
                  boolean hasCellular, String operatingSystem, double batteryLifeHours, boolean supportsPen,
                  Inventory inventory)
    {
        super(itemId, name, brand, purchasePrice, sellingPrice,purchaseDate,quantity);
        this.model = model;
        this.screenSize = screenSize;
        this.storageGB = storageGB;
        this.ramGB = ramGB;
        this.hasCellular = hasCellular;
        this.operatingSystem = operatingSystem;
        this.batteryLifeHours = batteryLifeHours;
        this.supportsPen = supportsPen;
        inventory.addItemtoInventory(this);

    }

    public String getModel() {
        return model;
    }

    public double getScreenSize() {
        return screenSize;
    }

    public int getStorageGB() {
        return storageGB;
    }

    public int getRamGB() {
        return ramGB;
    }

    public boolean hasCellular() {
        return hasCellular;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public double getBatteryLifeHours() {
        return batteryLifeHours;
    }

    public boolean supportsPen() {
        return supportsPen;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setScreenSize(double screenSize) {
        this.screenSize = screenSize;
    }

    public void setStorageGB(int storageGB) {
        this.storageGB = storageGB;
    }

    public void setRamGB(int ramGB) {
        this.ramGB = ramGB;
    }

    public void setHasCellular(boolean hasCellular) {
        this.hasCellular = hasCellular;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public void setBatteryLifeHours(double batteryLifeHours) {
        this.batteryLifeHours = batteryLifeHours;
    }

    public void setSupportsPen(boolean supportsPen) {
        this.supportsPen = supportsPen;
    }
    @Override
    public String getItemInfo()

    {
        return super.getItemInfo()+" Model:"+this.getModel()+" Storage/RAM:"+this.getStorageGB()+" "+this.getRamGB()+" "+
                " Screen Size:"+this.getScreenSize();


    }



}
