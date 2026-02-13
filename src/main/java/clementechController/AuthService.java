package clementechController;

import clementechModel.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AuthService {

    public enum Role { CASHIER, MANAGER, ADMIN }

    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final Employee user;
        private final Role role;

        public AuthResult(boolean success, String message, Employee user, Role role) {
            this.success = success;
            this.message = message;
            this.user = user;
            this.role = role;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Employee getUser() { return user; }
        public Role getRole() { return role; }
    }

    // Persisted lists (match DataStorage)
    private ArrayList<Cashier> cashiers = new ArrayList<>();
    private ArrayList<Manager> managers = new ArrayList<>();
    private Administrator admin;

    private Employee currentUser;
    private Role currentRole;

    public AuthService() {
        reloadFromStorage();
    }

    public void reloadFromStorage() {
        admin = DataStorage.loadAdmin();
        managers = DataStorage.loadManagers();
        cashiers = DataStorage.loadCashiers();
    }

    public void setCashiers(List<Cashier> list) {
        if (list == null || list.isEmpty()) {
            // DO NOT overwrite real data with empty
            return;
        }
        cashiers = new ArrayList<>(list);
        DataStorage.saveCashiers(cashiers);
    }

    public void setManagers(List<Manager> list) {
        if (list == null || list.isEmpty()) {
            // DO NOT overwrite real data with empty
            return;
        }
        managers = new ArrayList<>(list);
        DataStorage.saveManagers(managers);
    }

    public void setAdmin(Administrator admin) {
        if (admin == null) {
            // DO NOT overwrite admin.dat with null
            return;
        }
        this.admin = admin;
        DataStorage.saveAdmin(admin);
    }

    public AuthResult login(String username, String password) {
        reloadFromStorage();

        String u = safe(username);
        String p = password == null ? "" : password;

        if (u.isBlank() || p.isBlank()) {
            return new AuthResult(false, "Username and password are required.", null, null);
        }

        // 1) Admin
        if (admin != null && u.equalsIgnoreCase(admin.getUsername())) {
            if (Objects.equals(admin.getPassword(), p)) {
                setSession(admin, Role.ADMIN);
                try { admin.setLastLogIn(LocalDate.now()); } catch (Exception ignored) {}
                DataStorage.saveAdmin(admin);
                return new AuthResult(true, "Login successful.", admin, Role.ADMIN);
            }
            return new AuthResult(false, "Wrong password.", null, null);
        }

        // 2) Managers
        for (Manager m : managers) {
            if (u.equalsIgnoreCase(m.getUsername())) {
                if (Objects.equals(m.getPassword(), p)) {
                    setSession(m, Role.MANAGER);
                    try { m.setLastLogIn(LocalDate.now()); } catch (Exception ignored) {}
                    DataStorage.saveManagers(managers); //persist
                    return new AuthResult(true, "Login successful.", m, Role.MANAGER);
                }
                return new AuthResult(false, "Wrong password.", null, null);
            }
        }

        // 3) Cashiers
        for (Cashier c : cashiers) {
            if (u.equalsIgnoreCase(c.getUsername())) {
                if (Objects.equals(c.getPassword(), p)) {
                    setSession(c, Role.CASHIER);
                    try { c.setLastLogIn(LocalDate.now()); } catch (Exception ignored) {}
                    DataStorage.saveCashiers(cashiers); //persist
                    return new AuthResult(true, "Login successful.", c, Role.CASHIER);
                }
                return new AuthResult(false, "Wrong password.", null, null);
            }
        }

        return new AuthResult(false, "User not found.", null, null);
    }

    public void logout() {
        currentUser = null;
        currentRole = null;
    }

    public boolean isLoggedIn() { return currentUser != null; }
    public Employee getCurrentUser() { return currentUser; }
    public Role getCurrentRole() { return currentRole; }

    public boolean needsPasswordChange() {
        return currentUser != null && currentUser.needsPasswordChange();
    }
    public boolean changeCurrentUserPassword(String oldPass, String newPass) {
        if (currentUser == null) return false;

        String before = currentUser.getPassword();

        try {
            currentUser.changePassword(oldPass, newPass);
        } catch (Exception e) {
            return false;
        }

        String after = currentUser.getPassword();
        boolean changed = !Objects.equals(before, after);

        if (changed) saveCurrentUser();
        return changed;
    }

    private void saveCurrentUser() {
        if (currentUser instanceof Administrator a) {
            DataStorage.saveAdmin(a);
        } else if (currentUser instanceof Manager) {
            DataStorage.saveManagers(managers);
        } else if (currentUser instanceof Cashier) {
            DataStorage.saveCashiers(cashiers);
        }
    }

    private void setSession(Employee user, Role role) {
        currentUser = user;
        currentRole = role;
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
