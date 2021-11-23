package application;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Wall {
	int width;
	int length;
	Rectangle rect;
	double bearing;

	public Wall(int width, int length, double x, double y, double bearing) {
		rect = new Rectangle(width,length);
		
		rect.rotateProperty().set(bearing);
		this.bearing = bearing;
		rect.setTranslateX(x);
		rect.setTranslateY(y);
		this.width = width;
		this.length = length;
		
		
		Main.root.getChildren().add(rect);
	}
	public void move(double x, double y){
		rect.setTranslateX(x);
		rect.setTranslateY(y);
	}
	public void rotate(double val){
		bearing+=val;
		rect.rotateProperty().set(bearing);
		
	}
	public void stretchw(double val){
		width+=val;
		rect.setWidth(width);
	}
	public void stretchh(double val){
		length+=val;
		rect.setHeight(val);
	}
	public String toString(){
		return width + "\n" + length + "\n"+ rect.getTranslateX() + "\n" + rect.getTranslateY() + "\n" + bearing;
	}
}
