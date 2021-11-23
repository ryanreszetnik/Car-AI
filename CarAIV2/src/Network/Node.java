package Network;
import java.util.ArrayList;
import java.util.HashMap;

public class Node {
	// Temporary
	int lnum;
	int num;
	public String toString(){
		String weight = "";
		for(Node n:weights.keySet()){
			weight += "["+n.num + " * "+ weights.get(n).getWeight()+"]";
		}
		return "Layer: "+lnum + " Node: "+ num + " Bias: "+biasWeight.getWeight() +" Weights:"+weight;
	}
	
	//======================Variables==========================================
	
	public double output;
	private double bias =1;
	private Weight biasWeight;
	private double total;
	
	private ArrayList<Node> inputs= new ArrayList<>();
	private HashMap<Node, Weight> weights = new HashMap<>();
	private ArrayList<Node> outputs= new ArrayList<>();
	
	public HashMap<Node, Double> dInputs = new HashMap<>();
	
	
	enum ActFunc{
		ReLU, Sigmoid, Linear
	}
	ActFunc activationFunction;
	//======================Initialize=========================================
	public Node(ActFunc func){
		activationFunction = func;
		biasWeight=new Weight();
	}
	public void addInput(Node n){
		inputs.add(n);
		weights.put(n, new Weight());
	}
	public void addOutput(Node n){
		outputs.add(n);
	}
	//=====================Run Time============================================
	public void setOutput(double out){
		this.output=out;
	}
	public void run(){
		total = 0;
		for(Node n: inputs){
			total+=n.output*weights.get(n).getWeight();
		}
		total+=bias*biasWeight.getWeight();
		if(activationFunction==Node.ActFunc.Sigmoid){
			output = sigmoid(total);
		}else if(activationFunction==Node.ActFunc.ReLU){
			output = reLU(total);
		} else if(activationFunction ==Node.ActFunc.Linear){
			output = linear(total);
		}
	}
	public void backProp(){
		double dOut = 0;
		for(Node n: outputs){
			dOut+=n.dInputs.get(this);
		}
		backPropGeneral(dOut);
	}
	public void backProp(double eOut, double learning){// for final layer
		backPropGeneral((output-eOut));
//		backPropGeneral(learning);
	}
	public void backPropGeneral(double dOut){
		double dTotal = 0;
		if(activationFunction == Node.ActFunc.Sigmoid){
			dTotal = dsigmoid(total)*dOut;
		}else if(activationFunction == Node.ActFunc.ReLU){
			dTotal = dreLU(total)*dOut;
		} else if(activationFunction == Node.ActFunc.Linear){
			dTotal = dlinear(total)*dOut;
		}
		for(Node n: inputs){
			dInputs.put(n, dTotal*weights.get(n).getWeight());
			weights.get(n).adjustWeight(dTotal*n.output);
		}
		biasWeight.adjustWeight(bias*dTotal);
	}
	//=====================Functions===========================================
	private double sigmoid(double in){
		return 1.0/(1.0+Math.exp(-in));
	}
	private double dsigmoid(double in){
		double sig =sigmoid(in);
		return sig*(1-sig);
	}
	private double reLU(double in){
		return Math.max(in, 0);
	}
	private double dreLU(double in){
		if(in>0){
			return 1;
		}
		return 0;
	}
	private double linear(double in){
		return in;
	}
	private double dlinear(double in){
		return 1;
	}
	
}
