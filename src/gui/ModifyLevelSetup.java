package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import level.*;
import util.*;

public class ModifyLevelSetup extends JDialog implements ActionListener
{
	private GridLayout windowLayout = new GridLayout(3, 1);
	private JPanel windowContainer = new JPanel(windowLayout);
	private JPanel fieldContainer = new JPanel(new GridLayout(2, 4));
	private JPanel buttonContainer = new JPanel(new GridLayout(1, 2));
	private JLabel labelSize = new JLabel("Add/remove tiles (Enter negative numbers to remove tiles):");
	private JLabel labelSizeXLeft = new JLabel("From left (x):");
	private JLabel labelSizeXRight = new JLabel("From right (x):");
	private JLabel labelSizeYTop = new JLabel("From top (y):");
	private JLabel labelSizeYBottom = new JLabel("From bottom (y):");
	private JFormattedTextField fieldSizeXLeft;
	private JFormattedTextField fieldSizeXRight;
	private JFormattedTextField fieldSizeYTop;
	private JFormattedTextField fieldSizeYBottom;
	private JButton buttonCancel = new JButton("Cancel");
	private JButton buttonCreate = new JButton("Create");

	private Menu menu = null;

	private boolean choice;

	public ModifyLevelSetup(String caption, Menu newMenu)
	{
		this.setTitle(caption);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);

		this.menu = newMenu;

		this.fieldSizeXLeft = new JFormattedTextField(0);
		this.fieldSizeXRight = new JFormattedTextField(0);
		this.fieldSizeYTop = new JFormattedTextField(0);
		this.fieldSizeYBottom = new JFormattedTextField(0);

		this.fieldSizeXLeft.setColumns(3);
		this.fieldSizeXRight.setColumns(3);
		this.fieldSizeYTop.setColumns(3);
		this.fieldSizeYBottom.setColumns(3);

		this.labelSize.setHorizontalAlignment(JLabel.CENTER);
		this.labelSizeXLeft.setHorizontalAlignment(JLabel.CENTER);
		this.labelSizeXRight.setHorizontalAlignment(JLabel.CENTER);
		this.labelSizeYTop.setHorizontalAlignment(JLabel.CENTER);
		this.labelSizeYBottom.setHorizontalAlignment(JLabel.CENTER);

		fieldSizeXLeft.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				int value = ((Number)fieldSizeXLeft.getValue()).intValue();
//				if (value < 0)
//				{
//					value = 0;
//					fieldSizeXLeft.setValue(value);
//				}
//				else if (menu.getSizeX() + value > 500)
//				{
//					value = 500 - menu.getSizeX();
//					fieldSizeXLeft.setValue(value);
//				}
			}
		});

		fieldSizeXRight.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				int value = ((Number)fieldSizeXRight.getValue()).intValue();
//				if (value < 0)
//				{
//					value = 0;
//					fieldSizeXRight.setValue(value);
//				}
//				else if (menu.getSizeX() + value > 500)
//				{
//					value = 500 - menu.getSizeX();
//					fieldSizeXRight.setValue(value);
//				}
			}
		});

		fieldSizeYTop.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				int value = ((Number)fieldSizeYTop.getValue()).intValue();
//				if (value < 0)
//				{
//					value = 0;
//					fieldSizeYTop.setValue(value);
//				}
//				else if (menu.getSizeY() + value > 500)
//				{
//					value = 500 - menu.getSizeY();
//					fieldSizeYTop.setValue(value);
//				}
			}
		});

		fieldSizeYBottom.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				int value = ((Number)fieldSizeYBottom.getValue()).intValue();
//				if (value < 0)
//				{
//					value = 0;
//					fieldSizeYBottom.setValue(value);
//				}
//				else if (menu.getSizeY() + value > 500)
//				{
//					value = 500 - menu.getSizeY();
//					fieldSizeYBottom.setValue(value);
//				}
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
				menu.setSizeX(menu.getSizeX() + ((Number)fieldSizeXLeft.getValue()).intValue() + ((Number)fieldSizeXRight.getValue()).intValue());
				menu.setSizeY(menu.getSizeY() + ((Number)fieldSizeYTop.getValue()).intValue() + ((Number)fieldSizeYBottom.getValue()).intValue());
				menu.getMapPanel().level.resize(((Number)fieldSizeXLeft.getValue()).intValue(), ((Number)fieldSizeXRight.getValue()).intValue(), ((Number)fieldSizeYTop.getValue()).intValue(), ((Number)fieldSizeYBottom.getValue()).intValue(), 0);

				for(GameObject curObj : menu.getMapPanel().level.getObjectList())
				{
					curObj.setX(curObj.getX() + ((Number)fieldSizeXLeft.getValue()).intValue() * 16);
					curObj.setY(curObj.getY() + ((Number)fieldSizeYTop.getValue()).intValue() * 16);
				}

				choice = true;
				setVisible(false);
				dispose();
			}
		});

		fieldContainer.add(labelSizeXLeft);
		fieldContainer.add(fieldSizeXLeft);
		fieldContainer.add(labelSizeXRight);
		fieldContainer.add(fieldSizeXRight);
		fieldContainer.add(labelSizeYTop);
		fieldContainer.add(fieldSizeYTop);
		fieldContainer.add(labelSizeYBottom);
		fieldContainer.add(fieldSizeYBottom);

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
