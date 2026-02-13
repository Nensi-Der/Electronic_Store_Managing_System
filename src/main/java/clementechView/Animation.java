package clementechView;

import javafx.scene.shape.Rectangle;
import javafx.animation.FadeTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import java.util.Objects;

public class Animation extends StackPane {

    private final MediaPlayer player;
    private final MediaView mediaView;


    public Animation(String resourcePath) {
        setStyle("-fx-background-color: #FFFFFF;"); // background behind video

        String url = Objects.requireNonNull(
                getClass().getResource(resourcePath),
                "Video not found: " + resourcePath + " (put it in resources)"
        ).toExternalForm();

        Media media = new Media(url);
        player = new MediaPlayer(media);

        mediaView = new MediaView(player);
        mediaView.setPreserveRatio(true);

        //video scales with the window
        mediaView.fitWidthProperty().bind(widthProperty());
        mediaView.fitHeightProperty().bind(heightProperty());

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);

        getChildren().add(mediaView);
        player.setOnReady(this::updateViewportCrop);
        widthProperty().addListener((obs, o, n) -> updateViewportCrop());
        heightProperty().addListener((obs, o, n) -> updateViewportCrop());

        // Start invisible (for fade in)
        setOpacity(0.0);
    }

    private void updateViewportCrop() {
        double viewW = getWidth();
        double viewH = getHeight();
        if (viewW <= 0 || viewH <= 0) return;

        double mediaW = player.getMedia().getWidth();
        double mediaH = player.getMedia().getHeight();
        if (mediaW <= 0 || mediaH <= 0) return;

        double viewRatio = viewW / viewH;
        double mediaRatio = mediaW / mediaH;

        double cropW = mediaW;
        double cropH = mediaH;

        if (viewRatio > mediaRatio) {
            // window wider crop top bottom
            cropH = mediaW / viewRatio;
        } else {
            // window taller crop left right
            cropW = mediaH * viewRatio;
        }

        double x = (mediaW - cropW) / 2.0;
        double y = (mediaH - cropH) / 2.0;

        mediaView.setViewport(new Rectangle2D(x, y, cropW, cropH));
    }

    //fade in, play, fade out, run onFinished
    public void play(Runnable onFinished) {

        FadeTransition fadeIn = new FadeTransition(Duration.millis(450), this);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.setOnFinished(e -> player.play());

        player.setOnEndOfMedia(() -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(450), this);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(ev -> {
                player.stop();
                if (onFinished != null) onFinished.run();
            });
            fadeOut.play();
        });

        //if media errors, just continue to app
        player.setOnError(() -> {
            player.stop();
            if (onFinished != null) onFinished.run();
        });

        fadeIn.play();
    }

    public void dispose() {
        player.dispose();
    }
}
