package application;

import java.util.ArrayList;

public class Everyone {
	static ArrayList<Car> cars = new ArrayList<>();
	static ArrayList<Car> newCars = new ArrayList<>();
	static boolean running = false;
	static boolean firstround = true;
	static double bestScore=0;
	public static int roundCount =1;

	public static void setup() {
		for (int i = 0; i < 50; i++) {
			Car temp = new Car(100, 100);
			cars.add(temp);
		}
	}

	public static void addCar(int x, int y) {
		Car temp = new Car(x, y);
		cars.add(temp);
	}

	public static void round() {
		int carCount = 0;
		int alivecount = 0;
		for (int i = 0; i < cars.size(); i++) {
			if (cars.get(i).alive) {
				alivecount++;
			}
		}
		if (running) {
			if (cars.size() >= 50 && alivecount == 0) {
				// running = false;
				//System.out.println("done");
				firstround = false;
				evaluateRound();

			} else if (cars.size() < 50) {

				if (alivecount < 5) {
					if (firstround) {
						addCar(100, 50);
					} else {
						if (cars.size() < 45) {
							cars.add(newCars.get(cars.size()));
						} else {
							addCar(100, 50);
						}

						carCount++;
					}

				}
			}
		}

	}

	public static void runRound() {
		running = true;
		System.out.println(roundCount++);
	}

	public static void evaluateRound() {
		newCars.clear();
		/*
		for (int i = 0; i < cars.size(); i++) {
			System.out.println(i + " fit= " + cars.get(i).fitness + " / " + totalFit());

		}*/
		// Car first = findBest();
		// cars.remove(first);
		// Car second = findBest();
		/*
		 * double avgFit = totalFit()/50;
		 */

		for (int i = 0; i < 40; i++) {
			int worst = findWorst();

			cars.get(worst).selfDestruct();
			cars.remove(worst);

		}
		for(int i = 0; i <cars.size(); i++){
			if(cars.get(i).fitness>bestScore){
				bestScore = cars.get(i).fitness;
				Network.printWeights(cars.get(i).net.getWeights());
				System.out.println();
			}
		}
		for (int i = 0; i < 50 && newCars.size() < 50; i++) {

			//System.out.println(i);
			double rand = Math.random() * totalFit();
			//System.out.println("rand1: " + rand + " /" + totalFit());
			Car a = getCarFit(rand);
			cars.remove(a);
			double rand2 = Math.random() * totalFit();
			//System.out.println("rand2: " + rand2 + " /" + totalFit());
			Car b = getCarFit(rand2);
			cars.add(a);
			//System.out.println(a + "merge with " + b);
			Car c;
			if (Math.abs(a.fitness - b.fitness) > 3) {
				c = new Car(100, 50, Network.merge(a.net.getWeights(), b.net.getWeights(), false));
			} else {
				c = new Car(100, 50, Network.merge(a.net.getWeights(), b.net.getWeights(), true));
			}
			//System.out.println("c is " + c);
			newCars.add(c);
		}
		int temp = cars.size();
		for (int i = 0; i < temp; i++) {
			cars.get(0).selfDestruct();
			cars.remove(0);
		}
		cars.clear();
		//System.out.println("sizes " + cars.size() + " new: " + newCars.size());

	}

	public static double totalFit() {
		double totalFitness = 0;
		for (int i = 0; i < cars.size(); i++) {
			totalFitness += cars.get(i).fitness;
		}
		return totalFitness;
	}

	public static Car getCarFit(double val) {
		double current = 0;
		int count = -1;
		for (int i = 0; i < cars.size() && current < val; i++) {
			current += cars.get(i).fitness;
			count = i;

		}
		//System.out.println("count: " + count);
		return cars.get(count);
	}

	public static int findWorst() {
		Car worst = cars.get(0);
		int place = 0;
		for (int i = 0; i < cars.size(); i++) {
			if (cars.get(i).fitness < worst.fitness) {
				worst = cars.get(i);
				place = i;
			}
		}
		return place;
	}

}
