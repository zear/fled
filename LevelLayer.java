import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.imageio.ImageIO;
import java.util.ArrayList;

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
		protected void removeLastRow()
		{
			t.remove(getNumOfRows() - 1);
		}
		protected void putElement(int row, int element)
		{
			t.get(row).add(element);
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
		if(x < 0)
			return -1;
		if(y < 0)
			return -1;
		if(x > getWidth() - 1)
			return -1;
		if(y > getHeight() - 1)
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

	public void setTile(int x, int y, int value)
	{
		if(x < 0)
			return;
		if(y < 0)
			return;
		if(x > getWidth() - 1)
			return;
		if(y > getHeight() - 1)
			return;

		tiles.setElement(y, x, value);
	}

	public void load(int id)
	{
		this.id = id;
	}

	public void load(String fileName, int w, int h, int rowW, int size)
	{
		try
		{
			//this.img = ImageIO.read(new File(fileName));
			BufferedImage tmpImg = ImageIO.read(new File(fileName));
			this.img = new BufferedImage(tmpImg.getWidth(), tmpImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
			this.img.getGraphics().drawImage(tmpImg, 0, 0, null);
		}
		catch (IOException e)
		{
			System.out.printf("ERROR: Failed to load file %s!\n", fileName);
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

		while(fp.hasNext())
		{
			line = fp.getLine();
			words = line.split("\\s");
			token = -1;

			if(words[0].equals("END"))
				return;

			if(words.length > 0 && !(words[0].equals("END")))
				tiles.addRow();

			while(token < words.length - 1)
			{
				token++;
				tiles.putElement(tiles.getNumOfRows() - 1, Integer.parseInt(words[token]));
			}
		}
	}

	public void populate(int sizeX, int sizeY, int tile)
	{
		for(int j = 0; j < sizeY; j++)
		{
			tiles.addRow();
			for(int i = 0; i < sizeX; i++)
			{
				if (this.id != 0)
					tiles.putElement(tiles.getNumOfRows() - 1, tile);
				else
					tiles.putElement(tiles.getNumOfRows() - 1, 22); // HACK!
			}
		}
	}
}