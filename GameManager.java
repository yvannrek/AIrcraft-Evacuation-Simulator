package view;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Sprite;

public class GameManager {
	final int WIDTH = 400;
	final int LENGTH = 800;

	BorderPane borderPane = new BorderPane();
	Scene gameScene = new Scene(borderPane, WIDTH, LENGTH);
	Stage gameStage = new Stage();
	Pane playPane = new Pane();
	Pane layerPane = new Pane();
	private HBox bottomHBox = new HBox();
	private Button timeLabel = new Button();
	private Button pauseBt = new Button(), menuBt = new Button();
	private AnimationTimer gameLoop;
	private boolean isPaused = false;

	private ArrayList<Sprite> passengerList = new ArrayList<Sprite>();
	private ArrayList<Sprite> exitList = new ArrayList<Sprite>();
	private int numOfPassengers;
	private float elapsedTime;
	private int ex1;
	private int ex2;
	private int ex3;
	private int ex4;
	private int ex5;
	private int ex6;
	private int aircraft;

	ImageView iv1 = new ImageView();
	Image crj200 = new Image("view/resources/CRJ200.jpg");
	Image erj175 = new Image("view/resources/ERJ175.jpg");

	private ViewManager viewMan;
	private int attractIndex = 0;
	private double pauseDuration = 3.0;
	Image image;

	GameManager() throws Exception {

		startStage(gameStage);

	}

	public void startStage(Stage primaryStage) throws Exception {
		gameStage.setTitle("Simulation");
		gameStage.setResizable(false);
		gameStage.setScene(gameScene);
		gameStage.getIcons().add(new Image("view/resources/Icon.png"));
		gameStage.show();

		// layerPane contains the playPane which is in the center borderPane
		layerPane.getChildren().add(playPane);
		layerPane.setStyle("-fx-background-color: #FFFFE0");
		borderPane.setCenter(layerPane);
		buttonSetup();

	}

	public void gameLoop() {

		// Displays for a few paused seconds so user can see configuration
		// display() method is called so we set the circles to visible. They are now in
		// correct position.
		passengerList.forEach(Sprite::display);
		passengerList.forEach(Sprite::setVisible);
		PauseTransition pause = new PauseTransition(Duration.seconds(pauseDuration));
		pause.setOnFinished(e -> gameLoop.start());
		pause.play();

		// Once pause is finished, starts the below gameLoop
		gameLoop = new AnimationTimer() {
			long start = System.currentTimeMillis();

			@Override
			public void handle(long now) {
				int exitCount = 0;

				// Loop through the passengerList and attract all passengers to exits
				for (Sprite sp1 : passengerList) {

					attractIndex = sp1.nearestExit(exitList);
					sp1.setExitLocation(exitList.get(attractIndex).getLocation());

//					System.out.println(sp1.getLocation());

					Point2D force = exitList.get(attractIndex).attract(sp1);
					sp1.seperate(passengerList);
					sp1.walls(exitList);
					sp1.applyForce(force);

				}

				// Timer for the simulation
				float end = ((System.currentTimeMillis() - start) / 1000f);
				end = (float) (end - pauseDuration);
				end = (float) (Math.round(end * 10) / 10.0);
				timeLabel.setText("Elapsed Time: " + Float.toString(end));
				elapsedTime = end;

				// Display and move the passengers
				passengerList.forEach(Sprite::display);
				passengerList.forEach(Sprite::move);
				for (int i = 0; i < passengerList.size(); i++) {
					if (passengerList.get(i).getAtExit() == true) {
						playPane.getChildren().remove(passengerList.get(i));
						exitCount++;
						if (exitCount == numOfPassengers) {
							this.stop();
							printResults();
						}
					}
				}
			}
		};
	}


	public void addPassengers(double[][] seats) {
		// Declaring Variables and clearing list to ensure its empty
		int i = 0, j = 0;
		double x, y;
		passengerList.clear();

		// Loop through the list and assign x seat and y seat coordinates
		for (i = 0; i < numOfPassengers; i++) {
			x = seats[i][0] + 200;
			Sprite passenger = new Sprite();
			for (j = 0; j < seats[i].length; j++) {
				y = seats[i][j] + 200;
				Point2D location = new Point2D(x, y);
				Point2D velocity = new Point2D(0, 0);
				Point2D acceleration = new Point2D(0, 0);
				passenger.parameters(location, velocity, acceleration, randomType(0, 2));

			}

			// Adds passengers to list, and sets them invisible since they are currently
			// positioned at 0,0 until the display() method is called.
			passengerList.add(passenger);
			passenger.setVisible(false);
			playPane.getChildren().add(passenger);

		}

	}

