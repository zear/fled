package util;

import java.io.File;
import java.util.ArrayList;

public class Data
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

	public static String [] getTilesetList(File directory)
	{
		File [] list = directory.listFiles();
		ArrayList<String> tilesetNames = new ArrayList<String>();

		if (list == null)
		{
			return null;
		}

		for (File entry : list)
		{
			if (entry.getName().toLowerCase().contains("0.bmp"))
			{
				boolean addEntry = true;

				String toStore = entry.getName().substring(0, entry.getName().length() - 5);
				for (String name : tilesetNames)
				{
					if (name.equals(toStore))
					{
						addEntry = false;
						break;
					}
				}

				if (addEntry)
					tilesetNames.add(toStore);
			}
		}

		return tilesetNames.toArray(new String[tilesetNames.size()]);
	}
}
