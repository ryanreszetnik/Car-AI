package application;

import java.util.ArrayList;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Car {

	CustomLine front = new CustomLine(100, 100, 120, 120);
	CustomLine back = new CustomLine(100, 100, 120, 120);
	CustomLine left = new CustomLine(100, 100, 120, 120);
	CustomLine right = new CustomLine(100, 100, 120, 120);
	public static Point startingPoint = new Point(100, 100);

	Network net;

	public static Pane root;

	private float size = 20;
	private float lineRotate = 10;
	public int topScore;
	
	public float acceleration = 0;

	public float carHeading = 0;
	public float carSpeed = 0;
	public float steerAngle = 0;
	// private float wheelBase = 30;

	private float yvel = 1;
	private float xvel = 1;
	private float xpos = 100;
	private float ypos = 100;
	Radar rad = new Radar(xpos, ypos, 0);

	public boolean isAlive = true;

	public Car() {
		// root.getChildren().add(front.shown);
		rotate(0);
	}

	public void rotate(float deg) {

		carHeading += deg;
		float rad = (float) Math.toRadians(carHeading);
		front.updateStartXY(xpos + (float) Math.cos(rad - lineRotate) * size,
				ypos + (float) Math.sin(rad - lineRotate) * size);
		front.updateEndXY(xpos + (float) Math.cos(rad + lineRotate) * size,
				ypos + (float) Math.sin(rad + lineRotate) * size);
		back.updateStartXY(xpos - (float) Math.cos(rad - lineRotate) * size,
				ypos - (float) Math.sin(rad - lineRotate) * size);
		back.updateEndXY(xpos - (float) Math.cos(rad + lineRotate) * size,
				ypos - (float) Math.sin(rad + lineRotate) * size);
		left.updateStartXY(xpos - (float) Math.cos(rad - lineRotate) * size,
				ypos - (float) Math.sin(rad - lineRotate) * size);
		left.updateEndXY(xpos + (float) Math.cos(rad + lineRotate) * size,
				ypos + (float) Math.sin(rad + lineRotate) * size);
		right.updateStartXY(xpos + (float) Math.cos(rad - lineRotate) * size,
				ypos + (float) Math.sin(rad - lineRotate) * size);
		right.updateEndXY(xpos - (float) Math.cos(rad + lineRotate) * size,
				ypos - (float) Math.sin(rad + lineRotate) * size);

	}

	public boolean dead(ArrayList<CustomLine> obsticles) {
		for (CustomLine line : obsticles) {
			if (CustomLine.getIntersection(line, front) != null || CustomLine.getIntersection(line, back) != null
					|| CustomLine.getIntersection(line, left) != null
					|| CustomLine.getIntersection(line, right) != null) {
				return true;
			}
		}
		return false;
	}

	public void move(ArrayList<CustomLine> obsticles) {

		Point backWheel = back.midPoint();
		Point frontWheel = front.midPoint();
		// System.out.println(backWheel +" " + frontWheel + " start " +
		// carHeading + " steer: " + steerAngle);
		backWheel.x += Math.cos(Math.toRadians(carHeading)) * carSpeed;
		backWheel.y += Math.sin(Math.toRadians(carHeading)) * carSpeed;
		frontWheel.x += Math.cos(Math.toRadians(carHeading + steerAngle)) * carSpeed;
		frontWheel.y += Math.sin(Math.toRadians(carHeading + steerAngle)) * carSpeed;

		Point newMiddle = Point.midPoint(frontWheel, backWheel);
		xvel = newMiddle.x - xpos;
		yvel = newMiddle.y - ypos;
		// System.out.println("Move " + xvel + " " + yvel);
		carHeading = (float) Math.toDegrees(Math.atan2(frontWheel.y - backWheel.y, frontWheel.x - backWheel.x)) - 180;
		// System.out.println(backWheel +" " + frontWheel + " " + carHeading);

		
		
		rotate(0);
		rad.rotate(xpos, ypos, carHeading, obsticles);

		carSpeed *= 0.95;

		carSpeed += acceleration;

		carSpeed = Math.min(6, carSpeed);
		carSpeed = Math.max(0, carSpeed);
		xpos += xvel;
		ypos += yvel;
		front.moveBy(xvel, yvel);
		back.moveBy(xvel, yvel);
		left.moveBy(xvel, yvel);
		right.moveBy(xvel, yvel);
		System.out.println(front.start + " rotated by" );
	}

	public void kill() {
		isAlive = false;
	}

	public void reset(ArrayList<CustomLine> obsticles) {
		isAlive = true;
		// topScore = 0;
		// birdView.setVisible(true);
		// hitBox.setTranslateY(screenHeight / 2);
		xpos = startingPoint.x;
		ypos = startingPoint.y;
		carHeading = 0;
		xvel = 0;
		yvel = 0;
		move(obsticles);
	}

	public void run(float[] inputs, ArrayList<CustomLine> obsticles) {
		net.run(inputs);
		this.carHeading = net.outputs.get(0).getOutput() * 50-25;
		this.acceleration = net.outputs.get(1).getOutput()*1.5f-0.5f;
		move(obsticles);
	}

}
