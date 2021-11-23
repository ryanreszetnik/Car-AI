package application;

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Main extends Application {
	static CustomLine current;
	static boolean drawing = false;
	static Pane root;
	static ArrayList<CustomLine> border = new ArrayList<>();
	static ArrayList<CustomLine> checkpoints = new ArrayList<>();
	static ArrayList<Car> cars = new ArrayList<>();
	static final int popSize = 100;
	public static boolean running = false;
	public static int aliveCount = popSize;
	public static float[] inputs = new float[8];
	public static Label modeText = new Label("editing");
	public static boolean edit = true;

	@Override
	public void start(Stage primaryStage) {
		try {
			root = new Pane();
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			CustomLine.root = root;
			Car.root = root;
			for (int i = 0; i < popSize; i++) {
				cars.add(new Car());
			}
			root.getChildren().add(modeText);

			// root.getChildren().addAll(c.car);
			Counter nodeCount = new Counter();
			Counter connCount = new Counter();
			Genome a = new Genome(nodeCount, connCount);
			a.addNode(new Node(Node.TYPE.INPUT, nodeCount.getNextCount(), 0.5f));
			a.addNode(new Node(Node.TYPE.INPUT, nodeCount.getNextCount(), 0.5f));
			a.addNode(new Node(Node.TYPE.OUTPUT, nodeCount.getNextCount(), 5f));
			a.addNode(new Node(Node.TYPE.OUTPUT, nodeCount.getNextCount(), 5f));

			// System.out.println(a.getFitness() + " first");

			Evaluator eval = new Evaluator(a, 200, nodeCount, connCount) {

				@Override
				public void runRound(ArrayList<Network> networks) {
					// TODO Auto-generated method stub
					for (Network n : networks) {
						for (int i = 0; i < cars.size(); i++) {
							cars.get(i).net = networks.get(i);
						}
						running = true;
						System.out.println("start");
					}

				}

			};
			eval.makeSpecies();
			Car c = new Car();
			AnimationTimer timer = new AnimationTimer() {// what constantly runs
				@Override
				public void handle(long now) {
					// c.move(border);
					// if(c.dead(border)){
					// System.out.println("dead");
					// }
					c.move(border);

					if (running) {

						// System.out.println("Try");
						// if (nextMove) {
						// System.out.println("move");
						move();
						if (aliveCount == 0) {
							running = false;
						}
						// nextMove = false;
						// }
					} else {
						System.out.println("Done");
						for (Car b : cars) {
							b.reset(border);
						}

						eval.afterRound();
						eval.makeSpecies();
						// Testing.show(eval.topGenome, "Round: " + roundCount);
						// roundCount++;
					}

				}
			};

			scene.setOnKeyPressed(e -> {
				switch (e.getCode()) {
				case SPACE:
					timer.start();
					break;

				case A:
					c.steerAngle = 25;
					break;

				case D:
					c.steerAngle = -25;
					break;
				case W:

					c.acceleration = 0.3f;

					break;

				case M:
					edit = !edit;
					if (edit) {
						modeText.setText("edit");
					} else {
						modeText.setText("checkpoints");
					}

					break;

				case R:
					if (border.size() > 0) {
						CustomLine line = border.get(border.size() - 1);
						line.selfDestruct();
						border.remove(line);
					}
					break;

				}
			});

			scene.setOnKeyReleased(e -> {
				switch (e.getCode()) {

				case A:
					c.steerAngle = 0;
					break;

				case D:
					c.steerAngle = 0;
					break;
				case W:
					c.acceleration = 0;
				}
			});

			root.setOnMouseReleased(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					// System.out.println("Released " + mouseEvent.getSceneX() +
					// " " + mouseEvent.getSceneY());
					if (drawing) {
						drawing = false;
						if (edit) {
							border.add(current);
							// current.shown.setVisible(true);
							// root.getChildren().add(current.shown);
							System.out.println("Added " + current);

							for (CustomLine l : border) {
								if (CustomLine.getIntersection(l, current) != null) {
									Point p = CustomLine.getIntersection(l, current);
									Circle c = new Circle(p.x, p.y, 4);
									root.getChildren().add(c);
								}
							}

						} else {
							checkpoints.add(current);
							current.shown.setFill(Color.GREEN);
						}
					}
				}
			});
			root.setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					// System.out.println("Dragged " + mouseEvent.getSceneX() +
					// " " + mouseEvent.getSceneY());
					float x = (float) mouseEvent.getSceneX();
					float y = (float) mouseEvent.getSceneY();
					if (!drawing) {
						System.out.println("new line");

						for (CustomLine l : border) {
							if (Point.distance(l.end, new Point(x, y)) < 20) {
								x = l.end.x;
								y = l.end.y;
								break;
							} else if (Point.distance(l.start, new Point(x, y)) < 20) {
								x = l.start.x;
								y = l.start.y;
								break;
							}
						}
						current = new CustomLine(x, y, x, y);
						drawing = true;
					} else {
						for (CustomLine l : border) {
							if (Point.distance(l.end, new Point(x, y)) < 20) {
								x = l.end.x;
								y = l.end.y;
								break;
							} else if (Point.distance(l.start, new Point(x, y)) < 20) {
								x = l.start.x;
								y = l.start.y;
								break;
							}
						}
						current.updateEndXY(x, y);
					}
				}
			});
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);

	}

	public static void move() {
		if (!running) {
			return;
		}
		// next = pipes[Pipe.score % pipes.length];
		for (CustomLine p : border) {

			for (Car c : cars) {
				c.move(border);
				if (c.dead(border)) {
					// System.out.println("dead");
					c.kill();
					// }
				}

			}
		}
		// inputs: bird height, relative height to next pipe center, x dis
		// to
		// next pipe
		// inputs[0] = next.getX() - 100;
		System.out.println(aliveCount);
		aliveCount = 0;
		for (Car car : cars) {
			if (car.isAlive) {
				aliveCount++;
				// inputs[0] = bird.getHeight() * 1f / Bird.screenHeight;
				// inputs[1] = (next.getMidpoint() - bird.getHeight());
				inputs[0] = car.carHeading;
				inputs[1] = car.carSpeed;
				inputs[2] = car.rad.distance[0] / 300;
				inputs[3] = car.rad.distance[1] / 300;
				inputs[4] = car.rad.distance[2] / 300;
				inputs[5] = car.rad.distance[3] / 300;
				inputs[6] = car.rad.distance[4] / 300;
				inputs[7] = car.rad.distance[5] / 300;
				// inputs[2] = next.getX();
				// System.out.println(inputs[0]);
				// inputs[2] = next.getMidpoint() - inputs[1];

				car.run(inputs, border);
			} else if (car.net.gene.getFitness() == 0) {
				// bird.net.gene.setFitness(bird.topScore);
			}
		}

	}
}
