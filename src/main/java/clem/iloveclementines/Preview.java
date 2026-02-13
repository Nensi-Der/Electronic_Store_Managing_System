package clem.iloveclementines;

import clementechView.AdminView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Preview extends Application {
    @Override
    public void start(Stage stage) {
        AdminView view = new AdminView(
                "Lara Jane (Admin)",
                "/logo/clementech.png",
                "/avatar/admin.png"
        );

        stage.setTitle("AdminView Preview");
        stage.setScene(new Scene(view.getRoot(), 1100, 700));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
