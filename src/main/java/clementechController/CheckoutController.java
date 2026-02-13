package clementechController;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import clementechModel.*;
import clementechView.CheckoutView;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

public class CheckoutController {

    private final Cashier cashier;
    private final Navigator navigator;
    private final CheckoutView view;

    // Loaded items (from file)
    private final ArrayList<Item> items;

    private static final String LOGO_PATH = "/logo/clementech.png";
    private static final String AVATAR_PATH = "/logo/cashier3.png"; // change if needed

    public CheckoutController(Cashier cashier, Navigator navigator) {
        this.cashier = Objects.requireNonNull(cashier, "cashier");
        this.navigator = Objects.requireNonNull(navigator, "navigator");

        this.items = DataStorage.loadItems();

        this.view = new CheckoutView(cashier.getFullName(), LOGO_PATH, AVATAR_PATH);

        wireActions();
        wireSelectionStockWarning();
        performSearch(); // show all items by default
    }

    public Parent getView() {
        return view.getRoot();
    }

    // Wiring
    private void wireActions() {
        view.setOnLogout(() -> navigator.showLogin());

        view.setOnGoMainMenu(() -> navigator.showCashierHome(cashier));
        view.setOnGoTodaysBills(() -> navigator.showBillView(cashier));
        view.setOnChangePassword(() -> navigator.showChangePassword(cashier));

        view.setOnSearch(this::performSearch);
        view.setOnAddToCart(this::addSelectedToCart);
        view.setOnRemoveLine(this::removeSelectedLine);
        view.setOnFinalizeBill(this::finalizeBill);
    }

