package controller;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

import ai.Player;
import eval.Evaluator;
import model.AbstractState.MOVE;
import model.BinaryState;
import model.State;
import view.ControlPanel;
import view.FancyPanel;

public class Controller {

	private static boolean running = false;

	public static final List<Evaluator> evaluators = getAvailableInstances(Evaluator.class);
	
	private static final List<Player> players = getAvailableInstances(Player.class);
	
	private static final JButton playPause = new JButton("Play");
	private static final JButton restart = new JButton("Restart");

	private static ControlPanel cPanel;

	public static void main(String[] args) {
		final State board = new BinaryState();
		final FancyPanel panel = new FancyPanel(board);
		panel.setPreferredSize(new Dimension(600, 600));

		final JFrame frame = new JFrame("Score: 0");
		
		frame.addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode() - 37;
				if (code == 48) {
					board.undo();
				} else if (code == 45) {
					board.redo();
				} else if (code >= 0 && code < 4) {
					board.move(MOVE.values()[code]);
					board.getMoves();
					board.toLong();
				}
				panel.repaint();
				frame.setTitle("Score: " + board.getScore());
			}
		});
		
		MouseListener bml = new MouseListener() {

			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			
			public void mouseClicked(MouseEvent e) {
				Object source = e.getSource();
				if (source instanceof JButton) {
					JButton b = (JButton) source;
					System.out.println(b.getText());
					if (b.getText().equals("Play")) {
						running = true;
						b.setText("Pause");
					} else if (b.getText().equals("Pause")) {
						running = false;
						b.setText("Play");
					} else if (b.getText().equals("Restart")) {
						running = false;
						board.reset();
						frame.setTitle("Score: " + board.getScore());
						cPanel.reset();
						for (Player p : players) {
							p.reset();
						}
						if(playPause.getText().equals("Pause")) {
							playPause.setText("Play");
						}
						frame.repaint();
					}
				}
			}
		};
		
		playPause.addMouseListener(bml);
		restart.addMouseListener(bml);
		
		cPanel = new ControlPanel(players, playPause, restart);
		
		cPanel.setPreferredSize(new Dimension(250, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(cPanel, 0);
		frame.getContentPane().add(panel, 1);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.pack();

		cPanel.setFrame(frame);
		board.reset();
		cPanel.reset();
		while (true) {
			List<MOVE> moves = board.getMoves();
			while (!running) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {}
			}
			while (!moves.isEmpty()) {
				while (!running) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				long start = System.currentTimeMillis();
				MOVE move = cPanel.player.getMove(new BinaryState(board.toLong(), board.getScore()));
				cPanel.updateTime((int) (System.currentTimeMillis()-start));
				board.move(move);
				moves = board.getMoves();
				panel.repaint();
				frame.setTitle("Score: " + board.getScore());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getAvailableInstances(Class<T> type) {

		List<T> items = new LinkedList<T>();
		URL url = type.getResource("");
		File dir = new File(url.getFile().replaceAll("%20", " "));
		String classpath = dir.getPath().replaceAll("^.*/(.*)$","$1.");
		classpath = classpath.replaceAll("^.*\\\\(.*)$","$1.");
		for (String file : dir.list()) {
			Class<?> theClass;
			try {
				theClass = Class.forName(classpath
						+ file.substring(0, file.length() - 6));
				Constructor<?>[] cons = theClass.getConstructors();
				if (cons.length > 0) {
					Object obj = cons[0].newInstance();
					if (type.isInstance(obj)) {
						items.add((T) obj);
					}
				}
			} catch (Throwable e) {
			}
		}

		return items;
	}
}
