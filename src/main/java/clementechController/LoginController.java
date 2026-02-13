package clementechController;

import java.util.Objects;

import clementechModel.Administrator;
import clementechModel.Cashier;
import clementechModel.Manager;
import clementechView.LoginView;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController {

    private final LoginView view;
    private final AuthService authService;
    private final Navigator navigator;

    public LoginController(AuthService authService, Navigator navigator) {
        this.authService = Objects.requireNonNull(authService, "authService");
        this.navigator = Objects.requireNonNull(navigator, "navigator");
        this.view = new LoginView();
        wireActions();
    }

    private void wireActions() {
        view.getLoginButton().setOnAction(e -> attemptLogin());

        view.getRoot().addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                attemptLogin();
                e.consume();
            }
        });
    }

    private void attemptLogin() {
        String username = view.getUsernameField().getText();
        String password = view.getPassword();

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            showError("Missing fields", "Please enter username and password.");
            return;
        }

        AuthService.AuthResult res = authService.login(username, password);

        if (!res.isSuccess() || res.getUser() == null || res.getRole() == null) {
            showError("Login failed", res.getMessage() == null ? "Wrong username or password." : res.getMessage());
            return;
        }

        switch (res.getRole()) {
            case CASHIER -> navigator.showCashierHome((Cashier) res.getUser());
            case MANAGER -> navigator.showManagerHome((Manager) res.getUser());
            case ADMIN -> navigator.showAdminHome((Administrator) res.getUser());
            default -> showError("Login failed", "Unknown role.");
        }
    }

    private void showError(String title, String message) {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    public Parent getView() {
        return view.getRoot();
    }
}
