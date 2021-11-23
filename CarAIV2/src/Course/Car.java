package Course;

//https://ai.stackexchange.com/questions/4660/how-to-combine-backpropagation-in-neural-nets-and-reinforcement-learning
import java.util.ArrayList;
import java.util.Arrays;

import Network.Network;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Car {
	final int numRays = 10;
	final double[] rayPositions = { -Math.PI, -Math.PI * 3 / 4, -Math.PI / 2, -Math.PI / 4, -Math.PI / 15, 0,
			Math.PI * 3 / 4, Math.PI / 2, Math.PI / 4, Math.PI / 15 };
	final double startingAngleRay = -3;
	final double rayLength = 200;
	final double carWidth = 20;
	final double carLength = 40;
	public double score = 0;
	public double prevDistance = 100;
	int numAtRest = 0;
	double dcost = 0;
	Network net = new Network(3);
	Course c;
	Pane root;
	Rectangle car = new Rectangle(carLength, carWidth);
	double inputs[] = { 0, 0, 0, 0 };// 0=(-1) back->forward(1), 1=(-1)left ->
	double diff = 0; // (1)right
	int[] pMax = new int[10];
	double pos[] = { 175, 510 };
	double vel[] = { 0, 0 };
	double absVel = 0;
	double heading = -Math.PI / 2;
	int aliveCount = 0;
	CarRay[] rays = new CarRay[numRays];
	Line[] hitBox = new Line[4];
	int nextCheckpoint = 0;

	final double turningConstant = 0.1;//0.04
	final double accelConstant = 0.05;
	final double maxSpeed = 2;
	final double friction = 0.01;

	final double checkpointVal = 5;// 0.2
	final double wallHitVal = -100;// -0.5
	final double surivalVal = 0.02;// 0.05
	final double greed = 0.05;
	final double learningRate = 1;
	final double discount = 0.9;
	int numDeaths = 0;

	public Car(Course c, Pane root) {
		this.c = c;
		this.root = root;
		car.setFill(Color.RED);
		root.getChildren().add(car);
		for (int i = 0; i < numRays; i++) {
			// rays[i] = new CarRay(pos[0], pos[1], startingAngleRay -
			// startingAngleRay * 2 / (numRays - 1) * i, 200, 0,
			// root);
			rays[i] = new CarRay(pos[0], pos[1], rayPositions[i], rayLength, 0, root);
			root.getChildren().add(rays[i]);
		}
	}

	public void move(ArrayList<Wall> walls, ArrayList<Checkpoint> checkpoints) {
		prevDistance = disFromNext(checkpoints);
		double reward = 0;
		boolean isFinal = false;
		boolean reset = false;
		double[] netInputs = getInputs(walls);
		inputs = net.run(netInputs);
		aliveCount++;
		int chosenAction = 0;
		if (Math.random() < greed) {// choose weighted action
			// double total = 0;
			// for (int i = 0; i < inputs.length; i++) {
			// total += Math.abs(inputs[i]);
			// }
			// double val = Math.random() * total;
			// total = 0;
			// for (int i = 0; i < inputs.length; i++) {
			// total += Math.abs(inputs[i]);
			// if (total >= val) {
			// chosenAction = i;
			// break;
			// }
			// }
			chosenAction = (int) (Math.random() * 4);
		} else {// choose max action
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i] > inputs[chosenAction]) {
					chosenAction = i;
				}
			}
		}
		switch (chosenAction) {
		case 0:
			heading += turningConstant;
			break;
		case 1:
			heading -= turningConstant;
			break;
		case 2:
			absVel += accelConstant;
			break;
		case 3:
			absVel -= accelConstant;
			break;
		}

		absVel -= friction;
		absVel = Math.max(Math.min(maxSpeed, absVel), 0);
		if (absVel == 0) {
			numAtRest++;
		} else {
			numAtRest = 0;
		}
		vel[0] = Math.cos(heading) * absVel;
		vel[1] = Math.sin(heading) * absVel;
		pos[0] += vel[0];
		pos[1] += vel[1];
		car.setTranslateX(pos[0] - carLength / 2);
		car.setTranslateY(pos[1] - carWidth / 2);
		car.setRotate(heading * 180 / Math.PI);

		generateHitbox();
		if (isColliding(walls) || pos[0] < 0 || pos[1] < 0) {
			car.setFill(Color.RED);
			reward = wallHitVal;
			isFinal = true;
			reset = true;

		} else {
			car.setFill(Color.GREEN);
			// reward = absVel/100;
			// if (absVel == 0) {
			// reward = -surivalVal;
			// }
		}

		if (crossingCheckpoint(checkpoints) && reward >= 0) {
			reward = checkpointVal;
		}else if(reward >=0){
			reward= prevDistance-disFromNext(checkpoints);
			if(reward == 0){
				reward = -0.001;
			}
			System.out.println("Reward: "+ reward);
		}

		// if (aliveCount == 300) {
		// aliveCount = 0;
		// reward = wallHitVal;
		// isFinal = true;
		// reset=true;
		// }
		// reward+=absVel/5;
		score += reward;

		// if (reward != 0) {
		updateNetwork(netInputs, chosenAction, getInputs(walls), reward, isFinal);// isFinal
		// }
		if (reset) {
			reset();
		}
	}

	public double[] getInputs(ArrayList<Wall> walls) {
		double netInputs[] = new double[numRays];
		// netInputs[0] = absVel;
		for (int i = 0; i < numRays; i++) {
			rays[i].updatePosition(pos[0], pos[1], heading);
			netInputs[i] = rays[i].getValue(walls) / rayLength;
		}
		return netInputs;
	}

	public void updateNetwork(double[] oldInputs, int oldAction, double[] newInputs, double reward,
			boolean isFinalState) {
		double[] oldOutputs = net.run(oldInputs);
		double oldQ = oldOutputs[oldAction];
		double[] newOutputs = net.run(newInputs);
		double maxQ = Double.MIN_VALUE;
		for (int i = 0; i < newOutputs.length; i++) {
			if (newOutputs[i] > maxQ) {
				maxQ = newOutputs[i];
			}
		}
		// System.out.println("old: " + Arrays.toString(oldInputs));
		// System.out.println("new: " + Arrays.toString(newInputs));

		if (isFinalState) {
			// System.out.println("Final");
			diff = learningRate * (reward - oldQ);
		} else {
			diff = learningRate * (reward + discount * maxQ - oldQ);//
			// diff=0;
		}
		if (diff < 0) {
//			System.out.println("bad " + diff + " reward: " + reward);
		}
		// double minDiff = 1;
		// if(diff>0&&diff<minDiff){
		// diff=minDiff;
		// }else if(diff<0&&diff>-minDiff){
		// diff=-minDiff;
		// }
		double[] eOut = Arrays.copyOf(oldOutputs, oldOutputs.length);
		eOut[oldAction] += diff;
		// for(int i =0; i < eOut.length; i++){
		// if(i!=oldAction){
		// eOut[i]-=diff/3;
		// }
		// }
		// if (oldAction == 2) {
		// System.out.println("old: " + Arrays.toString(oldOutputs));
		// System.out.println("exp: " + Arrays.toString(eOut));

		// System.out.println("diff: " + diff + " " + reward + " max:" + maxQ +
		// " old " + oldQ);
		// }
		net.updateState(eOut, oldInputs, 3);
		double cost = net.cost(oldOutputs, eOut);
		double cost2 = net.cost(net.run(oldInputs), eOut);
		dcost = cost2 - cost;
		// System.out.println("new: " + Arrays.toString(net.run(oldInputs)) +"
		// "+oldAction +" gave: "+ score);
	}

	public boolean isColliding(ArrayList<Wall> walls) {
		for (Wall w : walls) {
			for (Line l : hitBox) {
				if (l.intersects(w) != null) {
					aliveCount = 0;
					return true;
				}
			}
		}
		return false;
	}

	public double disFromNext(ArrayList<Checkpoint> checkpoints) {
		Checkpoint w = checkpoints.get(nextCheckpoint);
		double[] coords = w.getMidpoint();
		return distance(coords[0], coords[1], pos[0], pos[1]);

	}

	public double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y2 - y1, 2));
	}

	public boolean crossingCheckpoint(ArrayList<Checkpoint> checkpoints) {
		if (checkpoints.size() == 0)
			return false;
		Checkpoint w = checkpoints.get(nextCheckpoint);
		for (Line l : hitBox) {
			if (l.intersects(w) != null) {
				nextCheckpoint++;
				aliveCount = 0;
				nextCheckpoint %= checkpoints.size();
				return true;
			}
		}

		return false;
	}

	public void generateHitbox() {
		Point fR = new Point(
				pos[0] + Math.cos(heading) * carLength / 2 - Math.cos(Math.PI / 2 - heading) * carWidth / 2,
				pos[1] + Math.sin(heading) * carLength / 2 + Math.sin(Math.PI / 2 - heading) * carWidth / 2);
		Point fL = new Point(
				pos[0] + Math.cos(heading) * carLength / 2 + Math.cos(Math.PI / 2 - heading) * carWidth / 2,
				pos[1] + Math.sin(heading) * carLength / 2 - Math.sin(Math.PI / 2 - heading) * carWidth / 2);
		Point bR = new Point(
				pos[0] - Math.cos(heading) * carLength / 2 - Math.cos(Math.PI / 2 - heading) * carWidth / 2,
				pos[1] - Math.sin(heading) * carLength / 2 + Math.sin(Math.PI / 2 - heading) * carWidth / 2);
		Point bL = new Point(
				pos[0] - Math.cos(heading) * carLength / 2 + Math.cos(Math.PI / 2 - heading) * carWidth / 2,
				pos[1] - Math.sin(heading) * carLength / 2 - Math.sin(Math.PI / 2 - heading) * carWidth / 2);
		hitBox[0] = new Line(fR.getX(), fR.getY(), fL.getX(), fL.getY());
		hitBox[1] = new Line(fR.getX(), fR.getY(), bR.getX(), bR.getY());
		hitBox[2] = new Line(bR.getX(), bR.getY(), bL.getX(), bL.getY());
		hitBox[3] = new Line(fL.getX(), fL.getY(), bL.getX(), bL.getY());
	}

	public void updateInput(double[] inputs) {
		this.inputs = inputs;
	}

	public void reset() {
		pos[0] = 175;
		pos[1] = 510;
		absVel = maxSpeed / 2;
		score = 0;
		heading = -Math.PI / 2;
		nextCheckpoint = 0;
		numDeaths++;
	}

	public String toString() {
		if (inputs.length == 4) {
			return "Diff: " + diff + "\ndCost: " + dcost + "\nTurn right: " + Math.round(inputs[0]) + "\nTurn left: "
					+ Math.round(inputs[1]) + "\nGo Forwards: " + Math.round(inputs[2]) + "\nBrake: "
					+ Math.round(inputs[3]);
		}
		return "";
	}

}
