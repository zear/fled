package util;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileRead
{
	private FileReader fr;
	private BufferedReader br;
	private File fileName;
	private String line;
	private String [] words;
	private int token;
	private boolean hasNext = true;

	public FileRead(File file) throws FileNotFoundException
	{
		this.open(file);
	}

	public void open(File file) throws FileNotFoundException
	{
		if (file == null)
		{
			System.out.printf("File is empty!\n");
			return;
		}

		this.fileName = file;

		fr = new FileReader(file);

		if (fr != null)
		{
			br = new BufferedReader(fr);
		}
	}

	public void close()
	{
		try
		{
			br.close();
		}
		catch (IOException e)
		{
			System.out.printf("Unable to close file %s!\n", fileName);
		}
		try
		{
			fr.close();
		}
		catch (IOException e)
		{
			System.out.printf("Unable to close file %s!\n", fileName);
		}
	}

	public String getLine()
	{
		try
		{
			String nextLine = br.readLine();
			if (nextLine == null)
				this.hasNext = false;

			return nextLine;
		}
		catch (IOException e)
		{
			System.out.printf("Unable to parse file %s!\n", fileName);
		}

		return null;
	}

	public boolean nextLine()
	{
		if ((this.line = this.getLine()) != null)
		{
			words = this.line.split("\\s");
			token = -1;
			return false;
		}
		else
			return true;
	}

	public String getNext()
	{
		if (words == null)
			if (nextLine())
				return "EOF";
		token++;
		if (token >= this.words.length)
		{
			if (nextLine())
				return "EOF";
			token++;
		}
		return words[token];
	}

	public boolean hasNext()
	{
		return hasNext;
	}
}
