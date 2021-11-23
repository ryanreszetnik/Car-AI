package Course;

public class Wall extends Line{

	public Wall(double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
	}
	public String toString(){
		return "W "+getStartX()+" "+getStartY()+" "+getEndX()+" "+getEndY();
	}

}
