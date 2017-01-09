/**
 * 
 */
package com.intel.tools.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

/**
 * Handler called to exit the application.
 * 
 * @author bgoudy
 * 
 */

public class FileUtils {
    /**
     * Open the OS default external editor for a given file.
     * 
     * @param filepath
     */
    public static void openExternalEditor(final String filepath) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                Logger.getLogger(FileUtils.class).debug("Opening file in external viewer: " + filepath);

                // TO KNOW MORE =>
                // http://fr.slideshare.net/baronm/editors-4677527
                IPath location = new Path(filepath);
                Program findProgram;
                if (location.getFileExtension() == null) {
                    findProgram = Program.findProgram("html");
                } else {
                    findProgram = Program.findProgram(location.getFileExtension());
                }
                if (findProgram == null) {
                    findProgram = Program.findProgram("txt");
                }

                if (findProgram != null) {
                    findProgram.execute(filepath);
                }
            }
        });
    }

    public static boolean isRunningFromEclipse() {
        String platformPath = new File(Platform.getInstallLocation().getURL().getPath()).getAbsolutePath();
        String classLocationPath =
            new File(FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
        Boolean runFromEclipse = !classLocationPath.contains(platformPath);
        // System.out.println(Platform.inDevelopmentMode());

        return runFromEclipse;
    }

    public static String[] executeCommandLine(String command) throws IOException, InterruptedException {
        List<String> lines = new ArrayList<String>();

        String line;
        Process proc;

        proc = Runtime.getRuntime().exec(command);

        BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        while ((line = input.readLine()) != null) {
            lines.add(line);
        }

        input.close();

        if (0 == proc.waitFor()) {
            // Succeeded
            proc.destroy();
            return lines.toArray(new String[lines.size()]);
        }

        return lines.toArray(new String[0]);
    }


    public static String getApplicationVersion() {
        try {
            IProduct product = Platform.getProduct();
            String aboutText = product.getProperty("aboutText");

            if (aboutText != null) {
                String pattern = "Version: (.*)\\s";
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(aboutText);
                boolean found = m.find();

                if (found) {
                    return m.group(1);
                }
            }
        } catch (Exception e) {
            return "NA";
        }

        return "";
    }

    
    public static String getOsNameAndVersion() throws IOException, InterruptedException {
        String osNameAndVersion = System.getProperty("os.name");
        if (System.getProperty("os.name").contains("Linux")) {
            String[] outputs = executeCommandLine("lsb_release -a"); // lsb_release
                                                                               // -ds
                                                                               // returns
                                                                               // only
            if (outputs.length > 1 && outputs[1] != null && outputs[1].length() > 0 && outputs[1].contains(":")) {
                osNameAndVersion += " (" + outputs[1].split(":")[1].trim() + ")";
            } else {
                osNameAndVersion += " (" + System.getProperty("os.version") + ")";
            }
        } else {
            osNameAndVersion += " (" + System.getProperty("os.version") + ")";
        }

        return osNameAndVersion;
    }

}