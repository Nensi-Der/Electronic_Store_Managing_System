package clementechView;

import java.util.Objects;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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

public class ManageStocksView {

    private final BorderPane root = new BorderPane();

    // ===== HEADER =====
    private final BaseStyles.HeaderParts headerParts;
    private Runnable onLogout;
    private Runnable onChangePassword;

    // ===== ROLE =====
    private final UserRole role; // MANAGER or ADMIN (inventory view)

    // ===== LEFT NAV (MANAGER) =====
    private final VBox sideNav = new VBox(10);

    private final Button homeBtn = new Button("Home");
    private final Button checkSalesBtn = new Button("Check Sales");
    private final Button setDiscountsBtn = new Button("Set Discounts");
    private final Button modifySuppliersBtn = new Button("Modify Suppliers");

    private Runnable onHome;
    private Runnable onCheckSales;
    private Runnable onSetDiscounts;
    private Runnable onModifySuppliers;

    // ===== LEFT NAV (ADMIN) =====
    private final Button adminHomeBtn = new Button("Home");
    private final Button viewSuppliersBtn = new Button("View Suppliers");
    private final Button manageEmployeeBtn = new Button("Manage Employees");
    private final Button managePermissionsBtn = new Button("Manage Permissions");
    private final Button viewInventoryBtn = new Button("View Inventory");

    private Runnable onAdminHome;
    private Runnable onViewSuppliers;
    private Runnable onManageEmployee;
    private Runnable onManagePermissions;
    private Runnable onViewInventory;

    // ===== RIGHT CONTENT =====
    private final TextField searchField = new TextField();
    private final Button searchBtn = new Button("Search");

    private final Label lowStockBanner = new Label();

    private final TableView<ItemRow> table = new TableView<>();
    private final TableColumn<ItemRow, String> colID = new TableColumn<>("ID");
    private final TableColumn<ItemRow, String> colName = new TableColumn<>("Name");
    private final TableColumn<ItemRow, String> colCategory = new TableColumn<>("Category");
    private final TableColumn<ItemRow, Number> colPurchasePrice = new TableColumn<>("Purchase Price");
    private final TableColumn<ItemRow, Number> colSellingPrice = new TableColumn<>("Selling Price");
    private final TableColumn<ItemRow, Number> colStock = new TableColumn<>("Stock");
    private final TableColumn<ItemRow, String> colSupplier = new TableColumn<>("Supplier");

    private final Button addStockBtn = new Button("Add Stock");
    private final Button deleteItemBtn = new Button("Delete Item");
    private final Button addNewItemBtn = new Button("Add New Item");

    private final HBox actionsRow = new HBox(12, addStockBtn, deleteItemBtn, addNewItemBtn);

    private Runnable onAddStock;
    private Runnable onDeleteItem;
    private Runnable onAddNewItem;

    // ===== DATA / SEARCH =====
    private final ObservableList<ItemRow> masterItems = FXCollections.observableArrayList();
    private final FilteredList<ItemRow> filteredItems = new FilteredList<>(masterItems, r -> true);

    private int lowStockThreshold = 3;

    // ===================== CONSTRUCTORS =====================
    // Manager version (existing usage)
    public ManageStocksView(String managerFullName, String logoPath, String avatarPath) {
        this(UserRole.MANAGER, managerFullName, logoPath, avatarPath);
    }

    // Admin version (use this from SceneChange for inventory view)
    public static ManageStocksView forAdminInventory(String adminFullName, String logoPath, String avatarPath) {
        ManageStocksView v = new ManageStocksView(UserRole.ADMIN, adminFullName, logoPath, avatarPath);
        v.enableReadOnly(); // force read-only
        return v;
    }

    // Main constructor
    public ManageStocksView(UserRole role, String fullName, String logoPath, String avatarPath) {
        this.role = Objects.requireNonNull(role, "role");
        String name = safeName(fullName, role == UserRole.ADMIN ? "Administrator" : "Manager");

        root.setStyle("-fx-background-color: white;");

        headerParts = BaseStyles.buildEmployeeHeader(name, logoPath, avatarPath);
        headerParts.setOnLogout(() -> { if (onLogout != null) onLogout.run(); });
        headerParts.setOnChangePassword(() -> { if (onChangePassword != null) onChangePassword.run(); });

        root.setTop(headerParts.headerBar);

        buildSideNav();
        root.setLeft(sideNav);

        buildRightContent();
        root.setCenter(buildMainLayout());

        // Live banner update
        masterItems.addListener((ListChangeListener<ItemRow>) c -> refreshLowStockBanner());
        refreshLowStockBanner();

        // If Admin, enforce read-only layout (no actions row)
        if (this.role == UserRole.ADMIN) enableReadOnly();
    }