    private void wireSelectionStockWarning() {
        view.getResultsTable().getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                view.setStockAlert("", false);
                return;
            }
            Item item = findItemById(newV.getItemCode());
            if (item == null) {
                view.setStockAlert("", false);
                return;
            }
            updateStockWarning(item);
        });
    }


    // Search
    private void performSearch() {
        reloadItemsFromStorage();
        String q = safe(view.getSearchField().getText()).toLowerCase();

        view.getResultsData().clear();
        view.setStockAlert("", false);

        if (q.isBlank()) {
            // show ALL items by default
            for (Item it : items) {
                int available = availableStock(it);
                String category = (it.getSector() == null) ? "-" : it.getSector().toString();
                view.getResultsData().add(new CheckoutView.ItemRow(
                        it.getItemName(),
                        it.getItemId(),
                        money(effectiveUnitPrice(it)),
                        String.valueOf(available),
                        category
                ));
            }
            if (view.getResultsData().isEmpty()) {
                view.setStockAlert("No items found.", false);
            }
            return;
        }


        for (Item it : items) {
            String id = safe(it.getItemId()).toLowerCase();
            String name = safe(it.getItemName()).toLowerCase();

            if (name.contains(q) || id.contains(q)) {
                int available = availableStock(it);

                String category = (it.getSector() == null) ? "-" : it.getSector().toString();
                String priceText = money(effectiveUnitPrice(it));
                String stockText = String.valueOf(available);

                CheckoutView.ItemRow row = new CheckoutView.ItemRow(
                        it.getItemName(),
                        it.getItemId(),
                        priceText,
                        stockText,
                        category
                );

                view.getResultsData().add(row);
            }
        }

        if (view.getResultsData().isEmpty()) {
            view.setStockAlert("No items found.", false);
        }
    }


    // Add to cart
    private void addSelectedToCart() {
        reloadItemsFromStorage();
        CheckoutView.ItemRow selected = view.getSelectedSearchResult();
        if (selected == null) {
            popup("Select an item", "Click an item in the left table first.", Alert.AlertType.WARNING);
            return;
        }

        Item item = findItemById(selected.getItemCode());
        if (item == null) {
            popup("Item missing", "This item is not found in storage anymore.", Alert.AlertType.ERROR);
            return;
        }

        int available = availableStock(item);
        if (available <= 0) {
            popup("Out of stock", "No stock available for this item.", Alert.AlertType.WARNING);
            updateStockWarning(item);
            return;
        }

        // If item already in cart -> increase quantity
        CheckoutView.CartRow cartRow = findCartRowByItemId(item.getItemId());

        if (cartRow == null) {
            // add new line with qty = 1
            view.getCartData().add(new CheckoutView.CartRow(
                    item.getItemName(),
                    item.getItemId(),
                    1,
                    money(effectiveUnitPrice(item)),
                    money(effectiveUnitPrice(item))
            ));
        } else {
            // increase existing qty
            int newQty = cartRow.quantityProperty().get() + 1;
            cartRow.quantityProperty().set(newQty);

            double unit = effectiveUnitPrice(item);
            cartRow.lineTotalProperty().set(money(unit * newQty));
        }

        // Update stock shown in search table (because reserved increased)
        refreshSearchRowStock(item);

        // Update totals
        refreshTotals();

        // Update low-stock warning label
        updateStockWarning(item);
    }

    private void reloadItemsFromStorage() {
        ArrayList<Item> fresh = DataStorage.loadItems();
        items.clear();
        items.addAll(fresh);
    }

    // Remove line
    private void removeSelectedLine() {
        CheckoutView.CartRow selected = view.getSelectedCartLine();
        if (selected == null) {
            popup("Select a line", "Click a line in the cart table first.", Alert.AlertType.WARNING);
            return;
        }

        String itemId = safe(selected.getItemCode());

        // remove that entire line (all quantity)
        view.getCartData().remove(selected);

        // Update stock shown in search results (reserved is now lower)
        Item item = findItemById(itemId);
        if (item != null) {
            refreshSearchRowStock(item);
            updateStockWarning(item);
        }

        refreshTotals();
    }


    // Finalize bill
    private void finalizeBill() {
        reloadItemsFromStorage();

        if (view.getCartData().isEmpty()) {
            popup("Cart is empty", "Add at least one item before finalizing.", Alert.AlertType.WARNING);
            return;
        }

        // Ask buyer info
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Finalize Bill");
        dialog.setHeaderText("Enter buyer information");
        dialog.setContentText("Buyer info:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String buyerInfo = safe(result.get());
        if (buyerInfo.isBlank()) {
            popup("Missing buyer info", "Buyer info cannot be empty.", Alert.AlertType.WARNING);
            return;
        }

        Bill bill;
        try {
            bill = cashier.createBill(buyerInfo);
        } catch (Exception ex) {
            popup("Bill creation failed", ex.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        // Add each cart line to bill qty times
        try {
            for (CheckoutView.CartRow row : view.getCartData()) {
                Item item = findItemById(row.getItemCode());
                if (item == null) continue;

                int qty = row.quantityProperty().get();

                for (int i = 0; i < qty; i++) {
                    bill.addBillItem(item); // this permanently decreases stock
                }
            }
        } catch (Exception ex) {
            popup("Finalize failed", ex.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        // Persist bill + updated items
        ArrayList<Bill> bills = DataStorage.loadBills();
        bills.add(bill);
        DataStorage.saveBills(bills);
        DataStorage.saveItems(items);

        // Clear cart UI
        view.getCartData().clear();
        refreshTotals();

        // Refresh search results stock (if they searched something)
        performSearch();

        popup("Success", "Bill finalized successfully.", Alert.AlertType.INFORMATION);

        // Optional: jump to today's bills
        navigator.showBillView(cashier);
    }


    // Helpers. everything is loops
    private Item findItemById(String itemId) {
        String id = safe(itemId);
        for (Item it : items) {
            if (safe(it.getItemId()).equals(id)) return it;
        }
        return null;
    }

    private CheckoutView.CartRow findCartRowByItemId(String itemId) {
        String id = safe(itemId);
        for (CheckoutView.CartRow r : view.getCartData()) {
            if (safe(r.getItemCode()).equals(id)) return r;
        }
        return null;
    }

    private int reservedQty(String itemId) {
        String id = safe(itemId);
        int sum = 0;
        for (CheckoutView.CartRow r : view.getCartData()) {
            if (safe(r.getItemCode()).equals(id)) {
                sum += r.quantityProperty().get();
            }
        }
        return sum;
    }

    private int availableStock(Item item) {
        return item.getStockQuantity() - reservedQty(item.getItemId());
    }

    private void refreshSearchRowStock(Item item) {
        String id = safe(item.getItemId());
        int available = availableStock(item);

        for (CheckoutView.ItemRow row : view.getResultsData()) {
            if (safe(row.getItemCode()).equals(id)) {
                row.stockProperty().set(String.valueOf(available));
                break;
            }
        }
    }

    private void updateStockWarning(Item item) {
        int available = availableStock(item);
        boolean low = available <= 3;

        if (low) {
            view.setStockAlert("LOW STOCK: only " + available + " left!", true);
        } else {
            view.setStockAlert("", false);
        }
    }

    private static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }

    private void popup(String title, String msg, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private static String money(double v) {
        return String.format(java.util.Locale.US, "%.2f", v);
    }

    private double effectiveUnitPrice(Item item) {
        if (item == null) return 0.0;

        double price = item.getSellingPrice();
        double discPct = item.getDiscountPercentage(); // 0..100

        // safety clamp (prevents weird negatives if something saved wrong)
        if (discPct < 0) discPct = 0;
        if (discPct > 100) discPct = 100;

        return price * (1.0 - discPct / 100.0);
    }

    private void refreshTotals() {
        double subtotal = 0;
        double discountAmount = 0;

        reloadItemsFromStorage();

        for (CheckoutView.CartRow row : view.getCartData()) {
            Item item = findItemById(row.getItemCode());
            if (item == null) continue;

            int qty = row.quantityProperty().get();
            double price = item.getSellingPrice();
            double discPct = item.getDiscountPercentage();

            subtotal += price * qty;
            discountAmount += price * qty * (discPct / 100.0);
        }

        double total = subtotal - discountAmount;

        view.setDiscountText("Discount: -" + money(discountAmount));
        view.setTotalText("Total: " + money(total));
    }
}