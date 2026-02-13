package clementechController;

import clementechModel.Administrator;
import clementechModel.Cashier;
import clementechModel.DataStorage;
import clementechModel.Manager;
import clementechView.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Objects;

public class SceneChange implements Navigator {

    private final Stage stage;
    private AuthService auth;

    private final Deque<Parent> history = new ArrayDeque<>();

    public SceneChange(Stage stage) {
        this.stage = Objects.requireNonNull(stage, "stage");
        this.auth = new AuthService();
    }

    private void setRoot(Parent root) {
        if (stage.getScene() == null) {
            stage.setScene(new Scene(root, 1000, 700));
        } else {
            stage.getScene().setRoot(root);
        }
        forceInteractive(stage.getScene().getRoot());
    }

    private static void forceInteractive(javafx.scene.Node n) {
        if (n == null) return;
        n.setMouseTransparent(false);
        n.setDisable(false);
        n.setPickOnBounds(true);
        if (n instanceof javafx.scene.Parent p) {
            for (javafx.scene.Node c : p.getChildrenUnmodifiable()) forceInteractive(c);
        }
    }

    private void pushCurrentRoot() {
        if (stage.getScene() != null && stage.getScene().getRoot() != null) {
            history.push(stage.getScene().getRoot());
        }
    }

    private void goBackOrLogin() {
        if (!history.isEmpty()) setRoot(history.pop());
        else showLogin();
    }

    //LOGIN
    @Override
    public void showLogin() {
        history.clear();
        this.auth = new AuthService();
        LoginController controller = new LoginController(auth, this);
        setRoot(controller.getView());
    }

    // CASHIER HOME
    @Override
    public void showCashierHome(Cashier cashier) {
        Objects.requireNonNull(cashier, "cashier");

        CashierView view = new CashierView(
                cashier.getFullName(),
                "/logo/clementech.png",
                "/logo/cashier3.png"
        );

        view.setOnCheckout(() -> showCheckout(cashier));
        view.setOnTodaysBills(() -> showBillView(cashier));
        view.setOnLogout(this::showLogin);
        view.setOnChangePassword(() -> showChangePassword(cashier));

        setRoot(view.getRoot());
    }

    // MANAGER HOME
    @Override
    public void showManagerHome(Manager manager) {
        Objects.requireNonNull(manager, "manager");
        ManagerController controller = new ManagerController(manager, this);
        setRoot(controller.getView());
    }

    //  ADMIN HOME
    @Override
    public void showAdminHome(Administrator admin) {
        Objects.requireNonNull(admin, "admin");
        AdminController controller = new AdminController(admin, this);
        setRoot(controller.getView());
    }

    //  CHECKOUT
    public void showCheckout(Cashier cashier) {
        Objects.requireNonNull(cashier, "cashier");
        CheckoutController controller = new CheckoutController(cashier, this);
        setRoot(controller.getView());
    }

    //  CHANGE PASSWORD
    public void showChangePassword(Cashier cashier) {
        Objects.requireNonNull(cashier, "cashier");
        pushCurrentRoot();

        ChangePassView view = new ChangePassView();
        view.setRequireOldPassword(!cashier.needsPasswordChange());

        view.setOnBack(this::goBackOrLogin);
        view.setOnSave(() -> {
            String oldP = view.getOldPassword();
            String newP = view.getNewPassword();
            String confirm = view.getConfirmPassword();

            String err = validatePasswords(newP, confirm, view.isRequireOldPassword() ? oldP : null);
            if (err != null) { view.setStatus(err); return; }

            boolean ok = updateCashierPasswordInStorage(cashier, oldP, newP);
            if (!ok) { view.setStatus("Wrong current password."); return; }

            view.setStatus("Password changed!");
            goBackOrLogin();
        });

        setRoot(view.getRoot());
    }

    @Override
    public void showChangePassword(Manager manager) {
        Objects.requireNonNull(manager, "manager");
        pushCurrentRoot();

        ChangePassView view = new ChangePassView();
        view.setRequireOldPassword(!manager.needsPasswordChange());

        view.setOnBack(this::goBackOrLogin);
        view.setOnSave(() -> {
            String oldP = view.getOldPassword();
            String newP = view.getNewPassword();
            String confirm = view.getConfirmPassword();

            String err = validatePasswords(newP, confirm, view.isRequireOldPassword() ? oldP : null);
            if (err != null) { view.setStatus(err); return; }

            boolean ok = updateManagerPasswordInStorage(manager, oldP, newP);
            if (!ok) { view.setStatus("Wrong current password."); return; }

            view.setStatus("Password changed!");
            goBackOrLogin();
        });

        setRoot(view.getRoot());
    }

