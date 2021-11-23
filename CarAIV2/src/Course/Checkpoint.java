package Course;

public class Checkpoint extends Line{

	public Checkpoint(double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
		this.setStyle("-fx-stroke: green;");
		// TODO Auto-generated constructor stub
	}
	public String toString(){
		return "C "+getStartX()+" "+getStartY()+" "+getEndX()+" "+getEndY();
	}
	public double[] getMidpoint(){
		double[] output = new double[2];
		output[0] = (getStartX()+getEndX())/2;
		output[1] = (getStartY()+getEndY())/2;
		return output;
	}

}
