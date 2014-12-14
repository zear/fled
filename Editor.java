import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import java.util.LinkedList;
import java.util.ListIterator;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

enum EditMode
{
	MODE_NONE,
	MODE_TILE_EDIT,
	MODE_TILE_SELECTION,
	MODE_OBJECT_EDIT,
	MODE_LEVEL_EDIT
}

class Data
{
	private static String dataDirectory = ".."; // default location

	public static String getDataDirectory()
	{
		return dataDirectory;
	}

	public static void setDataDirectory(String dataDirectory)
	{
		Data.dataDirectory = dataDirectory;
	}
}

class NewLevelSetup extends JDialog implements ActionListener
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

class ModifyLevelSetup extends JDialog implements ActionListener
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

class Menu extends JMenuBar
{
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem fileNew = new JMenuItem("New level");
	private JMenuItem fileOpen = new JMenuItem("Open level");
	private JMenuItem fileSave = new JMenuItem("Save level");
	private JMenuItem fileSaveAs = new JMenuItem("Save level as...");
	private JMenuItem fileModify = new JMenuItem("Modify level");
	private JMenuItem fileQuit = new JMenuItem("Quit editor");
	private JMenu runMenu = new JMenu("Run");
	private JMenuItem runSetExec = new JMenuItem("Set executable location");
	private JMenuItem runRunLevel = new JMenuItem("Run level");
	private JMenu helpMenu = new JMenu("Help");
	private JMenuItem helpAbout = new JMenuItem("About");

	private JFileChooser fileChooser = new JFileChooser(Data.getDataDirectory());

	private MapPanel mapPanel = null;
	private TilesetPanel tilesetPanel = null;
	private ToolbarPanel toolbarPanel = null;
	private ObjectPanel objectPanel = null;

	private int newSizeX = 20;
	private int newSizeY = 20;

