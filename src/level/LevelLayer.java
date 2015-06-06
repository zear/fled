package level;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.imageio.ImageIO;
import java.util.ArrayList;

import util.*;

// LevelLayer keeps data related to a single map layer
public class LevelLayer
{
	private int id;
	private Tiles tiles;
	private BufferedImage img;
	private String imgPath;
	private int imgSize;
	private int imgRowW;
	private int tileW;
	private int tileH;

	protected class Tiles
	{
		private ArrayList<ArrayList<Integer>> t;

		protected Tiles()
		{
			t = new ArrayList<ArrayList<Integer>>();
		}

		protected void addRow()
		{
			t.add(new ArrayList<Integer>());
		}
		protected void prependRow()
		{
			t.add(0, new ArrayList<Integer>());
		}
		protected void removeRow(int row)
		{
			t.remove(row);
		}
		protected void removeLastRow()
		{
			t.remove(getNumOfRows() - 1);
		}
		protected void putElement(int row, int element)
		{
			t.get(row).add(element);
		}
		protected void prependElement(int row, int element)
		{
			t.get(row).add(0, element);
		}
		protected void removeElement(int row, int element)
		{
			t.get(row).remove(element);
		}
		protected int getElement(int row, int element)
		{
			return t.get(row).get(element);
		}
		protected int getNumOfRows()
		{
			return t.size();
		}
		protected int getNumOfElements(int row) // technically all rows should have the same number of elements, but let's force passing a row
		{
			return t.get(row).size();
		}
		protected void setElement(int row, int element, int value)
		{
			t.get(row).set(element, value);
		}

		protected ArrayList<ArrayList<Integer>> getList()
		{
			return t;
		}
	}

	public LevelLayer()
	{
		tiles = new Tiles();
	}

	public ArrayList<ArrayList<Integer>> getList()
	{
		return this.tiles.getList();
	}

	public int getTile(int x, int y)
	{
		if (x < 0)
			return -1;
		if (y < 0)
			return -1;
		if (x > getWidth() - 1)
			return -1;
		if (y > getHeight() - 1)
			return -1;

		return tiles.getElement(y, x);
	}

	public int getWidth()
	{
		return tiles.getNumOfElements(0);
	}

	public int getHeight()
	{
		return tiles.getNumOfRows();
	}

	public int getId()
	{
		return this.id;
	}

	public BufferedImage getImg()
	{
		return this.img;
	}

	public String getImgPath()
	{
		return this.imgPath;
	}

	public int getImgSize()
	{
		return this.imgSize;
	}

	public int getTileW()
	{
		return this.tileW;
	}

	public int getTileH()
	{
		return this.tileH;
	}

	public int getImgRowW()
	{
		return this.imgRowW;
	}

	public void reloadImg() throws IOException
	{
		try
		{
			BufferedImage tmpImg = ImageIO.read(new File(this.imgPath));
			this.img = new BufferedImage(tmpImg.getWidth(), tmpImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
			this.img.getGraphics().drawImage(tmpImg, 0, 0, null);
		}
		catch (IOException e)
		{
			System.out.printf("ERROR: Failed to load file %s!\n", this.imgPath);
			throw new IOException("Missing game data:\n"+this.imgPath+"\n.");
		}
	}

	public void setTile(int x, int y, int value)
	{
		if (x < 0)
			return;
		if (y < 0)
			return;
		if (x > getWidth() - 1)
			return;
		if (y > getHeight() - 1)
			return;

		tiles.setElement(y, x, value);
	}

	public void load(int id)
	{
		this.id = id;
	}

	public void load(String fileName, int w, int h, int rowW, int size) throws IOException
	{
		try
		{
			BufferedImage tmpImg = ImageIO.read(new File(fileName));
			this.img = new BufferedImage(tmpImg.getWidth(), tmpImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
			this.img.getGraphics().drawImage(tmpImg, 0, 0, null);
		}
		catch (IOException e)
		{
			System.out.printf("ERROR: Failed to load file %s!\n", fileName);
			throw new IOException("Missing game data:\n"+fileName+"\nPlace the editor directory within the game directory.");
		}

		this.imgPath = fileName;
		
		this.imgSize = size;
		this.imgRowW = rowW;
		this.tileW = w;
		this.tileH = h;
	}

	public void load(FileRead fp)
	{
		String line;
		String [] words;
		int token;

		while (fp.hasNext())
		{
			line = fp.getLine();
			words = line.split("\\s");
			token = -1;

			if (words[0].equals("END"))
				return;

			if (words.length > 0 && !(words[0].equals("END")))
				tiles.addRow();

			while (token < words.length - 1)
			{
				token++;
				tiles.putElement(tiles.getNumOfRows() - 1, Integer.parseInt(words[token]));
			}
		}
	}

	public void populate(int sizeX, int sizeY, int tile)
	{
		for (int j = 0; j < sizeY; j++)
		{
			tiles.addRow();
			for (int i = 0; i < sizeX; i++)
			{
				tiles.putElement(tiles.getNumOfRows() - 1, tile);
			}
		}
	}

	public void expandLeft(int sizeX, int tile)
	{
		int numOfRows = tiles.getNumOfRows();

		for (int j = 0; j < numOfRows; j++)
		{
			for (int i = 0; i < sizeX; i++)
			{
				tiles.prependElement(j, tile);
			}
		}
	}

	public void expandRight(int sizeX, int tile)
	{
		int numOfRows = tiles.getNumOfRows();

		for (int j = 0; j < numOfRows; j++)
		{
			for (int i = 0; i < sizeX; i++)
			{
				tiles.putElement(j, tile);
			}
		}
	}

	public void expandTop(int sizeY, int tile)
	{
		int sizeOfRow = tiles.getNumOfElements(0);

		for (int j = 0; j < sizeY; j++)
		{
			tiles.prependRow();
		}

		for (int j = 0; j < sizeY; j++)
		{
			for (int i = 0; i < sizeOfRow; i++)
			{
				tiles.putElement(j, tile);
			}
		}
	}

	public void expandBottom(int sizeY, int tile)
	{
		int numOfRows = tiles.getNumOfRows();
		int sizeOfRow = tiles.getNumOfElements(0);

		for (int j = 0; j < sizeY; j++)
		{
			tiles.addRow();

			for (int i = 0; i < sizeOfRow; i++)
			{
				tiles.prependElement(numOfRows + j, tile);
			}
		}
	}

	public void reduceLeft(int sizeX)
	{
		int numOfRows = tiles.getNumOfRows();

		for (int j = 0; j < numOfRows; j++)
		{
			for (int i = 0; i < sizeX; i++)
			{
				tiles.removeElement(j, i);
			}
		}
	}

	public void reduceRight(int sizeX)
	{
		int numOfRows = tiles.getNumOfRows();
		int sizeOfRow = tiles.getNumOfElements(0);

		for (int j = 0; j < numOfRows; j++)
		{
			for (int i = 0; i < sizeX; i++)
			{
				tiles.removeElement(j, sizeOfRow - 1 - i);
			}
		}
	}

	public void reduceTop(int sizeY)
	{
		int sizeOfRow = tiles.getNumOfElements(0);

		for (int j = 0; j < sizeY; j++)
		{
			tiles.removeRow(j);
		}
	}

	public void reduceBottom(int sizeY)
	{
		int numOfRows = tiles.getNumOfRows();
		int sizeOfRow = tiles.getNumOfElements(0);

		for (int j = 0; j < sizeY; j++)
		{
			tiles.removeRow(numOfRows - 1 - j);
		}
	}
}
