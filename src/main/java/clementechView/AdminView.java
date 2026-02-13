package clementechView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.Objects;

public class AdminView {

    private final BorderPane root = new BorderPane();
    private final BaseStyles.HeaderParts headerParts;

    private final Button manageEmployeeBtn = new Button("Manage Employee");
    private final Button viewAllBillsBtn = new Button("View All Bills");
    private final Button viewSuppliersBtn = new Button("View Suppliers");
    private final Button viewInventoryBtn = new Button("View Inventory");
    private final Button managePermissionsBtn = new Button("Manage Permissions");

    private Runnable onManageEmployee;
    private Runnable onViewAllBills;
    private Runnable onViewSuppliers;
    private Runnable onViewInventory;
    private Runnable onManagePermissions;
    private Runnable onLogout;
    private Runnable onChangePassword;

    public AdminView(String adminFullName, String logoPath, String avatarPath) {

        //like CashierView
        root.setStyle("-fx-background-color: white;");

        headerParts = BaseStyles.buildEmployeeHeader(
                safeName(adminFullName, "Administrator"),
                logoPath,
                avatarPath
        );

        headerParts.setOnLogout(() -> { if (onLogout != null) onLogout.run(); });
        headerParts.setOnChangePassword(() -> { if (onChangePassword != null) onChangePassword.run(); });

        root.setTop(headerParts.headerBar);
        root.setCenter(buildCenter(adminFullName));
    }

    private Parent buildCenter(String adminFullName) {
        Label title = new Label("Welcome, " + safeName(adminFullName, "Administrator") + "!");
        BaseStyles.styleCenterTitle(title);

        Label subtitle = new Label("What do you feel like doing today?");
        BaseStyles.styleCenterSubtitle(subtitle);

        BaseStyles.styleBigOrangeButton(manageEmployeeBtn);
        BaseStyles.styleBigOrangeButton(viewAllBillsBtn);
        BaseStyles.styleBigOrangeButton(viewSuppliersBtn);
        BaseStyles.styleBigOrangeButton(viewInventoryBtn);
        BaseStyles.styleBigOrangeButton(managePermissionsBtn);

        manageEmployeeBtn.setOnAction(e -> { if (onManageEmployee != null) onManageEmployee.run(); });
        viewAllBillsBtn.setOnAction(e -> { if (onViewAllBills != null) onViewAllBills.run(); });
        viewSuppliersBtn.setOnAction(e -> { if (onViewSuppliers != null) onViewSuppliers.run(); });
        viewInventoryBtn.setOnAction(e -> { if (onViewInventory != null) onViewInventory.run(); });
        managePermissionsBtn.setOnAction(e -> { if (onManagePermissions != null) onManagePermissions.run(); });

        //3 top, 2 bottom, centered
        HBox topRow = new HBox(18, manageEmployeeBtn, viewAllBillsBtn, viewSuppliersBtn);
        topRow.setAlignment(Pos.CENTER);

        HBox bottomRow = new HBox(18, viewInventoryBtn, managePermissionsBtn);
        bottomRow.setAlignment(Pos.CENTER);

        VBox center = new VBox(
                10,
                title,
                subtitle,
                spacer(16),
                topRow,
                bottomRow
        );
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));

        return center;
    }

    private Region spacer(double h) {
        Region r = new Region();
        r.setMinHeight(h);
        r.setPrefHeight(h);
        r.setMaxHeight(h);
        return r;
    }

    public Parent getRoot() { return root; }

    //Controller wiring
    public void setOnManageEmployee(Runnable r) { this.onManageEmployee = Objects.requireNonNull(r); }
    public void setOnViewAllBills(Runnable r) { this.onViewAllBills = Objects.requireNonNull(r); }
    public void setOnEditSuppliers(Runnable r) { this.onViewSuppliers = Objects.requireNonNull(r); }
    public void setOnViewInventory(Runnable r) { this.onViewInventory = Objects.requireNonNull(r); }
    public void setOnManagePermissions(Runnable r) { this.onManagePermissions = Objects.requireNonNull(r); }
    public void setOnLogout(Runnable r) { this.onLogout = Objects.requireNonNull(r); }
    public void setOnChangePassword(Runnable r) { this.onChangePassword = Objects.requireNonNull(r); }

    private static String safeName(String fullName, String fallback) {
        String s = fullName == null ? "" : fullName.trim();
        return s.isBlank() ? fallback : s;
    }
}
