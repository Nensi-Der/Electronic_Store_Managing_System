package clementechModel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

public class DataStorage {



    public static void saveManagers(ArrayList<Manager> managerList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("managers.dat"))) {
            Manager[] managerArray = managerList.toArray(new Manager[0]);
            oos.writeObject(managerArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Manager> loadManagers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("managers.dat"))) {
            Manager[] managerArray = (Manager[]) ois.readObject();
            ArrayList<Manager> managerList = new ArrayList<>();
            for (Manager m : managerArray) {
                managerList.add(m);
            }
            return managerList;
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }


    public static void saveCashiers(ArrayList<Cashier> cashiers) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("cashiers.dat"))) {
            Cashier[] cashierArray = cashiers.toArray(new Cashier[0]);
            oos.writeObject(cashierArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Cashier> loadCashiers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("cashiers.dat"))) {
            Cashier[] cashierArray = (Cashier[]) ois.readObject();
            ArrayList<Cashier> cashierList = new ArrayList<>();
            for (Cashier c : cashierArray) cashierList.add(c);
            return cashierList;
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }


    public static void saveItems(ArrayList<Item> items) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("items.dat"))) {
            Item[] itemArray = items.toArray(new Item[0]);
            oos.writeObject(itemArray);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save items.dat: " + e.getMessage(), e);
        }
    }

    public static ArrayList<Item> loadItems() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("items.dat"))) {
            Item[] itemArray = (Item[]) ois.readObject();
            ArrayList<Item> itemList = new ArrayList<>();
            for (Item i : itemArray) itemList.add(i);
            return itemList;
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public static void saveBills(ArrayList<Bill> billList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("bills.dat"))) {
            Bill[] billArray = billList.toArray(new Bill[0]);
            oos.writeObject(billArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Bill> loadBills() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("bills.dat"))) {
            Bill[] billArray = (Bill[]) ois.readObject();
            ArrayList<Bill> billList = new ArrayList<>();

            int max = 0;
            for (Bill b : billArray) {
                if (b != null) {
                    billList.add(b);
                    if (b.getBillNumber() > max) max = b.getBillNumber();
                }
            }

            //make future bills continue after the largest number
            Bill.syncNextBillNumber(max + 1);

            return billList;
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
    public static void saveSuppliers(ArrayList<Supplier> supplierList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("suppliers.dat"))) {
            Supplier[] supplierArray = supplierList.toArray(new Supplier[0]);
            oos.writeObject(supplierArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Supplier> loadSuppliers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("suppliers.dat"))) {
            Supplier[] supplierArray = (Supplier[]) ois.readObject();
            ArrayList<Supplier> supplierList = new ArrayList<>();
            for (Supplier s : supplierArray) {
                supplierList.add(s);
            }
            return supplierList;
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }


    public static void saveAdmin(Administrator admin) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("admin.dat"))) {
            oos.writeObject(admin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Administrator loadAdmin() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("admin.dat"))) {
            return (Administrator) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Auto-create admin if not found
            Administrator admin = new Administrator(
                    1,
                    "Nensi",
                    "Der",
                    LocalDate.of(2005, 10, 17),
                    "0691234567",
                    "nensi.der@example.com",
                    1200.00
            );
            saveAdmin(admin);
            return admin;
        }
    }


}