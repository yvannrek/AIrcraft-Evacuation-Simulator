package model;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

public class AircraftEvacSimButton extends Button {

	public AircraftEvacSimButton(String text) {
		setText(text);
		setButtonFont();
		setPrefWidth(150);
		setPrefHeight(40);

		initializeButtonListeners();

	}

	private void setButtonFont() {
		setFont(Font.font("Calibri", 17));
	}

	private void setButtonPressedStyle() {
		setPrefHeight(39);
		setLayoutY(getLayoutY() + 2);
	}

	private void setButtonReleasedStyle() {
		setPrefHeight(39);
		setLayoutY(getLayoutY() - 2);
	}

	private void initializeButtonListeners() {
		setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					setButtonPressedStyle();
				}
			}
		});

		setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					setButtonReleasedStyle();
				}
			}
		});

		setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				setEffect(new DropShadow());
			}
		});

		setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				setEffect(null);
			}
		});

	}

}
