package application;

import java.util.ArrayList;
import java.util.Arrays;

public class Network2 {
	public Node inputs[]= new Node[6];
	private Node hidden1[];
	private Node hidden2[];
	public Node accel;
	public Node steer;
	public double acceleration;
	public double steering;
	public double probability;

	public static double[][][] genWeights(int sizes[]){
		
		double output[][][] = new double[sizes.length][][];
		
		//ArrayList<ArrayList<ArrayList<Double>>> output= new ArrayList<>();
		for(int i= 1; i < output.length; i++){
			//ArrayList<ArrayList<Double>> output1= new ArrayList<>();
			double output1[][] = new double[sizes[i]][];
			for(int p = 0; p<output1.length; p++){
				//ArrayList<Double> output2= new ArrayList<>();
				double output2[] = new double[sizes[i-1]+1];
				for(int q = 0; q< output2.length; q++){
					double temp = Math.random()*2-1;
					output2[q] = temp;
				}
				output1[p]=output2; 
				//System.out.println(Arrays.toString(output2));
			}
			output[i]=output1;
		//	System.out.println(Arrays.toString(output1));
		}
		//System.out.println(Arrays.toString(output));
		return output;
	}
	
	
	public Network2(double[][][] weights) {
		
		
		int h1=weights[1].length;
		int h2=weights[2].length;
		hidden1 = new Node[h1];
		hidden2 = new Node[h2];
		
		for(int i = 0; i < 6; i++){
			inputs[i]=new Node(0);
		}
		
		for(int i = 0 ;i< h1; i++){
			hidden1[i] = new Node(6);
			hidden1[i].setBiasWeight(weights[1][i][0]);
			for(int p = 0; p < inputs.length; p++){
				hidden1[i].addInput(inputs[p], weights[1][i][p+1],p);
			}
		}
		for(int i = 0 ;i< h2; i++){
			hidden2[i] = new Node(5);
			hidden1[i].setBiasWeight(weights[2][i][0]);
			for(int p = 0; p < hidden1.length; p++){
				hidden2[i].addInput(hidden1[p], weights[2][i][p+1],p);
			}
		}
		accel=new Node(4);
		accel.setBiasWeight(weights[3][0][0]);
		steer = new Node(4);
		steer.setBiasWeight(weights[3][1][0]);
		for(int i = 0; i < hidden2.length; i++){
			accel.addInput(hidden2[i], weights[3][0][i+1],i);
			steer.addInput(hidden2[i], weights[3][1][i+1],i);
		}
	}
	public static double[][][] merge (double[][][] a,double[][][] b){
		double[][][] output = new double[a.length][][];
		for(int i= 0; i < output.length; i++){
			//System.out.println(a[i].length);
			double output1[][]= new double[a[i].length][];
			for(int p = 0; p<output1.length; p++){
			//	System.out.println(i + " " +p );
				double output2[]= new double[a[i][p].length];
				for(int q = 0; q< output2.length; q++){
					double a1 = a[i][p][q];
					double b1 = b[i][p][q];
					if(Math.random()>0.5){
						output2[q]=a1;
					}else{
						output2[q]=b1;
					}
				}
				output1[p]=output2;
			}
			output[i]=output1;
		}
		return output;
	}

	public void run(double L, double FL, double F, double FR, double R, double speed) {
		inputs[0].output=L;
		inputs[1].output=FL;
		inputs[2].output=F;
		inputs[3].output=FR;
		inputs[4].output=R;
		inputs[5].output=speed;
		for(int i = 0; i < hidden1.length; i++){
	//		System.out.println("hidden:" + i);
			hidden1[i].run();
		}
		for(int i = 0; i < hidden2.length; i++){
			hidden2[i].run();
		}
		accel.run();
		steer.run();
		acceleration=accel.output;
		steering=steer.output;
	}
	public double[][][] getWeights (){
		double [][][] output = new double[4][][];
		output[0] = new double[6][];
		output[1] = new double[5][];
		output[2] = new double[4][];
		output[3] = new double[2][];
		
		for(int i = 0; i < inputs.length; i++){	
			output[0][i] = new double[inputs[i].inputs.length];
			double weightsstored[] = new double[inputs[i].inputs.length];
			for(int p = 0; p < weightsstored.length; p++){
				weightsstored[p] = 0;
			}
			output[0][i] = weightsstored;
		}
		
		for(int i= 0; i < hidden1.length; i++){
			double output1[] = new double[hidden1[i].weights.length+1];
			output1= hidden1[i].getWeights();
		
			output[1][i] = output1;
		}
		
		for(int i= 0; i < hidden2.length; i++){
			double output1[] = new double[hidden2[i].inputs.length+1];
			output1=hidden2[i].getWeights();
			output[2][i] = output1;
			
		}
		output[3][0] = new double[accel.weights.length];
		output[3][1] = new double[accel.weights.length];
		output[3][0] = accel.getWeights();
		output[3][1] = steer.getWeights();
		
		
		return output;
		
	}
	public static void printArray(double[][][] bad){
		//for(int )
	}

}
