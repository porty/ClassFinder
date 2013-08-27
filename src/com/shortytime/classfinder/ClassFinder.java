package com.shortytime.classfinder;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ClassFinder
{
	public static void main(String[] args) throws ZipException, IOException
	{
		String className;
		String dirName;
		boolean isAbsolute = false;

		if (args.length != 2)
		{
			System.err.println("className and dirName arguments required");
			return;
		}
		className = args[0];
		dirName = args[1];
		File dir = new File(dirName);
		if (!dir.isDirectory())
		{
			System.err.println("Specified directory doesn't exist: " + dirName);
			return;
		}

		if (className.contains("."))
		{
			isAbsolute = true;
			className = className.replace('.', '/');
		}

		searchDirectory(dir, className + ".class", isAbsolute);
	}

	private static void searchDirectory(File dir, String wantedFile, boolean isAbsolute) throws ZipException,
			IOException
	{
		System.out.println("Looking in " + dir.getAbsolutePath());
		File[] files = dir.listFiles();
		for (File file : files)
		{
			if (file.isDirectory())
			{
				searchDirectory(file, wantedFile, isAbsolute);
			}
			else if (file.getName().toLowerCase().endsWith(".jar"))
			{
				searchJar(file, wantedFile, isAbsolute);
			}
		}
	}

	private static void searchJar(File jar, String wantedFile, boolean isAbsolute) throws ZipException, IOException
	{
		System.out.println("Looking in jar " + jar.getName());
		ZipFile zip = new ZipFile(jar);

		try
		{
			if (isAbsolute)
			{
				ZipEntry entry = zip.getEntry(wantedFile);
				if (entry != null)
				{
					found(jar, entry);
					return;
				}
			}

			Enumeration<? extends ZipEntry> z = zip.entries();
			while (z.hasMoreElements())
			{
				ZipEntry entry = z.nextElement();
				if (entry.isDirectory())
					continue;

				// System.out.println("Looking at zip entry " + entry.getName());

				// if (entry.getName().equals(wantedFile))
				if (entry.getName().endsWith("/" + wantedFile))
				{
					// System.out.println(">>> Found class " + entry.getName() + " in " + jar.getAbsolutePath());
					found(jar, entry);
				}
			}
		}
		finally
		{
			zip.close();
		}
	}

	private static void found(File jar, ZipEntry entry)
	{
		System.out.println(">>> Found class " + entry.getName() + " in " + jar.getAbsolutePath());
	}
}
