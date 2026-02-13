package clementechController;

import clementechModel.Cashier;
import clementechModel.DataStorage;
import clementechModel.Employee;
import clementechModel.Manager;
import clementechModel.Permission;
import clementechView.ManagePermissionsView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ManagePermissionsController {

    private final ManagePermissionsView view;
    private final Navigator nav;
    private final clementechModel.Administrator admin; // optional, but useful for nav actions

    private final ArrayList<Cashier> cashiers;
    private final ArrayList<Manager> managers;

    public ManagePermissionsController(ManagePermissionsView view,
                                       clementechModel.Administrator admin,
                                       Navigator nav) {
        this.view = Objects.requireNonNull(view, "view");
        this.admin = Objects.requireNonNull(admin, "admin");
        this.nav = Objects.requireNonNull(nav, "nav");


        this.cashiers = DataStorage.loadCashiers();
        this.managers = DataStorage.loadManagers();

        wireNav();
        loadIntoTable();
        wireSave();
    }

    private void wireNav() {
        view.setOnLogout(nav::showLogin);
        view.setOnChangePassword(() -> nav.showChangePassword(admin));

        view.setOnHome(() -> nav.showAdminHome(admin));
        view.setOnManageEmployees(() -> nav.showManageEmployee(admin));
        view.setOnViewInventory(() -> nav.showInventory(admin));
        view.setOnViewSuppliers(() -> nav.showSuppliers(admin));
    }

    private void loadIntoTable() {
        ArrayList<Employee> employees = new ArrayList<>();
        employees.addAll(cashiers);
        employees.addAll(managers);

        view.setEmployees(employees);
        view.setStatus("");
    }

    private void wireSave() {
        view.setOnSave(() -> {
            try {
                applyPermissionsFromUI();
                DataStorage.saveCashiers(cashiers);
                DataStorage.saveManagers(managers);
                view.setStatus("Saved!");
            } catch (Throwable ex) {
                view.setStatus("Save failed: " + (ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage()));
                ex.printStackTrace();
            }
        });
    }

    private void applyPermissionsFromUI() {
        Permission[] perms = Permission.values();

        List<ManagePermissionsView.Row> rows = view.getRows();
        for (ManagePermissionsView.Row r : rows) {
            Employee e = r.employee;
            if (e == null) continue;

            // clear & rebuild (simple + safe)
            if (e.getPermissions() != null) e.getPermissions().clear();

            for (int i = 0; i < perms.length; i++) {
                boolean checked = r.perm[i].get();
                if (checked) {
                    e.getPermissions().add(perms[i]);
                }
            }
        }

        // IMPORTANT:
        // The Employee objects in rows must be the SAME objects from cashiers/managers lists.
        // We ensured that by building employees list using cashiers/managers themselves.
        // So saving those lists writes the updated permissions.
    }
}