	public Menu()
	{
		this.add(fileMenu);
		fileMenu.add(fileNew);
		fileMenu.add(fileOpen);
		fileMenu.add(fileSave);
		fileMenu.add(fileSaveAs);
		fileMenu.add(fileModify);
		fileMenu.add(fileQuit);
		this.add(runMenu);
		runMenu.add(runSetExec);
		runMenu.add(runRunLevel);
		this.add(helpMenu);
		helpMenu.add(helpAbout);

		fileNew.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// create new level
				if(showNewLevelSetup())
				{
					mapPanel.level = new Level(newSizeX, newSizeY);
					tilesetPanel.setImage(0, mapPanel.level.getLayer(1).getImg());
					tilesetPanel.showLayer[0] = true;
					tilesetPanel.defaultSettings();

					for(int i = 0; i < mapPanel.level.getNumOfLayers(); i++)
					{
						BufferedImage defMapImg = new BufferedImage(mapPanel.level.getLayer(i).getWidth() * 16, mapPanel.level.getLayer(i).getHeight() * 16, BufferedImage.TYPE_INT_ARGB);
						mapPanel.setImage(i, defMapImg);
						mapPanel.showLayer[i] = true;
					}

					if(mapPanel.drawAreaLayers.size() >= 1)
						mapPanel.paintOnLayer = 1; // We assume that layer 1 is the "walkable" layer

					int levelWidth;
					int levelHeight;
					int tile = 0;

					for(int n = 0; n < mapPanel.drawAreaLayers.size(); n++)
					{
						levelWidth = mapPanel.level.getLayer(n).getWidth() * 16;
						levelHeight = mapPanel.level.getLayer(n).getHeight() * 16;

						for(int i = 0, x = 0; i < levelWidth; i+=16, x++)
						{
							for(int j = 0, y = 0; j < levelHeight; j+=16, y++)
							{
								tile = mapPanel.level.getLayer(n).getTile(x, y);
								mapPanel.paintTile(n, tile, i, j, false);
							}
						}
					}

					toolbarPanel.defaultSettings();

					if(mapPanel.drawAreaLayers.size() > 0)
					{
						mapPanel.setPreferredSize(new Dimension(mapPanel.drawAreaLayers.get(0).getWidth(),mapPanel.drawAreaLayers.get(0).getHeight()));
						mapPanel.revalidate();
					}

					objectPanel.loadObjects();
				}
			}
		});
		fileOpen.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// open existing level
				int choice = fileChooser.showOpenDialog(fileChooser);
				//fileChooser.setFileFilter(new FileFilter(""));

				if(choice == JFileChooser.APPROVE_OPTION)
				{
					File file = fileChooser.getSelectedFile();

					mapPanel.level = new Level(file);

					// Set tileset panel image
					tilesetPanel.setImage(0, mapPanel.level.getLayer(1).getImg());
					tilesetPanel.showLayer[0] = true;
					tilesetPanel.defaultSettings();

					// Set map panel images
					for(int n = 0; n < mapPanel.level.getNumOfLayers(); n++)
					{
						BufferedImage defMapImg = new BufferedImage(mapPanel.level.getLayer(n).getWidth() * 16, mapPanel.level.getLayer(n).getHeight() * 16, BufferedImage.TYPE_INT_ARGB);
						mapPanel.setImage(n, defMapImg);
						mapPanel.showLayer[n] = true;
					}

					if(mapPanel.drawAreaLayers.size() >= 1)
						mapPanel.paintOnLayer = 1; // We assume that layer 1 is the "walkable" layer

					int levelWidth;
					int levelHeight;
					int tile = 0;

					for(int n = 0; n < mapPanel.drawAreaLayers.size(); n++)
					{
						levelWidth = mapPanel.level.getLayer(n).getWidth() * 16;
						levelHeight = mapPanel.level.getLayer(n).getHeight() * 16;

						for(int i = 0, x = 0; i < levelWidth; i+=16, x++)
						{
							for(int j = 0, y = 0; j < levelHeight; j+=16, y++)
							{
								tile = mapPanel.level.getLayer(n).getTile(x, y);
								mapPanel.paintTile(n, tile, i, j, false);
							}
						}
					}

					toolbarPanel.defaultSettings();

					if(mapPanel.drawAreaLayers.size() > 0)
					{
						mapPanel.setPreferredSize(new Dimension(mapPanel.drawAreaLayers.get(0).getWidth(),mapPanel.drawAreaLayers.get(0).getHeight()));
						mapPanel.revalidate();
					}

					objectPanel.loadObjects();
				}
			}
		});
		fileSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// save current level
				if(mapPanel.level != null)
				{
					if(mapPanel.level.getFilePath() != null)
					{
						mapPanel.level.write(mapPanel.level.getFilePath());
					}
					else
					{
						// ask for the file name
						int choice = fileChooser.showSaveDialog(fileChooser);

						if(choice == JFileChooser.APPROVE_OPTION)
						{
							File file = fileChooser.getSelectedFile();
							mapPanel.level.setFilePath(file);
							mapPanel.level.write(mapPanel.level.getFilePath());
						}
					}
				}
			}
		});
		fileSaveAs.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(mapPanel.level != null)
				{
					// save current level under a different name
					int choice = fileChooser.showSaveDialog(fileChooser);

					if(choice == JFileChooser.APPROVE_OPTION)
					{
						File file = fileChooser.getSelectedFile();
						mapPanel.level.setFilePath(file);
						mapPanel.level.write(mapPanel.level.getFilePath());
					}
				}
			}
		});
		fileModify.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Modify current level
				if(mapPanel.level != null && showModifyLevelSetup())
				{

					for(int i = 0; i < mapPanel.level.getNumOfLayers(); i++)
					{
						BufferedImage defMapImg = new BufferedImage(mapPanel.level.getLayer(i).getWidth() * 16, mapPanel.level.getLayer(i).getHeight() * 16, BufferedImage.TYPE_INT_ARGB);
						mapPanel.setImage(i, defMapImg);
					}

					int levelWidth;
					int levelHeight;
					int tile = 0;

					for(int n = 0; n < mapPanel.drawAreaLayers.size(); n++)
					{
						levelWidth = mapPanel.level.getLayer(n).getWidth() * 16;
						levelHeight = mapPanel.level.getLayer(n).getHeight() * 16;

						for(int i = 0, x = 0; i < levelWidth; i+=16, x++)
						{
							for(int j = 0, y = 0; j < levelHeight; j+=16, y++)
							{
								tile = mapPanel.level.getLayer(n).getTile(x, y);
								mapPanel.paintTile(n, tile, i, j, false);
							}
						}
					}

					if(mapPanel.drawAreaLayers.size() > 0)
					{
						mapPanel.setPreferredSize(new Dimension(mapPanel.drawAreaLayers.get(0).getWidth(),mapPanel.drawAreaLayers.get(0).getHeight()));
						mapPanel.revalidate();
					}
				}
			}
		});
		fileQuit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// quit program
				System.out.printf("Pressed Quit\n"); // TODO
			}
		});
		runRunLevel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(mapPanel.level != null)
				{
					boolean playerObjPresent = false;

					ListIterator<GameObject> objsli = mapPanel.level.getObjectList().listIterator();
					while (objsli.hasNext())
					{
						if(objsli.next().getName().equals("player"))
							playerObjPresent = true;
					}

					if(!playerObjPresent) // Don't launch the level if player object is missing.
					{
						JOptionPane.showMessageDialog(runRunLevel, "Add a player object first.");
						return;
					}

					File path = new File(Data.getDataDirectory() + "/frog.jar");

					if(!path.exists() || path.isDirectory())
					{
						JOptionPane.showMessageDialog(runRunLevel, "ERROR: Missing game executable at location:\n" + path.getAbsolutePath(), "Game launch issue", JOptionPane.ERROR_MESSAGE);
					}

					// save current level to a temporary file
					mapPanel.level.write(new File(Data.getDataDirectory() + "/lvl.tmp"));

					// run level
					ProcessBuilder builder = new ProcessBuilder("java", "-jar", path.getAbsolutePath(), "-l", "./lvl.tmp", "-nojoy");
					builder.environment().put("LD_LIBRARY_PATH","lib");
					builder.directory(new File(Data.getDataDirectory()).getAbsoluteFile());

					builder.redirectErrorStream(true);
					File log = new File("runlog.tmp");
					builder.redirectOutput(ProcessBuilder.Redirect.to(log));

					Process proc;

					try
					{
						System.out.printf("Launching game... (log: %s)\n", log.getAbsolutePath());
						proc = builder.start();
						assert builder.redirectInput() == ProcessBuilder.Redirect.PIPE;
						assert builder.redirectOutput().file() == log;
						assert proc.getInputStream().read() == -1;
					}
					catch (IOException ioe)
					{
						System.out.printf("Failed to launch the game:\n%s\n", ioe.getMessage());
						JOptionPane.showMessageDialog(runRunLevel, "ERROR: " + ioe.getMessage(), "Game launch issue", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		helpAbout.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(helpAbout, 
				"\nFROG Level Editor version 0.0.1\n\n" +
				"Copyright © 2014 Artur Rojek\n" +
				"Licensed under LGPL v2 +", "About", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("./data/about.png"));
			}
		});
	}

	public void setPanels(MapPanel newMapPanel, TilesetPanel newTilesetPanel, ToolbarPanel newToolbarPanel, ObjectPanel newObjectPanel)
	{
		this.mapPanel = newMapPanel;
		this.tilesetPanel = newTilesetPanel;
		this.toolbarPanel = newToolbarPanel;
		this.objectPanel = newObjectPanel;
	}

	public MapPanel getMapPanel()
	{
		return this.mapPanel;
	}

	public void setSizeX(int size)
	{
		this.newSizeX = size;
	}

	public void setSizeY(int size)
	{
		this.newSizeY = size;
	}
	public int getSizeX()
	{
		return this.newSizeX;
	}

	public int getSizeY()
	{
		return this.newSizeY;
	}

	public boolean showNewLevelSetup()
	{
		NewLevelSetup dialog = new NewLevelSetup("Create level", this);
		//dialog.setPreferredSize(new Dimension(320, 240));
		dialog.setLocationRelativeTo(this);
		dialog.pack();
		dialog.setVisible(true);

		return dialog.getChoice();
	}

	public boolean showModifyLevelSetup()
	{
		ModifyLevelSetup dialog = new ModifyLevelSetup("Modify level", this);
		//dialog.setPreferredSize(new Dimension(320, 240));
		dialog.setLocationRelativeTo(this);
		dialog.pack();
		dialog.setVisible(true);

		return dialog.getChoice();
	}
}

