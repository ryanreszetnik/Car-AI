package application;

public class Point {
	float x;
	float y;
	public Point(float x, float y){
		this.x=x;
		this.y=y;
	}
	public String toString(){
		return "("+ x+", " + y +")";
	}
	public static float distance(Point a, Point b){
		return (float) Math.sqrt(Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y,2));
	}
	public static Point midPoint(Point a, Point b){
		return new Point((a.x+b.x)/2,(a.y+b.y)/2);
	}

}
