package application;

import java.util.ArrayList;
import java.util.Arrays;

public class Radar {
	public static final float size = 300;

	CustomLine beams[] = new CustomLine[6];
	final int[] offsets = { 30, 50, 70, -30, -50, -70 };
	public float[] distance = new float[6];

	public Radar(float x, float y, float theta) {
		float rad = (float) Math.toRadians(theta);
		for (int i = 0; i < beams.length; i++) {
			beams[i] = new CustomLine(x, y, x + (float) Math.cos(rad + offsets[i]) * size,
					y + (float) Math.sin(rad + offsets[i]) * size);
		}
	}

	public void rotate(float x, float y, float theta, ArrayList<CustomLine> obsticles) {
		float rad = (float) Math.toRadians(theta);
		for (int i = 0; i < beams.length; i++) {
			beams[i].update(x, y, x + (float) Math.cos(rad + offsets[i]) * size,
					y + (float) Math.sin(rad + offsets[i]) * size);
			for (CustomLine ob : obsticles) {
				Point intersect = CustomLine.getIntersection(beams[i], ob);
				if(intersect !=null){
					distance[i] = Point.distance(new Point(x,y), intersect);
					beams[i].updateEndXY(intersect.x, intersect.y);
//					System.out.pri /ntln(distance[i]);
				}else{
					distance[i] = size;
				}
			}
		}
//		System.out.println(Arrays.toString(distance));
	}

}
