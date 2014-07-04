/**
 * 
 */
package com.intel.tools.utils;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
}