class MapPanel extends DrawPanel implements KeyListener, MouseInputListener
{
	public Level level = null;
	private TilesetPanel tileset = null;
	private TileInfoPanel tileInfoPanel = null;
	private ObjectPanel objectPanel = null;
	private EditMode editMode = EditMode.MODE_TILE_EDIT;
	private boolean canPaint = false;
	private int paintX = 0;
	private int paintY = 0;
	private int lastPaintOnLayer = 1;
	private int lastTile = 0;
	private int [][] selectedArea = null;
	private int selectedAreaX;
	private int selectedAreaY;
	private int selectedAreaX2;
	private int selectedAreaY2;
	private boolean drawSelection = false;

	private GameObject selectedObject = null;
	private boolean draggingObject = false;
	private boolean objectIsNew = false;

	protected boolean showGrid = true;

	public EditMode getEditMode()
	{
		return this.editMode;
	}
	public void setEditMode(EditMode newEditMode)
	{
		this.editMode = newEditMode;
	}

	public void setObjectIsNew(boolean value)
	{
		this.objectIsNew = value;
	}

	public void paintTile(int layer, int num, int x, int y, boolean repaint)
	{
		if (this.tileset == null)
		{
			System.out.printf("Tileset is empty!\n");
			return;
		}
		if (this.level == null)
		{
			System.out.printf("No level loaded!\n");
			return;
		}
		if (layer >= this.drawAreaLayers.size())
		{
			System.out.printf("No such layer: %d!\n", layer);
			return;
		}

		int tileX = num%16;
		int tileY = num/16;

		super.blit(layer, this.tileset.getImage(0), x, y, x + 16, y + 16, tileX * 16, tileY * 16, tileX * 16 + 16, tileY * 16 + 16);

		if(repaint)
		{
			super.repaint();
		}
	}

	public void paintTile(int x, int y)
	{
		if (this.tileset == null)
		{
			System.out.printf("Tileset is empty!\n");
			return;
		}

		int tileNum = tileset.getSelY() * 16 + tileset.getSelX();

		this.level.getLayer(super.paintOnLayer).setTile(x/16, y/16, tileNum);

		super.blit(super.paintOnLayer, this.tileset.getImage(0), x, y, x + 16, y + 16, this.tileset.getSelX() * 16, this.tileset.getSelY() * 16, this.tileset.getSelX() * 16 + 16, this.tileset.getSelY() * 16 + 16);
		super.repaint();
	}

	public void selectArea(int x, int y, int w, int h)
	{
		selectedArea = new int[Math.abs(w-x) + (x >= w ? 1 : 0)][Math.abs(h-y) + (y >= h ? 1 : 0)];

		for(int j = 0; j < selectedArea[0].length; j++)
		{
			for(int i = 0; i < selectedArea.length; i++)
			{
				selectedArea[i][j] = this.level.getLayer(super.paintOnLayer).getTile((x >= w ? w+i : x+i), (y >= h ? h+j : y+j));
			}
		}
	}

	public void deleteSelectedArea(int x, int y, int w, int h)
	{
		int startX = (x >= w ? w : x);
		int startY = (y >= h ? h : y);
		int width = (x >= w ? x+1 : w);
		int height = (y >= h ? y+1 : h);

		if(startX < 0)
		{
			startX = 0;
		}
		if(startY < 0)
		{
			startY = 0;
		}

		for(int j = startY; j < height; j++)
		{
			if(j * 16 >= super.drawAreaLayers.get(super.paintOnLayer).getHeight())
				continue;

			for(int i = startX; i < width; i++)
			{
				if(i * 16 >= super.drawAreaLayers.get(super.paintOnLayer).getWidth())
					continue;

				this.level.getLayer(super.paintOnLayer).setTile(i, j, 0);

				int dx = i*16;
				int dy = j*16;

				super.blit(super.paintOnLayer, this.tileset.getImage(0), dx, dy, dx + 16, dy + 16, 0, 0, 16, 16);
			}
		}

		super.repaint();
	}

	public void pasteSelectedArea(int x, int y)
	{
		if(selectedArea == null)
			return;

		if(x < 0 || y < 0)
			return;

		for(int j = 0; j < selectedArea[0].length; j++)
		{
			if(y * 16 + j * 16 >= super.drawAreaLayers.get(super.paintOnLayer).getHeight())
				continue;

			for(int i = 0; i < selectedArea.length; i++)
			{
				if(x * 16 + i * 16 >= super.drawAreaLayers.get(super.paintOnLayer).getWidth())
					continue;

				this.level.getLayer(super.paintOnLayer).setTile(x+i, y+j, selectedArea[i][j]);

				int tileX = selectedArea[i][j] % 16;
				int tileY = selectedArea[i][j] / 16;
				int dx = x*16 + i*16;
				int dy = y*16 + j*16;

				super.blit(super.paintOnLayer, this.tileset.getImage(0), dx, dy, dx + 16, dy + 16, tileX * 16, tileY * 16, tileX * 16 + 16, tileY * 16 + 16);
			}
		}
		super.repaint();
	}

	public void setPanels(TilesetPanel newTileset, TileInfoPanel newTileInfoPanel, ObjectPanel newObjectPanel)
	{
		this.tileset = newTileset;
		this.tileInfoPanel = newTileInfoPanel;
		this.objectPanel = newObjectPanel;
	}

	private boolean isInMapArea(int x, int y)
	{
		if(super.drawAreaLayers.size() < 1)
			return false;

		if(super.drawAreaLayers.get(0) == null)
			return false;

		if (x < 0)
			return false;
		else if (x >= super.drawAreaLayers.get(0).getWidth())
			return false;
		else if (y < 0)
			return false;
		else if (y >= super.drawAreaLayers.get(0).getHeight())
			return false;
		else
		{
			return true;
		}
	}

	public void setSelectedObject(GameObject selectedObject)
	{
		this.selectedObject = selectedObject;
		this.repaint();
	}

