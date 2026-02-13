package clementechView;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Font;

import java.util.Objects;

public class CheckoutView {

    private final BorderPane root = new BorderPane();
    private final BaseStyles.HeaderParts headerParts;

    private final VBox sideNav = new VBox(8);
    private final Label navTitle = new Label("Menu");
    private final Label navMainMenu = new Label("Main Menu");
    private final Label navTodaysBills = new Label("Today's Bills");

    private final TextField searchField = new TextField();
    private final Button searchBtn = new Button("Search");

    private final TableView<ItemRow> resultsTable = new TableView<>();
    private final ObservableList<ItemRow> resultsData = FXCollections.observableArrayList();

    private final Label stockAlertLabel = new Label("");
    private final Button addToCartBtn = new Button("Add to Cart");

    private final TableView<CartRow> cartTable = new TableView<>();
    private final ObservableList<CartRow> cartData = FXCollections.observableArrayList();

    private final Label discountLabel = new Label("Discount: -");
    private final Label totalLabel = new Label("Total: 0.00");

    private final Button removeLineBtn = new Button("Remove Line");
    private final Button finalizeBillBtn = new Button("Finalize Bill");

    private Runnable onLogout;
    private Runnable onGoMainMenu;
    private Runnable onGoTodaysBills;
    private Runnable onChangePassword;

    private Runnable onSearch;
    private Runnable onAddToCart;
    private Runnable onRemoveLine;
    private Runnable onFinalizeBill;

    public CheckoutView(String cashierFullName, String logoPath, String avatarPath) {
        BaseStyles.applyAppBackground(root);

        headerParts = BaseStyles.buildEmployeeHeader(cashierFullName, logoPath, avatarPath);

        // ✅ header always calls the fields
        headerParts.setOnLogout(() -> runAction("Logout", onLogout));
        headerParts.setOnChangePassword(() -> runAction("Change Password", onChangePassword));

        // ✅ force header clickable
        forceInteractive(headerParts.headerBar);

        root.setTop(headerParts.headerBar);

        root.setLeft(buildSideNav());
        root.setCenter(buildCenter());
        root.setRight(buildRight());
    }

    public Parent getRoot() { return root; }

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

        styleNavItem(navMainMenu, true);
        styleNavItem(navTodaysBills, false);

        navMainMenu.setOnMouseClicked(e -> runAction("Main Menu", onGoMainMenu));
        navTodaysBills.setOnMouseClicked(e -> runAction("Today's Bills", onGoTodaysBills));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sideNav.getChildren().setAll(navTitle, navMainMenu, navTodaysBills, spacer);
        return sideNav;
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

