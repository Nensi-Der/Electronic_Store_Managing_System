package clementechController;

import clementechModel.Administrator;
import clementechModel.Cashier;
import clementechModel.Manager;

public interface Navigator {
    void showLogin();
    void showCashierHome(Cashier cashier);
    void showManagerHome(Manager manager);
    void showAdminHome(Administrator admin);

    void showCheckout(Cashier cashier);
    void showChangePassword(Cashier cashier);
    void showChangePassword(Manager manager);
    void showChangePassword(Administrator admin);

    void showBillView(Cashier cashier);
    void showBillView(Manager manager);
    void showBillView(Administrator admin);

    void showManageStocks(Manager manager);
    void showDiscounts(Manager manager);
    void showSuppliers(Manager manager);
    void showItemsSectors(Manager manager, String supplierName);

    void showManageEmployee(Administrator admin);
    void showAdminBills(Administrator admin);
    void showInventory(Administrator admin);
    void showManagePermissions(Administrator admin);
    void showSuppliers(Administrator admin);
    void showItemsSectors(Administrator admin, String supplierName);


}
