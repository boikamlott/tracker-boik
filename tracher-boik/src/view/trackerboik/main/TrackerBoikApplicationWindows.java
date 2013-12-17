package view.trackerboik.main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import controller.trackerboik.main.TrackerBoikController;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TrackerBoikApplicationWindows {

	private TrackerBoikController controller = TrackerBoikController.getInstance();
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TrackerBoikApplicationWindows window = new TrackerBoikApplicationWindows();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TrackerBoikApplicationWindows() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 929, 573);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("Fichier");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmChargerLesDonnes = new JMenuItem("Rafraichir");
		mntmChargerLesDonnes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.getAtomicDataController().refreshCurrentFolder();
			}
		});
		mnNewMenu.add(mntmChargerLesDonnes);
		
		JMenuItem mntmQuitter = new JMenuItem("Quitter");
		mntmQuitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.exitApplicaiton();
				exit();
			}
		});
		mnNewMenu.add(mntmQuitter);
		
		JMenu mnConfiguration = new JMenu("Configuration");
		menuBar.add(mnConfiguration);
	}

	public void exit() {
		this.frame.dispose();
	}
}
