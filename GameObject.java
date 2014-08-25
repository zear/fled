import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

class ImgTemplate
{
	private int w;
	private int h;
	private BufferedImage image = null;
	private int tileW;
	private int tileH;
	private int tileLeft;	// # of IDLE LEFT first frame
	private int tileRight;	// # of IDLE RIGHT first frame

	public ImgTemplate(String fileName, int w, int h)
	{
		try
		{
			File file = new File(Data.getDataDirectory() + "/data/gfx/obj/" + fileName);
			BufferedImage tmpImg = ImageIO.read(file);
			this.image = new BufferedImage(tmpImg.getWidth(), tmpImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
			this.image.getGraphics().drawImage(tmpImg, 0, 0, null);

			// apply transparency
			WritableRaster raster = this.image.getRaster();
			for(int j = 0; j < this.image.getHeight(); j++)
			{
				for(int i = 0; i < this.image.getWidth(); i++)
				{
					int [] pixels = raster.getPixel(i, j, (int[]) null);

					if(pixels[0] == 255 && pixels[1] == 0 && pixels[2] == 255) // magenta
					{
						pixels[3] = 0;
						raster.setPixel(i, j, pixels);
					}
				}
			}

			this.tileW = w;
			this.tileH = h;
		}
		catch(IOException ioe)
		{
			System.out.printf("Failed to load file: %s\n", fileName);
		}
	}

	public int getW()
	{
		return this.w;
	}

	public void setW(int w)
	{
		this.w = w;
	}

	public int getH()
	{
		return this.h;
	}

	public void setH(int h)
	{
		this.h = h;
	}

	public BufferedImage getImg()
	{
		return this.image;
	}

	public void setImg(BufferedImage image)
	{
		this.image = image;
	}

	public int getTileW()
	{
		return this.tileW;
	}

	public int getTileH()
	{
		return this.tileH;
	}

	public BufferedImage getTile(int tileNum)
	{
		BufferedImage tile = new BufferedImage(this.tileW, this.tileH, BufferedImage.TYPE_INT_ARGB);
		tile.getGraphics().drawImage(this.image, 0, -tileNum * this.tileH, null);

		return tile;
	}
}

public class GameObject
{
	private String name;
	private int x;
	private int y;
	private boolean direction;
	private ImgTemplate img = null;

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getX()
	{
		return this.x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return this.y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getW()
	{
		return this.img.getW();
	}

	public void setW(int w)
	{
		this.img.setW(w);
	}

	public int getH()
	{
		return this.img.getH();
	}

	public void setH(int h)
	{
		this.img.setH(h);
	}

	public boolean getDirection()
	{
		return this.direction;
	}

	public void setDirection(boolean direction)
	{
		this.direction = direction;
	}

	public ImgTemplate getImgTemplate()
	{
		return this.img;
	}

	public void setImgTemplate(ImgTemplate template)
	{
		this.img = template;
	}

	public void setImgTemplate(String fileName, int w, int h)
	{
		if(this.img == null)
		{
			this.img = new ImgTemplate(fileName, w, h);
		}
	}

	public BufferedImage getImg()
	{
		return this.img.getImg();
	}

//	public void setImg(BufferedImage img)
//	{
//		this.img.setImg(img);
//	}

	public int getTileW()
	{
		return this.img.getTileW();
	}

	public int getTileH()
	{
		return this.img.getTileH();
	}

	public BufferedImage getTile(int tileNum)
	{
		return this.img.getTile(tileNum);
	}
}
