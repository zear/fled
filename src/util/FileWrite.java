package util;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileWrite
{
	private FileWriter fw;
	private BufferedWriter bw;
	private File fileName;
	private String line;
	private String [] words;
	private int token;
	private boolean hasNext = true;

	public FileWrite(File file)
	{
		this.open(file);
	}

	public void open(File file)
	{
		if(file == null)
		{
			System.out.printf("File is empty!\n");
			return;
		}

		this.fileName = file;

		try
		{
			fw = new FileWriter(file);
		}
		catch (IOException e)
		{
			System.out.printf("Cannot access file %s!\n", file);
		}

		if(fw != null)
		{
			bw = new BufferedWriter(fw);
		}
	}

	public void close()
	{
		try
		{
			bw.close();
		}
		catch (IOException e)
		{
			System.out.printf("Unable to close file %s!\n", fileName);
		}
		try
		{
			fw.close();
		}
		catch (IOException e)
		{
			System.out.printf("Unable to close file %s!\n", fileName);
		}
	}

	public void writeLine(String line)
	{
		try
		{
			bw.write(line);
		}
		catch (IOException e)
		{
			System.out.printf("Failed to write line %s to file %s!", line, fileName);
		}
	}
}
