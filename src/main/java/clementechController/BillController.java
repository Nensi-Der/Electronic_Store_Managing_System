package clementechController;

import clementechModel.Administrator;
import clementechModel.Bill;
import clementechModel.Cashier;
import clementechModel.DataStorage;
import clementechModel.Manager;
import clementechView.BillPeriod;
import clementechView.BillView;
import clementechView.UserRole;
import javafx.scene.Parent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BillController {

    private final BillView view;

    // keep all bills loaded once
    private final List<Bill> allBills = new ArrayList<>();

    private final UserRole role;

    // optional: remember last filter state for refresh()
    private BillPeriod lastManagerPeriod = BillPeriod.TODAY;
    private LocalDate lastAdminFrom = LocalDate.now();
    private LocalDate lastAdminTo = LocalDate.now();

    // ===================== CASHIER =====================
    public BillController(
            Cashier cashier,
            Runnable onMainMenu,
            Runnable onCheckout,
            Runnable onLogout,
            Runnable onChangePassword
    ) {
        Objects.requireNonNull(cashier, "cashier");
        this.role = UserRole.CASHIER;

        //new constructor
        this.view = new BillView(cashier);

        view.setOnChangePassword(onChangePassword);
        wireNav(onMainMenu, onCheckout, onLogout, onChangePassword);

        loadAllBills();


        view.setBills(allBills);
    }

    // ===================== MANAGER =====================
    public BillController(
            Manager manager,
            Runnable onHome,
            Runnable onManageStocks,
            Runnable onSetDiscounts,
            Runnable onModifySuppliers,
            Runnable onLogout,
            Runnable onChangePassword
    ) {
        Objects.requireNonNull(manager, "manager");
        this.role = UserRole.MANAGER;

        //new constructor
        this.view = new BillView(manager);

        view.setOnMainMenu(onHome);
        view.setOnManageStocks(onManageStocks);
        view.setOnSetDiscounts(onSetDiscounts);
        view.setOnModifySuppliers(onModifySuppliers);

        view.setOnLogout(onLogout);
        view.setOnChangePassword(onChangePassword);

        // if you want controller to also react when manager changes period:
        view.setOnManagerPeriodChanged(this::onManagerPeriodChanged);

        loadAllBills();
        view.setBills(allBills); // BillView shows TODAY by default
    }

    // ===================== ADMIN =====================
    public BillController(
            Administrator admin,
            Runnable onHome,
            Runnable onViewSuppliers,
            Runnable onManageEmployee,
            Runnable onManagePermissions,
            Runnable onViewInventory,
            Runnable onLogout,
            Runnable onChangePassword
    ) {
        Objects.requireNonNull(admin, "admin");
        this.role = UserRole.ADMIN;

        this.view = new BillView(admin);

        // header
        view.setOnLogout(onLogout);
        view.setOnChangePassword(onChangePassword);

        // side nav (ADMIN)
        view.setOnMainMenu(onHome);
        view.setOnViewSuppliers(onViewSuppliers);
        view.setOnManageEmployee(onManageEmployee);
        view.setOnManagePermissions(onManagePermissions);
        view.setOnViewInventory(onViewInventory);

        // admin date range callback (optional)
        view.setOnAdminDateRangeApply(this::onAdminRangeApply);

        loadAllBills();

        // show all bills; BillView will filter by date range itself
        view.setBills(allBills);
    }

    // ===================== API =====================
    public Parent getView() {
        return view.getRoot();
    }

    public void refresh() {
        loadAllBills();

        if (role == UserRole.CASHIER) {
            // cashier view filters itself (today + createdBy)
            view.setBills(allBills);
            return;
        }

        if (role == UserRole.MANAGER) {
            // manager view filters itself based on selectedPeriod inside BillView
            // but if you want the controller to “force” a period:
            onManagerPeriodChanged(lastManagerPeriod);
            return;
        }

        // admin: re-apply last selected range
        view.setBills(allBills);
    }

    //  WIRING
    private void wireNav(Runnable onMainMenu, Runnable onCheckout, Runnable onLogout, Runnable onChangePassword) {
        view.setOnMainMenu(onMainMenu);
        view.setOnCheckout(onCheckout);
        view.setOnLogout(onLogout);
        view.setOnChangePassword(onChangePassword);
    }

    // CALLBACKS
    private void onManagerPeriodChanged(BillPeriod period) {
        if (period == null) period = BillPeriod.TODAY;
        lastManagerPeriod = period;

        // Just ensure the view has the latest data.
        loadAllBills();
        view.setBills(allBills);
    }
    private void onAdminRangeApply(LocalDate from, LocalDate to) {
        if (from == null || to == null) return;

        lastAdminFrom = from;
        lastAdminTo = to;

        //DO NOT filter here. BillView does it.
        loadAllBills();
        view.setBills(allBills);
    }

    // LOADING/FILTERING
    private void loadAllBills() {
        allBills.clear();
        allBills.addAll(DataStorage.loadBills());
    }

    private List<Bill> filterBillsForPeriod(BillPeriod period) {
        LocalDate today = LocalDate.now();

        return switch (period) {
            case TODAY -> filterBillsBetween(today, today);

            // last 7 days including today
            case THIS_WEEK -> filterBillsBetween(today.minusDays(6), today);

            // last 30 days including today
            case THIS_MONTH -> filterBillsBetween(today.minusDays(29), today);

            // last 365 days including today
            case THIS_YEAR -> filterBillsBetween(today.minusDays(364), today);

            case ALL -> new ArrayList<>(allBills);
        };
    }

    private List<Bill> filterBillsBetween(LocalDate from, LocalDate to) {
        List<Bill> out = new ArrayList<>();
        for (Bill b : allBills) {
            if (b == null) continue;
            LocalDate d = b.getDateBillIsGettingCut();
            if (d == null) continue;

            boolean ok = !d.isBefore(from) && !d.isAfter(to);
            if (ok) out.add(b);
        }
        return out;
    }
}
