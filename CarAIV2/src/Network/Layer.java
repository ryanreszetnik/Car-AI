package Network;
import java.util.Arrays;

public class Layer {
	enum TYPE{
		input, output,middle
	}
	public TYPE type;
	Node[] nodes;
	double[] eOut;
	
	public Layer(TYPE t, int numNeurons, Node.ActFunc activation){
		this.type=t;
		if(this.type==Layer.TYPE.output){
			eOut = new double[numNeurons];
		}
		nodes = new Node[numNeurons];
		for(int i = 0; i < numNeurons; i++){
			nodes[i] = new Node(activation);
		}
	}
	public void addConnections(Layer previous){
		for(Node n: nodes){
			for(Node prev: previous.nodes){
				n.addInput(prev);
				prev.addOutput(n);
			}
		}
	}
	public void run(){
		if(type == Layer.TYPE.input){
			
		}else if(type == Layer.TYPE.output||type == Layer.TYPE.middle){
			for(Node n: nodes){
				n.run();
			}
		}
	}
	public void backProp(double learning){
		if(type == Layer.TYPE.input){
			
		}else if(type == Layer.TYPE.middle){
			for(Node n: nodes){
				n.backProp();
			}
		}else if(type == Layer.TYPE.output){
			for(int i = 0; i < nodes.length; i++){
				nodes[i].backProp(eOut[i],learning);
			}
		}
	}
	
	
	public void setOutputs(double[] out){
		for(int i = 0; i < nodes.length; i++){
			nodes[i].setOutput(out[i]);
		}
	}
	public double[] getOutputs(){
		double[] out = new double[nodes.length];
		for(int i = 0; i < out.length;i++){
			out[i]=nodes[i].output;
		}
		return out;
	}
	public void setExpectedOut(double[] out){
		eOut = Arrays.copyOf(out, out.length);
	}
}
