package clem.iloveclementines;

import clementechController.SceneChange;
import clementechView.Animation;
import clementechView.BaseStyles;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private SceneChange nav;

    @Override
    public void start(Stage stage) {
        BaseStyles.loadAppFonts();

        nav = new SceneChange(stage);

        Animation animation = new Animation("/animation/FINAL LOGO omg.mp4");
        Scene scene = new Scene(animation, 900, 600);
        scene.setFill(Color.web("#F98E02"));

        stage.setTitle("Clementech");
        stage.setScene(scene);
        stage.show();

        animation.play(() -> {
            nav.showLogin();
            animation.dispose();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
