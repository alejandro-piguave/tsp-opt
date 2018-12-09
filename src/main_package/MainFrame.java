package main_package;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.Dimension;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;

public class MainFrame {
	public static final int MIN_WIDTH = 600, MIN_HEIGHT=450, INIT_MAX_GEN=100000,INIT_ELITE_SIZE=1000,INIT_POP_SIZE=10000,
			TEMP=100,
			INIT_NUM_NODES=10;
	public static final double INIT_MUT_RATIO = 0.0015d,COOLING_RATIO=0.001d, ABS_TEMP=0.0001d;
	private boolean extra_options;
	private JFrame frame;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
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
	public MainFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setTitle("TSPGEN");
		frame.setMinimumSize(new Dimension(MIN_WIDTH,MIN_HEIGHT));
		frame.setBounds(100, 100, 100+MIN_WIDTH, 100+MIN_HEIGHT);
		//frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DisplayPanel displayPanel = new DisplayPanel(INIT_MAX_GEN,INIT_POP_SIZE,INIT_ELITE_SIZE,INIT_MUT_RATIO,TEMP,COOLING_RATIO,ABS_TEMP);
		displayPanel.setLayout(new BorderLayout(0, 0));
		
		JButton btnRun = new JButton("Run");
		btnRun.setEnabled(false);

		JButton btnRandomize = new JButton("Generate nodes");
		JSpinner numNodesSpinner = new JSpinner();
		numNodesSpinner.setModel(new SpinnerNumberModel(new Integer(INIT_NUM_NODES), new Integer(3), null, new Integer(1)));
		numNodesSpinner.setMinimumSize(new Dimension(29, 18));
		
		
		JLabel lblOutput = new JLabel("OUTPUT");
		lblOutput.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblOutput.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		JButton btnOptns = new JButton("...");
		
