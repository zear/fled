package util;

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
}
