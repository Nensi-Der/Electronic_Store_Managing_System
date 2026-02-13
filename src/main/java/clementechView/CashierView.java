package clementechView;

import java.util.Objects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class CashierView {

    private final BorderPane root = new BorderPane();

    private final BaseStyles.HeaderParts headerParts;

    private Runnable onLogout;
    private Runnable onChangePassword;

    private final Label welcome = new Label();
    private final Label prompt = new Label();

    private final Button checkoutBtn = new Button("Create New Bill");
    private final Button todaysBillsBtn = new Button("View Today's Bills");

    private Runnable onCheckout;
    private Runnable onTodaysBills;

    public CashierView(String cashierFullName, String logoResourcePath, String avatarResourcePath) {
        String displayName = safeName(cashierFullName, "Cashier");

        root.setStyle("-fx-background-color: white;");

        headerParts = BaseStyles.buildEmployeeHeader(displayName, logoResourcePath, avatarResourcePath);
        headerParts.setOnLogout(() -> runAction("Logout", onLogout));
        headerParts.setOnChangePassword(() -> runAction("Change Password", onChangePassword));
        forceInteractive(headerParts.headerBar);
        root.setTop(headerParts.headerBar);
        buildCenterContent(displayName);
    }

    public CashierView(String cashierFirstName, String cashierLastName,
                       String logoResourcePath, String avatarResourcePath) {
        this((safe(cashierFirstName) + " " + safe(cashierLastName)).trim(),
                logoResourcePath, avatarResourcePath);
    }

    private void buildCenterContent(String cashierFullName) {
        welcome.setText("Welcome, " + cashierFullName + "!");
        welcome.setStyle("-fx-font-weight: 800; -fx-text-fill: #F98E02;");
        welcome.setFont(loadSystemaOrFallback(40));

        prompt.setText("What do you feel like doing today?");
        prompt.setStyle("-fx-font-weight: 600; -fx-text-fill: #F98E02;");
        prompt.setFont(loadSystemaOrFallback(20));

        styleBigOrangeButton(checkoutBtn);
        styleBigOrangeButton(todaysBillsBtn);

        checkoutBtn.setOnAction(e -> runAction("Create New Bill", onCheckout));
        todaysBillsBtn.setOnAction(e -> runAction("Today's Bills", onTodaysBills));

        VBox center = new VBox(
                10,
                welcome,
                prompt,
                spacer(16),
                checkoutBtn,
                todaysBillsBtn
        );
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));

        root.setCenter(center);
    }

    private Region spacer(double height) {
        Region r = new Region();
        r.setMinHeight(height);
        r.setPrefHeight(height);
        r.setMaxHeight(height);
        return r;
    }

    private void styleBigOrangeButton(Button btn) {
        btn.setPrefWidth(420);
        btn.setPrefHeight(120);
        btn.setFont(Font.font(18));
        btn.setCursor(Cursor.HAND);

        String normal = """
                -fx-background-color: #F98E02;
                -fx-text-fill: white;
                -fx-font-weight: 800;
                -fx-background-radius: 22;
                """;

        String hover = """
                -fx-background-color: #ff9f1a;
                -fx-text-fill: white;
                -fx-font-weight: 800;
                -fx-background-radius: 22;
                """;

        btn.setStyle(normal);
        btn.setEffect(new DropShadow());

        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
    }

    private Font loadSystemaOrFallback(double size) {
        try {
            Font f = Font.loadFont(getClass().getResourceAsStream("/fonts/9SYSTEMA.TTF"), size);
            return f != null ? f : Font.font(size);
        } catch (Exception e) {
            return Font.font(size);
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static String safeName(String fullName, String fallback) {
        String s = safe(fullName);
        return s.isBlank() ? fallback : s;
    }

    public Parent getRoot() { return root; }

    public void setCashierName(String fullName) {
        String name = safeName(fullName, "Cashier");
        headerParts.setEmployeeName(name);
        welcome.setText("Welcome, " + name + "!");
    }

    public void setOnCheckout(Runnable onCheckout) {
        this.onCheckout = Objects.requireNonNull(onCheckout, "onCheckout");
    }
    public void setOnTodaysBills(Runnable onTodaysBills) {
        this.onTodaysBills = Objects.requireNonNull(onTodaysBills, "onTodaysBills");
    }
    public void setOnLogout(Runnable onLogout) {
        this.onLogout = Objects.requireNonNull(onLogout, "onLogout");
    }
    public void setOnChangePassword(Runnable onChangePassword) {
        this.onChangePassword = Objects.requireNonNull(onChangePassword, "onChangePassword");
    }
    public Button getCheckoutBtn() { return checkoutBtn; }
    public Button getTodaysBillsBtn() { return todaysBillsBtn; }

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
}
