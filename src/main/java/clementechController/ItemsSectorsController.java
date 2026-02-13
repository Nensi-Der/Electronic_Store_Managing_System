package clementechController;

import clementechModel.Administrator;
import clementechModel.DataStorage;
import clementechModel.Item;
import clementechModel.Manager;
import clementechModel.Sector;
import clementechView.ItemsSectorsView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeSet;

public class ItemsSectorsController {

    private final ItemsSectorsView view;

    public ItemsSectorsController(Manager manager, String supplierName, Navigator nav) {
        Objects.requireNonNull(manager, "manager");
        Objects.requireNonNull(nav, "nav");

        String supplier = safe(supplierName);

        this.view = new ItemsSectorsView(
                manager.getFullName(),
                "/logo/clementech.png",
                "/logo/manager.png",
                supplier
        );

        view.setOnLogout(nav::showLogin);
        view.setOnChangePassword(() -> nav.showChangePassword(manager));

        view.setOnBack(() -> nav.showSuppliers(manager));
        view.setOnHome(() -> nav.showManagerHome(manager));
        view.setOnCheckSales(() -> nav.showBillView(manager));
        view.setOnSetDiscounts(() -> nav.showDiscounts(manager));
        view.setOnManageStocks(() -> nav.showManageStocks(manager));

        ArrayList<Item> items = DataStorage.loadItems();
        view.setRows(buildRows(items, supplier));
    }

    public ItemsSectorsController(Administrator admin, String supplierName, Navigator nav) {
        Objects.requireNonNull(admin, "admin");
        Objects.requireNonNull(nav, "nav");

        String supplier = safe(supplierName);

        this.view = new ItemsSectorsView(
                admin.getFullName(),
                "/logo/clementech.png",
                "/logo/admin.png",
                supplier
        );

        view.setOnLogout(nav::showLogin);
        view.setOnChangePassword(() -> nav.showChangePassword(admin));

        view.setOnBack(() -> nav.showSuppliers(admin));
        view.setOnHome(() -> nav.showAdminHome(admin));
        view.setOnCheckSales(() -> nav.showBillView(admin));
        view.setOnSetDiscounts(() -> {});
        view.setOnManageStocks(() -> nav.showInventory(admin));

        ArrayList<Item> items = DataStorage.loadItems();
        view.setRows(buildRows(items, supplier));
    }

    public Parent getView() {
        return view.getRoot();
    }

    private ObservableList<ItemsSectorsView.Row> buildRows(ArrayList<Item> items, String supplierName) {
        String needle = safe(supplierName);

        TreeSet<String> itemNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        TreeSet<String> sectorNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        if (items != null && !needle.isBlank()) {
            for (Item it : items) {
                if (it == null) continue;

                String sup = safe(it.getSupplier());
                if (!sup.equalsIgnoreCase(needle)) continue;

                String nm = safe(it.getItemName());
                if (!nm.isBlank()) itemNames.add(nm);

                Sector s = it.getSector();
                if (s != null) sectorNames.add(s.name());
            }
        }

        ArrayList<String> itemsList = new ArrayList<>(itemNames);
        ArrayList<String> sectorsList = new ArrayList<>(sectorNames);

        int n = Math.max(itemsList.size(), sectorsList.size());
        ObservableList<ItemsSectorsView.Row> out = FXCollections.observableArrayList();

        for (int i = 0; i < n; i++) {
            String item = (i < itemsList.size()) ? itemsList.get(i) : "";
            String sector = (i < sectorsList.size()) ? sectorsList.get(i) : "";
            out.add(new ItemsSectorsView.Row(item, sector));
        }

        return out;
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
