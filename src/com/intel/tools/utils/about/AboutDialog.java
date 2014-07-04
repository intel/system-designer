package com.intel.tools.utils.about;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;
import org.osgi.framework.Bundle;

import com.intel.tools.utils.FileUtils;
import com.intel.tools.utils.IntelPalette;

public class AboutDialog extends Dialog {

    public static class LabelUrl {
        private String label;
        private String url;

        public LabelUrl(String label, String url) {
            this.label = label;
            this.url = url;
        }

        public String getLabel() {
            return label;
        }

        public String getURL() {
            return url;
        }
    }

    private class LabelURLMouseListener implements MouseListener {
        private LabelUrl labelUrl;

        public LabelURLMouseListener(LabelUrl labelUrl) {
            this.labelUrl = labelUrl;
        }

        @Override
        public void mouseUp(MouseEvent e) {
            FileUtils.openExternalEditor(labelUrl.getURL());
        }

        @Override
        public void mouseDown(MouseEvent e) {
        }

        @Override
        public void mouseDoubleClick(MouseEvent e) {
        }
    };

    /**
     * Application product
     */
    private IProduct product;
    private String productName;
    private Image aboutImage;

    private ArrayList<LabelUrl> urlList = new ArrayList<LabelUrl>();

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public AboutDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(SWT.BORDER | SWT.TITLE);
        product = Platform.getProduct();
        if (product != null) {
            productName = product.getName();
        }
        if (productName == null) {
            productName = "";
        }

    }

    private static URL getUrl(String value, Bundle definingBundle) {
        try {
            if (value != null) {
                return new URL(value);
            }
        } catch (MalformedURLException e) {
            if (definingBundle != null) {
                return FileLocator.find(definingBundle, new Path(value), null);
            }
        }

        return null;
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        if (product != null) {
            URL url = getUrl(product.getProperty("aboutImage"), product.getDefiningBundle());

            ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);

            if (imageDescriptor != null && url != null) {
                aboutImage = imageDescriptor.createImage();
            }
        }

        Composite container = (Composite) super.createDialogArea(parent);
        container.setBackgroundMode(SWT.INHERIT_DEFAULT);
        container.setBackground(IntelPalette.WHITE);
        GridLayout gl_container = new GridLayout(2, false);
        gl_container.marginLeft = 16;
        gl_container.marginHeight = 0;
        gl_container.verticalSpacing = 0;
        gl_container.marginWidth = 0;
        container.setLayout(gl_container);

        Label lblNewLabel_1 = new Label(container, SWT.NONE);
        lblNewLabel_1.setFont(SWTResourceManager.getFont("Segoe UI", 13, SWT.BOLD));
        if (aboutImage != null) {
            lblNewLabel_1.setImage(aboutImage);
        } else {
            lblNewLabel_1.setText(productName);
            lblNewLabel_1.setForeground(IntelPalette.LIGHT_BLUE);
        }
        GridData gd_lblNewLabel_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_lblNewLabel_1.verticalIndent = 16;
        lblNewLabel_1.setLayoutData(gd_lblNewLabel_1);

        Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setImage(ResourceManager.getPluginImage("com.intel.tools.utils", "images/vertical_banner.png"));
        GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 2 + urlList.size());
        gd_lblNewLabel.widthHint = 150;
        lblNewLabel.setLayoutData(gd_lblNewLabel);

        StyledText styledText = new StyledText(container, SWT.WRAP);
        styledText.setEnabled(false);
        styledText.setEditable(false);
        GridData gd_styledText = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_styledText.verticalIndent = 16;
        styledText.setLayoutData(gd_styledText);
        if (product != null) {
            styledText.setText(getAboutText(product));
        }
        for (LabelUrl labelUrl : urlList) {

            Label licenseLink = new Label(container, SWT.NONE);
            licenseLink.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
            licenseLink.setForeground(IntelPalette.LIGHT_BLUE);
            licenseLink.setText(labelUrl.getLabel());
            licenseLink.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
            licenseLink.addMouseListener(new LabelURLMouseListener(labelUrl));
        }

        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);

        return container;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);

        // Change window title
        shell.setText("About " + productName);
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(578, 428);
    }

    public static String getAboutText(IProduct product) {
        String property = product.getProperty("aboutText");
        if (property == null) {
            return ""; //$NON-NLS-1$
        }
        return property;

    }

    /**
     * Add a custom URL to the botom of the about box.
     * 
     * @param label
     *            the URL label
     * @param url
     *            the pointed URL
     * 
     */
    public void addURL(String label, String url) {
        urlList.add(new LabelUrl(label, url));
    }
}
