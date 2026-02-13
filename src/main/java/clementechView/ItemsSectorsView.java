package clementechView;

import java.util.Objects;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ContentDisplay;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

public class ItemsSectorsView {

    private final BorderPane root = new BorderPane();

    // ===== HEADER =====
    private final BaseStyles.HeaderParts headerParts;
    private Runnable onLogout;
    private Runnable onChangePassword;

    // ===== BACK + TITLE (top-left under header, right of nav) =====
    private final Button backBtn = new Button();
    private Runnable onBack;

    private final Label supplierTitle = new Label();
    private String supplierName = "";

    // ===== LEFT NAV (same as SupplierView) =====
    private final VBox sideNav = new VBox(10);
    private final Button homeBtn = new Button("Home");
    private final Button checkSalesBtn = new Button("Check Sales");
    private final Button setDiscountsBtn = new Button("Set Discounts");
    private final Button manageStocksBtn = new Button("Manage Stocks");

    private Runnable onHome;
    private Runnable onCheckSales;
    private Runnable onSetDiscounts;
    private Runnable onManageStocks;

    // ===== RIGHT CONTENT =====
    private final TextField searchField = new TextField();
    private final Button searchBtn = new Button("Search");

    private final Label infoBanner = new Label();

    private final TableView<Row> table = new TableView<>();
    private final TableColumn<Row, String> colItems = new TableColumn<>("Items Supplied");
    private final TableColumn<Row, String> colSectors = new TableColumn<>("Sectors Supplied");


    // ===== DATA / SEARCH =====
    private final ObservableList<Row> master = FXCollections.observableArrayList();
    private final FilteredList<Row> filtered = new FilteredList<>(master, r -> true);

    public ItemsSectorsView(String managerFullName, String logoPath, String avatarPath, String supplierName) {
        String name = safeName(managerFullName, "Manager");
        this.supplierName = safe(supplierName);

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
    }

    // ===================== LAYOUT =====================
    private Parent buildMainLayout() {
        VBox right = new VBox(12);
        right.setPadding(new Insets(18, 18, 18, 18));
        right.setAlignment(Pos.TOP_LEFT);

        // TOP ROW: back button + supplier name (under header, to the right of nav)
        HBox topRow = new HBox(12, backBtn, supplierTitle);
        topRow.setAlignment(Pos.CENTER_LEFT);

        HBox searchRow = new HBox(10, searchField, searchBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);


        right.getChildren().addAll(
                topRow,
                searchRow,
                infoBanner,
                table
        );

        VBox.setVgrow(table, Priority.ALWAYS);
        return right;
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

        styleNavButton(homeBtn, false);
        styleNavButton(checkSalesBtn, false);
        styleNavButton(setDiscountsBtn, false);
        styleNavButton(manageStocksBtn, false);

        homeBtn.setOnAction(e -> { if (onHome != null) onHome.run(); });
        checkSalesBtn.setOnAction(e -> { if (onCheckSales != null) onCheckSales.run(); });
        setDiscountsBtn.setOnAction(e -> { if (onSetDiscounts != null) onSetDiscounts.run(); });
        manageStocksBtn.setOnAction(e -> { if (onManageStocks != null) onManageStocks.run(); });

        sideNav.getChildren().addAll(
                navTitle,
                spacer(10),
                homeBtn,
                checkSalesBtn,
                setDiscountsBtn,
                manageStocksBtn
        );
    }

