package clementechModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;

    private int billNumber;
    private static int nextBillNumber = 1;

    private ArrayList<Item> billItems;
    private double totalBillPrice;      // sum before discount
    private double totalDiscount;       // total discount amount
    private double priceAfterDiscount;  // final payable amount

    private String buyerInfo;
    private LocalDate dateBillIsGettingCut;

    // NEW: track who created the bill se na duhet per cashier filtering
    private String createdByUsername;

    public Bill(String buyerInfo, String createdByUsername) {
        this.billNumber = nextBillNumber++;
        this.billItems = new ArrayList<>();
        setBuyerInfo(buyerInfo);
        this.dateBillIsGettingCut = LocalDate.now();
        this.createdByUsername = createdByUsername == null ? "" : createdByUsername.trim();

        recalcTotals();
        autoSave();
    }

    // keep your old constructor if you still use it somewhere
    public Bill(String buyerInfo) {
        this(buyerInfo, "");
    }


    public int getBillNumber() { return this.billNumber; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String u) { this.createdByUsername = (u == null ? "" : u.trim()); }

    //NEW: duhet per "items sold" stats in BillView
    public int getItemCount() {
        return billItems == null ? 0 : billItems.size();
    }

    public ArrayList<Item> getBillItems() { return billItems; } // optional, but useful

    public void addBillItem(Item billItem) {
        if (billItem == null) throw new IllegalArgumentException("Bill item cannot be null.");
        if (billItem.getStockQuantity() <= 0) throw new IllegalArgumentException("Cannot add item: out of stock.");

        billItems.add(billItem);

        // Update stock and sales info
        billItem.setStockQuantity(billItem.getStockQuantity() - 1);
        billItem.setNumberSold(billItem.getNumberSold() + 1);
        billItem.setDateSold(LocalDate.now());

        recalcTotals();
        autoSave();
    }

    public void deleteBillItem(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Id cannot be empty.");

        if (billItems.isEmpty()) {
            System.out.println("Bill is empty. Nothing to remove.");
            return;
        }

        for (int i = 0; i < billItems.size(); i++) {
            Item item = billItems.get(i);
            if (item.getItemId().equals(id)) {

                // Restore stock & sales info
                item.setStockQuantity(item.getStockQuantity() + 1);
                item.setNumberSold(item.getNumberSold() - 1);
                item.setDateSold(null);

                billItems.remove(i);

                recalcTotals();
                autoSave();
                return;
            }
        }

        System.out.println("Item with id " + id + " not found in bill.");
    }

    public double getTotalBillPrice() { return this.totalBillPrice; }         // before discount
    public double getTotalDiscount() { return this.totalDiscount; }
    public double getPriceAfterDiscount() { return this.priceAfterDiscount; } // ✅ use this as revenue

    private void recalcTotals() {
        double sum = 0;
        double disc = 0;

        for (Item item : billItems) {
            if (item == null) continue;

            // revenue is based on selling price
            double price = item.getSellingPrice();

            // discountPercentage is 0 to 100 (checkout uses /100.0)
            double discPct = item.getDiscountPercentage();

            // safety clamp
            if (discPct < 0) discPct = 0;
            if (discPct > 100) discPct = 100;

            sum += price;
            disc += price * (discPct / 100.0);
        }

        this.totalBillPrice = sum;           // before discount
        this.totalDiscount = disc;           // discount amount
        this.priceAfterDiscount = sum - disc; // final payable / revenue
    }

    public LocalDate getDateBillIsGettingCut() { return this.dateBillIsGettingCut; }

    public String getBuyerInfo() { return buyerInfo; }

    public void setBuyerInfo(String buyerInfo) {
        if (buyerInfo == null || buyerInfo.isBlank()) throw new IllegalArgumentException("Enter buyer information");
        this.buyerInfo = buyerInfo;
    }

    public String getBillInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sales Receipt\n");
        sb.append("Bill Number: ").append(billNumber).append("\n");
        sb.append("Buyer: ").append(buyerInfo).append("\n");
        sb.append("Date: ").append(dateBillIsGettingCut).append("\n");
        if (createdByUsername != null && !createdByUsername.isBlank()) {
            sb.append("Created by: ").append(createdByUsername).append("\n");
        }
        sb.append("\nItems:\n");

        for (Item item : billItems) {
            sb.append(item.getItemInfo()).append("\n");
        }

        sb.append("\nTotal (before discount): ").append(totalBillPrice);
        sb.append("\nDiscount: ").append(totalDiscount);
        sb.append("\nPrice after discount: ").append(priceAfterDiscount);
        return sb.toString();
    }

    public void saveToTextFile() throws IOException {
        File dir = new File("bills");
        if (!dir.exists()) dir.mkdirs();

        String filename = "bills/Bill_" + billNumber + "_" + dateBillIsGettingCut + ".txt";
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(getBillInfo());
        }
    }

    private void autoSave() {
        try {
            saveToTextFile();
        } catch (IOException e) {
            System.out.println("Failed to auto-save bill: " + e.getMessage());
        }
    }

    // called by DataStorage.loadBills()
    public static void syncNextBillNumber(int n) {
        nextBillNumber = Math.max(1, n);
    }
}
