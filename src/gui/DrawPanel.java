package gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.LinkedList;

public class DrawPanel extends JPanel
{
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

	public void drawCollision(int layer, int x, int y, Color color)
	{
		BufferedImage dest = this.drawAreaLayers.get(layer);
		Graphics2D g2d = (Graphics2D)dest.createGraphics();
		g2d.setColor(color);
		g2d.fill(new Rectangle2D.Double(x, y, 16, 16));
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
