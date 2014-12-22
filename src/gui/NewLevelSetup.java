package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class NewLevelSetup extends JDialog implements ActionListener
{
	private GridLayout windowLayout = new GridLayout(3, 1);
	private JPanel windowContainer = new JPanel(windowLayout);
	private JPanel fieldContainer = new JPanel(new GridLayout(1, 4));
	private JPanel buttonContainer = new JPanel(new GridLayout(1, 2));
	private JLabel labelSize = new JLabel("New level size:");
	private JLabel labelSizeX = new JLabel("x:");
	private JLabel labelSizeY = new JLabel("y:");
	private JFormattedTextField fieldSizeX;
	private JFormattedTextField fieldSizeY;
	private JButton buttonCancel = new JButton("Cancel");
	private JButton buttonCreate = new JButton("Create");

	private Menu menu = null;

	private boolean choice;

	public NewLevelSetup(String caption, Menu newMenu)
	{
		this.setTitle(caption);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);

		this.menu = newMenu;

		this.fieldSizeX = new JFormattedTextField(this.menu.getSizeX());
		this.fieldSizeY = new JFormattedTextField(this.menu.getSizeY());

		this.fieldSizeX.setColumns(3);
		this.fieldSizeY.setColumns(3);

		this.labelSize.setHorizontalAlignment(JLabel.CENTER);
		this.labelSizeX.setHorizontalAlignment(JLabel.CENTER);
		this.labelSizeY.setHorizontalAlignment(JLabel.CENTER);

		fieldSizeX.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				int value = ((Number)fieldSizeX.getValue()).intValue();
				if (value < 20)
				{
					value = 20;
					fieldSizeX.setValue(value);
				}
				else if (value > 500)
				{
					value = 500;
					fieldSizeX.setValue(value);
				}
			}
		});

		fieldSizeY.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				int value = ((Number)fieldSizeY.getValue()).intValue();
				if (value < 20)
				{
					value = 20;
					fieldSizeY.setValue(value);
				}
				else if (value > 500)
				{
					value = 500;
					fieldSizeY.setValue(value);
				}
			}
		});

		buttonCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				choice = false;
				setVisible(false);
				dispose();
			}
		});
		buttonCreate.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				menu.setSizeX(((Number)fieldSizeX.getValue()).intValue());
				menu.setSizeY(((Number)fieldSizeY.getValue()).intValue());

				choice = true;
				setVisible(false);
				dispose();
			}
		});

		fieldContainer.add(labelSizeX);
		fieldContainer.add(fieldSizeX);
		fieldContainer.add(labelSizeY);
		fieldContainer.add(fieldSizeY);

		buttonContainer.add(buttonCancel);
		buttonContainer.add(buttonCreate);

		windowLayout.setVgap(5);
		windowContainer.add(labelSize);
		windowContainer.add(fieldContainer);
		windowContainer.add(buttonContainer);


		this.add(windowContainer);

//		this.pack();
//		this.setVisible(true);
	}

	public boolean getChoice()
	{
		return this.choice;
	}

	public void actionPerformed(ActionEvent e)
	{
		this.choice = false;
		setVisible(false);
		dispose();
	}
}
