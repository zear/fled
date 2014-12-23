package gui;

import java.awt.*;
import javax.swing.*;

public class Editor
{
	private static void createGui()
	{
		JFrame frame = new JFrame("FROG Level Editor");
		ImageIcon icon = new ImageIcon("./data/icon.png");
		frame.setIconImage(icon.getImage());
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
		menuBar.setPanels(frame, mapPanel, tilesetPanel, toolbarPanel, objectPanel);

		frame.getContentPane().add(menuBar, BorderLayout.NORTH);
		frame.getContentPane().add(windowContainer);

		frame.setLocationRelativeTo(null);

		// display the window
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
