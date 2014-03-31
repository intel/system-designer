package com.intel.tools.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

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
                Process p =
                    Runtime.getRuntime()
                        .exec("reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v personal");
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
}