    private void buildRightContent() {
        // Search UI
        searchField.setPromptText("Search items or sectors.");
        searchField.setPrefWidth(520);
        searchField.setMinHeight(42);
        searchField.setFont(BaseStyles.font(16));

        BaseStyles.stylePrimaryButton(searchBtn);
        searchBtn.setPrefWidth(140);
        searchBtn.setFocusTraversable(false);

        searchBtn.setOnAction(e -> applySearch());
        searchField.setOnAction(e -> applySearch());

        // Supplier title beside back button
        supplierTitle.setText("Supplier: " + safe(supplierName));
        supplierTitle.setFont(BaseStyles.font(18));
        supplierTitle.setStyle("-fx-text-fill: rgba(0,0,0,0.78); -fx-font-weight: 900;");

        // Back button (brown square)
        BaseStyles.styleBackSquareButton(backBtn);
        //uploaded image on the button
        applyBackButtonIcon(backBtn, "/logo/Back Button.png", 44);

        backBtn.setOnAction(e -> { if (onBack != null) onBack.run(); });

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
        colItems.setCellValueFactory(d -> d.getValue().itemProperty());
        colSectors.setCellValueFactory(d -> d.getValue().sectorProperty());

        colItems.setPrefWidth(500);
        colSectors.setPrefWidth(400);

        table.getColumns().setAll(colItems, colSectors);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(520);
        table.setPlaceholder(new Label("No items/sectors to show."));

        SortedList<Row> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

    }

    // ===================== SEARCH =====================
    private void applySearch() {
        String q = safe(searchField.getText()).toLowerCase();

        if (q.isBlank()) {
            filtered.setPredicate(r -> true);
        } else {
            filtered.setPredicate(r -> {
                if (r == null) return false;
                return contains(r.getItem(), q) || contains(r.getSector(), q);
            });
        }

        refreshBanner();
    }

    private void refreshBanner() {
        int visible = 0;
        for (Row r : filtered) if (r != null) visible++;

        String sup = safe(supplierName);
        if (sup.isBlank()) sup = "Selected Supplier";

        if (visible == 0) infoBanner.setText("No items/sectors match your search.");
        else if (visible == 1) infoBanner.setText("Showing 1 row for " + sup + ".");
        else infoBanner.setText("Showing " + visible + " rows for " + sup + ".");
    }

    // ===================== STYLING HELPERS =====================

    private void applyBackButtonIcon(Button btn, String resourcePath, double iconSize) {
        var url = Objects.requireNonNull(
                getClass().getResource(resourcePath),
                "Back icon not found: " + resourcePath + " (put it in resources)"
        ).toExternalForm();

        Image img = new Image(url, iconSize, iconSize, true, true);
        ImageView iv = new ImageView(img);
        iv.setFitWidth(iconSize);
        iv.setFitHeight(iconSize);
        iv.setPreserveRatio(true);

        btn.setGraphic(iv);
        btn.setText(null);
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.setGraphicTextGap(0);
        btn.setPadding(Insets.EMPTY);
        btn.setFocusTraversable(false);
    }

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

    public void setOnBack(Runnable r) { this.onBack = Objects.requireNonNull(r, "onBack"); }

    public void setEmployeeName(String fullName) {
        headerParts.setEmployeeName(safeName(fullName, "Manager"));
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = safe(supplierName);
        supplierTitle.setText("Supplier: " + this.supplierName);
        refreshBanner();
    }

    public void setRows(ObservableList<Row> rows) {
        master.setAll(rows == null ? FXCollections.observableArrayList() : rows);
        applySearch();
    }

    public ObservableList<Row> getRows() { return master; }

    public Row getSelectedRow() {
        return table.getSelectionModel().getSelectedItem();
    }

    public TextField getSearchField() { return searchField; }
    public Button getSearchBtn() { return searchBtn; }
    public TableView<Row> getTable() { return table; }

    // UTIL
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

    // TABLE ROW MODEL
    public static final class Row {
        private final StringProperty item = new SimpleStringProperty("");
        private final StringProperty sector = new SimpleStringProperty("");

        public Row(String item, String sector) {
            this.item.set(item);
            this.sector.set(sector);
        }

        public StringProperty itemProperty() { return item; }
        public StringProperty sectorProperty() { return sector; }

        public String getItem() { return item.get(); }
        public String getSector() { return sector.get(); }
    }
}
