package com.intel.tools.utils.statusline;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 * This class presnets statics methods to access the StatusLine from the workbench.
 * 
 *  !! This class uses internal methods from the Workbench !!
 */
public class StatusLine {

    private static StatusLineManager manager;

    public static void showMessage(String message) {
        if (getStatusLineManager() != null)
        {
            getStatusLineManager().setMessage(message);
        }
    }

    public static void showMessage(Image image, String message) {
        if (getStatusLineManager() != null)
        {
            getStatusLineManager().setMessage(image, message);
        }
    }

    public static StatusLineManager getStatusLineManager()
    {
        if (manager == null) {
            IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (workbenchWindow != null) {
                manager = ((WorkbenchWindow) workbenchWindow).getStatusLineManager();
            } else
                return null;
        }
        return manager;
        
    }
}