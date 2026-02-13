package clementechModel;

import java.io.Serializable;
import java.util.ArrayList;
//completed

import java.util.HashSet;
import java.util.Set;



public class Supplier implements Serializable{

    private static final long serialVersionUID = 1L;
    private String supplierName;
    private String contactInfo;
    ArrayList<Item>itemsSupplied;
    private Set<Sector> sectorsSupplied;

    public Supplier(String supplierName, String contactInfo) {
        this.supplierName = supplierName;
        this.contactInfo = contactInfo;
        this.itemsSupplied = new ArrayList<>();
        this.sectorsSupplied = new HashSet<>();
    }


    public String getSupplierName() {
        return supplierName;
    }
    public String getContactInfo() {
        return contactInfo;
    }

    public ArrayList<Item> getItemsSupplied() {
        return itemsSupplied;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public Set<Sector> getSectorsSupplied()
    {
        return this.sectorsSupplied;
    }
    public void addSectorForSupplier(Sector sector)
    {
        sectorsSupplied.add(sector);
    }


}