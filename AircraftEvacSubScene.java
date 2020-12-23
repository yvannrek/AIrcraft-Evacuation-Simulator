package model;

import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.SubScene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AircraftEvacSubScene extends SubScene {

	private boolean isHidden;

	public AircraftEvacSubScene() {
		super(new AnchorPane(), 550, 420);
		prefWidth(620);
		prefHeight(400);

		setLayoutX(30);
		setLayoutY(170);

		isHidden = true;

		AnchorPane root2 = (AnchorPane) this.getRoot();

		root2.setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, new CornerRadii(20), Insets.EMPTY)));

	}

	public void moveSubScene() {
		TranslateTransition transition = new TranslateTransition();
		transition.setDuration(Duration.seconds(0.5));
		transition.setNode(this);

		if (isHidden) {
			transition.setToX(876);
			isHidden = false;
		} else {
			transition.setToX(0);
			isHidden = true;
		}

		transition.play();
	}

	public AnchorPane getPane() {
		return (AnchorPane) this.getRoot();

	}
}
