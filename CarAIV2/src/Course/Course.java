package Course;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class Course {
	Pane root;
	Scene scene;
	ArrayList<Wall> walls = new ArrayList<>();
	ArrayList<Checkpoint> checkpoints = new ArrayList<>();
	Label status = new Label();
	public boolean editing = true;
	public boolean drawing = false;
	public boolean drawingCheckpoint = false;
	boolean saved = false;
	Wall current = null;
	Checkpoint currentCheckpoint = null;
	double[] inputs = { 0, 0 };
	ArrayList<Car> cars = new ArrayList<>();
	Car car;

	public Course(Pane root, Scene scene) {
		this.root = root;
		this.scene = scene;
//		 for(int i= 0;i < 100; i++){
//		 cars.add(new Car(this,root));
//		 }
		car = new Car(this, root);
		root.getChildren().add(status);
		readFile();
		updateStatus();
	}

	public void addWall(double x1, double y1, double x2, double y2) {
		Wall w = new Wall(x1, y1, x2, y2);
		root.getChildren().add(w);
		walls.add(w);
	}

	public void printWalls() {
		for (Wall w : walls) {
			System.out.println(
					"(" + w.getStartX() + ", " + w.getStartY() + ") (" + w.getEndX() + ", " + w.getEndY() + ")");
		}
	}

	public void intesect() {
		Point p = walls.get(0).intersects(walls.get(1));
		if (p != null) {
			Circle c = new Circle();
			c.setCenterX(p.getX());
			c.setCenterY(p.getY());
			c.setRadius(5);
			root.getChildren().add(c);
		}
	}

	public void handleEvents() {
		root.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				System.out.println("Clicked: " + event.getSceneX() + " " + event.getSceneY());
				if (editing) {
					drawing = !drawing;
					if (!drawingCheckpoint) {
						if (!drawing) {
							// add line to arrayList
							double coords[] = getCloseLineEnd(event.getSceneX(), event.getSceneY(), 10);
							current.updateEnd(coords[0], coords[1]);
							walls.add(current);
							current = null;
							saved = false;
						} else {// start drawing
							double coords[] = getCloseLineEnd(event.getSceneX(), event.getSceneY(), 10);
							current = new Wall(coords[0], coords[1], event.getSceneX(), event.getSceneY());
							root.getChildren().add(current);
						}
					} else {// checkpoint drawing
						if (!drawing) {
							// add line to arrayList
							currentCheckpoint.updateEnd(event.getSceneX(), event.getSceneY());
							checkpoints.add(currentCheckpoint);
							currentCheckpoint = null;
							saved = false;
						} else {// start drawing
							currentCheckpoint = new Checkpoint(event.getSceneX(), event.getSceneY(), event.getSceneX(),
									event.getSceneY());
							root.getChildren().add(currentCheckpoint);

						}
					}
					updateStatus();
				}
			}
		});
		root.setOnMouseMoved(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (drawing) {
					if (current != null) {
						current.updateEnd(event.getSceneX(), event.getSceneY());
					} else if (currentCheckpoint != null) {
						currentCheckpoint.updateEnd(event.getSceneX(), event.getSceneY());
					}
				}
			}
		});
		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
			case E:
				editing = !editing;
				if (!editing) {
					current = null;
					drawing = false;
				}
				break;
			case S:
				for(int i = 0; i < 100000; i++){
					update();
				}
			}
			if (editing) {
				switch (e.getCode()) {
				case T:// change type
					drawingCheckpoint = !drawingCheckpoint;
					current = null;
					drawing = false;
					break;
				case S:// save
					writeFile();
					System.out.println("Saved to file");
					saved = true;
					break;
				case L:// load save
					readFile();
					saved = true;
					break;
				case BACK_SPACE:
					System.out.println("delete");
					if (drawingCheckpoint) {
						if (checkpoints.size() > 0) {
							root.getChildren().remove(checkpoints.get(checkpoints.size() - 1));
							checkpoints.remove(checkpoints.size() - 1);
							System.out.println("removed checkpoint");
							saved = false;
						}
					} else {
						if (walls.size() > 0) {
							root.getChildren().remove(walls.get(walls.size() - 1));
							walls.remove(walls.size() - 1);
							System.out.println("removed wall");
							saved = false;
						}
					}
					break;
				default:
					System.out.println(e.getCode());
				}
			} else {
				switch (e.getCode()) {
				case UP:
					inputs[0] = 1;
					break;
				case DOWN:
					inputs[0] = -1;
					break;
				case LEFT:
					inputs[1] = -1;
					break;
				case RIGHT:
					inputs[1] = 1;
					break;
				}
			}
			car.updateInput(inputs);
			updateStatus();
		});
		scene.setOnKeyReleased(e -> {
			if (!editing) {

				switch (e.getCode()) {
				case UP:
					inputs[0] = 0;
					break;
				case DOWN:
					inputs[0] = 0;
					break;
				case LEFT:
					inputs[1] = 0;
					break;
				case RIGHT:
					inputs[1] = 0;
					break;
				}
				car.updateInput(inputs);
			}
		});
	}

	public double[] getCloseLineEnd(double x, double y, double maxDis) {
		for (Wall w : walls) {
			if (maxDis > Math.sqrt(Math.pow(x - w.getStartX(), 2) + Math.pow(y - w.getStartY(), 2))) {
				x = w.getStartX();
				y = w.getStartY();
			}
			if (maxDis > Math.sqrt(Math.pow(x - w.getEndX(), 2) + Math.pow(y - w.getEndY(), 2))) {
				x = w.getEndX();
				y = w.getEndY();
			}
		}
		double[] output = { x, y };
		return output;
	}

	public void update() {
		if (!editing) {
			car.move(walls, checkpoints);
			for (Car c : cars) {
				c.move(walls, checkpoints);
			}
		}
		updateStatus();
	}

	public void updateStatus() {
		String statusText = "Editing Course (E): " + editing + "\n\n";
		String controls = "";
		if (editing) {
			controls = "Currently Drawing: " + drawing + "\nType of Line (T): "
					+ (drawingCheckpoint ? "Checkpoint" : "Wall") + "\nSave (S): " + saved
					+ "\nLoad Saved Course (L)\nRemove Last Wall(DELETE)";
		} else {
			controls = "Next Checkpoint: " + car.nextCheckpoint + "\nScore: " + car.score + "\nDeaths: " + car.numDeaths
					+ "\n" + car;
		}
		status.setText(statusText + controls);
	}

	public void readFile() {
		// first clear current course
		while (checkpoints.size() > 0) {
			root.getChildren().remove(checkpoints.get(0));
			checkpoints.remove(0);
		}
		while (walls.size() > 0) {
			root.getChildren().remove(walls.get(0));
			walls.remove(0);
		}
		BufferedReader br = null;

		File f = new File("CourseV2");
		try {
			if (f.exists() == false) {
				PrintWriter writer = new PrintWriter("CourseV2");
				writer.close();
			}
			br = new BufferedReader(new FileReader("CourseV2"));
			String CurrentLine;
			while ((CurrentLine = br.readLine()) != null) {
				String[] data = CurrentLine.split(" ");
				if (data[0].equals("W")) {// wall
					walls.add(new Wall(Double.valueOf(data[1]), Double.valueOf(data[2]), Double.valueOf(data[3]),
							Double.valueOf(data[4])));
				} else if (data[0].equals("C")) {// checkpoint
					checkpoints.add(new Checkpoint(Double.valueOf(data[1]), Double.valueOf(data[2]),
							Double.valueOf(data[3]), Double.valueOf(data[4])));
				} else {
					System.out.println("BAD LINE: " + CurrentLine);
				}
			}
			for (Wall w : walls) {
				root.getChildren().add(w);
			}
			for (Checkpoint c : checkpoints) {
				root.getChildren().add(c);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeFile() {
		// the string everything is put into
		String data = "";
		try {
			for (int i = 0; i < walls.size(); i++) {
				data += walls.get(i).toString() + "\n";
			}
			for (int i = 0; i < checkpoints.size(); i++) {
				data += checkpoints.get(i).toString() + "\n";
			}
			BufferedWriter out = new BufferedWriter(new FileWriter("CourseV2"));
			// changes the text of the file to the string
			out.write(data);
			// closes the file
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
