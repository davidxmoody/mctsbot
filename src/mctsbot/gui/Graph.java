package mctsbot.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Graph extends JPanel {
	
	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;
	private static final Color BACKGROUND_COLOUR = new Color(230, 230, 230);
	private static final Color RAISE_COLOUR = new Color(0, 255, 0);
	private static final Color CALL_COLOUR = new Color(0, 0, 255);
	private static final Color FOLD_COLOUR = new Color(255, 0, 0);
	private static final Color[] LINE_COLOURS = {RAISE_COLOUR, CALL_COLOUR, FOLD_COLOUR}; 
	
	private ArrayList<Double>[] data = null;
	private boolean updated = false;

	public Graph() {
		this.setDoubleBuffered(true);
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}
	
//	public void setWidth(int width) {
//		
//	}
	
//	public void setData(ArrayList<Double>[] data) {
//		System.err.println("setData was called, data = " + data);
//		this.data = data;
//		redrawGraph();
//	}
	
	public void redrawGraph(ArrayList<Double>[] data) {
		this.data = data;
		this.updated = true;
		repaint();
	}
	
	protected void paintComponent(Graphics g) {
//		System.err.println("paintComponent was called, updated = " + updated + ", data = " + data);
		if(updated) {
			g.setColor(BACKGROUND_COLOUR);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			drawData(g);
			updated = false;
		}
	}

	private void drawData(Graphics g) {
		if(data[0].size()==0||data[1].size()==0||data[2].size()==0) return;
		final double range = 2.0;
		final double minY = data[2].get(0)-range;
		final double maxY = data[2].get(0)+range;
		final int numToDraw = data[0].size();
		for(int i=0; i<3; i++) {
			if(data[i].size()==0) continue;
			g.setColor(LINE_COLOURS[i]);
			drawArray(g, data[i], minY,	maxY, numToDraw);
		}
	}
	
	private void drawArray(Graphics g, ArrayList<Double> array, 
			double minY, double maxY, int numToDraw) {
		final int width = this.getWidth();
		final int height = this.getHeight();
		int x1, y1, x2, y2;
		x1 = 0;
		y1 = (int)((1.0-(array.get(0)-minY)/(maxY-minY))*height);
		if(y1<0) y1 = 0;
		if(y1>height) y1 = height-1;
		for(int i=1; i<Math.min(array.size(), numToDraw); i++) {
			x2 = i*width/numToDraw;
			y2 = (int)((1.0-(array.get(i)-minY)/(maxY-minY))*height);
			if(y2<0) y2 = 0;
			if(y2>height) y2 = height-1;
			g.drawLine(x1, y1, x2, y2);
//			System.out.println("Drawing line from (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ")");
			x1 = x2;
			y1 = y2;
		}
//		System.out.println();
	}
	
	
	
}
