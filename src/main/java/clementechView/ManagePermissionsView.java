package clementechView;

import clementechModel.Employee;
import clementechModel.Permission;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Objects;

public class ManagePermissionsView {

    private final BorderPane root = new BorderPane();
    private final BaseStyles.HeaderParts headerParts;

    private final VBox sideNav = new VBox(8);
    private final Label navTitle = new Label("Menu");
    private final Label navHome = new Label("Home");
    private final Label navManageEmployees = new Label("Your Employees");
    private final Label navViewInventory = new Label("Inventory");
    private final Label navViewSuppliers = new Label("Suppliers");
    private final Label navViewBills = new Label("All Bills");

    private final TableView<Row> table = new TableView<>();
    private final ObservableList<Row> rows = FXCollections.observableArrayList();
    private final Button saveBtn = new Button("Save Permissions");
    private final Label statusLbl = new Label("");

    private Runnable onLogout;
    private Runnable onChangePassword;

    private Runnable onHome;
    private Runnable onEmployees;
    private Runnable onInventory;
    private Runnable onSuppliers;
    private Runnable onBills;

    private Runnable onSave;

    public ManagePermissionsView(String name, String logo, String avatar) {
        Objects.requireNonNull(name, "name");

        BaseStyles.applyAppBackground(root);

        headerParts = BaseStyles.buildEmployeeHeader(name, logo, avatar);
        forceInteractive(headerParts.headerBar);

        headerParts.logoutItem.setOnAction(e -> runAction(onLogout));
        headerParts.changePasswordItem.setOnAction(e -> runAction(onChangePassword));

        root.setTop(headerParts.headerBar);
        root.setLeft(buildSideNav());
        root.setCenter(buildCenter());

        configureTable();
    }

    public Parent getRoot() {
        return root;
    }

    public void setEmployees(List<Employee> employees) {
        rows.clear();
        if (employees != null) {
            for (Employee e : employees) rows.add(new Row(e));
        }
        table.setItems(rows);
        wireConfirmationListeners();
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setStatus(String msg) {
        statusLbl.setText(msg == null ? "" : msg);
    }

    public void setOnLogout(Runnable r) {
        this.onLogout = r;
    }

    public void setOnChangePassword(Runnable r) {
        this.onChangePassword = r;
    }

    public void setOnHome(Runnable r) {
        this.onHome = r;
    }

    public void setOnManageEmployees(Runnable r) {
        this.onEmployees = r;
    }

    public void setOnViewInventory(Runnable r) {
        this.onInventory = r;
    }

    public void setOnViewSuppliers(Runnable r) {
        this.onSuppliers = r;
    }

    public void setOnViewBills(Runnable r) {
        this.onBills = r;
    }

    public void setOnSave(Runnable r) {
        this.onSave = r;
    }

    private Parent buildSideNav() {
        sideNav.setPadding(new Insets(18, 12, 18, 12));
        sideNav.setPrefWidth(200);
        sideNav.setMinWidth(190);
        sideNav.setMaxWidth(220);
        sideNav.setStyle("-fx-background-color: #1f2d3d;");

        navTitle.setStyle("-fx-text-fill: rgba(255,255,255,0.65); -fx-font-weight: 700;");
        navTitle.setFont(BaseStyles.font(14));

        styleNavItem(navHome, false);
        styleNavItem(navManageEmployees, false);
        styleNavItem(navViewInventory, false);
        styleNavItem(navViewSuppliers, false);
        styleNavItem(navViewBills, false);

        navHome.setOnMouseClicked(e -> runAction(onHome));
        navManageEmployees.setOnMouseClicked(e -> runAction(onEmployees));
        navViewInventory.setOnMouseClicked(e -> runAction(onInventory));
        navViewSuppliers.setOnMouseClicked(e -> runAction(onSuppliers));
        navViewBills.setOnMouseClicked(e -> runAction(onBills));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sideNav.getChildren().setAll(
                navTitle,
                navHome,
                navManageEmployees,
                navViewInventory,
                navViewSuppliers,
                navViewBills,
                spacer
        );

        return sideNav;
    }

    private Parent buildCenter() {
        VBox center = new VBox(12);
        center.setPadding(new Insets(18));
        center.setStyle("-fx-background-color: white;");

        Label title = new Label("Manage Permissions");
        title.setFont(BaseStyles.font(22));
        title.setStyle("-fx-text-fill: rgba(0,0,0,0.85); -fx-font-weight: 900;");

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No employees found."));
        table.setFixedCellSize(38);

        BaseStyles.styleGreenPrimaryButton(saveBtn);
        saveBtn.setPrefWidth(200);
        saveBtn.setOnAction(e -> runAction(onSave));

        statusLbl.setFont(BaseStyles.font(14));
        statusLbl.setStyle("-fx-text-fill: rgba(0,0,0,0.65); -fx-font-weight: 700;");

        HBox bottom = new HBox(12, saveBtn, statusLbl);
        bottom.setAlignment(Pos.CENTER_LEFT);

        center.getChildren().addAll(title, table, bottom);
        VBox.setVgrow(table, Priority.ALWAYS);
        return center;
    }

    private void configureTable() {
        table.getColumns().clear();

        TableColumn<Row, String> nameCol = new TableColumn<>("Employee");
        nameCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().fullName));

