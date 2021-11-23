package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class Course {
	static ArrayList<Wall> walls = new ArrayList<>();
	static ArrayList<Car> cars = new ArrayList<>();
	static int selected = -1;
	static boolean shift = false;
	static boolean edit = true;
	static double offsetx = 0;
	static double offsety= 0;
	static Label mode= new Label("This is something");
	static Car car = new Car(100, 50);

	public static void createCourse() {
		Main.root.getChildren().add(mode);
		Main.scene.setOnMousePressed(e -> {
			selected = -1;
			for (int i = 0; i < walls.size(); i++) {
				if (walls.get(i).contain( e.getX(), e.getY())) {
					selected = i;
					System.out.println(i);
					break;
				}
			}
		});
		Main.scene.setOnMouseDragged(e -> {
			if (selected >= 0) {
				walls.get(selected).move(e.getX()-offsetx, e.getY()-offsety);
			}

		});
		Main.scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
			case P:
				edit = !edit;
				if(edit){
					mode.setText("Edit");
				}else{
					mode.setText("Not Edit");
				}
			case SHIFT:
				shift = true;
				break;
			}
			if (edit) {
				if (selected >= 0) {
					switch (e.getCode()) {

					case LEFT:
						if (shift) {
							walls.get(selected).rotate(-1);
						} else {
							walls.get(selected).rotate(-10);
						}

						break;
					case RIGHT:
						if (shift) {
							walls.get(selected).rotate(1);
						} else {
							walls.get(selected).rotate(10);
						}

						break;
					case UP:
						if (shift) {
							walls.get(selected).stretchw(1);
						} else {
							walls.get(selected).stretchw(10);
						}
						break;
					case DOWN:
						if (walls.get(selected).getWidth() > 15) {
							if (shift) {
								walls.get(selected).stretchw(-1);
							} else {
								walls.get(selected).stretchw(-10);
							}
						}
						break;
					case D:
						Main.root.getChildren().remove(walls.get(selected).rect);
						walls.remove(walls.get(selected));
						break;

					}
				}

				switch (e.getCode()) {
				case N:
					addWall(100, 50, 50, 0);
					break;

				case S:
					writeFile();
					break;
				}
			} else {
				switch (e.getCode()) {
				case A:
					addCar(100, 50);
					break;
				case UP:
					cars.get(0).acceleration = 0.1;
					break;
				case DOWN:
					cars.get(0).acceleration = -0.1;
					break;
				case LEFT:
					cars.get(0).turning = -3;
					break;
				case RIGHT:
					cars.get(0).turning = 3;
					break;
				case R:
//					Everyone.runRound();
					break;
				case L:
//					int temp[][] = {{0,0,0,0,0},{6,6,6,6,6},{6}};
//					Car c = new Car(100,50,Network.inputWeights(temp));
//					cars.add(c);
//					break;
				}

			}
		});
		Main.scene.setOnKeyReleased(e -> {
			switch (e.getCode()) {
			case SHIFT:
				shift = false;
				break;
			case UP:
				cars.get(0).acceleration = 0;
				break;
			case DOWN:
				cars.get(0).acceleration = 0;
				break;
			case LEFT:
				cars.get(0).turning = 0;
				break;
			case RIGHT:
				cars.get(0).turning = 0;
				break;

			}
		});

	}

	public static void addWall(double width, double height, double x, double y, double rotation) {
		Wall temp = new Wall(width, height, x, y, rotation);
		walls.add(temp);
	}

	public static void addWall(int width, double x, double y, double rotation) {
		Wall temp = new Wall(width, 10, x, y, rotation);
		walls.add(temp);
	}

	public static void addCar(int x, int y) {
		Car temp = new Car(x, y);
		cars.add(temp);
	}

	

	public static void readFile() {

		BufferedReader br = null;

		File f = new File("Course");
		try {
			// checks if the file already exists
			if (f.exists() == false) {
				// creates the file
				PrintWriter writer = new PrintWriter("Course");
				// close file
				writer.close();
			}
			// reads the file
			br = new BufferedReader(new FileReader("Course"));
			// for what is on each line
			String CurrentLine;

			int counter = 0;
			double width = 0;
			double length = 0;
			double x = 0;
			double y = 0;
			double bearing = 0;

			// runs through file and reads each line
			while ((CurrentLine = br.readLine()) != null) {

				// depending on which line it is reading, it sets the variables
				// above to that line
				switch (counter % 5) {

				case 0:
					width = Double.valueOf(CurrentLine);
					break;
				case 1:
					length = Double.valueOf(CurrentLine);
					break;
				case 2:
					x = Double.valueOf(CurrentLine);
					break;
				case 3:
					y = Double.valueOf(CurrentLine);
					break;
				case 4:
					bearing = Double.valueOf(CurrentLine);
					addWall(width, length, x, y, bearing);
					break;
				}
				//System.out.println(counter);
				counter++;

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeFile() {
		// the string everything is put into
		String data = "";
		try {
			// loops though all of the tasks and adds every necessary variable
			// to the string
			for (int i = 0; i < walls.size(); i++) {
				data += walls.get(i).toString() + "\n";
			}
			// edits the file named "Tasks"
			BufferedWriter out = new BufferedWriter(new FileWriter("Course"));
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
