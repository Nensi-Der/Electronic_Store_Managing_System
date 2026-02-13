package clementechModel;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class Item implements Serializable{

    private static final long serialVersionUID = 1L;
    protected String itemId;
    protected String name;
    protected String brand;

    protected double purchasePrice;
    protected double sellingPrice;

    protected LocalDate purchaseDate;
    protected Supplier supplier;
    protected Sector sector;
    protected LocalDate dateSold;
    protected LocalDate dateBought;
    protected double discountPercentage = 0;
    protected int stockQuatity;
    protected transient Inventory inventory;
    protected int numberSold = 0;
    protected int thresholdNrForWarning = 3;


    protected Item(String itemId, String name, String brand, double purchasePrice, double sellingPrice,
                   LocalDate purchaseDate, int stockQuantity)
    {
        this.itemId = itemId;
        this.name = name;
        this.brand = brand;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.purchaseDate = purchaseDate;
        this.stockQuatity = stockQuantity;
    }

    public String getItemId()
    {
        return itemId;
    }
    public int getNumberSold()
    {
        return this.numberSold;
    }
    public void setNumberSold(int quantity)
    {
        this.numberSold += quantity;
    }
    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }
    public String getItemName()
    {
        return name;
    }
    public void setItemName(String itemName)
    {
        this.name = itemName;
    }
    public String getItemBrand()
    {
        return brand;
    }
    public void setItemBrand(String brand)
    {
        this.brand = brand;
    }
    public double getPurchasePrice()
    {
        return purchasePrice;
    }
    public void setPurchasePrice(double purchasePrice)
    {
        this.purchasePrice = purchasePrice;
    }
    public double getSellingPrice()
    {
        return sellingPrice;
    }
    public void setSellingPrice(double sellingPrice)
    {
        this.sellingPrice = sellingPrice;
    }
    public LocalDate getPurchaseDate()
    {
        return purchaseDate;
    }
    public String getSupplier() {
        return (supplier == null) ? "-" : supplier.getSupplierName();
    }
    public Supplier getSupplierObj() {
        return supplier;
    }
    public void setSupplier(Supplier supplier)
    {
        this.supplier = supplier;
    }
    public String getItemInfo()
    {
        return "ID: "+this.getItemId()+"Item name: "+this.getItemName()+"Brand: "+this.getItemBrand()+"Price:"+this.getSellingPrice();
    }
    public void setSector(Sector sector) {
        this.sector = sector;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item other)) return false;
        if (this.itemId == null || other.itemId == null) return false;
        return this.itemId.equalsIgnoreCase(other.itemId);
    }

    @Override
    public int hashCode() {
        return itemId == null ? 0 : itemId.toLowerCase().hashCode();
    }

    public LocalDate getDateSold()
    {
        return dateSold;
    }
    public void setDateSold(LocalDate dateSold)
    {
        this.dateSold = dateSold;
    }
    public Sector getSector()
    {
        return this.sector;
    }
    public int getStockQuantity()
    {
        return this.stockQuatity;
    }
    public void setStockQuantity(int quantity)
    {
        this.stockQuatity = quantity;
    }
    public void setDiscountPercentage(double discount)
    {
        this.discountPercentage = discount;
    }
    public double getDiscountPercentage()
    {
        return discountPercentage;
    }
    public int getThresholdNrForWarning()
    {
        return this.thresholdNrForWarning;
    }
    public void setThresholdNrForWarning(int thresh)
    {
        this.thresholdNrForWarning = thresh;
    }
    public void setDateBought(LocalDate dateBought)
    {
        this.dateBought = dateBought;
    }
    public LocalDate getDateBought()
    {
        return this.dateBought;
    }


    public static class PersistedItem extends Item {
        private static final long serialVersionUID = 1L;

        public PersistedItem(String itemId, String name, String brand,
                             double purchasePrice, double sellingPrice,
                             LocalDate purchaseDate, int stockQuantity,
                             Sector sector, Supplier supplier) {
            super(itemId, name, brand, purchasePrice, sellingPrice, purchaseDate, stockQuantity);
            this.sector = sector;
            this.supplier = supplier;
        }
    }


}