	// adds the user defined number of passengers
	public void initializePassenger(double[][] crjseats) {
		for (int i = 0; i < numOfPassengers; i++) {
			addPassengers(crjseats);
		}

	}

	// Adds exits, exits still need to be derived from coordinates.
	// Need more than one exit
	public void addExits(double x, double y) {

		Sprite exit = new Sprite();
		exit.setRadius(5);
		Point2D location = new Point2D(x, y);
		Point2D velocity = new Point2D(0, 0);
		Point2D acceleration = new Point2D(0, 0);
		exit.parameters(location, velocity, acceleration, 25);
		exit.display();
		exitList.add(exit);
		playPane.getChildren().add(exit);

	}
	
	public void printResults() {
		String databaseURL = "jdbc:ucanaccess://view/resources/Results.accdb";

		try {
			Connection connection = DriverManager.getConnection(databaseURL);

			System.out.println("Database Connection Successful.");

			String sql = "INSERT INTO Results (Aircraft, Exits, Passengers, Evac_Time, FAA_Compliant) VALUES (?, ?, ?, ?, ?)";

			PreparedStatement statement = connection.prepareStatement(sql);

			if (aircraft == 1) {
				statement.setString(1, "Bombardier CRJ-200");
				String operExits = Integer.toString(ex1) + ',' + Integer.toString(ex2) + ',' + Integer.toString(ex3)
						+ ',' + Integer.toString(ex4);
				statement.setString(2, operExits);
			} else if (aircraft == 2) {
				statement.setString(1, "Embraer ERJ-175");
				String operExits = Integer.toString(ex1) + ',' + Integer.toString(ex2) + ',' + Integer.toString(ex3)
						+ ',' + Integer.toString(ex4) + ',' + Integer.toString(ex5) + ',' + Integer.toString(ex6);
				statement.setString(2, operExits);
			}

			statement.setString(3, Integer.toString(numOfPassengers));
			statement.setString(4, Float.toString(elapsedTime));
			if (elapsedTime <= 90) {
				statement.setString(5, "Y");
			} else if (elapsedTime > 90) {
				statement.setString(5, "N");
			}

			int rows = statement.executeUpdate();

			if (rows > 0) {
				System.out.println("Results have been recorded to the database successfully.");
			}

			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void buttonSetup() {
		// Setting up HBox with Pause and Menu buttons
		pauseBt.setText("Pause Simulation");
		pauseBt.setOnAction(e -> pauseSimulation());

		menuBt.setText("Main Menu");
		menuBt.setOnAction(e -> switchMenu());

		timeLabel.setText("Elapsed Time: 0.0");

		bottomHBox.setAlignment(Pos.CENTER);
		bottomHBox.setPadding(new Insets(0, 10, 10, 0));
		HBox.setMargin(menuBt, new Insets(0, 10, 0, 0));
		HBox.setMargin(pauseBt, new Insets(0, 10, 0, 0));
		bottomHBox.setStyle("-fx-background-color: #FFFFE0");
		bottomHBox.getChildren().addAll(pauseBt, menuBt, timeLabel);
		borderPane.setBottom(bottomHBox);
	}

	public void switchMenu() {
		// System.exit(0);
		gameStage.close();
		gameLoop.stop();
		viewMan = new ViewManager();
		viewMan.startViewManager();

	}

	public void pauseSimulation() {
		if (isPaused == false) {
			gameLoop.stop();
			isPaused = true;
			System.out.println("Is Paused");
		} else {
			gameLoop.start();
			isPaused = false;
			System.out.println("Is NOT Paused");
		}
	}

	// Random Method to define the 'type' of passenger.
	public int randomType(int min, int max) {
		int num = (int) (Math.random() * (max - min + 1) + min);
		return num;
	}


//*******************Getters and Setters**********************************

	// Sets the number of passengers
	public void setNumOfPassengers(int num) {
		numOfPassengers = num;
	}

	// Sets operable exits for database purposes
	public void setEx1(int num) {
		ex1 = num;
	}

	public void setEx2(int num) {
		ex2 = num;
	}

	public void setEx3(int num) {
		ex3 = num;
	}

	public void setEx4(int num) {
		ex4 = num;
	}

	public void setEx5(int num) {
		ex5 = num;
	}

	public void setEx6(int num) {
		ex6 = num;
	}

	// Sets chosen aircraft for database purposes
	public void setAircraft(int num) {
		aircraft = num;
	}

}
