package clementechView;

import java.util.Objects;
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

public class DiscountView {

    private final BorderPane root = new BorderPane();

    // header
    private final BaseStyles.HeaderParts headerParts;
    private Runnable onLogout;
    private Runnable onChangePassword;

    // left nav
    private final VBox sideNav = new VBox(10);
    private final Button homeBtn = new Button("Home");
    private final Button manageStocksBtn = new Button("Manage Stocks");
    private final Button checkSalesBtn = new Button("Check Sales");
    private final Button modifySuppliersBtn = new Button("Modify Suppliers");

    private Runnable onHome;
    private Runnable onManageStocks;
    private Runnable onCheckSales;
    private Runnable onModifySuppliers;

    // right content
    private final TextField searchField = new TextField();
    private final Button searchBtn = new Button("Search");

    private final TableView<Row> table = new TableView<>();
    private final TableColumn<Row, String> colItem = new TableColumn<>("Item");
    private final TableColumn<Row, Number> colDiscount = new TableColumn<>("Discount (%)");

    // data
    private final ObservableList<Row> master = FXCollections.observableArrayList();
    private final FilteredList<Row> filtered = new FilteredList<>(master, r -> true);

    public DiscountView(String managerFullName, String logoPath, String avatarPath) {
        String name = safeName(managerFullName, "Manager");

        root.setStyle("-fx-background-color: white;");

        headerParts = BaseStyles.buildEmployeeHeader(name, logoPath, avatarPath);
        headerParts.setOnLogout(() -> { if (onLogout != null) onLogout.run(); });
        headerParts.setOnChangePassword(() -> { if (onChangePassword != null) onChangePassword.run(); });
        root.setTop(headerParts.headerBar);

        buildSideNav();
        root.setLeft(sideNav);

        buildRight();
    }

    private void buildSideNav() {
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

        styleNavButton(homeBtn);
        styleNavButton(manageStocksBtn);
        styleNavButton(checkSalesBtn);
        styleNavButton(modifySuppliersBtn);

        homeBtn.setOnAction(e -> { if (onHome != null) onHome.run(); });
        manageStocksBtn.setOnAction(e -> { if (onManageStocks != null) onManageStocks.run(); });
        checkSalesBtn.setOnAction(e -> { if (onCheckSales != null) onCheckSales.run(); });
        modifySuppliersBtn.setOnAction(e -> { if (onModifySuppliers != null) onModifySuppliers.run(); });

        sideNav.getChildren().addAll(navTitle, spacer(10), homeBtn, manageStocksBtn, checkSalesBtn, modifySuppliersBtn);
    }

    private void buildRight() {
        VBox right = new VBox(12);
        right.setPadding(new Insets(18));
        right.setAlignment(Pos.TOP_LEFT);

        searchField.setPromptText("Search item name or id...");
        searchField.setPrefWidth(520);
        searchField.setMinHeight(42);
        searchField.setFont(BaseStyles.font(16));

        BaseStyles.stylePrimaryButton(searchBtn);
        searchBtn.setPrefWidth(140);

        HBox searchRow = new HBox(10, searchField, searchBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        colItem.setCellValueFactory(d -> d.getValue().itemProperty());
        colDiscount.setCellValueFactory(d -> d.getValue().discountProperty());

        table.getColumns().setAll(colItem, colDiscount);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No items to show."));
        VBox.setVgrow(table, Priority.ALWAYS);

        SortedList<Row> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        searchBtn.setOnAction(e -> applySearch());
        searchField.setOnAction(e -> applySearch());

        right.getChildren().addAll(searchRow, table);
        root.setCenter(right);
    }

    private void applySearch() {
        String q = safe(searchField.getText()).toLowerCase();
        if (q.isBlank()) {
            filtered.setPredicate(r -> true);
        } else {
            filtered.setPredicate(r -> r != null && (
                    safe(r.getItem()).toLowerCase().contains(q)
            ));
        }
    }

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

    private Region spacer(double h) {
        Region r = new Region();
        r.setMinHeight(h);
        r.setPrefHeight(h);
        r.setMaxHeight(h);
        return r;
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }
    private static String safeName(String fullName, String fallback) {
        String s = safe(fullName);
        return s.isBlank() ? fallback : s;
    }

    // ---------- controller API ----------
    public Parent getRoot() { return root; }

    public void setOnLogout(Runnable r) { this.onLogout = Objects.requireNonNull(r); }
    public void setOnChangePassword(Runnable r) { this.onChangePassword = Objects.requireNonNull(r); }

    public void setOnHome(Runnable r) { this.onHome = Objects.requireNonNull(r); }
    public void setOnManageStocks(Runnable r) { this.onManageStocks = Objects.requireNonNull(r); }
    public void setOnCheckSales(Runnable r) { this.onCheckSales = Objects.requireNonNull(r); }
    public void setOnModifySuppliers(Runnable r) { this.onModifySuppliers = Objects.requireNonNull(r); }

    public void setRows(ObservableList<Row> rows) {
        master.setAll(rows == null ? FXCollections.observableArrayList() : rows);
        applySearch();
    }

    public Row getSelectedRow() { return table.getSelectionModel().getSelectedItem(); }
    public TableView<Row> getTable() { return table; }

    // ---------- table row ----------
    public static final class Row {
        private final StringProperty item = new SimpleStringProperty("");
        private final DoubleProperty discount = new SimpleDoubleProperty(0);

        public Row(String itemText, double discountPercent) {
            this.item.set(itemText);
            this.discount.set(discountPercent);
        }

        public StringProperty itemProperty() { return item; }
        public DoubleProperty discountProperty() { return discount; }

        public String getItem() { return item.get(); }
        public double getDiscount() { return discount.get(); }
    }
}
