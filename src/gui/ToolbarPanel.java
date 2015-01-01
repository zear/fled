package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ToolbarPanel extends JPanel
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
	private JCheckBox showCollision = new JCheckBox("Collision");
	private JCheckBox showObjects = new JCheckBox("Objects");
	private JLabel paintLabel = new JLabel("Draw on:");
	private JLabel showLabel = new JLabel("Show:");

	public ToolbarPanel()
	{
		this.setLayout(new GridLayout(7, 2));

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
		this.add(new JLabel("")); // add an empty cell
		this.add(showCollision);
		this.add(new JLabel("")); // add an empty cell
		this.add(showObjects);

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
				switch (e.getStateChange())
				{
					case ItemEvent.SELECTED:
						drawOnBackground.setEnabled(true);
						mapPanel.showLayer[2] = true;
						mapPanel.repaint();
					break;
					case ItemEvent.DESELECTED:
						drawOnBackground.setEnabled(false);
						if (drawOnBackground.isSelected())
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
				switch (e.getStateChange())
				{
					case ItemEvent.SELECTED:
						drawOnMiddleground.setEnabled(true);
						mapPanel.showLayer[1] = true;
						mapPanel.repaint();
					break;
					case ItemEvent.DESELECTED:
						drawOnMiddleground.setEnabled(false);
						if (drawOnMiddleground.isSelected())
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
				switch (e.getStateChange())
				{
					case ItemEvent.SELECTED:
						drawOnForeground.setEnabled(true);
						mapPanel.showLayer[0] = true;
						mapPanel.repaint();
					break;
					case ItemEvent.DESELECTED:
						drawOnForeground.setEnabled(false);
						if (drawOnForeground.isSelected())
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
				switch (e.getStateChange())
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
		showCollision.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				int levelWidth;
				int levelHeight;
				int tile;

				switch (e.getStateChange())
				{
					case ItemEvent.SELECTED:
						mapPanel.showCollision = true;
					break;
					case ItemEvent.DESELECTED:
						mapPanel.showCollision = false;
					break;

					default:
					break;
				}

				for (int n = 0; n < mapPanel.drawAreaLayers.size(); n++)
				{
					levelWidth = mapPanel.level.getLayer(n).getWidth() * 16;
					levelHeight = mapPanel.level.getLayer(n).getHeight() * 16;

					for (int i = 0, x = 0; i < levelWidth; i+=16, x++)
					{
						for (int j = 0, y = 0; j < levelHeight; j+=16, y++)
						{
							tile = mapPanel.level.getLayer(n).getTile(x, y);
							mapPanel.paintTile(n, tile, i, j, true);
						}
					}
				}
			}
		});
		showObjects.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				switch (e.getStateChange())
				{
					case ItemEvent.SELECTED:
						mapPanel.showObjects = true;
						mapPanel.repaint();
					break;
					case ItemEvent.DESELECTED:
						mapPanel.showObjects = false;
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
		this.showCollision.setSelected(false);
		this.showObjects.setSelected(true);
	}

	public void setPanels(MapPanel newMapPanel, TilesetPanel newTilesetPanel)
	{
		this.mapPanel = newMapPanel;
		this.tilesetPanel = newTilesetPanel;
	}
}
