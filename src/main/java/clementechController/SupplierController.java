package clementechController;

import clementechModel.Administrator;
import clementechModel.DataStorage;
import clementechModel.Manager;
import clementechModel.Supplier;
import clementechView.SupplierView;
import clementechView.SupplierView.SupplierRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.util.ArrayList;

public class SupplierController {

    private final SupplierView view;
    private final Object user;
    private final Navigator nav;

    private final ObservableList<SupplierRow> supplierRows = FXCollections.observableArrayList();

    public SupplierController(SupplierView view, Object user, Navigator nav) {
        this.view = view;
        this.user = user;
        this.nav = nav;

        loadSuppliers();
        wireActions();
        wireOpenItemsSectors();
    }

    private void loadSuppliers() {
        ArrayList<Supplier> suppliers = DataStorage.loadSuppliers();

        supplierRows.clear();
        for (Supplier s : suppliers) {
            supplierRows.add(new SupplierRow(
                    s.getSupplierName(),
                    s.getContactInfo()
            ));
        }

        view.setSuppliers(supplierRows);
    }

    private void wireActions() {
        boolean canEdit = (user instanceof Manager);

        view.setOnEditSupplier(() -> {
            if (!canEdit) return;

            SupplierRow selected = view.getSelectedSupplier();
            if (selected == null) {
                popup("No selection", "Select a supplier first.", AlertType.WARNING);
                return;
            }

            TextInputDialog nameD = new TextInputDialog(selected.getName());
            nameD.setTitle("Edit Supplier");
            nameD.setHeaderText("Edit supplier name");
            nameD.setContentText("Name:");
            var nameRes = nameD.showAndWait();
            if (nameRes.isEmpty()) return;

            String newName = nameRes.get().trim();
            if (newName.isBlank()) {
                popup("Invalid", "Name cannot be empty.", AlertType.WARNING);
                return;
            }

            TextInputDialog contactD = new TextInputDialog(selected.getContact());
            contactD.setTitle("Edit Supplier");
            contactD.setHeaderText("Edit contact info");
            contactD.setContentText("Contact:");
            var contactRes = contactD.showAndWait();
            if (contactRes.isEmpty()) return;

            String newContact = contactRes.get().trim();

            selected.nameProperty().set(newName);
            selected.contactProperty().set(newContact);

            save();
            view.getTable().refresh();
            popup("Success", "Supplier updated successfully.", AlertType.INFORMATION);
        });

        view.setOnAddSupplier(() -> {
            if (!canEdit) return;

            TextInputDialog nameD = new TextInputDialog();
            nameD.setTitle("Add Supplier");
            nameD.setHeaderText("Supplier name");
            nameD.setContentText("Name:");
            var nameRes = nameD.showAndWait();
            if (nameRes.isEmpty()) return;

            String name = nameRes.get().trim();
            if (name.isBlank()) return;

            TextInputDialog contactD = new TextInputDialog();
            contactD.setTitle("Add Supplier");
            contactD.setHeaderText("Contact info");
            contactD.setContentText("Contact:");
            var contactRes = contactD.showAndWait();
            if (contactRes.isEmpty()) return;

            String contact = contactRes.get().trim();

            supplierRows.add(new SupplierRow(name, contact));
            view.setSuppliers(supplierRows);
            save();

            popup("Success", "Supplier created successfully.", AlertType.INFORMATION);
        });

        view.setOnDeleteSupplier(() -> {
            if (!canEdit) return;

            SupplierRow selected = view.getSelectedSupplier();
            if (selected == null) {
                popup("No selection", "Select a supplier first.", AlertType.WARNING);
                return;
            }

            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.setTitle("Delete Supplier");
            confirm.setHeaderText("Delete supplier: " + selected.getName());
            confirm.setContentText("This will remove the supplier from storage. Continue?");
            var ans = confirm.showAndWait();
            if (ans.isEmpty() || ans.get() != ButtonType.OK) return;

            supplierRows.remove(selected);
            save();
            view.setSuppliers(supplierRows);

            popup("Deleted", "Supplier removed.", AlertType.INFORMATION);
        });
    }

    private void wireOpenItemsSectors() {
        view.getTable().setRowFactory(tv -> {
            TableRow<SupplierRow> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() != 2 || row.isEmpty()) return;

                SupplierRow selected = row.getItem();
                if (selected == null) return;

                String supplierName = selected.getName();
                if (supplierName == null || supplierName.trim().isBlank()) return;

                if (user instanceof Manager m) {
                    nav.showItemsSectors(m, supplierName);
                } else if (user instanceof Administrator a) {
                    nav.showItemsSectors(a, supplierName);
                }
            });
            return row;
        });
    }

    private void save() {
        ArrayList<Supplier> toSave = new ArrayList<>();
        for (SupplierRow r : supplierRows) {
            toSave.add(new Supplier(r.getName(), r.getContact()));
        }
        DataStorage.saveSuppliers(toSave);
    }

    private void popup(String title, String msg, AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