        TableColumn<Row, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().username));

        TableColumn<Row, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().type));

        table.getColumns().addAll(nameCol, userCol, typeCol);

        Permission[] perms = Permission.values();

        for (int i = 0; i < perms.length; i++) {
            final int idx = i;
            final Permission p = perms[i];

            TableColumn<Row, Boolean> col = new TableColumn<>(p.name());
            col.setCellValueFactory(cell -> {
                Row r = cell.getValue();
                return r == null ? new SimpleBooleanProperty(false) : r.perm[idx];
            });

            col.setCellFactory(tc -> {
                CheckBoxTableCell<Row, Boolean> cell = new CheckBoxTableCell<>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            });

            col.setEditable(true);
            table.getColumns().add(col);
        }

        table.setItems(rows);
        table.setEditable(true);
    }

    public static final class Row {
        public final Employee employee;
        public final String fullName;
        public final String username;
        public final String type;

        public final SimpleBooleanProperty[] perm;

        boolean wired = false;
        boolean suppressConfirm = false;

        Row(Employee e) {
            this.employee = e;

            String fn = safe(e == null ? "" : e.getFullName());
            String un = safe(e == null ? "" : e.getUsername());

            this.fullName = fn.isBlank() ? "Employee" : fn;
            this.username = un;
            this.type = e == null ? "" : e.getClass().getSimpleName();

            Permission[] perms = Permission.values();
            this.perm = new SimpleBooleanProperty[perms.length];

            for (int i = 0; i < perms.length; i++) {
                boolean has = false;
                try {
                    has = e != null && e.getPermissions() != null && e.getPermissions().contains(perms[i]);
                } catch (Throwable ignored) {
                }
                this.perm[i] = new SimpleBooleanProperty(has);
            }
        }
    }

    private void styleNavItem(Label label, boolean selected) {
        label.setCursor(Cursor.HAND);
        label.setPadding(new Insets(10, 10, 10, 12));
        label.setFont(BaseStyles.font(15));
        label.setStyle(selected
                ? """
                  -fx-text-fill: white;
                  -fx-font-weight: 700;
                  -fx-background-color: rgba(255,255,255,0.08);
                  -fx-background-radius: 10;
                  """
                : """
                  -fx-text-fill: rgba(255,255,255,0.85);
                  -fx-font-weight: 600;
                  -fx-background-color: transparent;
                  -fx-background-radius: 10;
                  """
        );
    }

    private void wireConfirmationListeners() {
        Permission[] perms = Permission.values();

        for (Row r : rows) {
            if (r == null) continue;
            if (r.wired) continue;
            r.wired = true;

            for (int i = 0; i < perms.length; i++) {
                final int idx = i;
                final Permission p = perms[i];

                r.perm[idx].addListener((obs, oldVal, newVal) -> {
                    if (r.suppressConfirm) return;

                    boolean oldB = Boolean.TRUE.equals(oldVal);
                    boolean newB = Boolean.TRUE.equals(newVal);
                    if (oldB == newB) return;

                    if (needsRoleConfirmation(r, p)) {
                        boolean ok = confirmPermissionChange(r, p, newB);
                        if (!ok) {
                            r.suppressConfirm = true;
                            r.perm[idx].set(oldB);
                            r.suppressConfirm = false;
                        }
                    }
                });
            }
        }
    }

    private boolean needsRoleConfirmation(Row row, Permission p) {
        if (row == null || row.employee == null || p == null) return false;

        String type = row.type == null ? "" : row.type.trim().toLowerCase();
        boolean isCashier = type.contains("cashier");
        boolean isManager = type.contains("manager");

        boolean managerOnly = isManagerOnlyPermission(p);
        boolean cashierOnly = isCashierOnlyPermission(p);

        return (isCashier && managerOnly) || (isManager && cashierOnly);
    }

    private boolean isManagerOnlyPermission(Permission p) {
        String n = p.name();
        return n.equals("ADD_ITEM")
                || n.equals("APPLY_DISCOUNT")
                || n.equals("VIEW_STATS")
                || n.equals("ADD_CASHIER");
    }

    private boolean isCashierOnlyPermission(Permission p) {
        return p.name().equals("CREATE_BILL");
    }

    private boolean confirmPermissionChange(Row row, Permission p, boolean enabling) {
        String employeeName = row.fullName == null ? "Employee" : row.fullName;
        String role = row.type == null ? "" : row.type;

        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirm Permission Change");
        a.setHeaderText("Are you sure?");
        a.setContentText(
                (enabling ? "Grant" : "Revoke") +
                        " permission " + p.name() +
                        " for " + employeeName + " (" + role + ")?"
        );

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        a.getButtonTypes().setAll(yes, no);

        return a.showAndWait().orElse(no) == yes;
    }

    private static void runAction(Runnable r) {
        if (r != null) r.run();
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static void forceInteractive(Node n) {
        if (n == null) return;
        n.setMouseTransparent(false);
        n.setDisable(false);
        n.setPickOnBounds(true);
        if (n instanceof Parent p) {
            for (Node c : p.getChildrenUnmodifiable()) {
                forceInteractive(c);
            }
        }
    }
}