	// key listener
	public void keyPressed(KeyEvent e)
	{
		boolean left = false;
		boolean right = false;
		boolean up = false;
		boolean down = false;

		int modifiers = e.getModifiers();

		switch(e.getKeyCode())
		{
			case KeyEvent.VK_A:
				left = true;
			break;
			case KeyEvent.VK_D:
				right = true;
			break;
			case KeyEvent.VK_W:
				up = true;
			break;
			case KeyEvent.VK_S:
				down = true;
			break;
			case KeyEvent.VK_SHIFT:	// Select
				if (this.editMode == EditMode.MODE_TILE_EDIT)
				{
					this.editMode = EditMode.MODE_TILE_SELECTION;
				}
			break;
			case KeyEvent.VK_X:	// Cut
				if (this.editMode == EditMode.MODE_TILE_EDIT && ((modifiers & InputEvent.CTRL_MASK) > 0))
				{
					selectArea(selectedAreaX, selectedAreaY, selectedAreaX2, selectedAreaY2);
					deleteSelectedArea(selectedAreaX, selectedAreaY, selectedAreaX2, selectedAreaY2);
					drawSelection = false;
				}
			break;
			case KeyEvent.VK_C:	// Copy
				if (this.editMode == EditMode.MODE_TILE_EDIT && ((modifiers & InputEvent.CTRL_MASK) > 0))
				{
					selectArea(selectedAreaX, selectedAreaY, selectedAreaX2, selectedAreaY2);
				}
			break;
			case KeyEvent.VK_V:	// Paste
				if (this.editMode == EditMode.MODE_TILE_EDIT && ((modifiers & InputEvent.CTRL_MASK) > 0))
				{
					Point pos = this.getMousePosition();

					if (pos != null)
					{
						int x = (int)pos.getX()/16;
						int y = (int)pos.getY()/16;

						pasteSelectedArea(x, y);
					}

					drawSelection = false;
				}
			break;
			case KeyEvent.VK_INSERT: // Unix style
				if ((this.editMode == EditMode.MODE_TILE_EDIT || this.editMode == EditMode.MODE_TILE_SELECTION))
				{
					if ((modifiers & InputEvent.CTRL_MASK) > 0)		// Copy
					{
						selectArea(selectedAreaX, selectedAreaY, selectedAreaX2, selectedAreaY2);
					}
					else if (((modifiers & InputEvent.SHIFT_MASK) > 0))	// Paste
					{
						Point pos = this.getMousePosition();

						if (pos != null)
						{
							int x = (int)pos.getX()/16;
							int y = (int)pos.getY()/16;

							pasteSelectedArea(x, y);
						}

						drawSelection = false;
					}
				}
			break;
			case KeyEvent.VK_DELETE: // Delete
				if (this.editMode == EditMode.MODE_TILE_EDIT)
				{
					deleteSelectedArea(selectedAreaX, selectedAreaY, selectedAreaX2, selectedAreaY2);
					drawSelection = false;
				}
			break;

			default:
			break;
		}

		if((left || right || up || down) && (this.level != null && this.level.getNumOfLayers() > 0 && this.tileset != null))
		{
			int x = this.tileset.getSelX();
			int y = this.tileset.getSelY();

			if(left)
				x--;
			if(right)
				x++;
			if(up)
				y--;
			if(down)
				y++;

			if(x < 0)
				x = 0;
			if(x > 15)
				x = 15;
			if(y < 0)
				y = 0;
			if(y > 15)
				y = 15;

			this.tileset.setSelX(x);
			this.tileset.setSelY(y);
			this.tileset.repaint();
			if(this.tileInfoPanel != null)
				this.tileInfoPanel.updateInfo(x, y);
		}
	}
	public void keyReleased(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_SHIFT:
				if (this.editMode == EditMode.MODE_TILE_SELECTION)
				{
					this.editMode = EditMode.MODE_TILE_EDIT;
				}
			break;

			default:
			break;
		}
	}
	public void keyTyped(KeyEvent e)
	{
	}

	// mouse listener
	public void mouseExited(MouseEvent e)
	{
	}
	public void mouseEntered(MouseEvent e)
	{
		this.requestFocusInWindow();
	}
	public void mouseReleased(MouseEvent e)
	{
		if(this.editMode == EditMode.MODE_TILE_EDIT)
		{
			switch(e.getModifiers())
			{
				case InputEvent.BUTTON1_MASK:
					this.canPaint = false;
				break;

				default:
				break;
			}
		}
		else if(this.editMode == EditMode.MODE_TILE_SELECTION)
		{
				int modifiers = e.getModifiers();

				if((modifiers & InputEvent.BUTTON1_MASK) > 0)
				{
					if(this.level != null && this.level.getNumOfLayers() > 0 && this.tileset != null)
					{
						selectedAreaX2 = e.getX()/16+1;
						selectedAreaY2 = e.getY()/16+1;

						if(selectedAreaX2 <= selectedAreaX)
							selectedAreaX2 -= 1;
						if(selectedAreaY2 <= selectedAreaY)
							selectedAreaY2 -= 1;
						this.repaint();
					}
				}
		}
		else if(this.editMode == EditMode.MODE_OBJECT_EDIT)
		{
			switch(e.getModifiers())
			{
				case InputEvent.BUTTON1_MASK:
				{
					if(this.draggingObject)
						this.draggingObject = false;
				}
				break;

				default:
				break;
			}
		}
	}
	public void mousePressed(MouseEvent e)
	{
		if(this.editMode == EditMode.MODE_TILE_EDIT)
		{
			if(this.level != null && this.level.getNumOfLayers() > 0 && this.tileset != null)
			{
				switch(e.getModifiers())
				{
					case InputEvent.BUTTON1_MASK:
					{
						this.canPaint = true;
						int curX = e.getX()/16*16;
						int curY = e.getY()/16*16;
						int curTile = this.tileset.getSelY() * 16 + this.tileset.getSelX();

						if(this.paintX != curX || this.paintY != curY || this.lastPaintOnLayer != super.paintOnLayer || this.lastTile != curTile)
						{
							if((this.canPaint) && (this.isInMapArea(e.getX(), e.getY())))
							{
								this.paintX = e.getX()/16*16;
								this.paintY = e.getY()/16*16;
								this.lastPaintOnLayer = super.paintOnLayer;
								this.lastTile = curTile;
								this.paintTile(this.paintX, this.paintY);
							}
						}
						drawSelection = false;
					}
					break;
					case InputEvent.BUTTON3_MASK:
					{
						int tile = this.level.getLayer(super.paintOnLayer).getTile(e.getX()/16, e.getY()/16);
						int x = tile % 16;
						int y = tile / 16;

						this.tileset.setSelX(x);
						this.tileset.setSelY(y);
						this.tileset.repaint();
						if(this.tileInfoPanel != null)
							this.tileInfoPanel.updateInfo(x, y);
					}
					break;

					default:
					break;
				}
			}
		}
		else if(this.editMode == EditMode.MODE_TILE_SELECTION)
		{
			if(this.level != null && this.level.getNumOfLayers() > 0 && this.tileset != null)
			{
				int modifiers = e.getModifiers();

				if((modifiers & InputEvent.BUTTON1_MASK) > 0)
				{
					selectedAreaX = e.getX()/16;
					selectedAreaY = e.getY()/16;

					drawSelection = true;
					this.repaint();
				}
			}
		}
		else if(this.editMode == EditMode.MODE_OBJECT_EDIT)
		{
			if(this.level != null && this.objectPanel != null)
			{
				switch(e.getModifiers())
				{
					case InputEvent.BUTTON1_MASK:
					{
						int objX;
						int objY;
						int curX = e.getX();
						int curY = e.getY();

						if(!this.draggingObject)
						{
							for(GameObject curObj : this.level.getObjectList())
							{
								if(curX >= curObj.getX() && curX <= curObj.getX() + curObj.getW() && curY >= curObj.getY() && curY <= curObj.getY() + curObj.getH())
								{
									this.selectedObject = curObj;
									this.draggingObject = true;
									this.objectPanel.setSelectedObject(curObj);
									this.repaint();
									break;
								}
								else if(this.objectIsNew)
								{
									this.objectIsNew = false;
									this.draggingObject = true;

									curX = e.getX()/16*16;
									curY = e.getY()/16*16;

									if(super.drawAreaLayers.size() < 1)
										break;

									if(super.drawAreaLayers.get(0) == null)
										break;

									if(curX < (this.selectedObject.getW() - 1)/16*16)
										curX = (this.selectedObject.getW() - 1)/16*16;
									else if(curX >= super.drawAreaLayers.get(0).getWidth())
										curX = super.drawAreaLayers.get(0).getWidth() - 16;

									if(curY < (this.selectedObject.getH() - 1)/16*16)
										curY = (this.selectedObject.getH() - 1)/16*16;
									else if(curY >= super.drawAreaLayers.get(0).getHeight())
										curY = super.drawAreaLayers.get(0).getHeight() - 16;

									// center at the bottom-left corner
									if(this.selectedObject.getName().equals("diamond")) // TODO: Solve this by checking object type
									{
										this.selectedObject.setX(curX + 8 - this.selectedObject.getW()/2);
										this.selectedObject.setY(curY + 8 - this.selectedObject.getH()/2);
									}
									else
									{
										this.selectedObject.setX(curX + 16 - this.selectedObject.getW());
										this.selectedObject.setY(curY + 16 - this.selectedObject.getH());
									}
									this.repaint();
								}
							}
						}
					}
					break;
					case InputEvent.BUTTON3_MASK:
					break;

					default:
					break;
				}

				drawSelection = false;
			}
		}
	}
	public void mouseClicked(MouseEvent e)
	{
	}
	public void mouseMoved(MouseEvent e)
	{
	}
	public void mouseDragged(MouseEvent e)
	{
		if(this.editMode == EditMode.MODE_TILE_EDIT)
		{
			switch(e.getModifiers())
			{
				case InputEvent.BUTTON1_MASK:
				{
					int curX = e.getX()/16*16;
					int curY = e.getY()/16*16;
					int curTile = this.tileset.getSelY() * 16 + this.tileset.getSelX();

					if(this.paintX != curX || this.paintY != curY || this.lastPaintOnLayer != super.paintOnLayer || this.lastTile != curTile)
					{
						if((this.canPaint) && (this.isInMapArea(e.getX(), e.getY())))
						{
							this.paintX = curX;
							this.paintY = curY;
							this.lastPaintOnLayer = super.paintOnLayer;
							this.lastTile = curTile;
							this.paintTile(this.paintX, this.paintY);
						}
					}
				}
				break;

				default:
				break;
			}
		}
		else if(this.editMode == EditMode.MODE_TILE_SELECTION)
		{
				int modifiers = e.getModifiers();

				if((modifiers & InputEvent.BUTTON1_MASK) > 0)
				{
					if(this.level != null && this.level.getNumOfLayers() > 0 && this.tileset != null)
					{
						selectedAreaX2 = e.getX()/16+1;
						selectedAreaY2 = e.getY()/16+1;

						this.repaint();
					}
				}
		}
		else if(this.editMode == EditMode.MODE_OBJECT_EDIT)
		{
			if(this.level != null && this.objectPanel != null)
			{
				switch(e.getModifiers())
				{
					case InputEvent.BUTTON1_MASK:
					{
						if(this.selectedObject != null && this.draggingObject)
						{
							int curX = e.getX()/16*16;
							int curY = e.getY()/16*16;

							if(super.drawAreaLayers.size() < 1)
								break;

							if(super.drawAreaLayers.get(0) == null)
								break;

							if(curX < (this.selectedObject.getW() - 1)/16*16)
								curX = (this.selectedObject.getW() - 1)/16*16;
							else if(curX >= super.drawAreaLayers.get(0).getWidth())
								curX = super.drawAreaLayers.get(0).getWidth() - 16;

							if(curY < (this.selectedObject.getH() - 1)/16*16)
								curY = (this.selectedObject.getH() - 1)/16*16;
							else if(curY >= super.drawAreaLayers.get(0).getHeight())
								curY = super.drawAreaLayers.get(0).getHeight() - 16;

							// center at the bottom-left corner
							if(this.selectedObject.getName().equals("diamond")) // TODO: Solve this by checking object type
							{
								this.selectedObject.setX(curX + 8 - this.selectedObject.getW()/2);
								this.selectedObject.setY(curY + 8 - this.selectedObject.getH()/2);
							}
							else
							{
								this.selectedObject.setX(curX + 16 - this.selectedObject.getW());
								this.selectedObject.setY(curY + 16 - this.selectedObject.getH());
							}
							this.repaint();
						}
					}
					break;
					case InputEvent.BUTTON3_MASK:
					break;

					default:
					break;
				}
			}
		}
	}

	protected void draw(Graphics g)
	{
		// draw the level grid
		if(this.showGrid)
		{
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(new Color(0, 0 ,0, 100));

			if(this.level != null && this.level.getNumOfLayers() > 0)
			{
				for(int i = 0; i <= this.level.getLayer(0).getWidth(); i++)
				{
					// vertical
					g2d.drawLine(i * 16, 0, i * 16, this.level.getLayer(0).getHeight() * 16);
				}
				for(int j = 0; j <= this.level.getLayer(0).getHeight(); j++)
				{
					// horizontal
					g2d.drawLine(0, j * 16, this.level.getLayer(0).getWidth() * 16, j * 16);
				}
			}
		}

		if (this.drawSelection)
		{
			int curX = 0;
			int curY = 0;
			int x1 = selectedAreaX * 16;
			int y1 = selectedAreaY * 16;
			int x2;
			int y2;

			if(this.editMode == EditMode.MODE_TILE_SELECTION)
			{
				Point pos = this.getMousePosition();

				if(pos != null)
				{
					curX = (int)pos.getX()/16*16 + 16;
					curY = (int)pos.getY()/16*16 + 16;

					x2 = curX;
					y2 = curY;
				}
				else
				{
					x2 = selectedAreaX2 * 16;
					y2 = selectedAreaY2 * 16;
				}
			}
			else
			{
				x2 = selectedAreaX2 * 16;
				y2 = selectedAreaY2 * 16;
			}

			if(x1 < 0)
				x1 = 0;
			if(y1 < 0)
				y1 = 0;
			if(x1 >= this.level.getLayer(0).getWidth() * 16)
				x1 = this.level.getLayer(0).getWidth() * 16;
			if(y1 >= this.level.getLayer(0).getHeight() * 16)
				y1 = this.level.getLayer(0).getHeight() * 16;

			if(x2 < 0)
				x2 = 0;
			if(y2 < 0)
				y2 = 0;
			if(x2 >= this.level.getLayer(0).getWidth() * 16)
				x2 = this.level.getLayer(0).getWidth() * 16;
			if(y2 >= this.level.getLayer(0).getHeight() * 16)
				y2 = this.level.getLayer(0).getHeight() * 16;

			if(x2 <= x1)
			{
				x1 += 16;
				x2 -= 16;
			}
			if(y2 <= y1)
			{
				y1 += 16;
				y2 -= 16;
			}

			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(Color.yellow);
			// top
			g2d.drawLine(x1, y1, x2, y1);
			// bottom
			g2d.drawLine(x1, y2, x2, y2);
			// left
			g2d.drawLine(x1, y1, x1, y2);
			// right
			g2d.drawLine(x2, y1, x2, y2);
		}
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		this.draw(g);

		// temp
		if(this.editMode == EditMode.MODE_OBJECT_EDIT)
		{
			if(this.level != null)
			{
				for(GameObject curObj : this.level.getObjectList())
				{
					Graphics2D g2d = (Graphics2D)g;

					if(curObj.getDirection())
						g2d.drawImage(curObj.getTile(1), curObj.getX() + curObj.getOffsetRightX(), curObj.getY() + curObj.getOffsetRightY(), null);
					else
						g2d.drawImage(curObj.getTile(0), curObj.getX() + curObj.getOffsetLeftX(), curObj.getY() + curObj.getOffsetLeftY(), null);

					if(curObj == this.selectedObject)
						g2d.setColor(Color.GREEN);
					else
						g2d.setColor(Color.RED);

					// draw rectangle
					g2d.drawLine(curObj.getX(), curObj.getY(), curObj.getX() + curObj.getW(), curObj.getY());
					g2d.drawLine(curObj.getX(), curObj.getY() + curObj.getH(), curObj.getX() + curObj.getW(), curObj.getY() + curObj.getH());
					g2d.drawLine(curObj.getX(), curObj.getY(), curObj.getX(), curObj.getY() + curObj.getH());
					g2d.drawLine(curObj.getX() + curObj.getW(), curObj.getY(), curObj.getX() + curObj.getW(), curObj.getY() + curObj.getH());
//					// draw diagonal line
//					if(curObj.getDirection())
//						g2d.drawLine(curObj.getX(), curObj.getY(), curObj.getX() + curObj.getTileW(), curObj.getY() + curObj.getTileH());
//					else
//						g2d.drawLine(curObj.getX() + curObj.getTileW(), curObj.getY(), curObj.getX(), curObj.getY() + curObj.getTileH());
				}
			}
		}
	}
}
class TilesetPanel extends DrawPanel implements MouseInputListener 
{
	private TileInfoPanel tileInfoPanel = null;
	private int selX = 0;
	private int selY = 0;

