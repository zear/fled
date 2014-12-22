package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import level.*;
import util.*;

public class ObjectPanel extends JPanel
{
	private MapPanel mapPanel = null;
	private ListCellRenderer<Object> renderer;
	//private LinkedList<GameObject> availableObjectsList;
	private LinkedList<GameObject> addedObjectsList;
	private DefaultListModel<GameObject> availableListModel;
	private DefaultListModel<GameObject> addedListModel;
	private JList <GameObject> availableObjects;
	private JList <GameObject> addedObjects;
	private JScrollPane availablePane;
	private JScrollPane addedPane;
	private JButton buttonAdd = new JButton("Add");
	private JButton buttonRemove = new JButton("Remove");
	private ButtonGroup radioDirection = new ButtonGroup();
	private JRadioButton directionLeft = new JRadioButton("left");
	private JRadioButton directionRight = new JRadioButton("right");

	class ObjectCellRenderer extends JLabel implements ListCellRenderer<Object>
	{
		public ObjectCellRenderer()
		{
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			if(value instanceof GameObject)
				setText(((GameObject)value).getName() + " ["+((GameObject)value).getX()+","+((GameObject)value).getY()+"]");
			else
				setText(value.toString());

			Color background;
			Color foreground;

			// check if this cell represents the current DnD drop location
			JList.DropLocation dropLocation = list.getDropLocation();
			if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == index)
			{

				background = Color.BLUE;
				foreground = Color.WHITE;

			// check if this cell is selected
			}
			else if (isSelected)
			{
				background = Color.BLUE;
				foreground = Color.WHITE;

			// unselected, and not the DnD drop location
			}
			else
			{
				background = Color.WHITE;
				foreground = Color.BLACK;
			}

			setBackground(background);
			setForeground(foreground);