    @Override
    public void showChangePassword(Administrator admin) {
        Objects.requireNonNull(admin, "admin");
        pushCurrentRoot();

        ChangePassView view = new ChangePassView();
        view.setRequireOldPassword(!admin.needsPasswordChange());

        view.setOnBack(this::goBackOrLogin);
        view.setOnSave(() -> {
            String oldP = view.getOldPassword();
            String newP = view.getNewPassword();
            String confirm = view.getConfirmPassword();

            String err = validatePasswords(newP, confirm, view.isRequireOldPassword() ? oldP : null);
            if (err != null) { view.setStatus(err); return; }

            boolean ok = updateAdminPasswordInStorage(admin, oldP, newP);
            if (!ok) { view.setStatus("Wrong current password."); return; }

            view.setStatus("Password changed!");
            goBackOrLogin();
        });

        setRoot(view.getRoot());
    }

    //  BILLS
    @Override
    public void showBillView(Cashier cashier) {
        Objects.requireNonNull(cashier, "cashier");

        BillController controller = new BillController(
                cashier,
                () -> showCashierHome(cashier),
                () -> showCheckout(cashier),
                this::showLogin,
                () -> showChangePassword(cashier)
        );
        setRoot(controller.getView());
    }

    @Override
    public void showBillView(Manager manager) {
        Objects.requireNonNull(manager, "manager");

        BillController controller = new BillController(
                manager,
                () -> showManagerHome(manager),
                () -> showManageStocks(manager),
                () -> showDiscounts(manager),
                () -> showSuppliers(manager),
                this::showLogin,
                () -> showChangePassword(manager)
        );

        setRoot(controller.getView());
    }

    @Override
    public void showBillView(Administrator admin) {
        Objects.requireNonNull(admin, "admin");

        BillController controller = new BillController(
                admin,
                () -> showAdminHome(admin),
                () -> showSuppliers(admin),
                () -> showManageEmployee(admin),
                () -> showManagePermissions(admin),
                () -> showInventory(admin),
                this::showLogin,
                () -> showChangePassword(admin)
        );

        setRoot(controller.getView());
    }

    //  MANAGER PAGES
    @Override
    public void showManageStocks(Manager manager) {
        Objects.requireNonNull(manager, "manager");
        ManageStocksController c = new ManageStocksController(manager, this);
        setRoot(c.getView());
    }

    @Override
    public void showDiscounts(Manager manager) {
        Objects.requireNonNull(manager, "manager");

        DiscountController controller = new DiscountController(
                manager,
                () -> showManagerHome(manager),
                () -> showManageStocks(manager),
                () -> showBillView(manager),
                () -> showSuppliers(manager),
                this::showLogin,
                () -> showChangePassword(manager)
        );

        setRoot(controller.getView());
    }

    @Override
    public void showSuppliers(Manager manager) {
        Objects.requireNonNull(manager, "manager");

        SupplierView view = new SupplierView(
                UserRole.MANAGER,
                manager.getFullName(),
                "/logo/clementech.png",
                "/logo/manager.png"
        );

        view.setOnLogout(this::showLogin);
        view.setOnChangePassword(() -> showChangePassword(manager));

        view.setOnHome(() -> showManagerHome(manager));
        view.setOnManageStocks(() -> showManageStocks(manager));
        view.setOnSetDiscounts(() -> showDiscounts(manager));
        view.setOnCheckSales(() -> showBillView(manager));

        new SupplierController(view, manager, this);

        setRoot(view.getRoot());
    }

    @Override
    public void showSuppliers(Administrator admin) {
        Objects.requireNonNull(admin, "admin");

        SupplierView view = new SupplierView(
                UserRole.ADMIN,
                admin.getFullName(),
                "/logo/clementech.png",
                "/logo/admin.png"
        );

        view.setOnLogout(this::showLogin);
        view.setOnChangePassword(() -> showChangePassword(admin));

        view.setOnHome(() -> showAdminHome(admin));
        view.setOnManageEmployees(() -> showManageEmployee(admin));
        view.setOnManagePermissions(() -> showManagePermissions(admin));
        view.setOnViewInventory(() -> showInventory(admin));
        view.setOnBills(() -> showBillView(admin));

        new SupplierController(view, admin, this);

        setRoot(view.getRoot());
    }

