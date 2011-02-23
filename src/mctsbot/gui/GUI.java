package mctsbot.gui;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import mctsbot.MCTSBot;

public class GUI {
	
//	private static MCTSBot mctsbot = null;
	private static Graph graph = null;
	private static Thread updateThread = null;
	
	public static void initiate() {
//		GUI.mctsbot = mctsbot;
		graph = new Graph();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createWindow();
			}
		});
//		updateThread = new Thread(new Runnable() {
//			public void run() {
//				while(true) {
//					try { 
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					graph.redrawGraph();
//				}
//			}
//		});
//		
//		updateThread.setDaemon(true);
	}
	
	public static void stop() {
		
	}
	
	public static void updateGraph(ArrayList<Double>[] data) {
		if(graph!=null) graph.redrawGraph(data);
	}
	
	private static void createWindow() {
		final JFrame frame = new JFrame("MCTSBot");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocation(1200, 100);
		frame.setPreferredSize(new Dimension(800, 800));
		
//		JLabel label = new JLabel("Is it working?");
//		frame.getContentPane().add(label);
		
		graph = new Graph();
		frame.getContentPane().add(graph);
		
//		final ArrayList<Double>[] data = new ArrayList[3];
//		data[0] = new ArrayList<Double>();
//		data[1] = new ArrayList<Double>();
//		data[2] = new ArrayList<Double>();
//		
//		Random r = new Random();
//		
//		for(int i=0; i<25; i++) {
//			data[0].add(r.nextDouble()*300.0+850);
//			data[1].add(r.nextDouble()*300.0+850);
//			data[2].add(1000.0);
//		}
//		
//		graph.setData(data);
		
		
		frame.pack();
		frame.setVisible(true);
	}

	
	public static void main(String[] args) {
		initiate();
	}
	
}
