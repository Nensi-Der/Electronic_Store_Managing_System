package clementechController;

import clementechModel.Manager;
import clementechView.ManagerView;
import javafx.scene.Parent;

import java.util.Objects;

public class ManagerController {

    private final ManagerView view;

    public ManagerController(Manager manager, Navigator nav) {
        Objects.requireNonNull(manager, "manager");
        Objects.requireNonNull(nav, "nav");

        this.view = new ManagerView(
                manager.getFullName(),
                "/logo/clementech.png",
                "/logo/manager.png"
        );

        view.setOnManageStock(() -> nav.showManageStocks(manager));
        view.setOnCheckSales(() -> nav.showBillView(manager));
        view.setOnSetDiscounts(() -> nav.showDiscounts(manager));
        view.setOnModifySuppliers(() -> nav.showSuppliers(manager));

        view.setOnLogout(nav::showLogin);
        view.setOnChangePassword(() -> nav.showChangePassword(manager));
    }

    public Parent getView() {
        return view.getRoot();
    }
}
