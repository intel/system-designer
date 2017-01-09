package com.intel.tools.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class ZipUtils {

	/**
	 * Create a ZIP archive of a file
	 * @param file the file to be archived
	 * @param output the ZIP archived file
	 */
	public static void zipFile(File file, File output) {
		try {
			FileOutputStream fos = new FileOutputStream(output);
			ZipOutputStream zos = new ZipOutputStream(fos);
			ZipEntry ze = new ZipEntry(file.getName());
			zos.putNextEntry(ze);
			FileInputStream in = new FileInputStream(file);

			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);

			}

			in.close();
			zos.closeEntry();
			zos.close();

		} catch(IOException ioe) {
    		ioe.printStackTrace();

    	}
	}

	/**
	 * Create a ZIP archive of a list of files.
	 * The input list of files may contains files and/or directories.
	 * The algorithm used to browse directories is not recursive.
	 * @param files the list of files/directories to be archived
	 * @param output the ZIP archived file
	 */
	public static void zipFiles(List<File> files, File output) {
		try {
			FileOutputStream fos = new FileOutputStream(output);
			ZipOutputStream zos = new ZipOutputStream(fos);

			Collections.sort(files, new Comparator<File>() {
				@Override
				public int compare(File f1, File f2) {
					return f1.getAbsolutePath().compareTo(f2.getAbsolutePath());
				}
			});

			File baseDirectory = files.get(0).getParentFile();
			Deque<File> entries = new ArrayDeque<File>(files);

			while (!entries.isEmpty()) {
				File entry = entries.poll();

				if (entry.isDirectory()) {
					for (File current : entry.listFiles()) {
						entries.add(current);

					}

				} else if (entry.isFile()) {
					ZipEntry ze = new ZipEntry(entry.getAbsolutePath().substring(baseDirectory.getAbsolutePath().length() + 1));
//					ZipEntry ze = new ZipEntry(entry.getName());
					zos.putNextEntry(ze);
					FileInputStream in = new FileInputStream(entry);

					byte[] buffer = new byte[1024];
					int len;
					while ((len = in.read(buffer)) > 0) {
						zos.write(buffer, 0, len);

					}

					in.close();
				}
			}

			zos.closeEntry();
			zos.close();

		} catch(IOException ioe) {
    		ioe.printStackTrace();

    	}
	}

	/**
	 * Create a ZIP archive of a directory.
	 * The input directory may contains files and/or directories.
	 * The algorithm used to browse the whole input base directory is not recursive.
	 * @param baseDirectory the input directory to be archived
	 * @param output the ZIP archived directory
	 */
	public static void zipDirectory(File baseDirectory, File output) {
		try {
			FileOutputStream fos = new FileOutputStream(output);
			ZipOutputStream zos = new ZipOutputStream(fos);

			Deque<File> directories = new ArrayDeque<File>();
			if (baseDirectory.isDirectory()) {
				directories.add(baseDirectory);

				while (!directories.isEmpty()) {
					File directory = directories.poll(); 
					for (File current : directory.listFiles()) {
						if (current.isDirectory()) {
							directories.add(current);

						} else if (current.isFile()) {
							ZipEntry ze = new ZipEntry(current.getAbsolutePath().substring(baseDirectory.getAbsolutePath().length() + 1));
							zos.putNextEntry(ze);
							FileInputStream in = new FileInputStream(current);

							byte[] buffer = new byte[1024];
							int len;
							while ((len = in.read(buffer)) > 0) {
								zos.write(buffer, 0, len);

							}

							in.close();
						}
					}
				}
			}

			zos.closeEntry();
			zos.close();

		} catch (IOException ioe) {
    		ioe.printStackTrace();

    	}
	}

	/**
	 * Unzip a ZIP archive into the specified output directory
	 * @param archive a ZIP archive file
	 * @param output the directory where the ZIP archive file has to be unzipped
	 */
	public static void unzip(File archive, File output) {
	     try {
	    	if (!output.exists()) {
	    		output.mkdir();

	    	}

	    	ZipInputStream zis = new ZipInputStream(new FileInputStream(archive));
	    	ZipEntry ze = zis.getNextEntry();

	    	while (ze != null) {
	    	   String fileName = ze.getName();
	           File newFile = new File(output, fileName);

	           // create all non-existing folders to avoid FileNotFoundException
	           new File(newFile.getParent()).mkdirs();

	           FileOutputStream fos = new FileOutputStream(newFile);

	           byte[] buffer = new byte[1024];
	           int len;
	           while ((len = zis.read(buffer)) > 0) {
	        	   fos.write(buffer, 0, len);

	           }

	           fos.close();
	           ze = zis.getNextEntry();
	    	}

	    	zis.closeEntry();
	    	zis.close();

	    } catch (IOException ioe){
	    	ioe.printStackTrace();

	    }
	}
}