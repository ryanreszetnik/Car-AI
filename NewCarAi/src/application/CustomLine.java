package application;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class CustomLine {

	Point start;
	Point end;
	private float slope;
	private boolean vert;
	Line shown;
	public static Pane root;

	public void selfDestruct(){
		root.getChildren().remove(shown);
	}
	public CustomLine(float x1, float y1, float x2, float y2) {
		start = new Point(x1, y1);
		end = new Point(x2, y2);
		shown = new Line(x1, y1, x2, y2);
		root.getChildren().add(shown);
	}
	public void moveBy(float x, float y){
		updateStartX(start.x+=x);
		updateStartY(start.y+=y);
		updateEndX(end.x+=x);
		updateEndY(end.y+=y);
	}
	public Point midPoint(){
		return Point.midPoint(start, end);
	}

	public void updateEndX(float x) {
		end.x = x;
		shown.setEndX(x);
	}

	public void updateEndY(float y) {
		end.y = y;
		shown.setEndY(y);
	}

	public void updateStartX(float x) {
		start.x = x;
		shown.setStartX(x);
	}

	public void updateStartY(float y) {
		start.y = y;
		shown.setStartY(y);
	}

	public void updateStartXY(float x, float y) {
		updateStartY(y);
		updateStartX(x);
	}

	public void updateEndXY(float x, float y) {
		updateEndY(y);
		updateEndX(x);
	}
	public void update(float x1, float y1, float x2, float y2){
		updateStartXY(x1,y1);
		updateEndXY(x2,y2);
	}

	public boolean calcSlope() {
		if (!isVert()) {
			this.slope = getSlope();
			return true;
		}
		return false;
	}

	public boolean isVert() {
		vert = start.x == end.x;
		return vert;
	}

	private float getSlope() {
		return (end.y - start.y) / (end.x - start.x);
	}

	public float getYInt() {
		return -start.x * slope + start.y;
	}

	public static Point getIntersection(CustomLine l1, CustomLine l2) {
		// check for vertical lines

		// convert each equation into form y=mx + b
		float b1 = 0;
		float b2 = 0;
		if (l1.calcSlope()) {// a is not vertical
			b1 = l1.getYInt();
		}
		if (l2.calcSlope()) {// a is not vertical
			b2 = l2.getYInt();
		}
		if (!l1.vert && !l2.vert) {
			float xIntercept = (b2 - b1) / (l1.slope - l2.slope);
			if (l1.containsXVal(xIntercept) && l2.containsXVal(xIntercept)) {
				float yIntercept = l1.slope * xIntercept + b1;
				return new Point(xIntercept, yIntercept);
			}
			return null;
		}
		if (l1.vert && !l2.vert) {// l1 vertical
			if (l2.containsXVal(l1.start.x)) {
				float yInt = l2.slope * l1.start.x + b2;
				if (l1.containsYVal(yInt)) {
					return new Point(l1.start.x, yInt);
				}
			}

		} else if (!l1.vert && l2.vert) {// l2 vertical
			if (l1.containsXVal(l2.start.x)) {
				float yInt = l1.slope * l2.start.x + b1;
				if (l2.containsYVal(yInt)) {
					return new Point(l2.start.x, yInt);
				}
			}

		} else {// both vertical
				// check y ranges
			if (l1.start.x == l2.start.x) {
				if (l1.start.y > l2.start.y && l2.start.y > l2.end.y) {// l1
																		// start
																		// below
																		// (Greater
																		// y
																		// val)
					if (l1.containsYVal(l2.start.y) && l1.containsYVal(l2.end.y)) {
						return new Point(l1.start.x, Math.max(l2.start.y, l2.end.y));
					} else if (l1.end.y < l2.start.y) {
						return new Point(l1.start.x, l2.start.y);
					} else if (l1.end.y < l2.end.y) {
						return new Point(l1.start.x, l2.end.y);
					}

				} else if (l1.start.y < l2.start.y && l2.start.y < l2.end.y) {// l1
																				// start
																				// above
																				// (smaller
																				// y
																				// val)
					if (l1.containsYVal(l2.start.y) && l1.containsYVal(l2.end.y)) {
						return new Point(l1.start.x, Math.min(l2.start.y, l2.end.y));
					} else if (l1.end.y > l2.start.y) {
						return new Point(l1.start.x, l2.start.y);
					} else if (l1.end.y > l2.end.y) {
						return new Point(l1.start.x, l2.end.y);
					}
				} else if (l2.containsYVal(l1.start.y)) {// l1 start in between
					return new Point(l1.start.x, l1.start.y);
				}
			}
		}
		// find intersection

		return null;
	}

	public boolean containsXVal(float x) {
		return (start.x < x && end.x > x || start.x > x && end.x < x);
	}

	public boolean containsYVal(float y) {
		return (start.y < y && end.y > y || start.y > y && end.y < y);
	}

	public String toString() {
		return start.toString() + end.toString();
	}

}
