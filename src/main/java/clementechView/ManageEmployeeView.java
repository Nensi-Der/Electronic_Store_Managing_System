package clementechView;

import clementechModel.Cashier;
import clementechModel.Manager;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class ManageEmployeeView {

    private final BorderPane root = new BorderPane();
    private final BaseStyles.HeaderParts headerParts;
    private Runnable onLogout;
    private Runnable onChangePassword;

    // ---- Side nav (Label-based for BaseStyles) ----
    private final Label navHome = new Label("Home");
    private final Label navBills = new Label("Bills");
    private final Label navSuppliers = new Label("Suppliers");
    private final Label navInventory = new Label("Inventory");
    private final Label navPermissions = new Label("Permissions");

    private Runnable onHome, onBills, onSuppliers, onInventory, onPermissions;

    // ---- Table + buttons ----
    private final TableView<EmployeeRow> table = new TableView<>();
    private final Button saveBtn = new Button("Save Changes");
    private final Button addBtn = new Button("Add Employee");
    private final Button deleteBtn = new Button("Delete Employee");

    // Columns
    private final TableColumn<EmployeeRow, Number> colId = new TableColumn<>("ID");
    private final TableColumn<EmployeeRow, String> colFirst = new TableColumn<>("First Name");
    private final TableColumn<EmployeeRow, String> colLast = new TableColumn<>("Last Name");
    private final TableColumn<EmployeeRow, LocalDate> colDob = new TableColumn<>("Birthday");
    private final TableColumn<EmployeeRow, String> colPhone = new TableColumn<>("Phone");
    private final TableColumn<EmployeeRow, String> colEmail = new TableColumn<>("Email");
    private final TableColumn<EmployeeRow, Number> colSalary = new TableColumn<>("Salary");
    private final TableColumn<EmployeeRow, String> colJob = new TableColumn<>("Job");
    private final TableColumn<EmployeeRow, Number> colManagerId = new TableColumn<>("Manager ID");

    // optional status label (nice feedback)
    private final Label status = new Label("");

    public ManageEmployeeView(String adminFullName, String logoPath, String avatarPath) {
        BaseStyles.applyAppBackground(root);

        headerParts = BaseStyles.buildEmployeeHeader(
                nonBlank(adminFullName, "Administrator"),
                "/logo/clementech.png",
                "/logo/admin.png"
        );
        headerParts.setOnLogout(() -> { if (onLogout != null) onLogout.run(); });
        headerParts.setOnChangePassword(() -> { if (onChangePassword != null) onChangePassword.run(); });

        root.setTop(headerParts.headerBar);

        root.setLeft(buildSideNav());
        root.setCenter(buildCenter());

        // highlight current page
        BaseStyles.setSideNavSelected(navHome, navHome, navBills, navSuppliers, navInventory, navPermissions);
    }

    // ===================== Layout =====================

    private Parent buildSideNav() {
        // install side nav styling + create nav
        VBox side = BaseStyles.buildSideNav("Menu", navHome, navBills, navSuppliers, navInventory, navPermissions);

        navHome.setOnMouseClicked(e -> { if (onHome != null) onHome.run(); });
        navBills.setOnMouseClicked(e -> { if (onBills != null) onBills.run(); });
        navSuppliers.setOnMouseClicked(e -> { if (onSuppliers != null) onSuppliers.run(); });
        navInventory.setOnMouseClicked(e -> { if (onInventory != null) onInventory.run(); });
        navPermissions.setOnMouseClicked(e -> { if (onPermissions != null) onPermissions.run(); });

        return side;
    }

    private Parent buildCenter() {
        Label title = new Label("Manage Employees");
        BaseStyles.styleOrangeTitle(title, 28);

        BaseStyles.stylePrimaryButton(saveBtn);
        BaseStyles.stylePrimaryButton(addBtn);
        BaseStyles.stylePrimaryButton(deleteBtn);

        HBox buttons = new HBox(10, addBtn, deleteBtn, BaseStyles.hSpacer(), saveBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        status.setWrapText(true);
        BaseStyles.styleAlertLabel(status, false);

        VBox header = new VBox(8, title, buttons, status);
        header.setPadding(new Insets(12, 18, 10, 18));

        configureTable();

        VBox box = new VBox(10, header, table);
        BaseStyles.styleRightPanel(box);

        VBox.setVgrow(table, Priority.ALWAYS);
        return box;
    }

    private void configureTable() {
        table.setEditable(true);
        BaseStyles.styleStandardTable(table, "No employees to show.");

        // cell value factories
        colId.setCellValueFactory(d -> d.getValue().employeeIdProperty());
        colFirst.setCellValueFactory(d -> d.getValue().firstNameProperty());
        colLast.setCellValueFactory(d -> d.getValue().lastNameProperty());
        colDob.setCellValueFactory(d -> d.getValue().dateOfBirthProperty());
        colPhone.setCellValueFactory(d -> d.getValue().phoneProperty());
        colEmail.setCellValueFactory(d -> d.getValue().emailProperty());
        colSalary.setCellValueFactory(d -> d.getValue().salaryProperty());
        colJob.setCellValueFactory(d -> d.getValue().jobProperty());
        colManagerId.setCellValueFactory(d -> d.getValue().managerIdProperty());

        // EDITING behavior

        // ID: integer
        colId.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override public String toString(Number object) { return object == null ? "" : String.valueOf(object.intValue()); }
            @Override public Number fromString(String string) {
                String s = string == null ? "" : string.trim();
                if (s.isBlank()) return 0;
                return Integer.parseInt(s);
            }
        }));
        colId.setOnEditCommit(e -> e.getRowValue().setEmployeeId(e.getNewValue().intValue()));

        // text columns
        colFirst.setCellFactory(TextFieldTableCell.forTableColumn());
        colFirst.setOnEditCommit(e -> e.getRowValue().setFirstName(safe(e.getNewValue())));

        colLast.setCellFactory(TextFieldTableCell.forTableColumn());
        colLast.setOnEditCommit(e -> e.getRowValue().setLastName(safe(e.getNewValue())));

        colPhone.setCellFactory(TextFieldTableCell.forTableColumn());
        colPhone.setOnEditCommit(e -> e.getRowValue().setPhone(safe(e.getNewValue())));

        colEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        colEmail.setOnEditCommit(e -> e.getRowValue().setEmail(safe(e.getNewValue())));

        // DOB: string converter YYYY-MM-DD
        colDob.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<LocalDate>() {
            @Override public String toString(LocalDate d) { return d == null ? "" : d.toString(); }
            @Override public LocalDate fromString(String s) {
                String v = s == null ? "" : s.trim();
                if (v.isBlank()) return null;
                try { return LocalDate.parse(v); }
                catch (DateTimeParseException ex) { return null; }
            }
        }));
        colDob.setOnEditCommit(e -> e.getRowValue().setDateOfBirth(e.getNewValue()));

        // salary: double
        colSalary.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override public String toString(Number object) { return object == null ? "" : String.valueOf(object.doubleValue()); }
            @Override public Number fromString(String string) {
                String s = string == null ? "" : string.trim();
                if (s.isBlank()) return 0.0;
                return Double.parseDouble(s);
            }
        }));
        colSalary.setOnEditCommit(e -> e.getRowValue().setSalary(e.getNewValue().doubleValue()));

        // job: dropdown CASHIER/MANAGER
        colJob.setCellFactory(ComboBoxTableCell.forTableColumn("MANAGER", "CASHIER"));
        colJob.setOnEditCommit(e -> e.getRowValue().setJob(safe(e.getNewValue()).toUpperCase()));

        // managerId: integer (only meaningful for CASHIER)
        colManagerId.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>() {
            @Override public String toString(Number object) { return object == null ? "" : String.valueOf(object.intValue()); }
            @Override public Number fromString(String string) {
                String s = string == null ? "" : string.trim();
                if (s.isBlank()) return 0;
                return Integer.parseInt(s);
            }
        }));
        colManagerId.setOnEditCommit(e -> e.getRowValue().setManagerId(e.getNewValue().intValue()));

        // widths (clean spacing)
        colId.setPrefWidth(80);
        colFirst.setPrefWidth(140);
        colLast.setPrefWidth(140);
        colDob.setPrefWidth(120);
        colPhone.setPrefWidth(140);
        colEmail.setPrefWidth(220);
        colSalary.setPrefWidth(120);
        colJob.setPrefWidth(110);
        colManagerId.setPrefWidth(110);

        table.getColumns().setAll(
                colId, colFirst, colLast, colDob, colPhone, colEmail, colSalary, colJob, colManagerId
        );
    }

    // ===================== Controller API =====================

    public Parent getRoot() { return root; }

    public TableView<EmployeeRow> table() { return table; }
    public Button saveBtn() { return saveBtn; }
    public Button addBtn() { return addBtn; }
    public Button deleteBtn() { return deleteBtn; }

    public void setStatus(String msg) {
        status.setText(msg == null ? "" : msg);
    }

    public void setOnHome(Runnable r) { onHome = Objects.requireNonNull(r); }
    public void setOnBills(Runnable r) { onBills = Objects.requireNonNull(r); }
    public void setOnSuppliers(Runnable r) { onSuppliers = Objects.requireNonNull(r); }
    public void setOnInventory(Runnable r) { onInventory = Objects.requireNonNull(r); }
    public void setOnPermissions(Runnable r) { onPermissions = Objects.requireNonNull(r); }

    public void setOnLogout(Runnable r) { onLogout = Objects.requireNonNull(r); }
    public void setOnChangePassword(Runnable r) { onChangePassword = Objects.requireNonNull(r); }

    // ===================== Row Model =====================

    public static final class EmployeeRow {
        private final IntegerProperty employeeId = new SimpleIntegerProperty(0);
        private final StringProperty firstName = new SimpleStringProperty("");
        private final StringProperty lastName = new SimpleStringProperty("");
        private final ObjectProperty<LocalDate> dateOfBirth = new SimpleObjectProperty<>(null);
        private final StringProperty phone = new SimpleStringProperty("");
        private final StringProperty email = new SimpleStringProperty("");
        private final DoubleProperty salary = new SimpleDoubleProperty(0.0);
        private final StringProperty job = new SimpleStringProperty(""); // MANAGER / CASHIER
        private final IntegerProperty managerId = new SimpleIntegerProperty(0); // for cashiers

        public static EmployeeRow fromManager(Manager m) {
            EmployeeRow r = new EmployeeRow();
            r.setEmployeeId(m.getEmployeeId());
            r.setFirstName(m.getFirstName());
            r.setLastName(m.getLastName());
            r.setDateOfBirth(m.getDateOfBirth());
            r.setPhone(m.getPhone());
            r.setEmail(m.getEmail());
            r.setSalary(m.getSalary());
            r.setJob("MANAGER");
            r.setManagerId(0);
            return r;
        }

        public static EmployeeRow fromCashier(Cashier c) {
            EmployeeRow r = new EmployeeRow();
            r.setEmployeeId(c.getEmployeeId());
            r.setFirstName(c.getFirstName());
            r.setLastName(c.getLastName());
            r.setDateOfBirth(c.getDateOfBirth());
            r.setPhone(c.getPhone());
            r.setEmail(c.getEmail());
            r.setSalary(c.getSalary());
            r.setJob("CASHIER");
            r.setManagerId(c.getManager() == null ? 0 : c.getManager().getEmployeeId());
            return r;
        }

        public IntegerProperty employeeIdProperty() { return employeeId; }
        public StringProperty firstNameProperty() { return firstName; }
        public StringProperty lastNameProperty() { return lastName; }
        public ObjectProperty<LocalDate> dateOfBirthProperty() { return dateOfBirth; }
        public StringProperty phoneProperty() { return phone; }
        public StringProperty emailProperty() { return email; }
        public DoubleProperty salaryProperty() { return salary; }
        public StringProperty jobProperty() { return job; }
        public IntegerProperty managerIdProperty() { return managerId; }

        public int getEmployeeId() { return employeeId.get(); }
        public void setEmployeeId(int v) { employeeId.set(v); }

        public String getFirstName() { return firstName.get(); }
        public void setFirstName(String v) { firstName.set(v); }

        public String getLastName() { return lastName.get(); }
        public void setLastName(String v) { lastName.set(v); }

        public LocalDate getDateOfBirth() { return dateOfBirth.get(); }
        public void setDateOfBirth(LocalDate v) { dateOfBirth.set(v); }

        public String getPhone() { return phone.get(); }
        public void setPhone(String v) { phone.set(v); }

        public String getEmail() { return email.get(); }
        public void setEmail(String v) { email.set(v); }

        public double getSalary() { return salary.get(); }
        public void setSalary(double v) { salary.set(v); }

        public String getJob() { return job.get(); }
        public void setJob(String v) { job.set(v); }

        public int getManagerId() { return managerId.get(); }
        public void setManagerId(int v) { managerId.set(v); }

        public String getFullName() {
            return (getFirstName() + " " + getLastName()).trim();
        }
    }

    // ===================== tiny utils =====================

    private static String safe(String s) { return s == null ? "" : s.trim(); }
    private static String nonBlank(String s, String fallback) {
        String v = safe(s);
        return v.isBlank() ? fallback : v;
    }
}
