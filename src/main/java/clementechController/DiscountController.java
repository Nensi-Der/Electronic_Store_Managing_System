package clementechController;

import clementechModel.DataStorage;
import clementechModel.Item;
import clementechModel.Manager;
import clementechView.DiscountView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class DiscountController {

    private final DiscountView view;
    private final ArrayList<Item> items;

    public DiscountController(
            Manager manager,
            Runnable onHome,
            Runnable onManageStocks,
            Runnable onCheckSales,
            Runnable onModifySuppliers,
            Runnable onLogout,
            Runnable onChangePassword
    ) {
        Objects.requireNonNull(manager, "manager");

        this.view = new DiscountView(
                manager.getFullName(),
                "/logo/clementech.png",
                "/logo/manager.png"
        );

        // header
        view.setOnLogout(onLogout);
        view.setOnChangePassword(onChangePassword);

        // nav
        view.setOnHome(onHome);
        view.setOnManageStocks(onManageStocks);
        view.setOnCheckSales(onCheckSales);
        view.setOnModifySuppliers(onModifySuppliers);

        // load items
        this.items = DataStorage.loadItems();
        refreshTable();

        // double click to edit discount
        view.getTable().setRowFactory(tv -> {
            TableRow<DiscountView.Row> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY
                        && e.getClickCount() == 2
                        && !row.isEmpty()) {
                    editDiscount(row.getItem());
                }
            });
            return row;
        });
    }

    public Parent getView() {
        return view.getRoot();
    }

    private void refreshTable() {
        ObservableList<DiscountView.Row> rows = FXCollections.observableArrayList();
        for (Item it : items) {
            String text = safe(it.getItemName()) + "  (" + safe(it.getItemId()) + ")";
            rows.add(new DiscountView.Row(text, it.getDiscountPercentage()));
        }
        view.setRows(rows);
    }

    private void editDiscount(DiscountView.Row selectedRow) {
        if (selectedRow == null) return;

        String id = extractIdFromRowText(selectedRow.getItem());
        Item it = findById(id);
        if (it == null) {
            popup("Not found", "Could not locate item for this row.", Alert.AlertType.ERROR);
            return;
        }

        TextInputDialog d = new TextInputDialog(String.format(Locale.US, "%.2f", it.getDiscountPercentage()));
        d.setTitle("Set Discount");
        d.setHeaderText("Set discount for:\n" + safe(it.getItemName()) + " (" + safe(it.getItemId()) + ")");
        d.setContentText("Discount percentage (0 to remove):");

        Optional<String> res = d.showAndWait();
        if (res.isEmpty()) return;

        double val;
        try {
            val = Double.parseDouble(res.get().trim());
        } catch (Exception ex) {
            popup("Invalid", "Enter a number like 10 or 15.5", Alert.AlertType.WARNING);
            return;
        }

        if (val < 0 || val > 100) {
            popup("Invalid", "Discount must be between 0 and 100.", Alert.AlertType.WARNING);
            return;
        }

        it.setDiscountPercentage(val);   // 0 removes discount
        DataStorage.saveItems(items);

        refreshTable();
        popup("Saved", "Discount updated.", Alert.AlertType.INFORMATION);
    }

    private Item findById(String id) {
        String needle = safe(id);
        for (Item it : items) {
            if (safe(it.getItemId()).equalsIgnoreCase(needle)) return it;
        }
        return null;
    }

    // row text format: "Name (ID)"
    private String extractIdFromRowText(String text) {
        String s = safe(text);
        int open = s.lastIndexOf('(');
        int close = s.lastIndexOf(')');
        if (open >= 0 && close > open) return s.substring(open + 1, close).trim();
        return "";
    }

    private static void popup(String title, String msg, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }
}
