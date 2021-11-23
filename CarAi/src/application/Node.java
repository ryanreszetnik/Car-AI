package application;

import java.util.ArrayList;

public class Node {
	public static double bias = 1;
	public double biasweight;
	public double output;

	
	Node inputs[];
	double weights[];
	public double[] getWeights(){
		double[] output = new double[weights.length+1];
		output[0] = biasweight;
		for(int i = 1; i < output.length; i++){
			output[i] = weights[i-1];
		}
		return output;
	}
	public Node(int inputsize){
		inputs= new Node[inputsize];
		weights= new double[inputsize];
	}
	public void addInput(Node input, double weight, int place){
		inputs[place]=input;
		weights[place]=weight;
		
	}
	public void setBiasWeight(double b){
		biasweight=b;
	}
	public void run(){
		output = ActivationFunction(Sum());
	}
	public static double ActivationFunction(double input){
		return 1.0/(1.0+Math.pow(Math.E,-input));
	}
	
	public double Sum(){
		double total=0;
		for(int i = 0; i < inputs.length; i++){
	//		System.out.println(i+ "weight: "+weights[i] );
	//		System.out.println(i+ "output: "+ inputs[i]);
			total+= inputs[i].output*weights[i];
		}
		total+=bias*biasweight;
		return total;
	}
	

}
