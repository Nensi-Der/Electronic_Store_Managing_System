package clementechModel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Manager extends Employee implements Serializable{

    private static final long serialVersionUID = 1L;

    private Set<Sector> sectors;
    private ArrayList<Cashier>cashiers;
    private Inventory inventory;
    private double totalSpendings = 0;
    private ArrayList<Item>itemsPurchased;

    public Manager(int employeeId, String firstName, String lastName, LocalDate dateOfBirth, String phone,
                   String email, double salary) {

        super(employeeId, firstName, lastName, dateOfBirth, phone, email, salary);

        permissions.add(Permission.ADD_ITEM);
        permissions.add(Permission.APPLY_DISCOUNT);
        permissions.add(Permission.VIEW_STATS);
        permissions.add(Permission.ADD_CASHIER);

        this.sectors = new HashSet<>();
        this.cashiers = new ArrayList<>();
        this.itemsPurchased = new ArrayList<>();
        this.inventory = new Inventory();
    }
    public double getTotalSpendings()
    {
        return this.totalSpendings;
    }

    public void setDiscountPercentage(String name, double discount) {

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Item name cannot be null or empty.");

        if (discount < 0)
            throw new IllegalArgumentException("Discount cannot be negative.");

        if (!permissions.contains(Permission.APPLY_DISCOUNT))
            throw new SecurityException("You do not have permission to apply discount.");

        ArrayList<Item> list = DataStorage.loadItems();

        boolean found = false;
        for (Item item : list) {
            if (item == null) continue;
            if (item.getItemName() != null && item.getItemName().equalsIgnoreCase(name)) {
                item.setDiscountPercentage(discount);
                found = true;
            }
        }

        if (!found)
            throw new IllegalArgumentException("Item not found: " + name);

        DataStorage.saveItems(list);   //THIS makes cashier see it
    }

    public Set<Sector> getSectors()
    {
        return sectors;
    }
    public void setSectors(Set<Sector> s)
    {
        this.sectors = s;
    }
    public void addCashier(Cashier c)
    {
        if(!permissions.contains(Permission.ADD_CASHIER))
        {
            throw new SecurityException("You cannot add new cashier.");
        }
        if (c == null)
            throw new IllegalArgumentException("Enter cashier information.");
        if (Collections.disjoint(this.sectors, c.getSectors()))
            throw new IllegalArgumentException("Cashier must share at least one sector with the manager.");
        if (cashiers.contains(c))
            throw new IllegalArgumentException("Cashier already exists.");

        cashiers.add(c);
    }
    public void restockItems(Item item, int quantity)
    {
        if(!permissions.contains(Permission.ADD_ITEM))
        {
            throw new SecurityException("You cannot add new item or restock.");
        }
        if(!this.sectors.contains(item.getSector()))
        {
            throw new IllegalArgumentException("You cannot add items in sectors you do not manage.");
        }
        inventory.addItemtoInventory(item);
        this.totalSpendings += item.getSellingPrice()*quantity;
        this.itemsPurchased.add(item);
    }

    public void addNewItemType(Item item)
    {
        if(!permissions.contains(Permission.ADD_ITEM))
        {
            throw new SecurityException("You cannot add or restock itmes.");
        }
        for(int i = 0; i < inventory.getItemsInInventory().size();i++)
        {	if(inventory.getItemsInInventory().get(i).getItemName().equals(item.getItemName()))
        {
            System.out.println("This item already exists. Do you want to restock?");
            return;
        }
        }
        inventory.addItemtoInventory(item);

    }

    public String getTotalNumberOfBillsForAllCashiers()
    {
        int sum = 0;
        for(int i = 0; i < cashiers.size(); i++)
        {
            sum +=cashiers.get(i).getTotalBillNumbers();
        }
        return "Number of bills for all cashiers of your sectors is" +sum;
    }
    public String getCashierBillNumber(int employeeId)
    {
        for (int i = 0; i < cashiers.size(); i++) {
            if (cashiers.get(i).getEmployeeId() == employeeId) {
                return "Number of bills for "
                        + cashiers.get(i).getFullName()
                        + " is "
                        + cashiers.get(i).getTotalBillNumbers();
            }
        }
        return "No cashier found with id: " + employeeId;
    }
    public String getLastDayBills()
    {
        if(!permissions.contains(Permission.VIEW_BILL))
        {
            throw new SecurityException("You cannot view this bill.");
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < cashiers.size();i++)
        {
            sb.append(cashiers.get(i).viewTodaysBills());
        }
        return sb.toString();
    }
    public String getLastMonthBills()
    {
        if(!permissions.contains(Permission.VIEW_BILL))
        {
            throw new SecurityException("You cannot view this field.");
        }
        StringBuilder sb = new StringBuilder();
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        for(int i = 0; i < cashiers.size();i++)
        {
            for(int j = 0; j<cashiers.get(i).getBills().size();j++)
            {
                if(cashiers.get(i).getBills().get(j).getDateBillIsGettingCut().isAfter(oneMonthAgo))
                {
                    sb.append(cashiers.get(i).viewTodaysBills());
                }
            }
        }
        return sb.toString();
    }
    public String getTotalBills()
    {
        if(!permissions.contains(Permission.VIEW_BILL))
        {
            throw new SecurityException("You cannot view this field.");
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < cashiers.size();i++)
        {
            for(int j = 0; j<cashiers.get(i).getBills().size();j++)
            {
                sb.append(cashiers.get(i).viewTodaysBills());
            }
        }
        return sb.toString();
    }
    public String itemsSoldLastDay()
    {
        if(!permissions.contains(Permission.VIEW_STATS))
        {
            throw new SecurityException("You cannot view this field.");
        }
        StringBuilder sb = new StringBuilder();
        double sum = 0.0;
        for(int i = 0; i < inventory.getItemsInInventory().size();i++)
        {
            if(inventory.getItemsInInventory().get(i).getDateSold().equals(LocalDate.now()))
            {
                sb.append(inventory.getItemsInInventory().get(i).getItemInfo());
                sum += inventory.getItemsInInventory().get(i).getSellingPrice();
            }
        }
        sb.append("\n Total price of todays sold items is "+sum);
        return sb.toString();

    }
    public String itemsSoldLastMonth()
    {
        if(!permissions.contains(Permission.VIEW_STATS))
        {
            throw new SecurityException("You cannot view this field.");
        }
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        StringBuilder sb = new StringBuilder();
        double sum = 0.0;
        for(int i = 0; i < inventory.getItemsInInventory().size();i++)
        {
            if(inventory.getItemsInInventory().get(i).getDateSold().isAfter(oneMonthAgo))
            {
                sb.append(inventory.getItemsInInventory().get(i).getItemInfo());
                sum += inventory.getItemsInInventory().get(i).getSellingPrice();
            }
        }
        sb.append("Total price of items sold this past month is "+sum);
        return sb.toString();

    }
    public String itemsSoldInTotal()
    {
        if(!permissions.contains(Permission.VIEW_STATS))
        {
            throw new SecurityException("You cannot view this field.");
        }
        StringBuilder sb = new StringBuilder();
        double sum = 0.0;

        for(int i = 0; i < inventory.getItemsInInventory().size();i++)
        {
            if(inventory.getItemsInInventory().get(i).getDateSold()!= null)
            {
                sb.append(inventory.getItemsInInventory().get(i).getItemInfo());
                sum += inventory.getItemsInInventory().get(i).getSellingPrice();
            }
        }
        sb.append("Sum of total items sold is "+sum);
        return sb.toString();
    }
    public String getManagerInfo()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Name and surname: "+this.getFullName()+" Employee ID: "+this.getEmployeeId()+" Contact Information: "+this.getContactInfo()+" Sectors under management: "+this.getSectors()+" Salary: "+this.getSalary()+" Spendings: "+this.getTotalSpendings());
        sb.append("Employees Under management: ");
        for(int i = 0; i < cashiers.size();i++)
        {
            sb.append("Id: "+this.cashiers.get(i).getEmployeeId()+"Name and surname: "+this.cashiers.get(i).getFullName()+" Contact Information "+this.cashiers.get(i).getContactInfo());
        }

        return sb.toString();
    }
    public ArrayList<Cashier> getCashiers()
    {
        return cashiers;
    }
    public ArrayList<Item> getItemsPurchased()
    {
        return this.itemsPurchased;
    }
    public void setNewSupplier(Supplier supplier, Item item)
    {
        if(!inventory.getItemsInInventory().contains(item))
        {
            throw new IllegalArgumentException("No such item in inventory.");
        }
        item.setSupplier(supplier);
    }
    public Inventory getInventory()
    {
        return inventory;
    }
}
