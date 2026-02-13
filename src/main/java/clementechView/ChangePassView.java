package clementechView;

import java.util.Objects;
import clementechView.BaseStyles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class ChangePassView {

    private final StackPane root = new StackPane();
    private final Button backBtn = BaseStyles.buildBackImageButton("/logo/Back Button 2.png", 70);
    private final ImageView logoView = BaseStyles.buildAuthLogo("/logo/clementech.png", 140);
    private final PasswordField oldPass = new PasswordField();
    private final TextField oldPassVisible = new TextField();
    private final PasswordField newPass = new PasswordField();
    private final TextField newPassVisible = new TextField();
    private final PasswordField confirmPass = new PasswordField();
    private final TextField confirmPassVisible = new TextField();
    private final CheckBox showPasswords = new CheckBox("Show password");
    private final StackPane oldPassWrap = BaseStyles.wrapWithHoverGlow(oldPass);
    private final StackPane oldPassVisibleWrap = BaseStyles.wrapWithHoverGlow(oldPassVisible);
    private final StackPane newPassWrap = BaseStyles.wrapWithHoverGlow(newPass);
    private final StackPane newPassVisibleWrap = BaseStyles.wrapWithHoverGlow(newPassVisible);
    private final StackPane confirmPassWrap = BaseStyles.wrapWithHoverGlow(confirmPass);
    private final StackPane confirmPassVisibleWrap = BaseStyles.wrapWithHoverGlow(confirmPassVisible);

    private final Label statusLabel = new Label();
    private final Button saveBtn = new Button("Save Password");

    private Runnable onBack;
    private Runnable onSave;
    private boolean requireOldPassword = false;

    public ChangePassView() {
        buildUI();
    }

    private void buildUI() {
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F98E02;"); // orange background

        Font font = Font.font("Bahnschrift", 16);

        oldPass.setPromptText("current password");
        oldPass.setFont(font);
        oldPass.setMaxWidth(360);
        BaseStyles.styleAuthField(oldPass);

        oldPassVisible.setPromptText("current password");
        oldPassVisible.setFont(font);
        oldPassVisible.setMaxWidth(360);
        BaseStyles.styleAuthField(oldPassVisible);

        newPass.setPromptText("new password");
        newPass.setFont(font);
        newPass.setMaxWidth(360);
        BaseStyles.styleAuthField(newPass);

        newPassVisible.setPromptText("new password");
        newPassVisible.setFont(font);
        newPassVisible.setMaxWidth(360);
        BaseStyles.styleAuthField(newPassVisible);

        confirmPass.setPromptText("confirm new password");
        confirmPass.setFont(font);
        confirmPass.setMaxWidth(360);
        BaseStyles.styleAuthField(confirmPass);

        confirmPassVisible.setPromptText("confirm new password");
        confirmPassVisible.setFont(font);
        confirmPassVisible.setMaxWidth(360);
        BaseStyles.styleAuthField(confirmPassVisible);

        // bindings for visible fields
        oldPassVisible.textProperty().bindBidirectional(oldPass.textProperty());
        newPassVisible.textProperty().bindBidirectional(newPass.textProperty());
        confirmPassVisible.textProperty().bindBidirectional(confirmPass.textProperty());

        // show/hide
        showPasswords.setFont(font);

        oldPassVisibleWrap.setVisible(false);
        oldPassVisibleWrap.setManaged(false);

        newPassVisibleWrap.setVisible(false);
        newPassVisibleWrap.setManaged(false);

        confirmPassVisibleWrap.setVisible(false);
        confirmPassVisibleWrap.setManaged(false);

        showPasswords.setOnAction(e -> {
            boolean show = showPasswords.isSelected();

            toggleVisible(oldPassWrap, oldPassVisibleWrap, show);
            toggleVisible(newPassWrap, newPassVisibleWrap, show);
            toggleVisible(confirmPassWrap, confirmPassVisibleWrap, show);
        });

        statusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: 700;");
        statusLabel.setFont(Font.font("Bahnschrift", 14));
        saveBtn.setFont(Font.font("Bahnschrift", 18));
        BaseStyles.styleGreenPrimaryButton(saveBtn);

        StackPane.setAlignment(backBtn, Pos.TOP_LEFT);
        StackPane.setMargin(backBtn, new Insets(8, 0, 0, 8));
        backBtn.setOnAction(e -> { if (onBack != null) onBack.run(); });
        saveBtn.setOnAction(e -> { if (onSave != null) onSave.run(); });

        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        Label title = new Label("Create a new password");
        title.setStyle("-fx-text-fill: white; -fx-font-weight: 900;");
        title.setFont(Font.font("Bahnschrift", 22));

        setRequireOldPassword(false);

        box.getChildren().addAll(
                logoView,
                title,
                oldPassWrap, oldPassVisibleWrap,
                newPassWrap, newPassVisibleWrap,
                confirmPassWrap, confirmPassVisibleWrap,
                showPasswords,
                statusLabel,
                saveBtn
        );

        root.getChildren().addAll(box, backBtn);
        backBtn.toFront();

    }

    private void toggleVisible(StackPane hiddenWrap, StackPane visibleWrap, boolean show) {
        hiddenWrap.setVisible(!show);
        hiddenWrap.setManaged(!show);

        visibleWrap.setVisible(show);
        visibleWrap.setManaged(show);
    }

    public Parent getRoot() { return root; }

    public void setOnBack(Runnable r) { this.onBack = Objects.requireNonNull(r, "onBack"); }
    public void setOnSave(Runnable r) { this.onSave = Objects.requireNonNull(r, "onSave"); }

    // use this when it's NOT first login
    public void setRequireOldPassword(boolean require) {
        this.requireOldPassword = require;

        oldPassWrap.setVisible(require);
        oldPassWrap.setManaged(require);

        oldPassVisibleWrap.setVisible(false);
        oldPassVisibleWrap.setManaged(false);

        // if we hid old password, also clear it
        if (!require) {
            oldPass.clear();
            oldPassVisible.clear();
        }
    }

    public boolean isRequireOldPassword() { return requireOldPassword; }

    public String getOldPassword() {
        return showPasswords.isSelected() ? oldPassVisible.getText() : oldPass.getText();
    }

    public String getNewPassword() {
        return showPasswords.isSelected() ? newPassVisible.getText() : newPass.getText();
    }

    public String getConfirmPassword() {
        return showPasswords.isSelected() ? confirmPassVisible.getText() : confirmPass.getText();
    }

    public void setStatus(String msg) {
        statusLabel.setText(msg == null ? "" : msg);
    }

    public Button getBackButton() { return backBtn; }
    public Button getSaveButton() { return saveBtn; }
}
