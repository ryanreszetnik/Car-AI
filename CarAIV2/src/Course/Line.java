package Course;

import javafx.scene.shape.*;

public class Line extends javafx.scene.shape.Line {

	public Line(double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
	}
	public void updateEnd(double x, double y){
		setEndX(x);
		setEndY(y);
	}
	public void updateStart(double x, double y){
		setStartX(x);
		setStartY(y);
	}

	public Point intersects(Line l){
		Point p = intersectionPoint(l);
		if(p==null) return null;
		if(pointOnLine(p,l)&&pointOnLine(p,this)){
			return p;
		}
		return null;
	}
	
	public boolean pointOnLine(Point p, Line l){
		if(p.getX()>=Math.min(l.getStartX(), l.getEndX())){
			if(p.getY()>=Math.min(l.getStartY(), l.getEndY())){
				if(p.getX()<=Math.max(l.getStartX(), l.getEndX())){
					if(p.getY()<=Math.max(l.getStartY(), l.getEndY())){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public Point intersectionPoint(Line l) {
		// Line AB represented as a1x + b1y = c1
		double a1 = this.getEndY() - this.getStartY();
		double b1 = this.getStartX() - this.getEndX();
		double c1 = a1 * (this.getStartX()) + b1 * (this.getStartY());

		// Line CD represented as a2x + b2y = c2
		double a2 = l.getEndY() - l.getStartY();
		double b2 = l.getStartX() - l.getEndX();
		double c2 = a2 * (l.getStartX()) + b2 * (l.getStartY());

		double determinant = a1 * b2 - a2 * b1;

		if (determinant == 0) {
			// The lines are parallel. This is simplified
			// by returning a pair of FLT_MAX
			return null;
		} else {
			double x = (b2 * c1 - b1 * c2) / determinant;
			double y = (a1 * c2 - a2 * c1) / determinant;
			return new Point(x, y);
		}
	}

}
