package Course;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.*;

public class Main extends Application {
	public static Pane root;
	public static Scene scene;
	public static Course c;
	

	@Override
	public void start(Stage primaryStage) {
		try {
			System.out.println("Start");

			root = new Pane();
			scene = new Scene(root, 1400, 900);
			// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			c = new Course(root, scene);
			c.handleEvents();
//			c.intesect();
			


			AnimationTimer timer = new AnimationTimer() {
				@Override
				public void handle(long now) {
					c.update();
				}

			};
			
			timer.start();
			

			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	public static void main(String[] args) {
		launch(args);
	}
}