    // ===================== LAYOUT =====================
    private Parent buildMainLayout() {
        VBox right = new VBox(12);
        right.setPadding(new Insets(18, 18, 18, 18));
        right.setAlignment(Pos.TOP_LEFT);

        HBox searchRow = new HBox(10, searchField, searchBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        actionsRow.setAlignment(Pos.CENTER_LEFT);
        actionsRow.setPadding(new Insets(6, 0, 0, 0));

        right.getChildren().addAll(
                searchRow,
                lowStockBanner,
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

        if (role == UserRole.MANAGER) {
            styleNavButton(homeBtn);
            styleNavButton(checkSalesBtn);
            styleNavButton(setDiscountsBtn);
            styleNavButton(modifySuppliersBtn);

            homeBtn.setOnAction(e -> { if (onHome != null) onHome.run(); });
            checkSalesBtn.setOnAction(e -> { if (onCheckSales != null) onCheckSales.run(); });
            setDiscountsBtn.setOnAction(e -> { if (onSetDiscounts != null) onSetDiscounts.run(); });
            modifySuppliersBtn.setOnAction(e -> { if (onModifySuppliers != null) onModifySuppliers.run(); });

            sideNav.getChildren().addAll(
                    navTitle, spacer(10),
                    homeBtn, checkSalesBtn, setDiscountsBtn, modifySuppliersBtn
            );
        } else {
            // ADMIN SIDE NAV
            styleNavButton(adminHomeBtn);
            styleNavButton(viewSuppliersBtn);
            styleNavButton(manageEmployeeBtn);
            styleNavButton(managePermissionsBtn);
            styleNavButton(viewInventoryBtn);

            adminHomeBtn.setOnAction(e -> { if (onAdminHome != null) onAdminHome.run(); });
            viewSuppliersBtn.setOnAction(e -> { if (onViewSuppliers != null) onViewSuppliers.run(); });
            manageEmployeeBtn.setOnAction(e -> { if (onManageEmployee != null) onManageEmployee.run(); });
            managePermissionsBtn.setOnAction(e -> { if (onManagePermissions != null) onManagePermissions.run(); });
            viewInventoryBtn.setOnAction(e -> { if (onViewInventory != null) onViewInventory.run(); });

            sideNav.getChildren().addAll(
                    navTitle, spacer(10),
                    adminHomeBtn,
                    viewSuppliersBtn,
                    manageEmployeeBtn,
                    managePermissionsBtn,
                    viewInventoryBtn
            );
        }
    }

    private void buildRightContent() {
        // Search UI
        searchField.setPromptText("Search by name, category, supplier, price, or stock.");
        searchField.setPrefWidth(520);
        searchField.setMinHeight(42);
        searchField.setFont(BaseStyles.font(16));

        BaseStyles.stylePrimaryButton(searchBtn);
        searchBtn.setPrefWidth(140);

        searchBtn.setOnAction(e -> applySearch());
        searchField.setOnAction(e -> applySearch());

        // Low stock banner
        lowStockBanner.setWrapText(true);
        lowStockBanner.setPadding(new Insets(10, 12, 10, 12));
        lowStockBanner.setStyle("""
                -fx-background-color: #fff7ed;
                -fx-border-color: #fed7aa;
                -fx-border-width: 1;
                -fx-background-radius: 10;
                -fx-border-radius: 10;
                -fx-font-weight: 800;
                -fx-text-fill: #9a3412;
                """);
        lowStockBanner.setFont(BaseStyles.font(14));

        // Table
        colID.setCellValueFactory(d -> d.getValue().idProperty());
        colName.setCellValueFactory(d -> d.getValue().nameProperty());
        colCategory.setCellValueFactory(d -> d.getValue().categoryProperty());
        colPurchasePrice.setCellValueFactory(d -> d.getValue().purchasePriceProperty());
        colSellingPrice.setCellValueFactory(d -> d.getValue().sellingPriceProperty());
        colStock.setCellValueFactory(d -> d.getValue().stockProperty());
        colSupplier.setCellValueFactory(d -> d.getValue().supplierProperty());

        table.getColumns().setAll(colID, colName, colCategory, colPurchasePrice, colSellingPrice, colStock, colSupplier);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(520);

        SortedList<ItemRow> sorted = new SortedList<>(filteredItems);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        // Highlight low-stock rows
        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(ItemRow item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                    return;
                }

                boolean low = item.getStock() < lowStockThreshold;
                boolean selected = isSelected();

                if (low && selected) {
                    setStyle("""
                    -fx-background-color: #FF2828;
                    -fx-text-fill: white;
                    """);
                } else if (low) {
                    setStyle("""
                    -fx-background-color: #fff1f2;
                    -fx-text-fill: #991b1b;
                    """);
                } else {
                    setStyle("");
                }
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);
                ItemRow item = getItem();
                if (item == null) { setStyle(""); return; }

                boolean low = item.getStock() < lowStockThreshold;

                if (low && selected) {
                    setStyle("""
                    -fx-background-color: #FF2828;
                    -fx-text-fill: white;
                    """);
                } else if (low) {
                    setStyle("""
                    -fx-background-color: #fff1f2;
                    -fx-text-fill: #991b1b;
                    """);
                } else {
                    setStyle("");
                }
            }
        });

        // Action buttons under table (Manager only)
        styleActionButton(addStockBtn);
        styleActionButton(deleteItemBtn);
        styleActionButton(addNewItemBtn);

        addStockBtn.setOnAction(e -> { if (onAddStock != null) onAddStock.run(); });
        deleteItemBtn.setOnAction(e -> { if (onDeleteItem != null) onDeleteItem.run(); });
        addNewItemBtn.setOnAction(e -> { if (onAddNewItem != null) onAddNewItem.run(); });
    }

    // ===================== READ ONLY MODE =====================
    // Admin Inventory: hide + remove action buttons row entirely
    public void enableReadOnly() {
        addNewItemBtn.setVisible(false);
        addNewItemBtn.setManaged(false);

        deleteItemBtn.setVisible(false);
        deleteItemBtn.setManaged(false);

        addStockBtn.setVisible(false);
        addStockBtn.setManaged(false);

        actionsRow.setVisible(false);
        actionsRow.setManaged(false);
    }

    // ===================== SEARCH / ALERTS =====================
    private void applySearch() {
        String q = safe(searchField.getText()).toLowerCase();

        if (q.isBlank()) {
            filteredItems.setPredicate(r -> true);
        } else {
            filteredItems.setPredicate(r -> {
                if (r == null) return false;
                return contains(r.getId(), q)|| contains(r.getName(), q)
                        || contains(r.getCategory(), q)
                        || contains(r.getSupplier(), q)
                        || contains(String.valueOf(r.getStock()), q)
                        || contains(String.valueOf(r.getPurchasePrice()), q)
                        || contains(String.valueOf(r.getSellingPrice()), q);
            });
        }

        refreshLowStockBanner();
    }

    private void refreshLowStockBanner() {
        int lowCount = 0;
        for (ItemRow r : filteredItems) {
            if (r != null && r.getStock() < lowStockThreshold) lowCount++;
        }

        if (lowCount == 0) {
            lowStockBanner.setText("Stock levels look good. No items below the threshold (" + lowStockThreshold + ").");
        } else if (lowCount == 1) {
            lowStockBanner.setText("Low stock alert: 1 item below the threshold (" + lowStockThreshold + "). Restock recommended.");
        } else {
            lowStockBanner.setText("Low stock alert: " + lowCount
                    + " items below the threshold (" + lowStockThreshold + "). Restock recommended.");
        }
    }

    // ===================== STYLING HELPERS =====================
    private void styleNavButton(Button b) {
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setCursor(Cursor.HAND);
        b.setFont(BaseStyles.font(16));
        b.setPadding(new Insets(12, 14, 12, 14));

        b.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: rgba(255,255,255,0.92);
                -fx-font-weight: 800;
                -fx-background-radius: 10;
                """);

        b.setOnMouseEntered(e -> b.setStyle("""
                -fx-background-color: rgba(255,255,255,0.10);
                -fx-text-fill: rgba(255,255,255,0.98);
                -fx-font-weight: 800;
                -fx-background-radius: 10;
                """));

        b.setOnMouseExited(e -> b.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: rgba(255,255,255,0.92);
                -fx-font-weight: 800;
                -fx-background-radius: 10;
                """));
    }

    private void styleActionButton(Button b) {
        BaseStyles.stylePrimaryButton(b);
        b.setPrefWidth(170);
        b.setPrefHeight(44);
        b.setFont(BaseStyles.font(16));
    }

    private Region spacer(double h) {
        Region r = new Region();
        r.setMinHeight(h);
        r.setPrefHeight(h);
        r.setMaxHeight(h);
        return r;
    }

    // ===================== API =====================
    public Parent getRoot() { return root; }

    public void setOnLogout(Runnable r) { this.onLogout = Objects.requireNonNull(r, "onLogout"); }
    public void setOnChangePassword(Runnable r) { this.onChangePassword = Objects.requireNonNull(r, "onChangePassword"); }

    // Manager nav callbacks
    public void setOnHome(Runnable r) { this.onHome = Objects.requireNonNull(r, "onHome"); }
    public void setOnCheckSales(Runnable r) { this.onCheckSales = Objects.requireNonNull(r, "onCheckSales"); }
    public void setOnSetDiscounts(Runnable r) { this.onSetDiscounts = Objects.requireNonNull(r, "onSetDiscounts"); }
    public void setOnModifySuppliers(Runnable r) { this.onModifySuppliers = Objects.requireNonNull(r, "onModifySuppliers"); }

    // Admin nav callbacks
    public void setOnAdminHome(Runnable r) { this.onAdminHome = Objects.requireNonNull(r, "onAdminHome"); }
    public void setOnViewSuppliers(Runnable r) { this.onViewSuppliers = Objects.requireNonNull(r, "onViewSuppliers"); }
    public void setOnManageEmployee(Runnable r) { this.onManageEmployee = Objects.requireNonNull(r, "onManageEmployee"); }
    public void setOnManagePermissions(Runnable r) { this.onManagePermissions = Objects.requireNonNull(r, "onManagePermissions"); }
    public void setOnViewInventory(Runnable r) { this.onViewInventory = Objects.requireNonNull(r, "onViewInventory"); }
    // Manager actions
    public void setOnAddStock(Runnable r) { this.onAddStock = Objects.requireNonNull(r, "onAddStock"); }
    public void setOnDeleteItem(Runnable r) { this.onDeleteItem = Objects.requireNonNull(r, "onDeleteItem"); }
    public void setOnAddNewItem(Runnable r) { this.onAddNewItem = Objects.requireNonNull(r, "onAddNewItem"); }

    public void setEmployeeName(String fullName) {
        headerParts.setEmployeeName(safeName(fullName, role == UserRole.ADMIN ? "Administrator" : "Manager"));
    }

    public void setLowStockThreshold(int threshold) {
        this.lowStockThreshold = Math.max(0, threshold);
        refreshLowStockBanner();
        table.refresh();
    }

    public void enableAdminMode() {
        // hide manager nav buttons completely
        homeBtn.setVisible(false);
        checkSalesBtn.setVisible(false);
        setDiscountsBtn.setVisible(false);
        modifySuppliersBtn.setVisible(false);
    }

    public void setItems(ObservableList<ItemRow> items) {
        masterItems.setAll(items == null ? FXCollections.observableArrayList() : items);
        refreshLowStockBanner();
    }

    public ObservableList<ItemRow> getItems() { return masterItems; }

    public ItemRow getSelectedItem() {
        return table.getSelectionModel().getSelectedItem();
    }

    public TextField getSearchField() { return searchField; }
    public Button getSearchBtn() { return searchBtn; }
    public TableView<ItemRow> getTable() { return table; }

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
    public static final class ItemRow {
        private final StringProperty id = new SimpleStringProperty("");
        private final StringProperty name = new SimpleStringProperty("");
        private final StringProperty category = new SimpleStringProperty("");
        private final DoubleProperty purchasePrice = new SimpleDoubleProperty(0.0);
        private final DoubleProperty sellingPrice = new SimpleDoubleProperty(0.0);
        private final IntegerProperty stock = new SimpleIntegerProperty(0);
        private final StringProperty supplier = new SimpleStringProperty("");

        public ItemRow(String id, String name, String category, double purchasePrice, double sellingPrice, int stock, String supplier) {
            this.id.set(id);
            this.name.set(name);
            this.category.set(category);
            this.purchasePrice.set(purchasePrice);
            this.sellingPrice.set(sellingPrice);
            this.stock.set(stock);
            this.supplier.set(supplier);
        }

        public StringProperty idProperty() { return id; }
        public StringProperty nameProperty() { return name; }
        public StringProperty categoryProperty() { return category; }
        public DoubleProperty purchasePriceProperty() { return purchasePrice; }
        public DoubleProperty sellingPriceProperty() { return sellingPrice; }
        public IntegerProperty stockProperty() { return stock; }
        public StringProperty supplierProperty() { return supplier; }

        public String getId() { return id.get(); }
        public String getName() { return name.get(); }
        public String getCategory() { return category.get(); }
        public double getPurchasePrice() { return purchasePrice.get(); }
        public double getSellingPrice() { return sellingPrice.get(); }
        public int getStock() { return stock.get(); }
        public String getSupplier() { return supplier.get(); }
    }
}
