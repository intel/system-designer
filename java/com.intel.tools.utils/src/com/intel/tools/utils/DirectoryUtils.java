package com.intel.tools.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;

/**
 * Utility class used to provide easier directory access .
 */
public class DirectoryUtils {

	private static Logger logger = Logger.getLogger(DirectoryUtils.class);

	/**
	 * The name of the running OS
	 * 
	 * @return
	 */
	public static String getOsName() {
		return System.getProperty("os.name");
	}

	/**
	 * Returns the path to the running eclipse platform.
	 * 
	 * @return
	 */
	public static String getPlatformPath() {
		String platformPath = new File(Platform.getInstallLocation().getURL().getPath()).getAbsolutePath();
		return platformPath;
	}

	/**
	 * Get the user documents directory
	 * 
	 * @return The user document directory.
	 */
	public static String getDocumentDirectory() {
		String path = "";
		String fileSeparator = System.getProperty("file.separator");

		if (getOsName().contains("Linux")) {
			path = System.getProperty("user.home") + fileSeparator;
		} else /* Windows */
		{
			String myDocuments = null;

			try {
				// Gets the My Documents folder thanks to a registry query
				Process p = Runtime.getRuntime().exec(
						"reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v personal");
				p.waitFor();

				InputStream in = p.getInputStream();
				byte[] b = new byte[in.available()];
				in.read(b);
				in.close();

				myDocuments = new String(b);
				myDocuments = myDocuments.split("REG_SZ")[1].trim();
			} catch (IOException e) {
				logger.error("Error trying to access current user's documents directory", e);
			} catch (InterruptedException e) {
				logger.error("Error trying to access current user's documents directory", e);
			}

			path = new File(myDocuments).getPath();
		}

		File pathFile = new File(path);

		if (!pathFile.exists()) {
			// Create it
			pathFile.mkdirs();
		}

		return pathFile.getPath() + fileSeparator;
	}

	/**
	 * Returns a temporary directory for a given application
	 * 
	 * @param applicationName
	 *            The name of the application
	 * @return The requested path.
	 */
	public static String getTempDirectory(String applicationName) {
		String tempDir = getApplicationDataDirectory(applicationName) + "/temp/";
		File tempDirFile = new File(tempDir);

		if (!tempDirFile.exists()) {
			// Create it
			tempDirFile.mkdirs();
		}
		return tempDirFile.getAbsolutePath() + File.separatorChar;
	}

	/**
	 * This method finds the application data directory and creates (if it
	 * doesn't exist) the directory corresponding to the application. (based on
	 * the applicationName parameter) Under Linux, a directory is created in the
	 * home of the user named .applicationName under windows, the directory is
	 * creates in %APPDATA%/Intel/applicationName.
	 * 
	 * @param applicationName
	 *            The name of the application
	 * @return The string representing the created directory.
	 */
	public static String getApplicationDataDirectory(String applicationName) {
		String os = getOsName();
		String appDataPath = getApplicationDataDirectory();
		String fileSeparator = System.getProperty("file.separator");

		if (os.contains("Linux")) {
			appDataPath += fileSeparator + "." + applicationName;
		} else /* Windows */
		{
			appDataPath += fileSeparator + "Intel" + fileSeparator + applicationName;
		}

		File appDataFile = new File(appDataPath);

		if (!appDataFile.exists()) {
			// Create it
			appDataFile.mkdirs();
		}

		return (appDataFile.getPath() + File.separatorChar);

	}

	/**
	 * Get the application data directory.
	 * 
	 * @return The string representing the application data directory.
	 */
	public static String getApplicationDataDirectory() {
		String os = getOsName();
		String appDataPath;

		if (os.contains("Linux")) {
			appDataPath = System.getProperty("user.home");
		} else /* Windows */
		{
			appDataPath = System.getenv("APPDATA");
		}

		return (appDataPath + File.separatorChar);
	}

	/**
	 * Copy a directory and its whole content recursively.
	 * 
	 * @param sourcePath
	 *            the source directory path to copy from
	 * @param destinationPath
	 *            the destination directory path to copy to
	 * @throws IOException
	 */
	public static void copy(final String sourcePath, final String destinationPath) throws IOException {
		final Path source = Paths.get(sourcePath.trim());
		final Path destination = Paths.get(destinationPath.trim());

		if (Files.isDirectory(source)) {
			final CopyOption[] copyOptions = new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES,
					StandardCopyOption.REPLACE_EXISTING };
			final EnumSet<FileVisitOption> options = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
			Files.walkFileTree(source, options, Integer.MAX_VALUE, new FileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					FileVisitResult visited = FileVisitResult.CONTINUE;

					Path newDestination = destination.resolve(source.relativize(dir));
					try {
						Files.copy(dir, newDestination, copyOptions);
					} catch (FileAlreadyExistsException faee) {
						logger.error(faee);
					} catch (IOException ioe) {
						logger.error(ioe);
						visited = FileVisitResult.SKIP_SUBTREE;
					}

					return visited;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Path newDestination = destination.resolve(source.relativize(file));
					Files.copy(file, newDestination, copyOptions);

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}

	/**
	 * Delete a directory and its whole content recursively.
	 * 
	 * @param folder
	 *            the folder to be deleted
	 */
	public static boolean delete(File folder) {
		boolean result = true;
		for (File file : folder.listFiles()) {
			if (file.isFile()) {
				result = file.delete();
			} else {
				result = delete(file);
			}
		}

		result = result & folder.delete();

		return result;
	}
}
