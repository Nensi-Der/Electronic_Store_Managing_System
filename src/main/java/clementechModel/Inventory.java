package clementechModel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Inventory implements Serializable{
    private static final long serialVersionUID = 1L;
    private ArrayList<Item> itemsInInventory;

    public Inventory()
    {
        this.itemsInInventory = new ArrayList<>();
    }


    public ArrayList<Item> getItemsInInventory()
    {
        return itemsInInventory;
    }
    public void addItemtoInventory(Item itemToAdd) {

        if (itemsInInventory.contains(itemToAdd)) {
            itemToAdd.setStockQuantity(itemToAdd.getStockQuantity()+1);
            itemToAdd.setDateBought(LocalDate.now());
        } else {
            itemsInInventory.add(itemToAdd);
            itemToAdd.setDateBought(LocalDate.now());
        }
    }

    public Item findItemByName(String itemName) {
        for(int i = 0; i < itemsInInventory.size(); i++)
        {
            if(itemName.equals(itemsInInventory.get(i).getItemName()))
            {
                return itemsInInventory.get(i);
            }
        }
        System.out.println("No item by such name");
        return null;
    }

    public void showInventoryWithWarnings() {
        System.out.println("Inventory Report:");
        System.out.println("----------------------------");

        for (Item item : itemsInInventory) {

            System.out.println(item.getItemName() +
                    " - Quantity: " + item.getStockQuantity() +
                    " | Price: $" + item.getSellingPrice());

            if (item.getStockQuantity() < item.getThresholdNrForWarning()) {
                System.out.println("⚠ WARNING: Low stock!");
            }
        }

        System.out.println("----------------------------");
        System.out.println("Total Inventory Value: $" + calculateTotalInventoryValue());
        System.out.println();
    }

    public double calculateTotalInventoryValue() {
        double total = 0;
        for (Item item : itemsInInventory) {
            total += item.getSellingPrice() * item.getStockQuantity();
        }
        return total;
    }

}
