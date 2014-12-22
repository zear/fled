package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

enum EditMode
{
	MODE_NONE,
	MODE_TILE_EDIT,
	MODE_TILE_SELECTION,
	MODE_OBJECT_EDIT,
	MODE_LEVEL_EDIT
}

public class TilesetPanel extends DrawPanel implements MouseInputListener 
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

class TileBackupNode
{
	public int x;
	public int y;
	public int value;

	public TileBackupNode(int x, int y, int value)
	{
		this.x = x;
		this.y = y;
		this.value = value;
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
