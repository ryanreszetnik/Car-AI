package Network;

import java.util.Arrays;

public class Network {

	Layer[] layers;
	public Network(int numLayers){
		layers= new Layer[numLayers];
		layers[0] = new Layer(Layer.TYPE.input,10,Node.ActFunc.ReLU);
		layers[1]= new Layer(Layer.TYPE.middle,7,Node.ActFunc.ReLU);
		for(int i = 2; i < numLayers-1; i++){
			layers[i] = new Layer(Layer.TYPE.middle,7,Node.ActFunc.ReLU);
		}
		layers[layers.length-1] = new Layer(Layer.TYPE.output,4, Node.ActFunc.Linear);
		for(int i = 1; i < numLayers; i++){
			layers[i].addConnections(layers[i-1]);
		}
	}
	public double[] run(double[] in){
		layers[0].setOutputs(in);
		for(Layer l: layers){
			l.run();
		}
		double[] out = layers[layers.length-1].getOutputs();
		return out;
	}
	public void backProp(double eOut[],double learning){
		layers[layers.length-1].setExpectedOut(eOut);
		for(int i = layers.length-1; i >= 0; i--){
			layers[i].backProp(learning);
		}
	}
	public void updateState(double eOut[], double state[], int numTimes){
		for(int i =0 ;i <numTimes; i++){
			run(state);
			backProp(eOut,1);
		}
	}
	public double cost(double out[], double eOut[]){
		double total = 0;
		for(int i = 0; i < out.length; i++){
			total+=Math.pow(out[i]-eOut[i], 2);
		}
		return total;
	}
}
