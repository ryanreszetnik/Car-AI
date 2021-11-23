package Course;

import java.util.ArrayList;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class CarRay extends Line{

	public Circle intersect = new Circle(3);
	public double offsetAngle=0;
	public double length;
	
	public CarRay(double x1, double y1, double angle, double length, double carAngle, Pane root) {
		super(x1, y1,x1+Math.cos(angle+carAngle)*length,y1+Math.sin(angle+carAngle)*length);
		this.length=length;
		this.offsetAngle=angle;
		root.getChildren().add(intersect);
	}
	public void updatePosition(double x, double y, double angle){
		updateStart(x,y);
		updateEnd(x+Math.cos(angle+offsetAngle)*length,y+Math.sin(angle+offsetAngle)*length);
	}
	public double getValue(ArrayList<Wall> walls){
		double minDis = getDistance(getStartX(),getStartY(),getEndX(),getEndY());
		intersect.setTranslateX(getEndX());
		intersect.setTranslateY(getEndY());
		for(Wall w: walls){
			Point p =w.intersects(this);
			if(p!=null){
				double dis= getDistance(p.getX(),p.getY(),getStartX(),getStartY());
				if(dis<minDis){
					minDis= dis;
					intersect.setTranslateX(p.getX());
					intersect.setTranslateY(p.getY());
				}
			}
		}
		return minDis;
	}
	public double getDistance(double x, double y, double x2, double y2){
		return Math.sqrt(Math.pow(x2-x, 2)+Math.pow(y2-y, 2));
	}
	
	

}
