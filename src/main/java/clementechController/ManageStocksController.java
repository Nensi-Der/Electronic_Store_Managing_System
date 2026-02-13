package clementechController;

import clementechModel.*;
import clementechView.ManageStocksView;
import clementechView.UserRole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class ManageStocksController {

    private final ManageStocksView view;
    private final ArrayList<Item> items = new ArrayList<>();

    // manager is only set in manager mode
    private final Manager manager;

    //ADMIN INVENTORY (READ ONLY)
    public ManageStocksController(Administrator admin, Navigator nav) {
        Objects.requireNonNull(admin, "admin");
        Objects.requireNonNull(nav, "nav");

        this.manager = null;

        this.view = new ManageStocksView(
                UserRole.ADMIN,
                admin.getFullName(),
                "/logo/clementech.png",
                "/logo/admin.png"
        );

        // header
        view.setOnLogout(nav::showLogin);
        view.setOnChangePassword(() -> nav.showChangePassword(admin));

        //ADMIN left nav wiring
        view.setOnAdminHome(() -> nav.showAdminHome(admin));
        view.setOnViewSuppliers(() -> nav.showSuppliers(admin));
        view.setOnManageEmployee(() -> nav.showManageEmployee(admin));
        view.setOnManagePermissions(() -> nav.showManagePermissions(admin));
        view.setOnViewInventory(() -> nav.showInventory(admin));

        // Admin must be read-only
        view.enableReadOnly();

        reloadItems();
        view.setItems(toRows(items));
    }

    //  MANAGER (FULL ACCESS)
    public ManageStocksController(Manager manager, Navigator nav) {
        Objects.requireNonNull(manager, "manager");
        Objects.requireNonNull(nav, "nav");

        this.manager = manager;

        this.view = new ManageStocksView(
                manager.getFullName(),
                "/logo/clementech.png",
                "/logo/manager.png"
        );

        // header
        view.setOnLogout(nav::showLogin);
        view.setOnChangePassword(() -> nav.showChangePassword(manager));

        // nav
        view.setOnHome(() -> nav.showManagerHome(manager));
        view.setOnCheckSales(() -> nav.showBillView(manager));
        view.setOnSetDiscounts(() -> nav.showDiscounts(manager));
        view.setOnModifySuppliers(() -> nav.showSuppliers(manager));

        reloadItems();
        view.setItems(toRows(items));

        // actions
        view.setOnAddStock(this::addStock);
        view.setOnDeleteItem(this::deleteItem);
        view.setOnAddNewItem(this::addNewItem);
    }

    public Parent getView() {
        return view.getRoot();
    }

    // DATA
    private void reloadItems() {
        items.clear();
        items.addAll(DataStorage.loadItems());
    }

    private ObservableList<ManageStocksView.ItemRow> toRows(ArrayList<Item> list) {
        ObservableList<ManageStocksView.ItemRow> out = FXCollections.observableArrayList();

        for (Item it : list) {
            if (it == null) continue;

            String cat = (it.getSector() == null) ? "-" : it.getSector().name();

            String supplier = safe(it.getSupplier());
            if (supplier.isBlank()) supplier = "-";

            out.add(new ManageStocksView.ItemRow(
                    safe(it.getItemId()),
                    safe(it.getItemName()),
                    cat,
                    it.getPurchasePrice(),
                    it.getSellingPrice(),
                    it.getStockQuantity(),
                    supplier
            ));
        }
        return out;
    }

    private Item findItemByNameAndSupplier(String name, String supplier) {
        String n = safe(name);
        String s = safe(supplier);

        for (Item it : items) {
            if (it == null) continue;

            boolean sameName = safe(it.getItemName()).equalsIgnoreCase(n);

            String itSup = safe(it.getSupplier());
            if (itSup.isBlank()) itSup = "-";

            boolean sameSup = itSup.equalsIgnoreCase(s);
            if (sameName && sameSup) return it;
        }
        return null;
    }

    // MANAGER ACTIONS
    private void addStock() {
        if (manager == null) return;

        ManageStocksView.ItemRow sel = view.getSelectedItem();
        if (sel == null) {
            popup("No selection", "Select an item first.", Alert.AlertType.WARNING);
            return;
        }

        Item it = findItemByNameAndSupplier(sel.getName(), sel.getSupplier());
        if (it == null) {
            popup("Not found", "Could not find this item in storage.", Alert.AlertType.ERROR);
            return;
        }

        TextInputDialog d = new TextInputDialog("1");
        d.setTitle("Add Stock");
        d.setHeaderText("Add stock for: " + safe(it.getItemName()));
        d.setContentText("Quantity to add:");
        Optional<String> res = d.showAndWait();
        if (res.isEmpty()) return;

        int qty;
        try {
            qty = Integer.parseInt(res.get().trim());
        } catch (Exception ex) {
            popup("Invalid", "Enter an integer like 5.", Alert.AlertType.WARNING);
            return;
        }
        if (qty <= 0) {
            popup("Invalid", "Quantity must be positive.", Alert.AlertType.WARNING);
            return;
        }

        it.setStockQuantity(it.getStockQuantity() + qty);
        DataStorage.saveItems(items);

        reloadItems();
        view.setItems(toRows(items));
        popup("Saved", "Stock updated.", Alert.AlertType.INFORMATION);
    }

    private void deleteItem() {
        if (manager == null) return;

        ManageStocksView.ItemRow sel = view.getSelectedItem();
        if (sel == null) {
            popup("No selection", "Select an item first.", Alert.AlertType.WARNING);
            return;
        }

        Item it = findItemByNameAndSupplier(sel.getName(), sel.getSupplier());
        if (it == null) {
            popup("Not found", "Could not find this item in storage.", Alert.AlertType.ERROR);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Item");
        confirm.setHeaderText("Delete: " + safe(it.getItemName()));
        confirm.setContentText("This removes the item from items.dat. Continue?");
        Optional<ButtonType> ans = confirm.showAndWait();
        if (ans.isEmpty() || ans.get() != ButtonType.OK) return;

        items.remove(it);
        DataStorage.saveItems(items);

        reloadItems();
        view.setItems(toRows(items));
        popup("Deleted", "Item removed.", Alert.AlertType.INFORMATION);
    }

    private void addNewItem() {
        if (manager == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Item");
        dialog.setHeaderText("Enter new item details");

        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField brandField = new TextField();
        TextField purchaseField = new TextField();
        TextField sellingField = new TextField();
        TextField stockField = new TextField();

        ComboBox<Sector> sectorBox = new ComboBox<>();
        sectorBox.getItems().setAll(Sector.values());
        sectorBox.getSelectionModel().selectFirst();

        ArrayList<Supplier> supList = DataStorage.loadSuppliers();
        ComboBox<String> supplierBox = new ComboBox<>();
        supplierBox.getItems().add("-");
        for (Supplier s : supList) {
            if (s != null && safe(s.getSupplierName()).length() > 0) {
                supplierBox.getItems().add(s.getSupplierName());
            }
        }
        supplierBox.getSelectionModel().selectFirst();

        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        g.addRow(0, new Label("Item ID:"), idField);
        g.addRow(1, new Label("Name:"), nameField);
        g.addRow(2, new Label("Brand:"), brandField);
        g.addRow(3, new Label("Purchase price:"), purchaseField);
        g.addRow(4, new Label("Selling price:"), sellingField);
        g.addRow(5, new Label("Stock:"), stockField);
        g.addRow(6, new Label("Sector:"), sectorBox);
        g.addRow(7, new Label("Supplier:"), supplierBox);

        dialog.getDialogPane().setContent(g);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isEmpty() || res.get() != addBtn) return;

        String id = safe(idField.getText());
        String name = safe(nameField.getText());
        String brand = safe(brandField.getText());

        double purchase, selling;
        int stock;
        try {
            purchase = Double.parseDouble(safe(purchaseField.getText()));
            selling = Double.parseDouble(safe(sellingField.getText()));
            stock = Integer.parseInt(safe(stockField.getText()));
        } catch (Exception ex) {
            popup("Invalid", "Enter valid numbers for prices and stock.", Alert.AlertType.WARNING);
            return;
        }

        if (id.isBlank() || name.isBlank() || brand.isBlank() || stock < 0 || purchase < 0 || selling < 0) {
            popup("Invalid", "Fill all fields correctly.", Alert.AlertType.WARNING);
            return;
        }

        reloadItems();

        // check duplicate by ID
        Item existing = findItemById(id);
        if (existing != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Item already exists");
            confirm.setHeaderText("An item with ID " + id + " already exists.");
            confirm.setContentText("Do you want to increase its stock by " + stock + " instead?");
            Optional<ButtonType> ans = confirm.showAndWait();

            if (ans.isEmpty() || ans.get() != ButtonType.OK) {
                return;
            }

            existing.setStockQuantity(existing.getStockQuantity() + stock);

            // existing.setPurchasePrice(purchase);
            // existing.setSellingPrice(selling);

            DataStorage.saveItems(items);

            reloadItems();
            view.setItems(toRows(items));
            popup("Saved", "Stock increased for existing item.", Alert.AlertType.INFORMATION);
            return;
        }

        Sector chosenSector = sectorBox.getValue();
        if (chosenSector == null) {
            popup("Invalid", "Please select a sector.", Alert.AlertType.WARNING);
            return;
        }

        Supplier chosenSupplier = null;
        String supName = supplierBox.getValue();
        if (supName != null && !supName.equals("-")) {
            for (Supplier s : supList) {
                if (s != null && safe(s.getSupplierName()).equalsIgnoreCase(supName)) {
                    chosenSupplier = s;
                    break;
                }
            }
        }
        Item itemToSave = new Item.PersistedItem(
                id, name, brand,
                purchase, selling,
                LocalDate.now(),
                stock,
                chosenSector,
                chosenSupplier
        );

        items.add(itemToSave);
        DataStorage.saveItems(items);

        reloadItems();
        view.setItems(toRows(items));
        popup("Success", "New item added.", Alert.AlertType.INFORMATION);
    }

    private static void popup(String title, String msg, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
    private Item findItemById(String id) {
        String needle = safe(id);
        if (needle.isBlank()) return null;

        for (Item it : items) {
            if (it == null) continue;
            String cur = safe(it.getItemId());
            if (!cur.isBlank() && cur.equalsIgnoreCase(needle)) return it;
        }
        return null;
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

}