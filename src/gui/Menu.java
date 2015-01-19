package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.io.File;
import java.util.ListIterator;

import level.*;
import util.*;

public class Menu extends JMenuBar
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
	private FileFilter levelFilter = new FileNameExtensionFilter("FROG level file", "lvl");
	private FileFilter execFilter = new FileNameExtensionFilter("JAR file", "jar");

	private JFrame frame = null;
	private MapPanel mapPanel = null;
	private TilesetPanel tilesetPanel = null;
	private ToolbarPanel toolbarPanel = null;
	private ObjectPanel objectPanel = null;

	private int newSizeX = 20;
	private int newSizeY = 20;

	private String execFile = "frog.jar"; // Default location.

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
				if (showNewLevelSetup())
				{
					try
					{
						mapPanel.level = new Level(newSizeX, newSizeY);
					}
					catch (IOException ex)
					{
						JOptionPane.showMessageDialog(fileNew, ex.getMessage(), "Failed to create level file", JOptionPane.ERROR_MESSAGE);
						return;
					}

					tilesetPanel.setImage(0, mapPanel.level.getLayer(1).getImg());
					tilesetPanel.showLayer[0] = true;
					tilesetPanel.defaultSettings();

					for (int i = 0; i < mapPanel.level.getNumOfLayers(); i++)
					{
						BufferedImage defMapImg = new BufferedImage(mapPanel.level.getLayer(i).getWidth() * 16, mapPanel.level.getLayer(i).getHeight() * 16, BufferedImage.TYPE_INT_ARGB);
						mapPanel.setImage(i, defMapImg);
						mapPanel.showLayer[i] = true;
					}

					if (mapPanel.drawAreaLayers.size() >= 1)
						mapPanel.paintOnLayer = 1; // We assume that layer 1 is the "walkable" layer

					int levelWidth;
					int levelHeight;
					int tile = 0;

					for (int n = 0; n < mapPanel.drawAreaLayers.size(); n++)
					{
						levelWidth = mapPanel.level.getLayer(n).getWidth() * 16;
						levelHeight = mapPanel.level.getLayer(n).getHeight() * 16;

						for (int i = 0, x = 0; i < levelWidth; i+=16, x++)
						{
							for (int j = 0, y = 0; j < levelHeight; j+=16, y++)
							{
								tile = mapPanel.level.getLayer(n).getTile(x, y);
								mapPanel.paintTile(n, tile, i, j, false);
							}
						}
					}

					toolbarPanel.defaultSettings();

					if (mapPanel.drawAreaLayers.size() > 0)
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
				fileChooser.addChoosableFileFilter(levelFilter);
				fileChooser.setFileFilter(levelFilter);
				int choice = fileChooser.showOpenDialog(fileChooser);
				fileChooser.removeChoosableFileFilter(levelFilter);

				if (choice == JFileChooser.APPROVE_OPTION)
				{
					File file = fileChooser.getSelectedFile();

					try
					{
						mapPanel.level = new Level(file);
					}
					catch (IOException ex)
					{
						JOptionPane.showMessageDialog(fileOpen, ex.getMessage(), "Failed to open level file", JOptionPane.ERROR_MESSAGE);
						return;
					}

					// Set tileset panel image
					tilesetPanel.setImage(0, mapPanel.level.getLayer(1).getImg());
					tilesetPanel.showLayer[0] = true;
					tilesetPanel.defaultSettings();

					// Set map panel images
					for (int n = 0; n < mapPanel.level.getNumOfLayers(); n++)
					{
						BufferedImage defMapImg = new BufferedImage(mapPanel.level.getLayer(n).getWidth() * 16, mapPanel.level.getLayer(n).getHeight() * 16, BufferedImage.TYPE_INT_ARGB);
						mapPanel.setImage(n, defMapImg);
						mapPanel.showLayer[n] = true;
					}

					if (mapPanel.drawAreaLayers.size() >= 1)
						mapPanel.paintOnLayer = 1; // We assume that layer 1 is the "walkable" layer

					int levelWidth;
					int levelHeight;
					int tile = 0;

					for (int n = 0; n < mapPanel.drawAreaLayers.size(); n++)
					{
						levelWidth = mapPanel.level.getLayer(n).getWidth() * 16;
						levelHeight = mapPanel.level.getLayer(n).getHeight() * 16;

						for (int i = 0, x = 0; i < levelWidth; i+=16, x++)
						{
							for (int j = 0, y = 0; j < levelHeight; j+=16, y++)
							{
								tile = mapPanel.level.getLayer(n).getTile(x, y);
								mapPanel.paintTile(n, tile, i, j, false);
							}
						}
					}

					toolbarPanel.defaultSettings();

					if (mapPanel.drawAreaLayers.size() > 0)
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
				if (mapPanel.level != null)
				{
					if (mapPanel.level.getFilePath() != null)
					{
						mapPanel.level.write(mapPanel.level.getFilePath());
					}
					else
					{
						// ask for the file name
						fileChooser.addChoosableFileFilter(levelFilter);
						fileChooser.setFileFilter(levelFilter);
						int choice = fileChooser.showSaveDialog(fileChooser);
						fileChooser.removeChoosableFileFilter(levelFilter);

						if (choice == JFileChooser.APPROVE_OPTION)
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
				if (mapPanel.level != null)
				{
					// save current level under a different name
					fileChooser.addChoosableFileFilter(levelFilter);
					fileChooser.setFileFilter(levelFilter);
					int choice = fileChooser.showSaveDialog(fileChooser);
					fileChooser.removeChoosableFileFilter(levelFilter);

					if (choice == JFileChooser.APPROVE_OPTION)
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
				if (mapPanel.level != null && showModifyLevelSetup())
				{

					for (int i = 0; i < mapPanel.level.getNumOfLayers(); i++)
					{
						BufferedImage defMapImg = new BufferedImage(mapPanel.level.getLayer(i).getWidth() * 16, mapPanel.level.getLayer(i).getHeight() * 16, BufferedImage.TYPE_INT_ARGB);
						mapPanel.setImage(i, defMapImg);
					}

					int levelWidth;
					int levelHeight;
					int tile = 0;

					for (int n = 0; n < mapPanel.drawAreaLayers.size(); n++)
					{
						levelWidth = mapPanel.level.getLayer(n).getWidth() * 16;
						levelHeight = mapPanel.level.getLayer(n).getHeight() * 16;

						for (int i = 0, x = 0; i < levelWidth; i+=16, x++)
						{
							for (int j = 0, y = 0; j < levelHeight; j+=16, y++)
							{
								tile = mapPanel.level.getLayer(n).getTile(x, y);
								mapPanel.paintTile(n, tile, i, j, false);
							}
						}
					}

					if (mapPanel.drawAreaLayers.size() > 0)
					{
						mapPanel.setPreferredSize(new Dimension(mapPanel.drawAreaLayers.get(0).getWidth(),mapPanel.drawAreaLayers.get(0).getHeight()));
						mapPanel.revalidate();
					}
				}
			}
		});
		fileQuit.addActionListener(new ActionListener()
		{
			boolean leave = false;

			public void actionPerformed(ActionEvent e)
			{
				// Ask about saving the changes
				if (mapPanel.level != null)
				{
					int choice = JOptionPane.showConfirmDialog(fileChooser, "Store the changes?", "", JOptionPane.YES_NO_CANCEL_OPTION);

					if (choice == JOptionPane.YES_OPTION)
					{
						if (mapPanel.level.getFilePath() != null)
						{
							mapPanel.level.write(mapPanel.level.getFilePath());
						}
						else
						{
							// ask for the file name
							fileChooser.addChoosableFileFilter(levelFilter);
							fileChooser.setFileFilter(levelFilter);
							int choice2 = fileChooser.showSaveDialog(fileChooser);
							fileChooser.removeChoosableFileFilter(levelFilter);

							if (choice2 == JFileChooser.APPROVE_OPTION)
							{
								File file = fileChooser.getSelectedFile();
								mapPanel.level.setFilePath(file);
								mapPanel.level.write(mapPanel.level.getFilePath());

								leave = true;
							}
						}
					}
					else if (choice == JOptionPane.NO_OPTION)
					{
						leave = true;
					}
				}
				else
				{
					leave = true;
				}

				// Exit program.
				if (leave)
				{
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					frame.setVisible(false);
					frame.dispose(); // Destroy the main window.
				}
			}
		});
		runSetExec.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Select new location of the game executable (frog.jar)
				fileChooser.addChoosableFileFilter(execFilter);
				fileChooser.setFileFilter(execFilter);
				int choice = fileChooser.showOpenDialog(fileChooser);
				fileChooser.removeChoosableFileFilter(execFilter);

				if (choice == JFileChooser.APPROVE_OPTION)
				{
					File file = fileChooser.getSelectedFile();
					Data.setDataDirectory(file.getParent());
					execFile = file.getName();
				}
			}
		});
		runRunLevel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (mapPanel.level != null)
				{
					boolean playerObjPresent = false;

					ListIterator<GameObject> objsli = mapPanel.level.getObjectList().listIterator();
					while (objsli.hasNext())
					{
						if (objsli.next().getName().equals("player"))
							playerObjPresent = true;
					}

					if (!playerObjPresent) // Don't launch the level if player object is missing.
					{
						JOptionPane.showMessageDialog(runRunLevel, "Add a player object first.");
						return;
					}

					File path = new File(Data.getDataDirectory() + "/" + execFile);

					while (!path.exists() || path.isDirectory())
					{
						JOptionPane.showMessageDialog(runRunLevel, "No game executable (" + execFile + ") found.\nPlease select a new location for the executable.", "Game launch issue", JOptionPane.ERROR_MESSAGE);
						fileChooser.addChoosableFileFilter(execFilter);
						fileChooser.setFileFilter(execFilter);
						int choice = fileChooser.showOpenDialog(fileChooser);
						fileChooser.removeChoosableFileFilter(execFilter);

						if (choice == JFileChooser.APPROVE_OPTION)
						{
							File file = fileChooser.getSelectedFile();
							Data.setDataDirectory(file.getParent());
							execFile = file.getName();
							path = new File(Data.getDataDirectory() + "/" + execFile);
						}
						else if (choice == JFileChooser.CANCEL_OPTION)
						{
							return;
						}
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

	public void setPanels(JFrame newFrame, MapPanel newMapPanel, TilesetPanel newTilesetPanel, ToolbarPanel newToolbarPanel, ObjectPanel newObjectPanel)
	{
		this.frame = newFrame;
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
