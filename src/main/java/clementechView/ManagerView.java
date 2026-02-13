package clementechView;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Objects;

import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import clementechView.BaseStyles;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class ManagerView {

    private final BorderPane root = new BorderPane();
    //reusable header from BaseStyles
    private final BaseStyles.HeaderParts headerParts;
    //CENTER
    private final Label welcome = new Label();
    private final Label prompt = new Label();
    private final Button manageStockBtn     = new Button("Manage Stock");
    private final Button checkSalesBtn      = new Button("Check Sales");
    private final Button setDiscountsBtn    = new Button("Set Discounts");
    private final Button modifySuppliersBtn = new Button("Modify Suppliers");
    //Callbacks for controller wiring
    private Runnable onManageStock;
    private Runnable onCheckSales;
    private Runnable onSetDiscounts;
    private Runnable onModifySuppliers;
    private Runnable onLogout;
    private Runnable onChangePassword;


    public ManagerView(String managerFullName, String logoPath, String avatarPath) {
        this(safeName(managerFullName, "Manager"), logoPath, avatarPath, true);
    }

    public ManagerView(String managerFirstName, String managerLastName,
                       String logoPath, String avatarPath) {
        this(
                safeName((safe(managerFirstName) + " " + safe(managerLastName)).trim(), "Manager"),
                logoPath,
                avatarPath,
                true
        );
    }

    private ManagerView(String managerFullName, String logoPath, String avatarPath, boolean internal) {
        root.setStyle("-fx-background-color: white;");
        headerParts = BaseStyles.buildEmployeeHeader(managerFullName, logoPath, avatarPath);
        headerParts.setOnLogout(() -> { if (onLogout != null) onLogout.run(); });
        headerParts.setOnChangePassword(() -> { if (onChangePassword != null) onChangePassword.run(); });

        root.setTop(headerParts.headerBar);
        buildCenterContent(managerFullName);
    }

    private void buildCenterContent(String managerFullName) {
        welcome.setText("Welcome, " + managerFullName + "!");
        BaseStyles.styleCenterTitle(welcome);

        prompt.setText("What do you feel like doing today?");
        BaseStyles.styleCenterSubtitle(prompt);

        //orange big buttons
        BaseStyles.styleBigOrangeButton(manageStockBtn);
        BaseStyles.styleBigOrangeButton(checkSalesBtn);
        BaseStyles.styleBigOrangeButton(setDiscountsBtn);
        BaseStyles.styleBigOrangeButton(modifySuppliersBtn);

        // Actions
        manageStockBtn.setOnAction(e -> { if (onManageStock != null) onManageStock.run(); });
        checkSalesBtn.setOnAction(e -> { if (onCheckSales != null) onCheckSales.run(); });
        setDiscountsBtn.setOnAction(e -> { if (onSetDiscounts != null) onSetDiscounts.run(); });
        modifySuppliersBtn.setOnAction(e -> { if (onModifySuppliers != null) onModifySuppliers.run(); });

        // Left column (2 buttons)
        VBox leftCol = new VBox(18, manageStockBtn, checkSalesBtn);
        leftCol.setAlignment(Pos.CENTER);

        // Right column (2 buttons)
        VBox rightCol = new VBox(18, setDiscountsBtn, modifySuppliersBtn);
        rightCol.setAlignment(Pos.CENTER);

        // Two columns side-by-side, centered
        HBox twoCols = new HBox(40, leftCol, rightCol);
        twoCols.setAlignment(Pos.CENTER);

        // Whole center content
        VBox center = new VBox(
                10,
                welcome,
                prompt,
                spacer(20),
                twoCols
        );
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));

        root.setCenter(center);
    }

    private Region spacer(double height) {
        Region r = new Region();
        r.setMinHeight(height);
        r.setPrefHeight(height);
        r.setMaxHeight(height);
        return r;
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static String safeName(String fullName, String fallback) {
        String s = safe(fullName);
        return s.isBlank() ? fallback : s;
    }

    // ===== Controller-friendly API =====
    public Parent getRoot() { return root; }

    public void setManagerName(String fullName) {
        String name = safeName(fullName, "Manager");
        headerParts.setEmployeeName(name);
        welcome.setText("Welcome, " + name + "!");
    }

    public void setOnManageStock(Runnable r)     { this.onManageStock = Objects.requireNonNull(r); }
    public void setOnCheckSales(Runnable r)      { this.onCheckSales = Objects.requireNonNull(r); }
    public void setOnSetDiscounts(Runnable r)    { this.onSetDiscounts = Objects.requireNonNull(r); }
    public void setOnModifySuppliers(Runnable r) { this.onModifySuppliers = Objects.requireNonNull(r); }
    public void setOnLogout(Runnable r)          { this.onLogout = Objects.requireNonNull(r); }
    public void setOnChangePassword(Runnable r) { this.onChangePassword = Objects.requireNonNull(r, "onChangePassword"); }

    //getters
    public Button getManageStockBtn() { return manageStockBtn; }
    public Button getCheckSalesBtn() { return checkSalesBtn; }
    public Button getSetDiscountsBtn() { return setDiscountsBtn; }
    public Button getModifySuppliersBtn() { return modifySuppliersBtn; }

    public static final class EmployeeRow {

        private final IntegerProperty employeeId = new SimpleIntegerProperty(0);
        private final StringProperty firstName = new SimpleStringProperty("");
        private final StringProperty lastName = new SimpleStringProperty("");
        private final ObjectProperty<java.time.LocalDate> dateOfBirth = new SimpleObjectProperty<>(null);
        private final StringProperty phone = new SimpleStringProperty("");
        private final StringProperty email = new SimpleStringProperty("");
        private final DoubleProperty salary = new SimpleDoubleProperty(0.0);

        // REQUIRED by your spec: cashier/manager
        private final StringProperty job = new SimpleStringProperty(""); // "CASHIER" or "MANAGER"

        // helpful for reassignment logic (cashiers belong to a manager)
        private final IntegerProperty managerId = new SimpleIntegerProperty(0);

        public EmployeeRow() {}

        // ---------- factories ----------
        public static EmployeeRow fromManager(clementechModel.Manager m) {
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

        public static EmployeeRow fromCashier(clementechModel.Cashier c) {
            EmployeeRow r = new EmployeeRow();
            r.setEmployeeId(c.getEmployeeId());
            r.setFirstName(c.getFirstName());
            r.setLastName(c.getLastName());
            r.setDateOfBirth(c.getDateOfBirth());
            r.setPhone(c.getPhone());
            r.setEmail(c.getEmail());
            r.setSalary(c.getSalary());
            r.setJob("CASHIER");

            // Your Cashier has field `Manager manager;` in the model you pasted
            int mid = (c.getManager() == null) ? 0 : c.getManager().getEmployeeId();
            r.setManagerId(mid);

            return r;
        }

        // ---------- properties ----------
        public IntegerProperty employeeIdProperty() { return employeeId; }
        public StringProperty firstNameProperty() { return firstName; }
        public StringProperty lastNameProperty() { return lastName; }
        public ObjectProperty<LocalDate> dateOfBirthProperty() { return dateOfBirth; }
        public StringProperty phoneProperty() { return phone; }
        public StringProperty emailProperty() { return email; }
        public DoubleProperty salaryProperty() { return salary; }
        public StringProperty jobProperty() { return job; }
        public IntegerProperty managerIdProperty() { return managerId; }

        // ---------- getters ----------
        public int getEmployeeId() { return employeeId.get(); }
        public String getFirstName() { return firstName.get(); }
        public String getLastName() { return lastName.get(); }
        public java.time.LocalDate getDateOfBirth() { return dateOfBirth.get(); }
        public String getPhone() { return phone.get(); }
        public String getEmail() { return email.get(); }
        public double getSalary() { return salary.get(); }
        public String getJob() { return job.get(); }
        public int getManagerId() { return managerId.get(); }

        public String getFullName() {
            return (getFirstName() + " " + getLastName()).trim();
        }

        // ---------- setters ----------
        public void setEmployeeId(int v) { employeeId.set(v); }
        public void setFirstName(String v) { firstName.set(v == null ? "" : v.trim()); }
        public void setLastName(String v) { lastName.set(v == null ? "" : v.trim()); }
        public void setDateOfBirth(java.time.LocalDate v) { dateOfBirth.set(v); }
        public void setPhone(String v) { phone.set(v == null ? "" : v.trim()); }
        public void setEmail(String v) { email.set(v == null ? "" : v.trim()); }
        public void setSalary(double v) { salary.set(v); }
        public void setJob(String v) { job.set(v == null ? "" : v.trim().toUpperCase()); }
        public void setManagerId(int v) { managerId.set(v); }
    }

}
