package level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import util.*;

public class Collision
{
	public static final int COLLISION_NONE		= 0;
	public static final int COLLISION_SOLID		= (1 << 0);
	public static final int COLLISION_PLATFORM	= (1 << 1);
	public static final int COLLISION_DAMAGE	= (1 << 2);
	public static final int COLLISION_DESTRUCTIBLE	= (1 << 3);
	public static final int COLLISION_HIDDEN	= (1 << 4);
	public static final int COLLISION_CLIMB		= (1 << 5);

	private int[] map; // Collision map.

	public Collision(String fileName, int size) throws IOException
	{
		map = new int[size];
		load(fileName);
	}

	private void load(String fileName) throws IOException
	{
		FileRead fp = null;
		File file = new File(Data.getDataDirectory() + "/data/level/" + fileName);

		try
		{
			fp = new FileRead(file);
		}
		catch (Exception e)
		{
			System.out.printf("Failed to load collision map: %s\n", fileName);
			throw new IOException("Missing game data:\n"+fileName+"\nPlace the editor directory within the game directory.");
		}

		if(fp != null)
		{
			int index = 0;

			while(fp.hasNext())
			{
				String next = fp.getNext();

				if (next.equals("EOF"))
					break;
				map[index] = Integer.parseInt(next);

				index++;
			}
		}
	}

	public int getCollision(int index)
	{
		if(index == -1)
			return COLLISION_NONE;

		switch(map[index])
		{
			case 1:	// solid
				return COLLISION_SOLID;
			case 2:	// platform
				return COLLISION_PLATFORM;
			case 3:	// damage
				return COLLISION_DAMAGE;
			case 4:	// destructible block
				return COLLISION_DESTRUCTIBLE | COLLISION_SOLID;
			case 5:	// middle of the ladder
				return COLLISION_CLIMB;
			case 6:	// top of the ladder
				return COLLISION_CLIMB | COLLISION_PLATFORM;
			case 7:	// hidden block
				return COLLISION_HIDDEN;

			default:
				return COLLISION_NONE;
		}
	}
}
