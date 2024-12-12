import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import javax.swing.JOptionPane;
@SuppressWarnings("serial")
class BarChartFrame extends Frame
{
	Hashtable<String, Color> colorMap = new Hashtable<String, Color>();

	protected Vector<Integer>	data;
	protected Vector<String>	labels;
	protected Vector<Color>		colors;

	Choice 	colorSelect;
	TextField	labelSelect;
	TextField	dataSelect;

	BarChart chart;

	class BarChartFrameControl extends WindowAdapter implements ActionListener 
	{
		// SER515 #2: The line which adds an element to the colors vector
		// assumes the selected item is available in the colorMap. Is this always true?
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof Button)
			{
				String selectedColorName = colorSelect.getSelectedItem();
				Color selectedColor = colorMap.get(selectedColorName);
				boolean colorExists = false;
				for (Color color : colors) {
					if (color.equals(selectedColor)) {
						colorExists = true;
						break;
					}
				}
				if (colorExists) {
					JOptionPane.showMessageDialog(null,
							"Warning: The selected color is already in use.",
							"Duplicate Color",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				labels.addElement(labelSelect.getText());
				data.addElement(new Integer(dataSelect.getText()));
				colors.addElement(colorMap.get(colorSelect.getSelectedItem()));

				chart.setData(data);
				chart.setColors(colors);
				chart.setLabels(labels);
				chart.repaint();
			}
			else if (e.getSource() instanceof Menu)
			{
				BarChartFrame.this.dispose();
				System.exit(0);		// only option at this time
			}
		}
	}

	public void initData(String fname)
	{
		data = new Vector<Integer>();
		labels = new Vector<String>();
		colors = new Vector<Color>();

		colorMap.put("red", Color.red);
		colorMap.put("green", Color.green);
		colorMap.put("blue", Color.blue);
		colorMap.put("magenta", Color.magenta);
		colorMap.put("gray", Color.gray);

		// SER515 #3: There are multiple problems here, ranging from input data validation
		// to data in the file matching what is in the color map to how exceptions are
		// handled. Improve the code to handle these 3 problems.
		try {
			FileReader bridge = new FileReader(fname);
			StreamTokenizer	tokens = new StreamTokenizer(bridge);

			while (tokens.nextToken() != StreamTokenizer.TT_EOF) {
				if (tokens.ttype != StreamTokenizer.TT_NUMBER) {
					JOptionPane.showMessageDialog(null,
							"Invalid data value: requires an integer. Found: " + tokens.sval,
							"Data Input Error",
							JOptionPane.ERROR_MESSAGE);
					continue;
				}
				int number = (int) tokens.nval;
				tokens.nextToken();
				if (tokens.ttype != StreamTokenizer.TT_WORD) {
					JOptionPane.showMessageDialog(null,
							"Invalid label: requires a string. Found: " + tokens.nval,
							"Label Input Error",
							JOptionPane.ERROR_MESSAGE);
					continue;
				}
				String label = tokens.sval;
				tokens.nextToken();
				if (tokens.ttype != StreamTokenizer.TT_WORD || !colorMap.containsKey(tokens.sval)) {
					JOptionPane.showMessageDialog(null,
							"Invalid color: '" + tokens.sval + "' is not known, blakc is default.",
							"Color Input Warning",
							JOptionPane.WARNING_MESSAGE);
					colors.addElement(Color.BLACK);
				} else {
					colors.addElement(colorMap.get(tokens.sval));
				}

				data.addElement(new Integer(number));
				labels.addElement(label);
//				colors.addElement(color);
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}

	public BarChartFrame(String fname) {
		BarChartFrameControl control = new BarChartFrameControl();

		initData(fname);

		setSize(350,350);
		setLayout(new BorderLayout());

		MenuBar mb = new MenuBar();
		Menu file = new Menu("File");
		file.addActionListener(control);
		file.add("Exit");
		mb.add(file);
		setMenuBar(mb);

		chart = new BarChart();

		chart.setData(data);
		chart.setLabels(labels);
		chart.setColors(colors);

		Panel components = new Panel();
		components.setSize(350,50);
		components.setLayout(new FlowLayout());

		colorSelect = new Choice();
		colorSelect.add ("red");
		colorSelect.add("green");
		colorSelect.add("blue");
		colorSelect.add("magenta");
		colorSelect.add("gray");
		colorSelect.add("orange");
		components.add(colorSelect);
		labelSelect = new TextField("label", 10);
		components.add(labelSelect);
		dataSelect = new TextField("data", 5);
		components.add(dataSelect);

		Button button = new Button("Add Data");
		button.addActionListener(control);
		components.add(button);

		setBackground(Color.lightGray);
		add(components, "South");
		add(chart, "North");
		chart.repaint();
		setVisible(true);
	}
}
