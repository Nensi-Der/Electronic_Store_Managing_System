package clementechController;

import clementechModel.Cashier;
import clementechModel.DataStorage;
import clementechModel.Manager;
import clementechView.ManageEmployeeView;
import clementechView.ManageEmployeeView.EmployeeRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ManageEmployeeController {

    private final ManageEmployeeView view;
    private final ObservableList<EmployeeRow> rows = FXCollections.observableArrayList();

    public ManageEmployeeController(ManageEmployeeView view) {
        this.view = Objects.requireNonNull(view, "view");

        loadRows();
        view.table().setItems(rows);

        view.saveBtn().setOnAction(e -> onSave());
        view.addBtn().setOnAction(e -> onAdd());
        view.deleteBtn().setOnAction(e -> onDelete());
    }

    private void loadRows() {
        rows.clear();

        ArrayList<Manager> managers = DataStorage.loadManagers();
        ArrayList<Cashier> cashiers = DataStorage.loadCashiers();

        for (Manager m : managers) rows.add(EmployeeRow.fromManager(m));
        for (Cashier c : cashiers) rows.add(EmployeeRow.fromCashier(c));
    }

    private void onSave() {
        try {
            Set<Integer> ids = new HashSet<>();
            for (EmployeeRow r : rows) {
                if (r == null) continue;

                int id = r.getEmployeeId();
                if (id <= 0) throw new IllegalArgumentException("Employee ID must be positive.");
                if (!ids.add(id)) throw new IllegalArgumentException("Duplicate Employee ID: " + id);

                validateRow(r);
            }

            ArrayList<Manager> oldManagers = DataStorage.loadManagers();
            Map<Integer, Manager> oldManagerById = new HashMap<>();
            for (Manager m : oldManagers) {
                if (m != null) oldManagerById.put(m.getEmployeeId(), m);
            }

            ArrayList<Manager> newManagers = new ArrayList<>();
            Map<Integer, Manager> newManagerById = new HashMap<>();

            for (EmployeeRow r : rows) {
                if (r == null) continue;
                if (!"MANAGER".equalsIgnoreCase(safe(r.getJob()))) continue;

                int id = r.getEmployeeId();
                Manager m = oldManagerById.get(id);

                if (m == null) {
                    m = new Manager(
                            id,
                            r.getFirstName(),
                            r.getLastName(),
                            r.getDateOfBirth(),
                            r.getPhone(),
                            r.getEmail(),
                            r.getSalary()
                    );
                } else {
                    m.setFirstName(r.getFirstName());
                    m.setLastName(r.getLastName());
                    m.setDateOfBirth(r.getDateOfBirth());
                    m.setPhone(r.getPhone());
                    m.setEmail(r.getEmail());
                    m.setSalary(r.getSalary());
                }

                newManagers.add(m);
                newManagerById.put(id, m);
            }

            if (newManagers.isEmpty()) {
                throw new IllegalArgumentException("You must have at least ONE manager in the system.");
            }

            ArrayList<Cashier> oldCashiers = DataStorage.loadCashiers();
            Map<Integer, Cashier> oldCashierById = new HashMap<>();
            Map<Integer, Integer> oldCashierToManagerId = new HashMap<>();

            for (Cashier c : oldCashiers) {
                if (c == null) continue;
                oldCashierById.put(c.getEmployeeId(), c);
                int mid = (c.getManager() == null) ? 0 : c.getManager().getEmployeeId();
                oldCashierToManagerId.put(c.getEmployeeId(), mid);
            }

            ArrayList<Cashier> newCashiers = new ArrayList<>();

            for (EmployeeRow r : rows) {
                if (r == null) continue;
                if (!"CASHIER".equalsIgnoreCase(safe(r.getJob()))) continue;

                int id = r.getEmployeeId();

                int managerId = r.getManagerId();
                if (managerId <= 0) {
                    managerId = oldCashierToManagerId.getOrDefault(id, 0);
                }

                Manager manager = newManagerById.get(managerId);
                if (manager == null) manager = newManagers.get(0);

                Cashier c = oldCashierById.get(id);
                if (c == null) {
                    c = new Cashier(
                            id,
                            r.getFirstName(),
                            r.getLastName(),
                            r.getDateOfBirth(),
                            r.getPhone(),
                            r.getEmail(),
                            r.getSalary(),
                            manager
                    );
                } else {
                    c.setFirstName(r.getFirstName());
                    c.setLastName(r.getLastName());
                    c.setDateOfBirth(r.getDateOfBirth());
                    c.setPhone(r.getPhone());
                    c.setEmail(r.getEmail());
                    c.setSalary(r.getSalary());
                    c.setManager(manager);
                }

                newCashiers.add(c);
            }

            DataStorage.saveManagers(newManagers);
            DataStorage.saveCashiers(newCashiers);

            info("Success", "Employee data saved successfully.");
            loadRows();
        } catch (Exception ex) {
            ex.printStackTrace();
            error("Save failed", ex.getMessage() == null ? "Employee data not saved." : ex.getMessage());
        }
    }

    private static void validateRow(EmployeeRow r) {
        if (blank(r.getFirstName()) || blank(r.getLastName()))
            throw new IllegalArgumentException("First name and last name cannot be empty.");

        if (r.getDateOfBirth() == null)
            throw new IllegalArgumentException("Date of birth cannot be empty.");

        if (blank(r.getPhone()))
            throw new IllegalArgumentException("Phone cannot be empty.");

        if (blank(r.getEmail()))
            throw new IllegalArgumentException("Email cannot be empty.");

        if (r.getSalary() < 400)
            throw new IllegalArgumentException("Salary cannot be less than 400.");

        String job = safe(r.getJob());
        if (!"MANAGER".equalsIgnoreCase(job) && !"CASHIER".equalsIgnoreCase(job))
            throw new IllegalArgumentException("Job must be CASHIER or MANAGER.");
    }

    private void onAdd() {
        try {
            Integer id = askInt("Add Employee", "Employee ID", "Enter a unique employee ID:");
            if (id == null) return;

            if (rows.stream().anyMatch(r -> r != null && r.getEmployeeId() == id)) {
                error("Duplicate ID", "Employee ID already exists: " + id);
                return;
            }

            String fn = askText("Add Employee", "First name", "First name:");
            if (fn == null) return;

            String ln = askText("Add Employee", "Last name", "Last name:");
            if (ln == null) return;

            LocalDate dob = askDate("Add Employee", "Date of Birth", "Enter date (YYYY-MM-DD):");
            if (dob == null) return;

            String phone = askText("Add Employee", "Phone", "Phone:");
            if (phone == null) return;

            String email = askText("Add Employee", "Email", "Email:");
            if (email == null) return;

            Double salary = askDouble("Add Employee", "Salary", "Salary (>= 400):");
            if (salary == null) return;

            String job = askChoice("Add Employee", "Job", "Select job:", List.of("MANAGER", "CASHIER"));
            if (job == null) return;

            if ("MANAGER".equalsIgnoreCase(job)) {
                Manager m = new Manager(id, fn, ln, dob, phone, email, salary);
                ArrayList<Manager> managers = DataStorage.loadManagers();
                managers.add(m);
                DataStorage.saveManagers(managers);
            } else {
                ArrayList<Manager> managers = DataStorage.loadManagers();
                if (managers.isEmpty()) {
                    error("No managers", "You must create a manager before adding a cashier.");
                    return;
                }
                Manager chosen = pickManager(managers);
                if (chosen == null) return;

                Cashier c = new Cashier(id, fn, ln, dob, phone, email, salary, chosen);
                ArrayList<Cashier> cashiers = DataStorage.loadCashiers();
                cashiers.add(c);
                DataStorage.saveCashiers(cashiers);
            }

            loadRows();
            info("Success", "Employee created successfully.\nUsername/password were generated automatically.");
        } catch (Exception ex) {
            ex.printStackTrace();
            error("Add failed", ex.getMessage() == null ? "Could not add employee." : ex.getMessage());
        }
    }

    private void onDelete() {
        EmployeeRow selected = view.table().getSelectionModel().getSelectedItem();
        if (selected == null) {
            warn("No selection", "Select an employee first.");
            return;
        }

        String job = safe(selected.getJob());

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Employee");
        confirm.setHeaderText("Delete " + safe(selected.getFullName()) + " (" + job + ")?");
        confirm.setContentText("This will remove the employee from the system.");
        Optional<ButtonType> ans = confirm.showAndWait();
        if (ans.isEmpty() || ans.get() != ButtonType.OK) return;

        try {
            if ("CASHIER".equalsIgnoreCase(job)) {
                deleteCashier(selected.getEmployeeId());
                loadRows();
                info("Deleted", "Cashier removed successfully.");
                return;
            }

            if ("MANAGER".equalsIgnoreCase(job)) {
                deleteManagerWithReplacement(selected.getEmployeeId());
                loadRows();
                info("Deleted", "Manager removed and cashiers reassigned.");
                return;
            }

            error("Delete failed", "Unknown job type: " + job);
        } catch (Exception ex) {
            ex.printStackTrace();
            error("Delete failed", ex.getMessage() == null ? "Could not delete employee." : ex.getMessage());
        }
    }

    private void deleteCashier(int cashierId) {
        ArrayList<Cashier> cashiers = DataStorage.loadCashiers();
        cashiers.removeIf(c -> c != null && c.getEmployeeId() == cashierId);
        DataStorage.saveCashiers(cashiers);
    }

    private void deleteManagerWithReplacement(int managerIdToDelete) {
        ArrayList<Manager> managers = DataStorage.loadManagers();
        ArrayList<Cashier> cashiers = DataStorage.loadCashiers();

        Manager victim = null;
        for (Manager m : managers) {
            if (m != null && m.getEmployeeId() == managerIdToDelete) { victim = m; break; }
        }
        if (victim == null) throw new IllegalArgumentException("Manager not found in storage.");

        if (managers.size() <= 1) {
            throw new IllegalArgumentException("You cannot delete the only manager. Create a new manager first.");
        }

        ArrayList<Manager> candidates = new ArrayList<>();
        for (Manager m : managers) {
            if (m != null && m.getEmployeeId() != managerIdToDelete) candidates.add(m);
        }

        Manager replacement = pickManager(candidates);
        if (replacement == null) throw new IllegalArgumentException("Manager deletion cancelled.");

        for (Cashier c : cashiers) {
            if (c == null) continue;
            int mid = (c.getManager() == null) ? -1 : c.getManager().getEmployeeId();
            if (mid == managerIdToDelete) {
                c.setManager(replacement);
            }
        }

        managers.removeIf(m -> m != null && m.getEmployeeId() == managerIdToDelete);

        DataStorage.saveManagers(managers);
        DataStorage.saveCashiers(cashiers);
    }

    private static void info(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private static void warn(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private static void error(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private static String askText(String title, String header, String content) {
        TextInputDialog d = new TextInputDialog();
        d.setTitle(title);
        d.setHeaderText(header);
        d.setContentText(content);
        Optional<String> r = d.showAndWait();
        if (r.isEmpty()) return null;
        String s = r.get().trim();
        return s.isBlank() ? null : s;
    }

    private static Integer askInt(String title, String header, String content) {
        while (true) {
            String s = askText(title, header, content);
            if (s == null) return null;
            try { return Integer.parseInt(s.trim()); }
            catch (NumberFormatException e) { error("Invalid number", "Enter a valid integer."); }
        }
    }

    private static Double askDouble(String title, String header, String content) {
        while (true) {
            String s = askText(title, header, content);
            if (s == null) return null;
            try { return Double.parseDouble(s.trim()); }
            catch (NumberFormatException e) { error("Invalid number", "Enter a valid number."); }
        }
    }

    private static LocalDate askDate(String title, String header, String content) {
        while (true) {
            String s = askText(title, header, content);
            if (s == null) return null;
            try { return LocalDate.parse(s.trim()); }
            catch (DateTimeParseException e) { error("Invalid date", "Use YYYY-MM-DD."); }
        }
    }

    private static String askChoice(String title, String header, String content, List<String> options) {
        ChoiceDialog<String> d = new ChoiceDialog<>(options.get(0), options);
        d.setTitle(title);
        d.setHeaderText(header);
        d.setContentText(content);
        Optional<String> r = d.showAndWait();
        return r.orElse(null);
    }

    private static Manager pickManager(ArrayList<Manager> managers) {
        if (managers == null || managers.isEmpty()) return null;

        List<String> labels = new ArrayList<>();
        Map<String, Manager> map = new HashMap<>();
        for (Manager m : managers) {
            if (m == null) continue;
            String label = m.getEmployeeId() + " - " + safe(m.getFullName());
            labels.add(label);
            map.put(label, m);
        }

        if (labels.isEmpty()) return null;

        ChoiceDialog<String> d = new ChoiceDialog<>(labels.get(0), labels);
        d.setTitle("Select Manager");
        d.setHeaderText("Choose a manager");
        d.setContentText("Manager:");
        Optional<String> r = d.showAndWait();
        if (r.isEmpty()) return null;
        return map.get(r.get());
    }

    private static boolean blank(String s) {
        return s == null || s.trim().isBlank();
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
