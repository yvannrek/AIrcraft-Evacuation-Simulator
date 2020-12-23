package application;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import view.ViewManager;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		ViewManager manager = new ViewManager();
		primaryStage = manager.getMainStage();
		primaryStage.getIcons().add(new Image("view/resources/Icon.png"));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}