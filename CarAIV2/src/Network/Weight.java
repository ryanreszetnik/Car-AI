package Network;

public class Weight {
	private double weight;
	public static final double learningRate = 0.1;
	
	public Weight(){
		weight = Math.random()*2-1;
	}
	
	public void setWeight(double weight){
		this.weight=weight;
	}
	public void adjustWeight(double change){
		this.weight-=change*learningRate;
	}
	public double getWeight(){
		return weight;
	}
	
}
