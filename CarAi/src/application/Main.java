package application;
	
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.*;


public class Main extends Application {
	public static Pane root;
	public static Scene scene;
	@Override
	public void start(Stage primaryStage) {
		try {
			System.out.println("Start");
			
			root = new Pane();
			scene = new Scene(root,1400,900);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Course.readFile();
			Course.createCourse();
			AnimationTimer timer = new AnimationTimer(){
				@Override
				public void handle(long now) {
					// TODO Auto-generated method stub
					for(int i = 0; i < Course.cars.size(); i++){
						Course.cars.get(i).move();
					}
					for(int i = 0; i < Everyone.cars.size(); i++){
						Everyone.cars.get(i).move();
					}
					int alivecount = 0;
					for (int i = 0; i < Everyone.cars.size(); i++) {
						if (Everyone.cars.get(i).alive) {
							alivecount++;
						}
					}
					if(alivecount ==0&& Everyone.cars.size()>0){
						Everyone.runRound();
					}
					Everyone.round();
				}
				
			};
			timer.start();
			
			
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
