package clementechModel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class Administrator extends Employee implements Serializable{

    private static final long serialVersionUID = 1L;
    private ArrayList<Manager>managers;
    public Administrator(int employeeId, String firstName, String lastName, LocalDate dateOfBirth, String phone,
                         String email, double salary)
    {
        super(employeeId, firstName, lastName, dateOfBirth, phone, email, salary);
        this.managers = new ArrayList<>();
    }

    public void addManagers(Manager manager)
    {
        if(managers.contains(manager))
        {
            throw new IllegalArgumentException("This manager already exists.");
        }
        managers.add(manager);
        System.out.println("Manager successfully aded.");
    }
    public String getManagerInfoById(int id)
    {
        String manager = null;
        for(int i = 0; i < managers.size();i++)
        {
            if(managers.get(i).getEmployeeId() == id)
            {
                manager= managers.get(i).getManagerInfo();
            }
        }
        return manager;
    }
    public void revokePermissionFromManager(int employeeId, Permission permission)
    {
        for(int i = 0; i < managers.size();i++)
        {
            if(managers.get(i).getEmployeeId() == employeeId)
            {
                if(managers.get(i).getPermissions().contains(permission))
                {
                    managers.get(i).getPermissions().remove(permission);
                    System.out.println("Actions completed successfully.");
                    return;}
            }

        }
        System.out.println("This action could not be completed.");
    }
    public void revokePermissionFromCashier(int employeeId, Permission permission)
    {
        for(int i = 0; i < managers.size();i++)
        {
            for(int j = 0; j < managers.get(i).getCashiers().size();j++)
            {
                if(managers.get(i).getCashiers().get(j).getEmployeeId() == employeeId)
                {
                    if(managers.get(i).getCashiers().get(j).getPermissions().contains(permission))
                    {
                        managers.get(i).getCashiers().get(j).getPermissions().remove(permission);
                        System.out.println("Permission revoked successfully.");
                        return;
                    }
                }
            }
        }
        System.out.println("Action could not be completed.");
    }
    public void addPermissionToManager(int employeeId, Permission permission)
    {
        for(int i = 0; i < managers.size();i++)
        {
            if(managers.get(i).getEmployeeId() == employeeId)
            {
                if(!managers.get(i).getPermissions().contains(permission))
                {
                    managers.get(i).getPermissions().add(permission);
                    System.out.println("Actions completed successfully.");
                    return;}
            }

        }
        System.out.println("This action could not be completed.");
    }
    public void addPermissionToCashier(int employeeId, Permission permission)
    {
        for(int i = 0; i < managers.size();i++)
        {
            for(int j = 0; j < managers.get(i).getCashiers().size();j++)
            {
                if(managers.get(i).getCashiers().get(j).getEmployeeId() == employeeId)
                {
                    if(!managers.get(i).getCashiers().get(j).getPermissions().contains(permission))
                    {
                        managers.get(i).getCashiers().get(j).getPermissions().add(permission);
                        System.out.println("Permission added successfully.");
                        return;
                    }

                }
            }
        }
        System.out.println("Action could not be completed.");
    }
    public String getTotalSpendingPerManager()
    {
        double total = 0.0;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < managers.size();i++)
        {
            sb.append(managers.get(i).getEmployeeId())
                    .append(" ")
                    .append(managers.get(i).getFullName())
                    .append(" ")
                    .append(managers.get(i).getTotalSpendings())
                    .append("\n");


            total += managers.get(i).getTotalSpendings();
        }
        sb.append("Total spendings accross all managers are: "+total+" dollars");
        return sb.toString();
    }
    public String getTotalSpendingOverTimePeriod(LocalDate earlyDate, LocalDate lateDate)
    {
        double sum = 0.0;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < managers.size();i++)
        {
            for(int j = 0; j < managers.get(i).getItemsPurchased().size();j++)
            {
                if(managers.get(i).getItemsPurchased().get(j).getDateBought().isBefore(lateDate)&&
                        managers.get(i).getItemsPurchased().get(j).getDateBought().isAfter(earlyDate))
                {
                    sb.append(managers.get(i).getItemsPurchased().get(j).getItemInfo());
                    sum += managers.get(i).getItemsPurchased().get(j).getPurchasePrice();
                }
            }
        }
        sb.append("Total spendings over chosen time period are: "+sum+"dollars");
        return sb.toString();
    }
    public void addCashier(Cashier cashier)
    {
        for(int i = 0; i < managers.size(); i++)
        {
            if(!Collections.disjoint(managers.get(i).getSectors(), cashier.getSectors()))

            {
                managers.get(i).addCashier(cashier);
                System.out.println("Cashier added successfully.");
                return;
            }
        }
        System.out.println("You cannot add cashier to that sector yet.");
    }
    public void getManagerSalaryById(int id)
    {
        for(int i = 0; i < managers.size(); i++)
        {
            if(managers.get(i).getEmployeeId() == id)
            {
                System.out.println("The salary of employee with id: "+id+ " is"+ managers.get(i).getSalary()+" dollars");
                return;
            }
        }
        System.out.println("No manager with this id was found.");
    }
    public void getCashierSalaryById(int id)
    {
        for(int i = 0; i < managers.size();i++)
        {
            for(int j = 0; j < managers.get(i).getCashiers().size();j++)
            {
                if(managers.get(i).getCashiers().get(j).getEmployeeId() == id)
                {
                    System.out.println("Salary of cashier with id: "+id+" is "+managers.get(i).getCashiers().get(j).getSalary()+" dollars");
                    return;
                }
            }
        }
        System.out.println("No cashier found with this id.");
    }
    public void deleteCashier(int id)
    {
        for(int i = 0; i < managers.size(); i++)
        {
            for(int j = 0; j < managers.get(i).getCashiers().size(); j++)
            {
                if(managers.get(i).getCashiers().get(j).getEmployeeId() == id)
                {
                    managers.get(i).getCashiers().remove(managers.get(i).getCashiers().get(j));
                    System.out.println("Cashier deleted succesfully.");
                }
            }
        }
        System.out.println("No cashier with that id was found.");
    }

    public void deleteManager(int oldId, int newId)
    {
        ArrayList<Cashier>cashiers = new ArrayList<>();
        Inventory inventory = new Inventory();
        Set<Sector> sectors = new HashSet<>();
        boolean oldExists = false;
        boolean newExists = false;
        int id = 0;

        if (managers.size() <= 1) {
            throw new IllegalStateException("Cannot delete the last manager.");
        }

        if(oldId == newId)
        {
            throw new IllegalArgumentException("Please choose two different ids.");
        }
        for(int a = 0; a < managers.size();a++)
        {
            if(managers.get(a).getEmployeeId() == oldId)
            {
                oldExists = true;
            }
            if(managers.get(a).getEmployeeId() == newId)
            {
                newExists = true;
            }

        }
        if(!oldExists || !newExists)
        {
            throw new IllegalArgumentException("Invalid ids");
        }
        for(int i = 0; i < managers.size();i++)
        {
            if(managers.get(i).getEmployeeId() == oldId)
            {cashiers = managers.get(i).getCashiers();
                inventory = managers.get(i).getInventory();
                sectors = managers.get(i).getSectors();
                id = i;
                System.out.println("Manager account deleted succesfully.");

            }}
        managers.remove(managers.get(id));
        for(int j = 0; j < managers.size();j++)
        {
            if(managers.get(j).getEmployeeId() == newId)
            {
                managers.get(j).getCashiers().addAll(cashiers);
                managers.get(j).getInventory().getItemsInInventory().addAll(inventory.getItemsInInventory());
                managers.get(j).getSectors().addAll(sectors);
                System.out.println("Info added to new manager.");
                return;
            }
        }
    }

}
