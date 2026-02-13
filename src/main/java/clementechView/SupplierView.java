package clementechView;

import java.util.Objects;
import java.util.function.Consumer;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class SupplierView {

    private final BorderPane root = new BorderPane();

    //HEADER
    private final BaseStyles.HeaderParts headerParts;
    private Runnable onLogout;
    private Runnable onChangePassword;

    //LEFT NAV
    private final VBox sideNav = new VBox(10);
    private final Button homeBtn = new Button("Home");
    private final Button checkSalesBtn = new Button("Check Sales");
    private final Button setDiscountsBtn = new Button("Set Discounts");
    private final Button manageStocksBtn = new Button("Manage Stocks");
    private final Button manageEmployeesBtn = new Button("Employees");
    private final Button managePermissions = new Button("Permissions");
    private final Button viewInventoryBtn = new Button("Inventory");
    private final Button viewBillsBtn = new Button("View Bills");

    private final UserRole role;

    private final Button addSupplierBtn = new Button("Add Supplier");
    private Runnable onAddSupplier;

    private Runnable onHome;
    private Runnable onCheckSales;
    private Runnable onSetDiscounts;
    private Runnable onManageStocks;
    private Runnable onManageEmployees;
    private Runnable onManagePermissions;
    private Runnable onViewInventory;
    private Runnable onBills;

    private Consumer<SupplierRow> onOpenSupplier;

    // ===== RIGHT CONTENT =====
    private final TextField searchField = new TextField();
    private final Button searchBtn = new Button("Search");

    // Optional info banner (not low stock)
    private final Label infoBanner = new Label();

    private final TableView<SupplierRow> table = new TableView<>();
    private final TableColumn<SupplierRow, String> colName = new TableColumn<>("Name");
    private final TableColumn<SupplierRow, String> colContact = new TableColumn<>("Contact");

    // Actions under table
    private final Button editSupplierBtn = new Button("Edit Supplier");
    private final Button deleteSupplierBtn = new Button("Delete Supplier");

    private Runnable onEditSupplier;
    private Runnable onDeleteSupplier;
 // -> ItemsSectorsView

    // ===== DATA / SEARCH =====
    private final ObservableList<SupplierRow> masterSuppliers = FXCollections.observableArrayList();
    private final FilteredList<SupplierRow> filteredSuppliers = new FilteredList<>(masterSuppliers, r -> true);

    public SupplierView(String fullName, String logoPath, String avatarPath) {
        this(UserRole.MANAGER, fullName, logoPath, avatarPath);
    }

    public SupplierView(UserRole role, String fullName, String logoPath, String avatarPath) {
        this.role = Objects.requireNonNull(role, "role");
        String fallback = (role == UserRole.ADMIN) ? "Administrator" : "Manager";
        String name = safeName(fullName, fallback);

        root.setStyle("-fx-background-color: white;");

        headerParts = BaseStyles.buildEmployeeHeader(name, logoPath, avatarPath);
        headerParts.setOnLogout(() -> { if (onLogout != null) onLogout.run(); });
        headerParts.setOnChangePassword(() -> { if (onChangePassword != null) onChangePassword.run(); });

        root.setTop(headerParts.headerBar);

        buildSideNav();
        root.setLeft(sideNav);

        buildRightContent();
        root.setCenter(buildMainLayout());

        refreshBanner();
        if (role == UserRole.ADMIN) {
            enableReadOnly();
        }
    }

    // ===================== LAYOUT =====================
    private Parent buildMainLayout() {
        VBox right = new VBox(12);
        right.setPadding(new Insets(18, 18, 18, 18));
        right.setAlignment(Pos.TOP_LEFT);

        HBox searchRow = new HBox(10, searchField, searchBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        HBox actionsRow = new HBox(12, addSupplierBtn, editSupplierBtn, deleteSupplierBtn);
        actionsRow.setAlignment(Pos.CENTER_LEFT);
        actionsRow.setPadding(new Insets(6, 0, 0, 0));

        right.getChildren().addAll(
                searchRow,
                infoBanner,
                table,
                actionsRow
        );

        VBox.setVgrow(table, Priority.ALWAYS);
        return right;
    }

    private void buildSideNav() {
        sideNav.getChildren().clear();

        sideNav.setPadding(new Insets(18, 12, 18, 12));
        sideNav.setPrefWidth(200);
        sideNav.setMinWidth(190);
        sideNav.setMaxWidth(220);
        sideNav.setAlignment(Pos.TOP_LEFT);

        sideNav.setStyle("""
            -fx-background-color: #1f2d3d;
            -fx-border-color: #0b1220;
            -fx-border-width: 0 1 0 0;
            """);

        Label navTitle = new Label("Menu");
        navTitle.setStyle("-fx-text-fill: rgba(255,255,255,0.65); -fx-font-weight: 700;");
        Font f = BaseStyles.font(16);
        if (f != null) navTitle.setFont(f);

        sideNav.getChildren().addAll(navTitle, spacer(10));

        if (role == UserRole.MANAGER) {
            styleNavButton(homeBtn, false);
            styleNavButton(checkSalesBtn, false);
            styleNavButton(setDiscountsBtn, false);
            styleNavButton(manageStocksBtn, false);

            homeBtn.setOnAction(e -> { if (onHome != null) onHome.run(); });
            checkSalesBtn.setOnAction(e -> { if (onCheckSales != null) onCheckSales.run(); });
            setDiscountsBtn.setOnAction(e -> { if (onSetDiscounts != null) onSetDiscounts.run(); });
            manageStocksBtn.setOnAction(e -> { if (onManageStocks != null) onManageStocks.run(); });

            sideNav.getChildren().addAll(
                    homeBtn, checkSalesBtn, setDiscountsBtn, manageStocksBtn,
                    spacer(12)
            );
        } else {
            // ADMIN NAV
            styleNavButton(homeBtn, false);
            homeBtn.setOnAction(e -> { if (onHome != null) onHome.run(); });

            styleNavButton(manageEmployeesBtn, false);
            manageEmployeesBtn.setOnAction(e -> { if (onManageEmployees != null) onManageEmployees.run(); });

            styleNavButton(managePermissions, false);
            managePermissions.setOnAction(e -> { if (onManagePermissions != null) onManagePermissions.run(); });

            styleNavButton(viewInventoryBtn, false);
            viewInventoryBtn.setOnAction(e -> { if (onViewInventory != null) onViewInventory.run(); });

            styleNavButton(viewBillsBtn, false);
            viewBillsBtn.setOnAction(e -> { if (onBills != null) onBills.run(); });

            sideNav.getChildren().addAll(
                    homeBtn,
                    manageEmployeesBtn,
                    managePermissions,
                    viewInventoryBtn,
                    viewBillsBtn,
                    spacer(12)
            );
        }
    }

    public void setOnAddSupplier(Runnable r) { this.onAddSupplier = Objects.requireNonNull(r, "onAddSupplier"); }

    private void buildRightContent() {
        // Search UI
        searchField.setPromptText("Search supplier name or contact.");
        searchField.setPrefWidth(520);
        searchField.setMinHeight(42);
        searchField.setFont(BaseStyles.font(16));

        BaseStyles.stylePrimaryButton(searchBtn);
        searchBtn.setPrefWidth(140);
        searchBtn.setFocusTraversable(false);

        // Search actions
        searchBtn.setOnAction(e -> applySearch());
        searchField.setOnAction(e -> applySearch());

        // Info banner
        infoBanner.setWrapText(true);
        infoBanner.setPadding(new Insets(10, 12, 10, 12));
        infoBanner.setStyle("""
                -fx-background-color: rgba(0,0,0,0.04);
                -fx-border-color: rgba(0,0,0,0.10);
                -fx-border-width: 1;
                -fx-background-radius: 10;
                -fx-border-radius: 10;
                -fx-font-weight: 800;
                -fx-text-fill: rgba(0,0,0,0.70);
                """);
        infoBanner.setFont(BaseStyles.font(14));

        // Table columns
        colName.setCellValueFactory(d -> d.getValue().nameProperty());
        colContact.setCellValueFactory(d -> d.getValue().contactProperty());

        colName.setPrefWidth(300);
        colContact.setPrefWidth(400);

        table.getColumns().setAll(colName, colContact);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(520);
        table.setPlaceholder(new Label("No suppliers to show."));

        // Sorted + filtered list
        SortedList<SupplierRow> sorted = new SortedList<>(filteredSuppliers);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        //double-click to open supplier details
        table.setRowFactory(tv -> {
            TableRow<SupplierRow> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY
                        && e.getClickCount() == 2
                        && !row.isEmpty()) {
                    SupplierRow selected = row.getItem();
                    if (onOpenSupplier != null) onOpenSupplier.accept(selected);
                }
            });
            return row;
        });

        // Action buttons under table
        styleActionButton(editSupplierBtn);
        styleActionButton(deleteSupplierBtn);
        styleActionButton(addSupplierBtn);
        addSupplierBtn.setOnAction(e -> { if (onAddSupplier != null) onAddSupplier.run(); });
        editSupplierBtn.setOnAction(e -> { if (onEditSupplier != null) onEditSupplier.run(); });
        deleteSupplierBtn.setOnAction(e -> { if (onDeleteSupplier != null) onDeleteSupplier.run(); });

    }

    //SEARCH
    private void applySearch() {
        String q = safe(searchField.getText()).toLowerCase();

        if (q.isBlank()) {
            filteredSuppliers.setPredicate(r -> true);
        } else {
            filteredSuppliers.setPredicate(r -> {
                if (r == null) return false;
                return contains(r.getName(), q) || contains(r.getContact(), q);
            });
        }

        refreshBanner();
    }

    private void refreshBanner() {
        int visible = 0;
        for (SupplierRow r : filteredSuppliers) if (r != null) visible++;

        if (visible == 0) infoBanner.setText("No suppliers match your search.");
        else if (visible == 1) infoBanner.setText("Showing 1 supplier.");
        else infoBanner.setText("Showing " + visible + " suppliers.");
    }

    // ===================== STYLING HELPERS =====================
    private void styleNavButton(Button b, boolean selected) {
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setCursor(Cursor.HAND);
        b.setFont(BaseStyles.font(16));
        b.setPadding(new Insets(12, 14, 12, 14));
        b.setFocusTraversable(false);

        String base = selected
                ? """
                   -fx-background-color: rgba(255,255,255,0.08);
                   -fx-text-fill: rgba(255,255,255,0.98);
                   -fx-font-weight: 800;
                   -fx-background-radius: 10;
                   """
                : """
                   -fx-background-color: transparent;
                   -fx-text-fill: rgba(255,255,255,0.92);
                   -fx-font-weight: 800;
                   -fx-background-radius: 10;
                   """;

        b.setStyle(base);

        b.setOnMouseEntered(e -> b.setStyle("""
                -fx-background-color: rgba(255,255,255,0.10);
                -fx-text-fill: rgba(255,255,255,0.98);
                -fx-font-weight: 800;
                -fx-background-radius: 10;
                """));

        b.setOnMouseExited(e -> b.setStyle(base));
    }

    private void styleActionButton(Button b) {
        BaseStyles.stylePrimaryButton(b);
        b.setPrefWidth(190);
        b.setPrefHeight(44);
        b.setFont(BaseStyles.font(16));
        b.setFocusTraversable(false);
    }

    private Region spacer(double h) {
        Region r = new Region();
        r.setMinHeight(h);
        r.setPrefHeight(h);
        r.setMaxHeight(h);
        return r;
    }

    // ===================== API (controller-friendly) =====================
    public Parent getRoot() { return root; }

    public void setOnLogout(Runnable r) { this.onLogout = Objects.requireNonNull(r, "onLogout"); }
    public void setOnChangePassword(Runnable r) { this.onChangePassword = Objects.requireNonNull(r, "onChangePassword"); }

    public void setOnHome(Runnable r) { this.onHome = Objects.requireNonNull(r, "onHome"); }
    public void setOnCheckSales(Runnable r) { this.onCheckSales = Objects.requireNonNull(r, "onCheckSales"); }
    public void setOnSetDiscounts(Runnable r) { this.onSetDiscounts = Objects.requireNonNull(r, "onSetDiscounts"); }
    public void setOnManageStocks(Runnable r) { this.onManageStocks = Objects.requireNonNull(r, "onManageStocks"); }

    public void setOnEditSupplier(Runnable r) { this.onEditSupplier = Objects.requireNonNull(r, "onEditSupplier"); }
    public void setOnDeleteSupplier(Runnable r) { this.onDeleteSupplier = Objects.requireNonNull(r, "onDeleteSupplier"); }
    public void setOnManageEmployees(Runnable r) {
        this.onManageEmployees = Objects.requireNonNull(r, "onManageEmployees");
    }
    public void setOnBills(Runnable r) {
        this.onBills = Objects.requireNonNull(r, "onBills");
    }

    public void setOnManagePermissions(Runnable r) {
        this.onManagePermissions = Objects.requireNonNull(r, "onManagePermissions");
    }

    public void setOnViewInventory(Runnable r) {
        this.onViewInventory = Objects.requireNonNull(r, "onViewInventory");
    }

    public void setOnOpenSupplier(Consumer<SupplierRow> c) {
        this.onOpenSupplier = Objects.requireNonNull(c, "onOpenSupplier");
    }

    public void enableReadOnly() {
        addSupplierBtn.setVisible(false);
        addSupplierBtn.setManaged(false);

        editSupplierBtn.setVisible(false);
        editSupplierBtn.setManaged(false);

        deleteSupplierBtn.setVisible(false);
        deleteSupplierBtn.setManaged(false);
    }

    public void setEmployeeName(String fullName) {
        String fallback = (role == UserRole.ADMIN) ? "Administrator" : "Manager";
        headerParts.setEmployeeName(safeName(fullName, fallback));
    }

    public void setSuppliers(ObservableList<SupplierRow> suppliers) {
        masterSuppliers.setAll(suppliers == null ? FXCollections.observableArrayList() : suppliers);
        applySearch(); // keeps current search filter
    }

    public ObservableList<SupplierRow> getSuppliers() { return masterSuppliers; }

    public SupplierRow getSelectedSupplier() {
        return table.getSelectionModel().getSelectedItem();
    }

    public TextField getSearchField() { return searchField; }
    public Button getSearchBtn() { return searchBtn; }
    public TableView<SupplierRow> getTable() { return table; }

    // ===================== UTIL =====================
    private static boolean contains(String value, String q) {
        return value != null && value.toLowerCase().contains(q);
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static String safeName(String fullName, String fallback) {
        String s = safe(fullName);
        return s.isBlank() ? fallback : s;
    }

    // ===================== TABLE ROW MODEL =====================
    public static final class SupplierRow {
        private final StringProperty name = new SimpleStringProperty("");
        private final StringProperty contact = new SimpleStringProperty("");

        public SupplierRow(String name, String contact) {
            this.name.set(name);
            this.contact.set(contact);
        }


        public StringProperty nameProperty() { return name; }
        public StringProperty contactProperty() { return contact; }

        public String getName() { return name.get(); }
        public String getContact() { return contact.get(); }
    }
}