	public int getSelX()
	{
		return this.selX;
	}
	public int getSelY()
	{
		return this.selY;
	}
	public void setSelX(int value)
	{
		this.selX = value;
	}
	public void setSelY(int value)
	{
		this.selY = value;
	}

	// mouse listener
	public void mouseExited(MouseEvent e)
	{
	}
	public void mouseEntered(MouseEvent e)
	{
	}
	public void mouseReleased(MouseEvent e)
	{
	}
	public void mousePressed(MouseEvent e)
	{
	}
	public void mouseClicked(MouseEvent e)
	{
		if(super.drawAreaLayers.size() > 0 && super.drawAreaLayers.get(0) != null)
		{
			if(e.getX() > 0 && e.getX() < super.drawAreaLayers.get(0).getWidth() && e.getY() > 0 && e.getY() < super.drawAreaLayers.get(0).getHeight())
			{
				this.selX = e.getX()/16;
				this.selY = e.getY()/16;

				this.tileInfoPanel.updateInfo(this.selX, this.selY);

				super.repaint();
			}
		}
	}
	public void mouseMoved(MouseEvent e)
	{
	}
	public void mouseDragged(MouseEvent e)
	{
		if(super.drawAreaLayers.size() > 0 && super.drawAreaLayers.get(0) != null)
		{
			if(e.getX() > 0 && e.getX() < super.drawAreaLayers.get(0).getWidth() && e.getY() > 0 && e.getY() < super.drawAreaLayers.get(0).getHeight())
			{
				this.selX = e.getX()/16;
				this.selY = e.getY()/16;

				this.tileInfoPanel.updateInfo(this.selX, this.selY);

				super.repaint();
			}
		}
	}

