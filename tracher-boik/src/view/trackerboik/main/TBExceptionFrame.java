package view.trackerboik.main;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class TBExceptionFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int TEXT_ERROR = 1;
	private JPanel contentPane;
	private JTextPane textPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TBExceptionFrame frame = new TBExceptionFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TBExceptionFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 461, 184);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JLabel lblImagelabel = new JLabel("");
		lblImagelabel.setIcon(new ImageIcon(TBExceptionFrame.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		contentPane.add(lblImagelabel, BorderLayout.WEST);
		
		textPane = new JTextPane();
		textPane.setBackground(SystemColor.menu);
		textPane.setEditable(false);
		contentPane.add(textPane, BorderLayout.CENTER);
	}
	
	public void setErrorText(String errorTxt) {
		JTextPane jl = ((JTextPane) contentPane.getComponent(TEXT_ERROR));
		jl.setText(errorTxt);
	}

}
