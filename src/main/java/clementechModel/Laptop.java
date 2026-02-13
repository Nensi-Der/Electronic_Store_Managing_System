package clementechModel;

import java.io.Serializable;
import java.time.LocalDate;

public class Laptop extends Item implements Serializable{

    private static final long serialVersionUID = 1L;
    private String model;
    private String processor;
    private int ramGB;
    private int storageGB;
    private String storageType;
    private double screenSize;
    private boolean hasDedicatedGPU;
    private String operatingSystem;


    public Laptop(String itemId, String name, String brand, double purchasePrice, double sellingPrice,
                  LocalDate purchaseDate,int quantity, String model, String processor, int ramGB, int storageGB,
                  String storageType, double screenSize, boolean hasDedicatedGPU, String operatingSystem,
                  Inventory inventory)
    {
        super(itemId,name, brand, purchasePrice,sellingPrice,purchaseDate,quantity);
        this.model = model;
        this.processor = processor;
        this.ramGB = ramGB;
        this.storageGB = storageGB;
        this.storageType = storageType;
        this.screenSize = screenSize;
        this.hasDedicatedGPU = hasDedicatedGPU;
        this.operatingSystem = operatingSystem;
        super.sector = Sector.LAPTOP;
        inventory.addItemtoInventory(this);
    }

    public String getLaptopModel()
    {
        return this.model;
    }
    public void setLaptopModel(String model)
    {
        this.model = model;
    }
    public String getProcessor()
    {
        return this.processor;
    }
    public void setProcessor(String processor)
    {
        this.processor = processor;
    }
    public int getRamGB()
    {
        return this.ramGB;
    }
    public void setRamGB(int ramGB)
    {
        this.ramGB = ramGB;
    }
    public int getStorageGB()
    {
        return this.storageGB;
    }
    public void setStorageGB(int storageGB)
    {
        this.storageGB = storageGB;
    }
    public String getStorageType()
    {
        return this.storageType;
    }
    public void setStorageType(String storageType)
    {
        this.storageType = storageType;
    }
    public double getScreenSize()
    {
        return this.screenSize;
    }
    public void setScreenSize(double screenSize)
    {
        this.screenSize = screenSize;
    }
    public boolean gethasDedicatedGPU()
    {
        return this.hasDedicatedGPU;
    }
    public void sethasDedicatedGPU(boolean hasDedicatedGPU)
    {
        this.hasDedicatedGPU = hasDedicatedGPU;
    }
    public String getOperatingSystem()
    {
        return this.operatingSystem;
    }
    public void setOperatingSystem(String operatingSystem)
    {
        this.operatingSystem = operatingSystem;
    }
    @Override
    public String getItemInfo()
    {
        return super.getItemInfo()+" Model:"+this.getLaptopModel()+" Operating System: "+this.getOperatingSystem()+ "Processor:"+this.getProcessor()+
                " RAM GB:"+this.getRamGB()+" StorageSize and Type: "+this.getStorageGB()+" "+this.getStorageType()
                +" Screen Size:"+this.getScreenSize();
    }

}