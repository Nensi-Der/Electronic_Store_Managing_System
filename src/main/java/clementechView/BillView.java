package clementechView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import clementechModel.DataStorage;
import java.util.ArrayList;
import clementechModel.Administrator;
import clementechModel.Bill;
import clementechModel.Cashier;
import clementechModel.Manager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class BillView {

    private final BorderPane root = new BorderPane();
    private final BaseStyles.HeaderParts headerParts;

    private String selectedCashier = ""; // all cashiers
    private ComboBox<String> cashierFilterBox;
    private Button cashierFilterClearBtn;

    private final VBox sideNav = new VBox(8);
    private final Label navTitle = new Label("Menu");
    private final Label navMainMenu = new Label("Home");
    private final Label navCheckout = new Label("Checkout");
    private final Label navManageStocks = new Label("Manage Stocks");
    private final Label navSetDiscounts = new Label("Set Discounts");
    private final Label navModifySuppliers = new Label("Modify Suppliers");
    private final Label navManageEmployee = new Label("ManageEmployees");
    private final Label navManagePermissions = new Label("Manage Permissions");
    private final Label navViewSuppliers = new Label("View Suppliers");
    private final Label navViewInventory = new Label("View Inventory");
    private final Label incomeValue = new Label("0.00");
    private final Label costValue = new Label("0.00");


    private final ObservableList<Bill> master = FXCollections.observableArrayList();
    private final FilteredList<Bill> filtered = new FilteredList<>(master, b -> true);

    private final TableView<Bill> table = new TableView<>();
    private final TextField searchField = new TextField();
    private final Button searchBtn = new Button("Search");

    private final HBox filterBar = new HBox(10);
    private final HBox summaryBar = new HBox(12);

    private final Label billsValue = new Label("0");
    private final Label itemsValue = new Label("0");
    private final Label revenueValue = new Label("0.00");
    private final Label extraValue = new Label("0");

    private final UserRole role;
    private final String cashierUsername;

    private BillPeriod selectedPeriod = BillPeriod.TODAY;
    private LocalDate adminFrom = LocalDate.now();
    private LocalDate adminTo = LocalDate.now();

    private Runnable onLogout;
    private Runnable onMainMenu;
    private Runnable onCheckout;
    private Runnable onManageStocks;
    private Runnable onSetDiscounts;
    private Runnable onModifySuppliers;
    private Runnable onChangePassword;
    private Runnable onManageEmployee;
    private Runnable onManagePermissions;
    private Runnable onViewSuppliers;
    private Runnable onViewInventory;

    private Consumer<BillPeriod> onManagerPeriodChanged;
    private BiConsumer<LocalDate, LocalDate> onAdminDateRangeApply;

    public BillView(Cashier cashier) {
        this(
                UserRole.CASHIER,
                cashier.getFullName(),
                cashier.getUsername(),
                "/logo/clementech.png",
                "/logo/cashier3.png"
        );
    }

    public BillView(Manager manager) {
        this(
                UserRole.MANAGER,
                manager.getFullName(),
                "",
                "/logo/clementech.png",
                "/logo/manager.png"
        );
    }

    public BillView(Administrator admin) {
        this(
                UserRole.ADMIN,
                admin.getFullName(),
                "",
                "/logo/clementech.png",
                "/logo/admin.png"
        );
    }

    public BillView(
            UserRole role,
            String fullName,
            String cashierUsername,
            String logoPath,
            String avatarPath
    ) {
        this.role = Objects.requireNonNull(role, "role");
        this.cashierUsername = cashierUsername == null ? "" : cashierUsername.trim();
        Objects.requireNonNull(fullName, "fullName");

        BaseStyles.applyAppBackground(root);

        headerParts = BaseStyles.buildEmployeeHeader(fullName, logoPath, avatarPath);
        headerParts.logoutItem.setOnAction(e -> runAction("Logout", onLogout));
        headerParts.changePasswordItem.setOnAction(e -> runAction("Change Password", onChangePassword));
        forceInteractive(headerParts.headerBar);
        root.setTop(headerParts.headerBar);
        root.setLeft(buildSideNav());
        root.setCenter(buildCenter());

        table.setItems(filtered);

        wireSearch();
        configureColumns();
        wireDoubleClickOpenPopup();

        buildRoleFilters();
        buildSummaryCards();
        applyFiltersAndSummaries();
    }

    public Parent getRoot() { return root; }

    public void setBills(java.util.List<Bill> bills) {
        master.setAll(bills == null ? java.util.List.of() : bills);
        refreshCashierFilter();
        applyFiltersAndSummaries();
    }

    private Parent buildSideNav() {
        sideNav.setPadding(new Insets(18, 12, 18, 12));
        sideNav.setPrefWidth(200);
        sideNav.setMinWidth(190);
        sideNav.setMaxWidth(220);

        sideNav.setStyle("""
            -fx-background-color: #1f2d3d;
            """);

        navTitle.setStyle("-fx-text-fill: rgba(255,255,255,0.65); -fx-font-weight: 700;");
        navTitle.setFont(BaseStyles.font(14));

        //style everything (even if not used)
        styleNavItem(navMainMenu, false);
        styleNavItem(navCheckout, false);
        styleNavItem(navManageStocks, false);
        styleNavItem(navSetDiscounts, false);
        styleNavItem(navModifySuppliers, false);
        styleNavItem(navManageEmployee, false);
        styleNavItem(navManagePermissions, false);
        styleNavItem(navViewSuppliers, false);
        styleNavItem(navViewInventory, false);

        // click wiring
        navMainMenu.setOnMouseClicked(e -> runAction("Home", onMainMenu));
        navCheckout.setOnMouseClicked(e -> runAction("Checkout", onCheckout));
        navManageStocks.setOnMouseClicked(e -> runAction("Manage Stocks", onManageStocks));
        navSetDiscounts.setOnMouseClicked(e -> runAction("Set Discounts", onSetDiscounts));
        navModifySuppliers.setOnMouseClicked(e -> runAction("Modify Suppliers", onModifySuppliers));
        navViewSuppliers.setOnMouseClicked(e -> runAction("View Suppliers", onViewSuppliers));
        navViewInventory.setOnMouseClicked(e -> runAction("View Inventory", onViewInventory));
        navManageEmployee.setOnMouseClicked(e -> runAction("Manage Employee", onManageEmployee));
        navManagePermissions.setOnMouseClicked(e -> runAction("Manage Permissions", onManagePermissions));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // role-based
        if (role == UserRole.CASHIER) {
            sideNav.getChildren().setAll(navTitle, navMainMenu, navCheckout, spacer);
        } else if (role == UserRole.MANAGER) {
            sideNav.getChildren().setAll(navTitle, navMainMenu, navManageStocks, navSetDiscounts, navModifySuppliers, spacer);
        } else {
            sideNav.getChildren().setAll(navTitle, navMainMenu, navViewSuppliers, navManageEmployee, navManagePermissions, navViewInventory, spacer);
        }

        return sideNav;
    }

    private HBox buildCashierPicker() {
        Label who = new Label("Cashier");
        who.setFont(BaseStyles.font(14));
        who.setStyle("-fx-text-fill: rgba(0,0,0,0.70); -fx-font-weight: 700;");

        cashierFilterBox = new ComboBox<>();
        cashierFilterBox.setEditable(false);
        cashierFilterBox.setPrefWidth(220);

        cashierFilterClearBtn = new Button("Clear");
        BaseStyles.styleGreenPrimaryButton(cashierFilterClearBtn);
        cashierFilterClearBtn.setPrefWidth(110);

        refreshCashierFilter();

        cashierFilterBox.setOnAction(e -> {
            String picked = cashierFilterBox.getValue();
            if (picked == null || picked.startsWith("All cashiers")) {
                selectedCashier= "";
            } else {
                int idx = picked.indexOf(" ");
                selectedCashier= (idx <= 0) ? picked.trim() : picked.substring(0, idx).trim();
            }
            applyFiltersAndSummaries();
        });

        cashierFilterClearBtn.setOnAction(e -> {
            selectedCashier= "";
            if (cashierFilterBox.getItems().isEmpty()) refreshCashierFilter();
            cashierFilterBox.getSelectionModel().selectFirst();
            applyFiltersAndSummaries();
        });

        HBox box = new HBox(8, who, cashierFilterBox, cashierFilterClearBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void buildRoleFilters() {
        filterBar.getChildren().clear();

        Label filterLabel = new Label("Filter:");
        filterLabel.setFont(BaseStyles.font(14));
        filterLabel.setStyle("-fx-text-fill: rgba(0,0,0,0.60); -fx-font-weight: 800;");

        if (role == UserRole.CASHIER) {
            Label fixed = new Label("Today only (your bills)");
            fixed.setFont(BaseStyles.font(14));
            fixed.setStyle("-fx-text-fill: rgba(0,0,0,0.70); -fx-font-weight: 700;");
            filterBar.getChildren().addAll(filterLabel, fixed);
            return;
        }

        if (role == UserRole.MANAGER) {
            Label info = new Label("Period");
            info.setFont(BaseStyles.font(14));
            info.setStyle("-fx-text-fill: rgba(0,0,0,0.70); -fx-font-weight: 700;");

            ComboBox<BillPeriod> period = new ComboBox<>(FXCollections.observableArrayList(BillPeriod.values()));
            period.getSelectionModel().select(BillPeriod.TODAY);
            period.setPrefWidth(170);

            period.setOnAction(e -> {
                selectedPeriod = period.getValue();
                if (selectedPeriod == null) selectedPeriod = BillPeriod.TODAY;
                applyFiltersAndSummaries();

                if (onManagerPeriodChanged != null) onManagerPeriodChanged.accept(selectedPeriod);
            });

            HBox cashierPicker = buildCashierPicker();
            filterBar.getChildren().addAll(filterLabel, info, period, cashierPicker);
            return;
        }

        Label fromL = new Label("From");
        Label toL = new Label("To");
        fromL.setFont(BaseStyles.font(14));
        toL.setFont(BaseStyles.font(14));
        fromL.setStyle("-fx-text-fill: rgba(0,0,0,0.70); -fx-font-weight: 700;");
        toL.setStyle("-fx-text-fill: rgba(0,0,0,0.70); -fx-font-weight: 700;");

        DatePicker from = new DatePicker(LocalDate.now());
        DatePicker to = new DatePicker(LocalDate.now());
        from.setPrefWidth(160);
        to.setPrefWidth(160);

        Button apply = new Button("Apply");
        BaseStyles.styleGreenPrimaryButton(apply);
        apply.setMaxWidth(140);
        apply.setPrefWidth(140);

        apply.setOnAction(e -> {
            LocalDate f = from.getValue();
            LocalDate t = to.getValue();

            if (f == null || t == null) {
                showAlert("Missing dates", "Please select both From and To dates.");
                return;
            }
            if (t.isBefore(f)) {
                showAlert("Invalid range", "To date must be the same as or after From date.");
                return;
            }

            adminFrom = f;
            adminTo = t;
            applyFiltersAndSummaries();

            if (onAdminDateRangeApply != null) onAdminDateRangeApply.accept(f, t);
        });

        HBox cashierPicker = buildCashierPicker();
        filterBar.getChildren().addAll(filterLabel, fromL, from, toL, to, apply, cashierPicker);
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

        label.setOnMouseEntered(e -> label.setStyle("""
                -fx-text-fill: white;
                -fx-font-weight: 700;
                -fx-background-color: rgba(255,255,255,0.10);
                -fx-background-radius: 10;
                """));

        label.setOnMouseExited(e -> label.setStyle(selected
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
        ));
    }
    private Parent buildCenter() {
        VBox center = new VBox(12);
        center.setPadding(new Insets(18));
        center.setStyle("-fx-background-color: white;");

        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        searchField.setPromptText("Search buyer info or bill number...");
        searchField.setPrefHeight(42);
        searchField.setFont(BaseStyles.font(15));
        searchField.setStyle("""
                -fx-background-color: rgba(0,0,0,0.04);
                -fx-background-radius: 12;
                -fx-padding: 10 12 10 12;
                -fx-border-color: rgba(0,0,0,0.10);
                -fx-border-radius: 12;
                -fx-border-width: 1;
                """);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        BaseStyles.styleGreenPrimaryButton(searchBtn);
        searchBtn.setMaxWidth(140);
        searchBtn.setPrefWidth(140);

        searchRow.getChildren().addAll(searchField, searchBtn);

        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(6, 0, 0, 0));

        summaryBar.setAlignment(Pos.CENTER_LEFT);
        summaryBar.setPadding(new Insets(4, 0, 6, 0));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No bills to show."));
        table.setFixedCellSize(36);

        center.getChildren().addAll(searchRow, filterBar, summaryBar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return center;
    }

    private void buildSummaryCards() {
        summaryBar.getChildren().clear();

        if (role == UserRole.CASHIER) {
            summaryBar.getChildren().addAll(
                    statCard("Your bills today", billsValue),
                    statCard("Total bills (system)", extraValue)
            );
        } else if (role == UserRole.MANAGER) {
            summaryBar.getChildren().addAll(
                    statCard("Total bills", billsValue),
                    statCard("Items sold", itemsValue),
                    statCard("Revenue", revenueValue)
            );
        } else {
            summaryBar.getChildren().addAll(
                    statCard("Total bills", billsValue),
                    statCard("Items sold", itemsValue),
                    statCard("Total income", incomeValue),
                    statCard("Total costs", costValue)
            );
        }
    }

    private VBox statCard(String title, Label valueLabel) {
        Label t = new Label(title);
        t.setFont(BaseStyles.font(13));
        t.setStyle("-fx-text-fill: rgba(0,0,0,0.55); -fx-font-weight: 800;");

        valueLabel.setFont(BaseStyles.font(22));
        valueLabel.setStyle("-fx-text-fill: rgba(0,0,0,0.85); -fx-font-weight: 900;");

        VBox card = new VBox(4, t, valueLabel);
        card.setPadding(new Insets(12));
        card.setStyle("""
                -fx-background-color: rgba(0,0,0,0.03);
                -fx-background-radius: 12;
                -fx-border-color: rgba(0,0,0,0.08);
                -fx-border-radius: 12;
                -fx-border-width: 1;
                """);
        return card;
    }

    private void applyFiltersAndSummaries() {
        final String q = safe(searchField.getText()).trim().toLowerCase();

        filtered.setPredicate(b -> {
            if (b == null) return false;

            if (!passesRoleFilter(b)) return false;

            if (q.isBlank()) return true;

            String num = String.valueOf(b.getBillNumber());
            String buyer = safe(b.getBuyerInfo()).toLowerCase();
            return num.contains(q) || buyer.contains(q);
        });

        updateSummaryValues();
    }

    private boolean passesRoleFilter(Bill b) {
        LocalDate d = b.getDateBillIsGettingCut();
        LocalDate today = LocalDate.now();
        if (d == null) return false;

        if (role == UserRole.CASHIER) {
            if (!d.equals(today)) return false;

            String createdBy = safe(b.getCreatedByUsername());
            if (cashierUsername.isBlank()) return true;
            return createdBy.equalsIgnoreCase(cashierUsername);
        }

        if (role == UserRole.MANAGER) {
            BillPeriod period = (selectedPeriod == null) ? BillPeriod.TODAY : selectedPeriod;

            LocalDate from = switch (period) {
                case TODAY      -> today;
                case THIS_WEEK  -> today.minusDays(6);
                case THIS_MONTH -> today.minusDays(29);
                case THIS_YEAR  -> today.minusDays(364);
                case ALL        -> LocalDate.MIN;
            };

            if (d.isBefore(from) || d.isAfter(today)) return false;
            if (!selectedCashier.isBlank()) {
                String createdBy = safe(b.getCreatedByUsername());
                if (!createdBy.equalsIgnoreCase(selectedCashier)) return false;
            }

            return true;
        }

        if (d.isBefore(adminFrom) || d.isAfter(adminTo)) return false;

        if (!selectedCashier.isBlank()) {
            String createdBy = safe(b.getCreatedByUsername());
            if (!createdBy.equalsIgnoreCase(selectedCashier)) return false;
        }

        return true;
    }

    private void updateSummaryValues() {
        int billsCount = filtered.size();

        if (role == UserRole.CASHIER) {
            billsValue.setText(String.valueOf(billsCount));
            extraValue.setText(String.valueOf(master.size()));
            return;
        }

        double revenue = 0;
        int itemsSold = 0;

        for (Bill b : filtered) {
            if (b == null) continue;
            revenue += b.getPriceAfterDiscount();
            itemsSold += b.getItemCount();
        }

        billsValue.setText(String.valueOf(billsCount));
        revenueValue.setText(String.format(java.util.Locale.US, "%.2f", revenue));

        if (role == UserRole.MANAGER) {
            itemsValue.setText(String.valueOf(itemsSold));

        } else if (role == UserRole.ADMIN) {
            double income = revenue;
            double costs = computeAdminCosts(adminFrom, adminTo);

            incomeValue.setText(String.format(java.util.Locale.US, "%.2f", income));
            costValue.setText(String.format(java.util.Locale.US, "%.2f", costs));
        }
    }

    private void wireSearch() {
        Runnable run = this::applyFiltersAndSummaries;
        searchBtn.setOnAction(e -> run.run());
        searchField.setOnAction(e -> run.run());
    }

    private void configureColumns() {
        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;

        TableColumn<Bill, String> numberCol = new TableColumn<>("Bill #");
        numberCol.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(String.valueOf(c.getValue().getBillNumber()))
        );

        TableColumn<Bill, String> buyerCol = new TableColumn<>("Buyer");
        buyerCol.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(safe(c.getValue().getBuyerInfo()))
        );

        TableColumn<Bill, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(
                        c.getValue().getDateBillIsGettingCut() == null
                                ? ""
                                : c.getValue().getDateBillIsGettingCut().format(df)
                )
        );

        TableColumn<Bill, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(
                        String.format(java.util.Locale.US, "%.2f", c.getValue().getPriceAfterDiscount())
                )
        );

        table.getColumns().setAll(numberCol, buyerCol, dateCol, totalCol);
    }

    private void wireDoubleClickOpenPopup() {
        table.setRowFactory(tv -> {
            TableRow<Bill> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY
                        && e.getClickCount() == 2
                        && !row.isEmpty()) {
                    try {
                        Bill selected = row.getItem();
                        Stage owner = (Stage) root.getScene().getWindow();
                        BillOverlay.show(owner, selected);
                    } catch (Throwable ex) {
                        showError("Open bill failed", ex);
                    }
                }
            });
            return row;
        });
    }

    public void setOnLogout(Runnable r) { this.onLogout = r; }
    public void setOnChangePassword(Runnable r) { this.onChangePassword = r; }
    public void setOnMainMenu(Runnable r) { this.onMainMenu = r; }
    public void setOnCheckout(Runnable r) { this.onCheckout = r; }
    public void setOnManageStocks(Runnable r) { this.onManageStocks = r; }
    public void setOnSetDiscounts(Runnable r) { this.onSetDiscounts = r; }
    public void setOnModifySuppliers(Runnable r) { this.onModifySuppliers = r; }
    public void setOnManageEmployee(Runnable r) { this.onManageEmployee = r; }
    public void setOnManagePermissions(Runnable r) { this.onManagePermissions = r; }
    public void setOnViewSuppliers(Runnable r) { this.onViewSuppliers = r; }
    public void setOnViewInventory(Runnable r) { this.onViewInventory = r; }

    public void setOnManagerPeriodChanged(Consumer<BillPeriod> c) { this.onManagerPeriodChanged = c; }
    public void setOnAdminDateRangeApply(BiConsumer<LocalDate, LocalDate> c) { this.onAdminDateRangeApply = c; }

    // Utils
    private static String safe(String s) { return s == null ? "" : s; }

    private void runAction(String name, Runnable r) {
        try {
            if (r != null) r.run();
        } catch (Throwable ex) {
            showError(name + " action failed", ex);
        }
    }

    private static void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private static void showError(String title, Throwable ex) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(ex.getClass().getSimpleName());
        a.setContentText(ex.getMessage() == null ? "(no message)" : ex.getMessage());
        a.showAndWait();
        ex.printStackTrace();
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

    private void refreshCashierFilter() {
        if (cashierFilterBox == null) return;
        String keep = selectedCashier == null ? "" : selectedCashier.trim();

        cashierFilterBox.getItems().clear();
        cashierFilterBox.getItems().add("All cashiers");

        ArrayList<Cashier> cashiers = DataStorage.loadCashiers();
        ArrayList<String> addedUsernamesLower = new ArrayList<>();

        for (Cashier c : cashiers) {
            if (c == null) continue;

            String u = safe(c.getUsername()).trim();
            if (u.isBlank()) continue;

            String uLower = u.toLowerCase();
            boolean already = false;
            for (String existing : addedUsernamesLower) {
                if (existing.equals(uLower)) {
                    already = true;
                    break;
                }
            }
            if (already) continue;

            addedUsernamesLower.add(uLower);

            String name = safe(c.getFullName()).trim();
            String display = name.isBlank() ? u : (u + " (" + name + ")");
            cashierFilterBox.getItems().add(display);
        }

        if (!keep.isBlank()) {
            for (String item : cashierFilterBox.getItems()) {
                if (item == null) continue;
                String itemLower = item.toLowerCase();
                if (itemLower.equals(keep.toLowerCase()) || itemLower.startsWith(keep.toLowerCase() + " ")) {
                    cashierFilterBox.getSelectionModel().select(item);
                    return;
                }
            }
        }

        cashierFilterBox.getSelectionModel().selectFirst();
    }

    private double computeAdminCosts(LocalDate from, LocalDate to) {
        double totalMonthlySalary = 0.0;

        ArrayList<Manager> managers = DataStorage.loadManagers();
        ArrayList<Cashier> cashiers = DataStorage.loadCashiers();

        for (Manager m : managers) if (m != null) totalMonthlySalary += m.getSalary();
        for (Cashier c : cashiers) if (c != null) totalMonthlySalary += c.getSalary();

        long days = java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1;
        double salaryCostForPeriod = totalMonthlySalary * (days / 30.0);
        double purchasesCost = 0.0;

        for (Manager m : managers) {
            if (m == null) continue;

            purchasesCost += m.getTotalSpendings();
        }

        return purchasesCost + salaryCostForPeriod;
    }

}
