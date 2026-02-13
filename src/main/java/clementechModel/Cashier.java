package clementechModel;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Cashier extends Employee implements Serializable{

    private static final long serialVersionUID = 1L;

    private Set<Sector> sectors = new HashSet<>();
    private ArrayList<Bill> totalBills;
    Manager manager;
    int totalBillNumbers = 0;

    public Cashier(int employeeId, String firstName, String lastName, LocalDate dateOfBirth, String phone,
                   String email, double salary, Manager manager) {

        super(employeeId, firstName, lastName, dateOfBirth, phone, email, salary);
        permissions.add(Permission.CREATE_BILL);
        permissions.add(Permission.VIEW_BILL);
        sectors = new HashSet<>();
        this.totalBills = new ArrayList<>();
        this.manager = manager;


    }

    public int getTotalBillNumbers()
    {
        return this.totalBillNumbers;
    }

    public void getSector()
    {
        for(Sector s: sectors)
        {
            System.out.println(s);
        }
    }

    public Bill createBill(String buyerInfo)
    {
        if (!permissions.contains(Permission.CREATE_BILL)) {
            throw new SecurityException("You do not have permission to create bills.");
        }
        try {
            Bill bill = new Bill(buyerInfo, this.getUsername());
            totalBills.add(bill);
            totalBillNumbers++;
            return bill;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bill: " + e.getMessage());
        }
    }

    public String viewTodaysBills()
    {
        if (!permissions.contains(Permission.VIEW_BILL)) {
            throw new SecurityException("You do not have permission to view bills.");
        }

        LocalDate today = LocalDate.now();
        boolean found = false;
        StringBuilder sb = new StringBuilder();

        for (Bill bill : totalBills) {
            if (bill.getDateBillIsGettingCut().equals(today)) {
                sb.append(bill.getBillInfo());
                found = true;
            }
        }
        if (!found) {
            return"No bills created today.";
        }
        else return sb.toString();
    }

    public Set<Sector> getSectors() {
        return sectors;
    }
    public ArrayList<Bill> getBills()
    {
        return totalBills;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }
}