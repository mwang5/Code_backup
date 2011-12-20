package Cartoon;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.event.*;

/**
 * This is the main panel. I have two large panels that contained by mainpanel(using BorderLayout), 
 * one is called panel_1 which uses BorderLayout. CENTER is my DrawPanel, SOUTH 
 * is X-axis slider, EAST is Y-axis slider; another one is called southpanel which
 * uses GridLayout to let the panel_3 and panel_4 arrange level by level. In SouthPanel,
 * it contains the panel_3 and panel_4 then in panel_3, it contains X JLabel and Y JLabel using  
 * FlowLayout, last panel4 simple contained a exit button which exit the program properly.
 *
 *
 * @author mwang5
 *
 */

@SuppressWarnings("serial")
public class MainPanel extends JPanel{
	
	private int value1, value2;	//stored the x location and y location of Alien
	
	public MainPanel() {
		
		super();
		this.setLayout(new BorderLayout());	// set the MainPanel to BorderLayout contains southpanel and panel_1
		
		JPanel southpanel = new JPanel(new GridLayout(0, 1));	//initialize southpanel in MainPanel
		JPanel panel_1 = new JPanel(new BorderLayout());	//initialize panel_1 in MainPanel
		
		final DrawingPanel myPanel = new DrawingPanel();
		new NiceAlien(myPanel);
		
		Dimension size = new Dimension(300, 300);
		myPanel.setPreferredSize(size);	//Set the preferred size of myPanel.
		myPanel.setSize(size);	//set Size of myPanel.
		
		JPanel panel_3 = new JPanel(new FlowLayout());	//initialize panel_3 in southpanel
		JPanel panel_4 = new JPanel();	//initialize panel_4 in southpanel
		
		/*intialize componets in lay in panel_3 and panel_4*/
		JButton exitbutton = new JButton("Exit");
		final JSlider slider1 = new JSlider(JSlider.HORIZONTAL, 0, 300, 0);
		final JSlider slider2 = new JSlider(JSlider.VERTICAL, 0, 300, 0);
		slider2.setInverted(true);
		final JLabel label1 = new JLabel("X = " + slider1.getValue());
		final JLabel label2 = new JLabel("Y = " + slider2.getValue());
		
		/*slider1 and slider2 changestatelistener for monitoring the slider change*/
		class ChangedSateListener1 implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				 value1 = slider1.getValue();
				String str = Integer.toString(value1);
				label1.setText("X = " + str);
				myPanel.moveAlien(value1,value2);
			}
		}
		slider1.addChangeListener(new ChangedSateListener1());
		
		class ChangedSateListener2 implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				 value2 = slider2.getValue();
				String str = Integer.toString(value2);
				label2.setText("Y = " + str);
				myPanel.moveAlien(value1, value2);
			}
		}
		slider2.addChangeListener(new ChangedSateListener2());
		
		/*add components to panel_1*/
		panel_1.add(slider1, BorderLayout.SOUTH);
		panel_1.add(slider2, BorderLayout.EAST);
		panel_1.add(myPanel, BorderLayout.CENTER);
		
		/*add components to panel_3 and panel_4*/
		panel_3.add(label1);
		panel_3.add(label2);
		panel_4.add(exitbutton);
		
		/*add components to southpanel*/
		southpanel.add(panel_3);
		southpanel.add(panel_4);

		/*exitbutton listener for monitoring exit*/
		class ExitButtonListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		}
		exitbutton.addActionListener(new ExitButtonListener());
		
		
		this.add(panel_1, BorderLayout.CENTER);	//add panel_1 to mainPanel
		this.add(southpanel, BorderLayout.SOUTH);	//add southpanel to mainPanel
	}	
	
}