	protected void draw(Graphics g)
	{
		if(super.drawAreaLayers.size() > 0)
		{
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(Color.yellow);
			// top
			g2d.drawLine(this.selX * 16, this.selY * 16, this.selX * 16 + 16, this.selY * 16);
			// bottom
			g2d.drawLine(this.selX * 16, this.selY * 16 + 16, this.selX * 16 + 16, this.selY * 16 + 16);
			// left
			g2d.drawLine(this.selX * 16, this.selY * 16, this.selX * 16, this.selY * 16 + 16);
			// right
			g2d.drawLine(this.selX * 16 + 16, this.selY * 16, this.selX * 16 + 16, this.selY * 16 + 16);
		}
	}

	public void setTileInfoPanel(TileInfoPanel newInfoPanel)
	{
		this.tileInfoPanel = newInfoPanel;
	}

	public void defaultSettings()
	{
		this.selX = 0;
		this.selY = 0;
		this.tileInfoPanel.updateInfo(this.selX, this.selY);
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		this.draw(g);
	}
}

class DrawPanel extends JPanel
{
	//protected BufferedImage drawArea = null;
	protected LinkedList<BufferedImage> drawAreaLayers = new LinkedList<BufferedImage>();
	protected boolean [] showLayer = new boolean[10]; // Let's hardcode the max number of layers to display for now.
	protected int paintOnLayer = 0;

