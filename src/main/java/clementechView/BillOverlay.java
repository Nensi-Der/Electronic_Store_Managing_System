package clementechView;

import clementechModel.Bill;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public final class BillOverlay {

    private BillOverlay() {}

    public static void show(Stage owner, Bill bill) {
        Stage dialog = new Stage(StageStyle.DECORATED);
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Bill " + bill.getBillNumber());

        Label title = new Label("Bill " + bill.getBillNumber());
        BaseStyles.styleSubtitle(title);

        HBox top = new HBox(10, title, new Region());
        HBox.setHgrow(top.getChildren().get(1), Priority.ALWAYS);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(10));

        TextArea area = new TextArea(bill.getBillInfo());
        area.setEditable(false);
        area.setWrapText(true);
        area.setFocusTraversable(false);
        area.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13;");

        VBox wrapper = new VBox(10, top, new Separator(), area);
        wrapper.setPadding(new Insets(10));

        wrapper.setBackground(new Background(new BackgroundFill(BaseStyles.SURFACE, new CornerRadii(BaseStyles.RADIUS), Insets.EMPTY)));
        wrapper.setBorder(new Border(new BorderStroke(BaseStyles.BORDER, BorderStrokeStyle.SOLID,
                new CornerRadii(BaseStyles.RADIUS), new BorderWidths(1))));

        dialog.setScene(new Scene(wrapper, 720, 520));
        dialog.show();
    }
}
