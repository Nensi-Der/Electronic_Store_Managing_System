package clementechController;

import clementechModel.Administrator;
import clementechModel.Cashier;
import clementechModel.DataStorage;
import clementechModel.Employee;
import clementechModel.Manager;
import clementechView.ChangePassView;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.Objects;

public class ChangePassController {

    private final ChangePassView view = new ChangePassView();

    private final Employee user;
    private final Runnable goBack;

    public ChangePassController(Employee user, Runnable goBack) {
        this.user = Objects.requireNonNull(user, "user");
        this.goBack = Objects.requireNonNull(goBack, "goBack");

        // If first login -> no old password required
        view.setRequireOldPassword(!user.needsPasswordChange());

        view.setOnBack(this.goBack);
        view.setOnSave(this::handleSave);
    }

    public Parent getView() {
        return view.getRoot();
    }

    private void handleSave() {
        String oldP = view.getOldPassword();
        String newP = view.getNewPassword();
        String confirm = view.getConfirmPassword();

        // If old password is required, enforce it
        if (view.isRequireOldPassword() && (oldP == null || oldP.isBlank())) {
            view.setStatus("Enter your current password.");
            return;
        }

        if (newP == null || newP.isBlank()) {
            view.setStatus("New password cannot be empty.");
            return;
        }

        if (confirm == null || confirm.isBlank()) {
            view.setStatus("Please confirm your new password.");
            return;
        }

        if (!newP.equals(confirm)) {
            view.setStatus("Passwords do not match.");
            return;
        }

        // optional simple rule
        if (newP.length() < 4) {
            view.setStatus("Password must be at least 4 characters.");
            return;
        }

        boolean changed = persistPasswordChange(user, oldP, newP);

        if (!changed) {
            view.setStatus("Wrong current password.");
            return;
        }

        view.setStatus("Password changed!");
        goBack.run(); // return to wherever they came from
    }

    /**
     * Changes password AND saves to DataStorage.
     * Returns true if password actually changed.
     */
    private static boolean persistPasswordChange(Employee emp, String oldP, String newP) {

        // First login: old password does not matter in your Employee.changePassword
        if (oldP == null) oldP = "";

        if (emp instanceof Cashier cashier) {
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

        if (emp instanceof Manager manager) {
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

        if (emp instanceof Administrator admin) {
            // If your DataStorage uses a single admin object, adapt accordingly.
            // Example pattern (common): loadAdmin() / saveAdmin(admin)
            Administrator stored = DataStorage.loadAdmin();
            if (stored == null) return false;

            String before = stored.getPassword();
            stored.changePassword(oldP, newP);
            DataStorage.saveAdmin(stored);

            return !Objects.equals(before, stored.getPassword());
        }

        // Fallback: change on the object (no persistence if unknown type)
        String before = emp.getPassword();
        emp.changePassword(oldP, newP);
        return !Objects.equals(before, emp.getPassword());
    }
}