	public LinkedList<BufferedImage> getLayers()
	{
		return this.drawAreaLayers;
	}

	public BufferedImage getImage(int layer)
	{
		return this.drawAreaLayers.get(layer);
	}

	public void setImage(int layer, BufferedImage src)
	{
		if(layer >= this.drawAreaLayers.size())
		{
			this.drawAreaLayers.push(src);
		}
		else
		{
			this.drawAreaLayers.set(layer, src);
		}
		super.repaint();
	}

	public void loadImage(int layer, String fileName)
	{
		if(fileName.equals(""))
			return;

		try
		{
			this.drawAreaLayers.set(layer, ImageIO.read(this.getClass().getResource("./data/" + fileName)));
		}
		catch (IOException e)
		{
			System.out.printf("ERROR: Failed to load image!");
		}
	}

	public void blit(int layer, BufferedImage src, int destx1, int desty1, int destx2, int desty2, int srcx1, int srcy1, int srcx2, int srcy2)
	{
		if(this.drawAreaLayers.get(layer) != null && src != null)
		{
			BufferedImage dest = this.drawAreaLayers.get(layer);

			dest.createGraphics().drawImage(src, destx1, desty1, destx2, desty2, srcx1, srcy1, srcx2, srcy2, null);

			WritableRaster raster = dest.getRaster();
			for(int j = desty1; j < desty2; j++)
			{
				for(int i = destx1; i < destx2; i++)
				{
					int [] pixels = raster.getPixel(i, j, (int[]) null);

					if(pixels[0] == 255 && pixels[1] == 0 && pixels[2] == 255) // magenta
					{
						pixels[3] = 0;
						raster.setPixel(i, j, pixels);
					}
				}
			}

		}
	}

	protected void draw(int layer, Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;

		if(layer < this.drawAreaLayers.size() && this.drawAreaLayers.get(layer) != null)
		{
			g2d.drawImage(drawAreaLayers.get(layer), 0, 0, null);
		}
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for(int i = 0; i < this.drawAreaLayers.size(); i++)
		{
			if(this.showLayer[this.drawAreaLayers.size() - 1 - i])
			{
				this.draw(this.drawAreaLayers.size() - 1 - i, g);
			}
		}
	}
}

class TileInfoPanel extends JPanel
{
	private JLabel infoLabel = new JLabel();
	private String infoText;

	TileInfoPanel()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.infoLabel.setHorizontalAlignment(JLabel.LEFT);
		this.defaultInfo();
		this.add(infoLabel);
		this.setVisible(true);
	}

	public void defaultInfo()
	{
		infoText = " ";
		this.infoLabel.setText(this.infoText);
	}

	public void updateInfo(int tileX, int tileY)
	{
		int tile = tileY * 16 + tileX;

		this.infoText = " Tile #" + tile + " (" + tileX + "," + tileY + ")";
		this.infoLabel.setText(this.infoText);
	}
}

class ToolbarPanel extends JPanel
{
	private MapPanel mapPanel = null;
	private TilesetPanel tilesetPanel = null;
	private ButtonGroup radioLayers = new ButtonGroup();
	private JRadioButton drawOnBackground = new JRadioButton("BGD");
	private JRadioButton drawOnMiddleground = new JRadioButton("MGD");
	private JRadioButton drawOnForeground = new JRadioButton("FGD");
	private JCheckBox showBackground = new JCheckBox("BGD");
	private JCheckBox showMiddleground = new JCheckBox("MGD");
	private JCheckBox showForeground = new JCheckBox("FGD");
	private JCheckBox showGrid = new JCheckBox("Tile grid");
	private JLabel paintLabel = new JLabel("Draw on:");
	private JLabel showLabel = new JLabel("Show:");