    @Override
    public void showItemsSectors(Manager manager, String supplierName) {
        Objects.requireNonNull(manager, "manager");
        ItemsSectorsController c = new ItemsSectorsController(manager, supplierName, this);
        setRoot(c.getView());
    }

    @Override
    public void showItemsSectors(Administrator admin, String supplierName) {
        Objects.requireNonNull(admin, "admin");
        ItemsSectorsController c = new ItemsSectorsController(admin, supplierName, this);
        setRoot(c.getView());
    }

    //  helpers
    private static String validatePasswords(String newP, String confirm, String oldPRequired) {
        if (oldPRequired != null && oldPRequired.isBlank()) return "Enter your current password.";
        if (newP == null || newP.isBlank()) return "New password cannot be empty.";
        if (confirm == null || confirm.isBlank()) return "Please confirm your new password.";
        if (!newP.equals(confirm)) return "Passwords do not match.";
        if (newP.length() < 4) return "Password must be at least 4 characters.";
        return null;
    }

    private static boolean updateCashierPasswordInStorage(Cashier cashier, String oldP, String newP) {
        ArrayList<Cashier> list = DataStorage.loadCashiers();
        for (Cashier c : list) {
            if (c.getEmployeeId() == cashier.getEmployeeId()) {
                String before = c.getPassword();
                c.changePassword(oldP, newP);
                DataStorage.saveCashiers(list);
                return !Objects.equals(before, c.getPassword());
            }
        }
        return false;
    }

    private static boolean updateManagerPasswordInStorage(Manager manager, String oldP, String newP) {
        ArrayList<Manager> list = DataStorage.loadManagers();
        for (Manager m : list) {
            if (m.getEmployeeId() == manager.getEmployeeId()) {
                String before = m.getPassword();
                m.changePassword(oldP, newP);
                DataStorage.saveManagers(list);
                return !Objects.equals(before, m.getPassword());
            }
        }
        return false;
    }

    private static boolean updateAdminPasswordInStorage(Administrator admin, String oldP, String newP) {
        Administrator stored = DataStorage.loadAdmin();
        if (stored == null) return false;

        boolean samePerson =
                stored.getEmployeeId() == admin.getEmployeeId()
                        || stored.getUsername().equalsIgnoreCase(admin.getUsername());

        if (!samePerson) return false;

        String before = stored.getPassword();
        stored.changePassword(oldP, newP);
        DataStorage.saveAdmin(stored);

        return !Objects.equals(before, stored.getPassword());
    }

    @Override
    public void showManageEmployee(Administrator admin) {
        ManageEmployeeView view = new ManageEmployeeView(
                admin.getFullName(),
                "/logo/clementech.png",
                "/logo/admin.png"
        );
        view.setOnLogout(this::showLogin);
        view.setOnHome(() -> showAdminHome(admin));
        view.setOnBills(() -> showBillView(admin));
        view.setOnSuppliers(() -> showSuppliers(admin));
        view.setOnInventory(() -> showInventory(admin));
        view.setOnChangePassword(() -> showChangePassword(admin));
        view.setOnPermissions(() -> showManagePermissions(admin));
        new ManageEmployeeController(view);
        setRoot(view.getRoot());
    }

    @Override
    public void showAdminBills(Administrator admin) {
        showBillView(admin);
    }

    @Override
    public void showInventory(Administrator admin) {
        Objects.requireNonNull(admin, "admin");
        ManageStocksController controller = new ManageStocksController(admin, this);
        setRoot(controller.getView());
    }

    @Override
    public void showManagePermissions(Administrator admin) {
        Objects.requireNonNull(admin, "admin");

        ManagePermissionsView view = new ManagePermissionsView(
                admin.getFullName(),
                "/logo/clementech.png",
                "/logo/admin.png"
        );

        new ManagePermissionsController(view, admin, this);
        setRoot(view.getRoot());
    }

    @SuppressWarnings("unused")
    private static void popup(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