			return this;
		}
	}

	public ObjectPanel()
	{
		this.renderer = new ObjectCellRenderer();

		this.radioDirection.add(directionLeft);
		this.radioDirection.add(directionRight);

		buttonAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(availableListModel != null)
				{
					GameObject newObj = new GameObject();
					int selectedIndex = availableObjects.getSelectedIndex();

					if(selectedIndex != -1)
					{
						newObj.setName(availableListModel.get(selectedIndex).getName());
						newObj.setImgTemplate(availableListModel.get(selectedIndex).getImgTemplate());
						addedObjectsList.push(newObj);
						addedListModel.addElement(newObj);
						addedObjects.setSelectedValue(newObj, true);
						mapPanel.setObjectIsNew(true);
					}
					mapPanel.repaint();
				}
			}
		});
		buttonRemove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(addedListModel != null)
				{
					int selectedIndex = addedObjects.getSelectedIndex();

					if(selectedIndex != -1)
					{
						GameObject objToRem = addedListModel.get(selectedIndex);

						ListIterator<GameObject> objsli = addedObjectsList.listIterator();
						while (objsli.hasNext())
						{
							if(objsli.next() == objToRem)
							{
								objsli.remove();
								addedListModel.remove(selectedIndex);
								addedObjects.setSelectedIndex(selectedIndex == 0 ? 0 : selectedIndex - 1);
								break;
							}
						}
						mapPanel.repaint();
					}
				}
			}
		});
		directionLeft.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(addedObjects != null && addedObjects.getSelectedIndex() != -1)
				{
					addedListModel.get(addedObjects.getSelectedIndex()).setDirection(false);
					mapPanel.repaint();
				}
			}
		});
		directionRight.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(addedObjects != null && addedObjects.getSelectedIndex() != -1)
				{
					addedListModel.get(addedObjects.getSelectedIndex()).setDirection(true);
					mapPanel.repaint();
				}
			}
		});
	}

	void addListeners()
	{
//		if(availableObjects != null)
//		{
//			availableObjects.addListSelectionListener(new ListSelectionListener()
//			{
//				public void valueChanged(ListSelectionEvent e)
//				{
//					if(e.getValueIsAdjusting() == false)
//					{
//						if(availableObjects.getSelectedIndex() == -1)
//						{
//							//No selection
//						}
//						else
//						{
//							System.out.printf("Selected: %s [%d,%d]\n", availableListModel.get(availableObjects.getSelectedIndex()).getName(), availableListModel.get(availableObjects.getSelectedIndex()).getX(), availableListModel.get(availableObjects.getSelectedIndex()).getY());
//						}
//					}
//				}
//			});
//		}
		if(addedObjects != null)
		{
			addedObjects.addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if(e.getValueIsAdjusting() == false)
					{
						if(addedObjects.getSelectedIndex() == -1)
						{
							//No selection
						}
						else
						{
							GameObject selObj = addedListModel.get(addedObjects.getSelectedIndex());

							mapPanel.setSelectedObject(selObj);
							mapPanel.setObjectIsNew(false);

							if(selObj.getDirection())
							{
								directionRight.setSelected(true);
							}
							else
							{
								directionLeft.setSelected(true);
							}
						}
					}
				}
			});
		}
	}

	void loadObjects()
	{
		this.availableListModel = new DefaultListModel<GameObject>();
		this.addedListModel = new DefaultListModel<GameObject>();

		// Check a directory for object files
		File folder = new File(Data.getDataDirectory() + "/data/obj/");
		File [] listOfFiles = folder.listFiles();

		FileRead fp;
		String line;
		String [] words;

		for(int i = 0; i < listOfFiles.length; i++)
		{
			if(listOfFiles[i].isFile())
			{
				fp = new FileRead((File)listOfFiles[i]);

				GameObject newObj = null;
				int tmpW = 0;
				int tmpH = 0;
				boolean parsingFrames = false;

				while(fp.hasNext())
				{
					line = fp.getLine();
					if(line == null)
					break;

					words = line.split("\\s");

					if (words[0].equals("NAME"))
					{
						newObj = new GameObject();
						this.availableListModel.addElement(newObj);
						newObj.setName(words[1]);
						continue;
					}
					else if (words[0].equals("WIDTH"))
					{
						tmpW = Integer.parseInt(words[1]);
						continue;
					}
					else if (words[0].equals("HEIGHT"))
					{
						tmpH = Integer.parseInt(words[1]);
						continue;
					}
					else if (words[0].equals("IMG"))
					{
						if(newObj != null)
						{
							newObj.setImgTemplate(words[1], Integer.parseInt(words[2]), Integer.parseInt(words[3]));
							newObj.setW(tmpW);
							newObj.setH(tmpH);
						}
						continue;
					}
					else if (words[0].equals("IDLE"))
					{
						parsingFrames = true;
						continue;
					}
					else if (parsingFrames)
					{
						if (words[0].equals("LEFT"))
						{
							newObj.setOffset(false, Integer.parseInt(words[1]), Integer.parseInt(words[2]));
							continue;
						}
						else if (words[0].equals("RIGHT"))
						{
							newObj.setOffset(true, Integer.parseInt(words[1]), Integer.parseInt(words[2]));
							break;
						}
					}
				}

				fp.close();
			}
		}

		// Load objects present in the level file
		this.addedObjectsList = this.mapPanel.level.getObjectList();

		for(GameObject curObj : this.addedObjectsList)
		{
			curObj.setImgTemplate(this.getObjectTemplateByName(curObj.getName()).getImgTemplate());
			this.addedListModel.addElement(curObj);
		}

		this.availableObjects = new JList<>(this.availableListModel);
		this.availableObjects.setCellRenderer(this.renderer);
		this.addedObjects = new JList<>(this.addedListModel);
		this.addedObjects.setCellRenderer(this.renderer);

		this.availableObjects.setLayoutOrientation(JList.VERTICAL);
		this.availableObjects.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.addedObjects.setLayoutOrientation(JList.VERTICAL);
		this.addedObjects.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.addListeners();

		availablePane = new JScrollPane(availableObjects);
		addedPane = new JScrollPane(addedObjects);

		this.removeAll();
		this.add(availablePane);
		this.add(addedPane);
		this.add(buttonAdd);
		this.add(buttonRemove);
		this.add(directionLeft);
		this.add(directionRight);
		this.repaint();
	}

	void setSelectedObject(GameObject selObj)
	{
		addedObjects.setSelectedValue(selObj, true);
	}

	void setPanels(MapPanel newMapPanel)
	{
		this.mapPanel = newMapPanel;
	}

	GameObject getObjectTemplateByName(String objName)
	{
		if(this.availableListModel != null)
		{
			for(int i = 0; i < this.availableListModel.getSize(); i++)
			{
				if(this.availableListModel.get(i).getName().equals(objName))
					return this.availableListModel.get(i);
			}
			return null;
		}
		else
		{
			return null;
		}
	}
}
