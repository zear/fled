import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import java.util.LinkedList;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

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

class Menu extends JMenuBar
{
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem fileNew = new JMenuItem("New level");
	private JMenuItem fileOpen = new JMenuItem("Open level");
	private JMenuItem fileSave = new JMenuItem("Save level");
	private JMenuItem fileSaveAs = new JMenuItem("Save level as...");
	private JMenuItem fileQuit = new JMenuItem("Quit editor");
	private JMenu runMenu = new JMenu("Run");
	private JMenuItem runSetExec = new JMenuItem("Set executable location");
	private JMenuItem runRunLevel = new JMenuItem("Run level");
	private JMenu helpMenu = new JMenu("Help");
	private JMenuItem helpAbout = new JMenuItem("About");

	private JFileChooser fileChooser = new JFileChooser(".");

	private MapPanel mapPanel = null;
	private TilesetPanel tilesetPanel = null;
	private ToolbarPanel toolbarPanel = null;

	private int newSizeX = 20;
	private int newSizeY = 20;

	public Menu()
	{
		this.add(fileMenu);
		fileMenu.add(fileNew);
		fileMenu.add(fileOpen);
		fileMenu.add(fileSave);
		fileMenu.add(fileSaveAs);
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
					File path = new File(Data.getDataDirectory() + "/frog.jar");

					// save current level to a temporary file
					mapPanel.level.write(new File(Data.getDataDirectory() + "/lvl.tmp"));

					// run level
					ProcessBuilder builder = new ProcessBuilder("java", "-jar", path.getAbsolutePath(), "-l", "./lvl.tmp", "-nojoy");
					builder.environment().put("LD_LIBRARY_PATH","lib");
					builder.directory(new File(Data.getDataDirectory()).getAbsoluteFile());

					builder.redirectErrorStream(true);
					File log = new File("runlog.tmp");
					builder.redirectOutput(ProcessBuilder.Redirect.appendTo(log));

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
						System.out.printf("Failed to launch the game due to:\n%s\n", ioe.getMessage());
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

	public void setPanels(MapPanel newMapPanel, TilesetPanel newTilesetPanel, ToolbarPanel newToolbarPanel)
	{
		this.mapPanel = newMapPanel;
		this.tilesetPanel = newTilesetPanel;
		this.toolbarPanel = newToolbarPanel;
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
}

class MapPanel extends DrawPanel implements MouseInputListener
{
	public Level level = null;
	private TilesetPanel tileset = null;
	private TileInfoPanel tileInfoPanel = null;
	private boolean canPaint = false;
	private int paintX = 0;
	private int paintY = 0;
	private int lastPaintOnLayer = 1;
	private int lastTile = 0;
	protected boolean showGrid = true;

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

	public void setPanels(TilesetPanel newTileset, TileInfoPanel newTileInfoPanel)
	{
		this.tileset = newTileset;
		this.tileInfoPanel = newTileInfoPanel;
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

	// mouse listener
	public void mouseExited(MouseEvent e)
	{
	}
	public void mouseEntered(MouseEvent e)
	{
	}
	public void mouseReleased(MouseEvent e)
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
	public void mousePressed(MouseEvent e)
	{
		if(this.level != null && this.level.getNumOfLayers() > 0 && this.tileset != null)
		{
			switch(e.getModifiers())
			{
				case InputEvent.BUTTON1_MASK:
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
				break;
				case InputEvent.BUTTON3_MASK:
					int tile = this.level.getLayer(super.paintOnLayer).getTile(e.getX()/16, e.getY()/16);
					int x = tile % 16;
					int y = tile / 16;

					this.tileset.setSelX(x);
					this.tileset.setSelY(y);
					this.tileset.repaint();
					if(this.tileInfoPanel != null)
						this.tileInfoPanel.updateInfo(x, y);
				break;

				default:
				break;
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
		switch(e.getModifiers())
		{
			case InputEvent.BUTTON1_MASK:
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
			break;

			default:
			break;
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
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		this.draw(g);
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
		JPanel toolPanel = new JPanel();
		TilesetPanel tilesetPanel = new TilesetPanel();
		TileInfoPanel tileInfoPanel = new TileInfoPanel();
		ToolbarPanel toolbarPanel = new ToolbarPanel();
		tilesetPanel.setTileInfoPanel(tileInfoPanel);
		toolbarPanel.setPanels(mapPanel, tilesetPanel);

		toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.PAGE_AXIS));

		JScrollPane scrollFrame = new JScrollPane(mapPanel);
		mapPanel.setAutoscrolls(true);

		mapPanel.addMouseListener(mapPanel);
		mapPanel.addMouseMotionListener(mapPanel);
		tilesetPanel.addMouseListener(tilesetPanel);
		tilesetPanel.addMouseMotionListener(tilesetPanel);

		mapPanel.setPreferredSize(new Dimension(640,480));
		tileInfoPanel.setMinimumSize(new Dimension(256,15));
		tileInfoPanel.setMaximumSize(new Dimension(256,15));
		toolPanel.setPreferredSize(new Dimension(256,256));
		toolPanel.setMinimumSize(new Dimension(256,256));
		toolPanel.setMaximumSize(new Dimension(256,1200));
		tilesetPanel.setPreferredSize(new Dimension(256,256));
		tilesetPanel.setMinimumSize(new Dimension(256,256));
		tilesetPanel.setMaximumSize(new Dimension(256,256));
		toolbarPanel.setPreferredSize(new Dimension(256,150));
		toolbarPanel.setMinimumSize(new Dimension(256,80));
		toolbarPanel.setMaximumSize(new Dimension(256,150));
		mapPanel.setPanels(tilesetPanel, tileInfoPanel);


		toolPanel.add(tilesetPanel, BorderLayout.NORTH);
		toolPanel.add(tileInfoPanel);
		toolPanel.add(toolbarPanel, BorderLayout.NORTH);

		windowContainer.add(scrollFrame);
		windowContainer.add(toolPanel);

		mapPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		tilesetPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		//tileInfoPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		// menu
		Menu menuBar = new Menu();
		menuBar.setPanels(mapPanel, tilesetPanel, toolbarPanel);

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