	public ToolbarPanel()
	{
		this.setLayout(new GridLayout(6, 2));

		this.radioLayers.add(drawOnBackground);
		this.radioLayers.add(drawOnMiddleground);
		this.radioLayers.add(drawOnForeground);

		this.defaultSettings();

		this.add(paintLabel);
		this.add(showLabel);
		this.add(drawOnBackground);
		this.add(showBackground);
		this.add(drawOnMiddleground);
		this.add(showMiddleground);
		this.add(drawOnForeground);
		this.add(showForeground);
		this.add(new JLabel("")); // add an empty cell
		this.add(showGrid);

		drawOnBackground.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				mapPanel.paintOnLayer = 2;
			}
		});
		drawOnMiddleground.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				mapPanel.paintOnLayer = 1;
			}
		});
		drawOnForeground.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				mapPanel.paintOnLayer = 0;
			}
		});

		showBackground.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				switch(e.getStateChange())
				{
					case ItemEvent.SELECTED:
						drawOnBackground.setEnabled(true);
						mapPanel.showLayer[2] = true;
						mapPanel.repaint();
					break;
					case ItemEvent.DESELECTED:
						drawOnBackground.setEnabled(false);
						if(drawOnBackground.isSelected())
							radioLayers.clearSelection();
						mapPanel.showLayer[2] = false;
						mapPanel.repaint();
					break;

					default:
					break;
				}
			}
		});
		showMiddleground.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				switch(e.getStateChange())
				{
					case ItemEvent.SELECTED:
						drawOnMiddleground.setEnabled(true);
						mapPanel.showLayer[1] = true;
						mapPanel.repaint();
					break;
					case ItemEvent.DESELECTED:
						drawOnMiddleground.setEnabled(false);
						if(drawOnMiddleground.isSelected())
							radioLayers.clearSelection();
						mapPanel.showLayer[1] = false;
						mapPanel.repaint();
					break;

					default:
					break;
				}
			}
		});
		showForeground.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				switch(e.getStateChange())
				{
					case ItemEvent.SELECTED:
						drawOnForeground.setEnabled(true);
						mapPanel.showLayer[0] = true;
						mapPanel.repaint();
					break;
					case ItemEvent.DESELECTED:
						drawOnForeground.setEnabled(false);
						if(drawOnForeground.isSelected())
							radioLayers.clearSelection();
						mapPanel.showLayer[0] = false;
						mapPanel.repaint();
					break;

					default:
					break;
				}
			}
		});
		showGrid.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				switch(e.getStateChange())
				{
					case ItemEvent.SELECTED:
						mapPanel.showGrid = true;
						mapPanel.repaint();
					break;
					case ItemEvent.DESELECTED:
						mapPanel.showGrid = false;
						mapPanel.repaint();
					break;

					default:
					break;
				}
			}
		});
	}

	public void defaultSettings()
	{
		this.drawOnMiddleground.setSelected(true);
		this.showBackground.setSelected(true);
		this.showMiddleground.setSelected(true);
		this.showForeground.setSelected(true);
		this.showGrid.setSelected(true);
	}

	public void setPanels(MapPanel newMapPanel, TilesetPanel newTilesetPanel)
	{
		this.mapPanel = newMapPanel;
		this.tilesetPanel = newTilesetPanel;
	}
}

class ObjectPanel extends JPanel
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

class ToolsetTabPane extends JTabbedPane
{
	private MapPanel mapPanel = null;

	public void setMapPanel(MapPanel newMapPanel)
	{
		this.mapPanel = newMapPanel;

		this.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				switch(getSelectedIndex())
				{
					case 0:
						mapPanel.setEditMode(EditMode.MODE_TILE_EDIT);
					break;
					case 1:
						mapPanel.setEditMode(EditMode.MODE_OBJECT_EDIT);
					break;
					case 2:
						mapPanel.setEditMode(EditMode.MODE_LEVEL_EDIT);
					break;

					default:
					break;
				}

				mapPanel.repaint();
			}
		});
	}
}

public class Editor
{
	private static void createGui()
	{
		JFrame frame = new JFrame("FROG Level Editor");
		ImageIcon icon = new ImageIcon("./data/icon.png");
		frame.setIconImage(icon.getImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel windowContainer = new JPanel(new BorderLayout());
		windowContainer.setLayout(new BoxLayout(windowContainer, BoxLayout.LINE_AXIS));

		MapPanel mapPanel = new MapPanel();
		ToolsetTabPane toolsetTabPane = new ToolsetTabPane();
		JPanel toolPanel = new JPanel();
		ObjectPanel objectPanel = new ObjectPanel();
		JPanel levelSettingsPanel = new JPanel();
		TilesetPanel tilesetPanel = new TilesetPanel();
		TileInfoPanel tileInfoPanel = new TileInfoPanel();
		ToolbarPanel toolbarPanel = new ToolbarPanel();
		objectPanel.setPanels(mapPanel);
		tilesetPanel.setTileInfoPanel(tileInfoPanel);
		toolbarPanel.setPanels(mapPanel, tilesetPanel);

		toolsetTabPane.addTab("Tiles", null, toolPanel, "Tile edit mode");
		toolsetTabPane.addTab("Objects", null, objectPanel, "Object edit mode");
		toolsetTabPane.addTab("Level", null, levelSettingsPanel, "Level settings");
		toolsetTabPane.setMapPanel(mapPanel);

		toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.PAGE_AXIS));

		JScrollPane scrollFrame = new JScrollPane(mapPanel);
		mapPanel.setAutoscrolls(true);
		mapPanel.setFocusable(true);

		mapPanel.addKeyListener(mapPanel);
		mapPanel.addMouseListener(mapPanel);
		mapPanel.addMouseMotionListener(mapPanel);
		tilesetPanel.addMouseListener(tilesetPanel);
		tilesetPanel.addMouseMotionListener(tilesetPanel);

		mapPanel.setPreferredSize(new Dimension(640,480));
		tileInfoPanel.setMinimumSize(new Dimension(256,15));
		tileInfoPanel.setMaximumSize(new Dimension(256,15));
		toolsetTabPane.setPreferredSize(new Dimension(256,256));
		toolsetTabPane.setMinimumSize(new Dimension(256,256));
		toolsetTabPane.setMaximumSize(new Dimension(256,1200));
		toolPanel.setPreferredSize(new Dimension(256,256));
		toolPanel.setMinimumSize(new Dimension(256,256));
		toolPanel.setMaximumSize(new Dimension(256,1200));
		tilesetPanel.setPreferredSize(new Dimension(256,256));
		tilesetPanel.setMinimumSize(new Dimension(256,256));
		tilesetPanel.setMaximumSize(new Dimension(256,256));
		toolbarPanel.setPreferredSize(new Dimension(256,150));
		toolbarPanel.setMinimumSize(new Dimension(256,80));
		toolbarPanel.setMaximumSize(new Dimension(256,150));
		mapPanel.setPanels(tilesetPanel, tileInfoPanel, objectPanel);


		toolPanel.add(tilesetPanel, BorderLayout.NORTH);
		toolPanel.add(tileInfoPanel);
		toolPanel.add(toolbarPanel, BorderLayout.NORTH);

		windowContainer.add(scrollFrame);
		windowContainer.add(toolsetTabPane);
		//windowContainer.add(toolPanel);

		mapPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		tilesetPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		//tileInfoPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		// menu
		Menu menuBar = new Menu();
		menuBar.setPanels(mapPanel, tilesetPanel, toolbarPanel, objectPanel);

		frame.getContentPane().add(menuBar, BorderLayout.NORTH);
		frame.getContentPane().add(windowContainer);

		frame.setLocationRelativeTo(null);

		// display the window
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String [] args)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				createGui();
			}
		});
	}
}
