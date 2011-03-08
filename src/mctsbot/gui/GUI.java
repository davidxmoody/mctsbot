package mctsbot.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import mctsbot.MCTSBot;

public class GUI {
	
	private static Graph graph = null;
	private static JLabel cardLabel = null;
	private static JTextField thinkingTimeField = null;
	private static JButton restartButton = null;
	private static boolean restart = false;
	private static JButton forceStopButton = null;
	private static boolean forceStop = false;
	
	
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
	
//	public static void stop() {
//		
//	}
	
	public static void setCards(String cards) {
		if(cardLabel!=null) cardLabel.setText(cards);
	}
	
	public static void updateGraph(ArrayList<Double>[] data) {
		if(graph!=null) graph.redrawGraph(data);
	}
	
	private static void createWindow() {
		final JFrame frame = new JFrame("MCTSBot");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocation(1200, 100);
		frame.setPreferredSize(new Dimension(800, 800));
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
//		JLabel label = new JLabel("Is it working?");
//		frame.getContentPane().add(label);
		
		graph = new Graph();
		frame.add(graph);
		
		restartButton = new JButton("Restart");
		restartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restart = true;
			}
		});
		frame.add(restartButton);
		
		forceStopButton = new JButton("Force Stop");
		forceStopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				forceStop = true;
			}
		});
		frame.add(forceStopButton);
		
		thinkingTimeField = new JTextField("" + MCTSBot.THINKING_TIME);
		thinkingTimeField.setMaximumSize(new Dimension(200, 60));
		frame.add(thinkingTimeField);
		
		cardLabel = new JLabel();
		frame.add(cardLabel);
		
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

	public static long getThinkingTime() {
		try {
			return Integer.parseInt(thinkingTimeField.getText());
		} catch(Exception e) {
			return MCTSBot.THINKING_TIME;
		}
	}
	
	public static boolean stopThinking() {
		if(forceStop) {
			forceStop = false;
			return true;
		}
		return false;
	}

	public static boolean startOver() {
		if(restart ) {
			restart = false;
			return true;
		}
		return false;
	}
	
}
