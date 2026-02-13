package clementechController;

import clementechModel.Administrator;
import clementechView.AdminView;
import javafx.scene.Parent;

import java.util.Objects;

public class AdminController {

    private final Administrator admin;
    private final Navigator nav;
    private final AdminView view;

    public AdminController(Administrator admin, Navigator nav) {
        this.admin = Objects.requireNonNull(admin, "admin");
        this.nav = Objects.requireNonNull(nav, "nav");

        this.view = new AdminView(
                admin.getFullName(),
                "/logo/clementech.png",
                "/logo/admin.png"
        );

        view.setOnManageEmployee(() -> nav.showManageEmployee(admin));
        view.setOnViewAllBills(() -> nav.showAdminBills(admin));
        view.setOnEditSuppliers(() -> nav.showSuppliers(admin));
        view.setOnViewInventory(() -> nav.showInventory(admin));
        view.setOnManagePermissions(() -> nav.showManagePermissions(admin));

        view.setOnLogout(nav::showLogin);
        view.setOnChangePassword(() -> nav.showChangePassword(admin));
    }

    public Parent getView() {
        return view.getRoot();
    }
}
