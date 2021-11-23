package application;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Wall {
	Rectangle rect;
	double bearing;

	public Wall(double width, double length, double x, double y, double bearing) {
		rect = new Rectangle(width,length);
		rect.rotateProperty().set(bearing);
		this.bearing = bearing;
		rect.setTranslateX(x);
		rect.setTranslateY(y);
		Main.root.getChildren().add(rect);
	}
	public Wall(String data) {
		String values[] = data.split(":");
		rect = new Rectangle(Double.valueOf(values[0]),Double.valueOf(values[1]));
		rect.rotateProperty().set(Double.valueOf(values[4]));
		this.bearing = Double.valueOf(values[4]);
		rect.setTranslateX(Double.valueOf(values[2]));
		rect.setTranslateY(Double.valueOf(values[3]));
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
		rect.setWidth(rect.getWidth()+val);
	}
	public void stretchh(double val){
		rect.setHeight(rect.getHeight()+val);
	}
	public boolean contain(double x1, double y1) {

		double cx = rect.getTranslateX() + rect.getWidth() / 2;
		double cy = rect.getTranslateY() + rect.getHeight() / 2;
		double dx = x1 - cx;
		double a = Math.sqrt(Math.pow(cx - x1, 2) + Math.pow(cy - y1, 2));
		double angle = Math.toRadians(bearing);
		double angle2;
		if (cy > y1) {
			angle2 = Math.acos((dx / a));
		} else {
			angle2 = Math.toRadians(360) - Math.acos((dx / a));
		}

		double anglef = Math.toRadians(bearing + Math.toDegrees(angle2));
		double yf =  cy-a * Math.sin(anglef);
		double xf = cx-a * Math.cos(anglef) ;
		double rectx = rect.getTranslateX();
		double recty = rect.getTranslateY();
		double sx = rect.getWidth();
		double sy = rect.getHeight();
		if(xf>=rectx && xf<=rectx+sx && yf>=recty && yf <= recty+sy){
//			offsetx=xf-rectx;
//			offsety=yf-recty;
			return true;
		}
		
		

		return false;
	}
	public double getWidth(){
		return rect.getWidth();
	}
	public String toString(){
		return rect.getWidth() + "\n" + rect.getHeight() + "\n"+ rect.getTranslateX() + "\n" + rect.getTranslateY() + "\n" + bearing;
	}
}