        label.setOnMouseExited(e -> {
            if (label == navMainMenu && selected) {
                label.setStyle("""
                        -fx-text-fill: white;
                        -fx-font-weight: 700;
                        -fx-background-color: rgba(255,255,255,0.08);
                        -fx-background-radius: 10;
                        """);
            } else {
                label.setStyle("""
                        -fx-text-fill: rgba(255,255,255,0.85);
                        -fx-font-weight: 600;
                        -fx-background-color: transparent;
                        -fx-background-radius: 10;
                        """);
            }
        });
    }

    private Parent buildCenter() {
        VBox center = new VBox(12);
        center.setPadding(new Insets(18));
        center.setStyle("-fx-background-color: white;");

        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        searchField.setPromptText("Search item name or item code...");
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

        searchBtn.setOnAction(e -> runAction("Search", onSearch));
        searchField.setOnAction(e -> runAction("Search", onSearch));

        searchRow.getChildren().addAll(searchField, searchBtn);

        configureResultsTable();

        HBox underRow = new HBox(10);
        underRow.setAlignment(Pos.CENTER_LEFT);

        stockAlertLabel.setFont(BaseStyles.font(14));
        setStockAlert("", false);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        BaseStyles.styleGreenPrimaryButton(addToCartBtn);
        addToCartBtn.setMaxWidth(170);
        addToCartBtn.setPrefWidth(170);
        addToCartBtn.setOnAction(e -> runAction("Add to cart", onAddToCart));

        underRow.getChildren().addAll(stockAlertLabel, spacer, addToCartBtn);

        center.getChildren().addAll(searchRow, resultsTable, underRow);
        VBox.setVgrow(resultsTable, Priority.ALWAYS);

        return center;
    }

    private void configureResultsTable() {
        resultsTable.setItems(resultsData);
        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        resultsTable.setPlaceholder(new Label("Search for an item to see results."));
        resultsTable.setFixedCellSize(36);
        resultsTable.setPrefHeight(420);

        TableColumn<ItemRow, String> colItem = new TableColumn<>("Item");
        colItem.setCellValueFactory(c -> c.getValue().itemNameProperty());

        TableColumn<ItemRow, String> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(c -> c.getValue().priceProperty());

        TableColumn<ItemRow, String> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(c -> c.getValue().stockProperty());

        TableColumn<ItemRow, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(c -> c.getValue().categoryProperty());

        resultsTable.getColumns().setAll(colItem, colPrice, colStock, colCategory);
    }

    private Parent buildRight() {
        VBox right = new VBox(12);
        right.setPadding(new Insets(18));
        right.setPrefWidth(430);
        right.setMinWidth(380);
        right.setStyle("""
                -fx-background-color: white;
                -fx-border-color: rgba(0,0,0,0.08);
                -fx-border-width: 0 0 0 1;
                """);

        configureCartTable();

        VBox totalsBox = new VBox(6);
        totalsBox.setAlignment(Pos.CENTER_RIGHT);

        discountLabel.setFont(Font.font(BaseStyles.FONT_FAMILY, FontWeight.SEMI_BOLD, 14));
        discountLabel.setStyle("-fx-text-fill: rgba(0,0,0,0.60);");

        totalLabel.setFont(Font.font(BaseStyles.FONT_FAMILY, FontWeight.EXTRA_BOLD, 18));
        totalLabel.setStyle("-fx-text-fill: #F98E02;");

        totalsBox.getChildren().addAll(discountLabel, totalLabel);

        HBox buttonsRow = new HBox(10);
        buttonsRow.setAlignment(Pos.CENTER_RIGHT);

        removeLineBtn.setFont(BaseStyles.font(16));
        removeLineBtn.setCursor(Cursor.HAND);
        removeLineBtn.setPrefHeight(48);
        removeLineBtn.setStyle("""
                -fx-background-color: white;
                -fx-text-fill: #F98E02;
                -fx-background-radius: 24;
                -fx-font-weight: 800;

                -fx-border-color: #F98E02;
                -fx-border-width: 2;
                -fx-border-radius: 24;
                """);
        removeLineBtn.setOnAction(e -> runAction("Remove line", onRemoveLine));

        BaseStyles.styleGreenPrimaryButton(finalizeBillBtn);
        finalizeBillBtn.setMaxWidth(220);
        finalizeBillBtn.setPrefWidth(220);
        finalizeBillBtn.setOnAction(e -> runAction("Finalize bill", onFinalizeBill));

        buttonsRow.getChildren().addAll(removeLineBtn, finalizeBillBtn);

        right.getChildren().addAll(cartTable, totalsBox, buttonsRow);
        VBox.setVgrow(cartTable, Priority.ALWAYS);

        return right;
    }

    private void configureCartTable() {
        cartTable.setItems(cartData);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        cartTable.setPlaceholder(new Label("Cart is empty. Add items from the middle table."));
        cartTable.setFixedCellSize(36);

        TableColumn<CartRow, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(c -> c.getValue().nameProperty());

        TableColumn<CartRow, Integer> colQty = new TableColumn<>("Quantity");
        colQty.setCellValueFactory(c -> c.getValue().quantityProperty().asObject());

        TableColumn<CartRow, String> colUnit = new TableColumn<>("Unit Price");
        colUnit.setCellValueFactory(c -> c.getValue().unitPriceProperty());

        TableColumn<CartRow, String> colLineTotal = new TableColumn<>("Line Total");
        colLineTotal.setCellValueFactory(c -> c.getValue().lineTotalProperty());

        cartTable.getColumns().setAll(colName, colQty, colUnit, colLineTotal);
    }

    // ✅ setters only set fields
    public void setOnLogout(Runnable r) { this.onLogout = r; }
    public void setOnChangePassword(Runnable r) { this.onChangePassword = r; }

    public void setOnGoMainMenu(Runnable r) { this.onGoMainMenu = r; }
    public void setOnGoTodaysBills(Runnable r) { this.onGoTodaysBills = r; }

    public void setOnSearch(Runnable r) { this.onSearch = r; }
    public void setOnAddToCart(Runnable r) { this.onAddToCart = r; }
    public void setOnRemoveLine(Runnable r) { this.onRemoveLine = r; }
    public void setOnFinalizeBill(Runnable r) { this.onFinalizeBill = r; }

    public TextField getSearchField() { return searchField; }
    public TableView<ItemRow> getResultsTable() { return resultsTable; }
    public TableView<CartRow> getCartTable() { return cartTable; }

    public ItemRow getSelectedSearchResult() { return resultsTable.getSelectionModel().getSelectedItem(); }
    public CartRow getSelectedCartLine() { return cartTable.getSelectionModel().getSelectedItem(); }

    public ObservableList<ItemRow> getResultsData() { return resultsData; }
    public ObservableList<CartRow> getCartData() { return cartData; }

    public void setStockAlert(String msg, boolean low) {
        if (msg == null || msg.isBlank()) {
            stockAlertLabel.setText("");
            stockAlertLabel.setStyle("");
            return;
        }
        stockAlertLabel.setText(msg);
        stockAlertLabel.setStyle(low
                ? "-fx-text-fill: #c0392b; -fx-font-weight: 800;"
                : "-fx-text-fill: rgba(0,0,0,0.65); -fx-font-weight: 700;");
    }

    public void setDiscountText(String text) {
        discountLabel.setText(text == null ? "Discount: -" : text);
    }

    public void setTotalText(String text) {
        totalLabel.setText(text == null ? "Total: 0.00" : text);
    }

    private void runAction(String name, Runnable r) {
        try {
            if (r != null) r.run();
        } catch (Throwable ex) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle(name + " failed");
            a.setHeaderText(ex.getClass().getSimpleName());
            a.setContentText(ex.getMessage() == null ? "(no message)" : ex.getMessage());
            a.showAndWait();
            ex.printStackTrace();
        }
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

    // =========================
    // Row models
    // =========================
    public static class ItemRow {
        private final StringProperty itemName = new SimpleStringProperty();
        private final StringProperty price = new SimpleStringProperty();
        private final StringProperty stock = new SimpleStringProperty();
        private final StringProperty category = new SimpleStringProperty();
        private final StringProperty itemCode = new SimpleStringProperty();

        public ItemRow(String itemName, String price, String stock, String category) {
            this(itemName, null, price, stock, category);
        }

        public ItemRow(String itemName, String itemCode, String price, String stock, String category) {
            this.itemName.set(itemName);
            this.itemCode.set(itemCode);
            this.price.set(price);
            this.stock.set(stock);
            this.category.set(category);
        }

        public StringProperty itemNameProperty() { return itemName; }
        public StringProperty priceProperty() { return price; }
        public StringProperty stockProperty() { return stock; }
        public StringProperty categoryProperty() { return category; }
        public StringProperty itemCodeProperty() { return itemCode; }

        public String getItemCode() { return itemCode.get(); }
    }

    public static class CartRow {
        private final StringProperty name = new SimpleStringProperty();
        private final IntegerProperty quantity = new SimpleIntegerProperty(1);
        private final StringProperty unitPrice = new SimpleStringProperty();
        private final StringProperty lineTotal = new SimpleStringProperty();
        private final StringProperty itemCode = new SimpleStringProperty();

        public CartRow(String name, int qty, String unitPrice, String lineTotal) {
            this(name, null, qty, unitPrice, lineTotal);
        }

        public CartRow(String name, String itemCode, int qty, String unitPrice, String lineTotal) {
            this.name.set(name);
            this.itemCode.set(itemCode);
            this.quantity.set(qty);
            this.unitPrice.set(unitPrice);
            this.lineTotal.set(lineTotal);
        }

        public StringProperty nameProperty() { return name; }
        public IntegerProperty quantityProperty() { return quantity; }
        public StringProperty unitPriceProperty() { return unitPrice; }
        public StringProperty lineTotalProperty() { return lineTotal; }
        public StringProperty itemCodeProperty() { return itemCode; }

        public String getItemCode() { return itemCode.get(); }
    }
}