		JComboBox<String> algSelector = new JComboBox<String>();
		algSelector.addItem("Genetic algorithm");
		algSelector.addItem("Check all solutions");
		algSelector.addItem("Greedy algorithm");
		algSelector.addItem("2-opt tour");
		algSelector.addItem("Greedy + 2-opt tour");
		algSelector.addItem("Simulated annealing");
		algSelector.addItem("Greedy + Simulated annealing");
		algSelector.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent event) {
		       if (event.getStateChange() == ItemEvent.SELECTED) {
		    	  displayPanel.setCurrentAlgorithm(algSelector.getSelectedIndex());
		          if(algSelector.getSelectedIndex()==0 || algSelector.getSelectedIndex()==5 || algSelector.getSelectedIndex()==6) {
		        	  btnOptns.setEnabled(true);
		        	  extra_options=true;
		          }else{
		        	  btnOptns.setEnabled(false);
		        	  extra_options=false;
		          }
		       }  
			}});
		
		JTextArea output = new JTextArea();
		output.setEditable(false);

		displayPanel.setOutputLabel(output);
		
		btnRandomize.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 

			    if(displayPanel.getNumNodes()==0)
			    	displayPanel.randomNodes(INIT_NUM_NODES);
			    else displayPanel.randomNodes();
				btnRun.setEnabled(true);
				  } 
			} );

		numNodesSpinner.addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            displayPanel.randomNodes((int)numNodesSpinner.getValue());
				btnRun.setEnabled(true);
	        }

	    });

		btnOptns.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
					if(algSelector.getSelectedIndex()==0) {
						double min = 0.0000d, value = displayPanel.getMut_ratio(), max = 1.0000d, stepSize = 0.0001d;
						SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, stepSize);
						  
						JSpinner generations = new JSpinner();
						generations.setValue(new Integer(displayPanel.getMax_gen()));
						JSpinner population_size = new JSpinner();
						population_size.setValue(new Integer(displayPanel.getPop_size()));
						JSpinner elite_size = new JSpinner();
						elite_size.setValue(new Integer(displayPanel.getElite_size()));
						JSpinner mutation_ratio = new JSpinner(model);
						JSpinner.NumberEditor numberEditor = new JSpinner.NumberEditor(mutation_ratio,"0.0000");
						mutation_ratio.setEditor(numberEditor);
						final JComponent[] inputs = new JComponent[] {
								new JLabel("Generations:"),
								generations,
								new JLabel("Population size:"),
								population_size,
								new JLabel("Elite size:"),
								elite_size,
								new JLabel("Mutation ratio:"),
								mutation_ratio };
						  int result = JOptionPane.showConfirmDialog(null, inputs, "Genetic algorithm parameters",   
								  JOptionPane.OK_CANCEL_OPTION ,
								  JOptionPane.PLAIN_MESSAGE);
						  if (result == JOptionPane.OK_OPTION) {
						      displayPanel.setGeneticParameters((int)generations.getValue(), (int)population_size.getValue(),
						    		  (int)elite_size.getValue(), (double)mutation_ratio.getValue());
						  }
					}else {
						double min = 0.0000d,value1 = displayPanel.getCooling_ratio(), value2=displayPanel.getAbsolute_temperature(),max = 1.0000d, stepSize = 0.0001d;
				        SpinnerNumberModel model1 = new SpinnerNumberModel(value1, min, max, stepSize),
				        		model2=new SpinnerNumberModel(value2, min, max, stepSize);
				  
						JSpinner temp_spinner = new JSpinner();
						temp_spinner.setValue(new Integer((int)(displayPanel.getStart_temperature())));

						JSpinner cooling_ratio_spinner = new JSpinner(model1);
						JSpinner abs_temp_spinner = new JSpinner(model2);
						
						
						JSpinner.NumberEditor edit1 = new JSpinner.NumberEditor(cooling_ratio_spinner,"0.0000"),
								edit2 = new JSpinner.NumberEditor(abs_temp_spinner,"0.00000000");
						cooling_ratio_spinner.setEditor(edit1);
						abs_temp_spinner.setEditor(edit2);
						final JComponent[] inputs = new JComponent[] {
				        new JLabel("Temperature:"),
				        temp_spinner,
				        new JLabel("Cooling rate:"),
				        cooling_ratio_spinner,
				        new JLabel("Absolute temperature:"),
				        abs_temp_spinner};
				  int result = JOptionPane.showConfirmDialog(null, inputs, "Simulated annealing parameters",   
						  JOptionPane.OK_CANCEL_OPTION ,
						  JOptionPane.PLAIN_MESSAGE);
				  if (result == JOptionPane.OK_OPTION) {
				      displayPanel.setAnnealingParameters((int)temp_spinner.getValue(),
				    		  (double)cooling_ratio_spinner.getValue(), (double)abs_temp_spinner.getValue());
				  }
						
						
					}
					
				  } 
			} );
		
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(output, GroupLayout.PREFERRED_SIZE, 177, GroupLayout.PREFERRED_SIZE)
						.addComponent(numNodesSpinner, GroupLayout.PREFERRED_SIZE, 177, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnRandomize, GroupLayout.PREFERRED_SIZE, 177, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblOutput, GroupLayout.PREFERRED_SIZE, 178, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnRun, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnOptns, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
						.addComponent(algSelector, GroupLayout.PREFERRED_SIZE, 177, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(displayPanel, GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
					.addGap(19))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(displayPanel, GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(algSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnRun, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnOptns))
							.addGap(13)
							.addComponent(numNodesSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnRandomize)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblOutput)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(output, GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)))
					.addContainerGap())
		);
		frame.getContentPane().setLayout(groupLayout);

		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(btnRun.getText().equals("Run")) {
					enableRunningMode(frame.getContentPane(),true);
					displayPanel.runAlgorithm();
					btnRun.setText("Stop");
				}else if(btnRun.getText().equals("Stop")){
					enableRunningMode(frame.getContentPane(),false);
					displayPanel.stopAlgorithm();
					btnRun.setText("Run");
					output.setText("");
				}
			}
		});
	}
	private void enableRunningMode(Container container,boolean b) {
        Component[] components = container.getComponents();
        for (Component component : components) {
        	if (component instanceof DisplayPanel || component instanceof JLabel || component instanceof JTextArea) 
        		continue;
        	if(component instanceof JButton) {
        		JButton btn = (JButton)component;
        		if (btn.getText().equals("Run"))
        			continue;
        		else if(btn.getText().equals("...") && !extra_options && !b)
        			continue;
        	}
            component.setEnabled(!b);
            if (component instanceof Container) {
            	enableRunningMode((Container)component, b);
            }
        }
	}

//	private void enableEditMode(Container container,boolean b) {
//        Component[] components = container.getComponents();
//        for (Component component : components) {
//        	if (component instanceof DisplayPanel) {
//        		DisplayPanel dPanel = (DisplayPanel)component;
//        		dPanel.enableEditMode(b);
//        		if(b) dPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
//        		else dPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//        		continue;}
//        	if(component instanceof JButton) {
//        		JButton btn = (JButton)component;
//        		if (btn.getText().equals("Done"))
//        			continue;
//        		else if(btn.getText().equals("...") && !extra_options && !b)
//        			continue;
//        	}
//            component.setEnabled(!b);
//            if (component instanceof Container) {
//            	enableEditMode((Container)component, b);
//            }
//        }
//	}
}
