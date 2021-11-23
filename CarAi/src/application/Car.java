package application;

import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Car {
	public double Left;
	public double Right;
	public double Forward;
	public double FLeft;
	public double FRight;
	public double speed;
	public double acceleration;
	public double turning;
	public double bearing;
	public static double maxSpeed = 10;
	Line L = new Line();
	Line R = new Line();
	Line FL = new Line();
	Line FR = new Line();
	Line F = new Line();
	public static int Lang = -90;
	public static int Rang = 90;
	public static int FLang = -45;
	public static int FRang = 45;
	public static int Fang = 0;
	public double fitness;
	public boolean alive = true;
	private int stopcount = 0;
	Image carImage;
	ImageView carView;
	
	
	Network net;

	Rectangle car = new Rectangle(30, 20);

	public void selfDestruct(){
		Main.root.getChildren().remove(car);
		Main.root.getChildren().remove(carView);
	}
	
	public Car(int startingx, int startingy) {
		speed = 7;
		acceleration = 0;
		turning = 0;
		Image carImage = new Image(getClass().getResource("Car.png").toExternalForm());
	
		carView = new ImageView(carImage);
		Main.root.getChildren().addAll(carView);
//		Main.root.getChildren().addAll(L, R, FL, FR, F);
		car.setFill(Color.rgb(255, 0, 0));
		car.setTranslateX(startingx);
		car.setTranslateY(startingy);
		int[] size = {5, 5, 1 };
		net = new Network(Network.genWeights(size));
		carView.setTranslateX(startingx);
		carView.setTranslateY(startingy);
		
	}
	public Car(int startingx, int startingy, double[][][] weights) {
		speed = 7;
		acceleration = 0;
		turning = 0;
		Image carImage = new Image(getClass().getResource("Car.png").toExternalForm());
		carView = new ImageView(carImage);
		Main.root.getChildren().addAll(carView);
		//Main.root.getChildren().addAll(L, R, FL, FR, F);
		car.setFill(Color.rgb(255, 0, 0));
		car.setTranslateX(startingx);
		car.setTranslateY(startingy);
		net = new Network(weights);
		carView.setTranslateX(startingx);
		carView.setTranslateY(startingy);
	}
	

	public void move() {
		if (alive) {
			net.run(Left/200, FLeft/200, Forward/200, FRight/200, Right/200, maxSpeed);
			//acceleration = net.acceleration * 2 - 1;
			turning = (net.steering * 2 - 1)*35;
			/*
			if (acceleration == 0) {
				speed *= 0.99;
			} else {
				speed = Math.max(0, Math.min(acceleration + speed, maxSpeed));
			}
			if(speed==0){
				stopcount++;
			}
			if(stopcount>2){
				stopcount =0;
				alive=false;
			}*/
			bearing += turning;
			car.setRotate(bearing);
			car.setTranslateX(car.getTranslateX() + Math.cos(Math.toRadians(bearing)) * speed);
			car.setTranslateY(car.getTranslateY() + Math.sin(Math.toRadians(bearing)) * speed);
			carView.setTranslateX(car.getTranslateX());
			carView.setTranslateY(car.getTranslateY());
			carView.setRotate(bearing);

			double frontx = car.getTranslateX() + car.getWidth() / 2
					+ car.getWidth() / 2 * Math.cos(Math.toRadians(bearing));
			double fronty = car.getTranslateY() + car.getHeight() / 2
					+ car.getHeight() / 2 * Math.sin(Math.toRadians(bearing));

			dis(L, frontx, fronty, Lang);
			dis(R, frontx, fronty, Rang);
			dis(FL, frontx, fronty, FLang);
			dis(FR, frontx, fronty, FRang);
			dis(F, frontx, fronty, Fang);
			Left = size(L);
			Right = size(R);
			Forward = size(F);
			FLeft = size(FL);
			FRight = size(FR);
			fitness += (Left + Right + Forward + FLeft + FRight) / 1000;
			if (Left < 11 || Right < 11 || Forward < 5 || FLeft < 3 || FRight < 3) {
				alive = false;
			}
		}
	}

	public void dis(Line l, double sx, double sy, int ang) {
		l.setStartX(sx);
		l.setStartY(sy);
		double ex = sx;
		double ey = sy;
		for (int i = 0; i < 200; i++) {
			for (int p = 0; p < Course.walls.size(); p++) {
				if (Course.contain(Course.walls.get(p), ex, ey)) {
					l.setEndX(ex);
					l.setEndY(ey);
					return;
				}
			}
			ex += Math.cos(Math.toRadians(bearing + ang));
			ey += Math.sin(Math.toRadians(bearing + ang));
		}
		l.setEndX(ex);
		l.setEndY(ey);
	}

	public static int size(Line l) {
		return (int) Math
				.round(Math.sqrt(Math.pow(l.getStartX() - l.getEndX(), 2) + Math.pow(l.getStartY() - l.getEndY(), 2)));
	}

}
