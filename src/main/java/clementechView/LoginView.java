package clementechView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class LoginView {

    private final StackPane root = new StackPane();
    private final ImageView logoView = new ImageView();

    private final TextField username=new TextField();
    private final PasswordField password = new PasswordField();
    private final TextField passwordVisible = new TextField();

    private final CheckBox showPassword = new CheckBox("Show password");

    private final StackPane usernameWrap = wrapWithHoverGlow(username);
    private final StackPane passwordWrap = wrapWithHoverGlow(password);
    private final StackPane passwordVisibleWrap = wrapWithHoverGlow(passwordVisible);

    private Button loginBt=new Button("Login");

    public LoginView(){
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F98E02;"); // ORNGE BACKGROUND

        Font font = Font.font("Bahnschrift", 16);

        Image logo = new Image(getClass().getResourceAsStream("/logo/clementech.png"));
        logoView.setImage(logo);
        logoView.setPreserveRatio(true);
        logoView.setFitHeight(140);

        username.setPromptText("username");//Ai teksti qe zhduket kur shkruan
        username.setFont(font);
        username.setMaxWidth(360);
        styleField(username);

        password.setPromptText("password");//Ai teksti qe zhduket kur shkruan
        password.setFont(font);
        password.setMaxWidth(360);
        styleField(password);

        //field per passwordin visible pasi shtyp checkboxin
        passwordVisible.setPromptText("password");
        passwordVisible.setFont(font);
        passwordVisible.setMaxWidth(360);
        styleField(passwordVisible);

        passwordVisible.textProperty().bindBidirectional(password.textProperty());
        showPassword.setFont(font);

        passwordVisibleWrap.setVisible(false);
        passwordVisibleWrap.setManaged(false);

        showPassword.setOnAction(e -> {
            boolean show = showPassword.isSelected();

            passwordWrap.setVisible(!show);
            passwordWrap.setManaged(!show);

            passwordVisibleWrap.setVisible(show);
            passwordVisibleWrap.setManaged(show);
        });


        loginBt.setFont(Font.font("Bahnschrift", 18));
        loginBt.setMaxWidth(360);
        loginBt.setPrefHeight(48);
        loginBt.setMinHeight(48);
        loginBt.setMaxHeight(48);
        loginBt.setStyle(""" 
        -fx-background-color: #51b403;
        -fx-text-fill: #FFFFFF;
        -fx-background-radius: 24;
        -fx-font-weight: bold;
        -fx-font-family: "Bahnschrift";
        -fx-font-size: 19px;
        
        -fx-border-color: #1c5901;
        -fx-border-width: 2;
        -fx-border-radius: 24;
        """);

        loginBt.setOnMouseEntered(e -> loginBt.setStyle("""
        -fx-background-color: #FFFFFF;
        -fx-text-fill: #51b403;
        -fx-background-radius: 24;
        -fx-font-weight: bold;
        -fx-font-family: "Bahnschrift";
        -fx-font-size: 21px;
        
        -fx-border-color: #1c5901;
        -fx-border-width: 2;
        -fx-border-radius: 24;
        """));

        loginBt.setOnMouseExited(e -> loginBt.setStyle("""
        -fx-background-color: #51b403;
        -fx-text-fill: #FFFFFF;
        -fx-background-radius: 24;
        -fx-font-weight: bold;
        -fx-font-family: "Bahnschrift";
        -fx-font-size: 19px;
        
        -fx-border-color: #1c5901;
        -fx-border-width: 2;
        -fx-border-radius: 24;
        """));


        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        box.getChildren().addAll(logoView, usernameWrap, passwordWrap, passwordVisibleWrap, showPassword, loginBt);

        root.getChildren().add(box);
    }

    private StackPane wrapWithHoverGlow(Control field) {
        StackPane wrap = new StackPane(field);
        wrap.setPadding(new Insets(2));
        wrap.setMaxWidth(499);

        DropShadow glow = new DropShadow();
        glow.setRadius(18);
        glow.setSpread(0.25);
        glow.setColor(Color.WHITE);

        wrap.setOnMouseEntered(e -> wrap.setEffect(glow));
        wrap.setOnMouseExited(e -> wrap.setEffect(null));

        return wrap;
    }

    private static void styleField(TextField f) {
        f.setPrefHeight(48);
        f.setFont(Font.font(16));
        f.setStyle("""
                -fx-background-color: rgba(255,255,255,0.50);
                -fx-background-radius: 24;
                -fx-background-insets: 0;

                -fx-border-color: rgba(255,255,255,1.0);
                -fx-border-width: 2;
                -fx-border-radius: 24;
                -fx-border-insets: 0;

                -fx-text-fill: #000000;
                -fx-prompt-text-fill: rgba(0,0,0,0.50);
                
                -fx-focus-color: transparent;
                -fx-faint-focus-color: transparent;

                -fx-padding: 10 14 10 14;
                """);
    }

    public StackPane getRoot() {
        return root;
    }

    public TextField getUsernameField() { return username; }
    public String getPassword() { return showPassword.isSelected() ? passwordVisible.getText() : password.getText(); }
    public Button getLoginButton() { return loginBt; }
}
