package com.intel.tools.utils;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class IntelLogoToolItem {
    public IntelLogoToolItem() {
        System.out.println("CONSTRUCTOR");
    }

    @PostConstruct
    public void createControl(Composite parent) {
        Composite compo = new Composite(parent, SWT.None);
        compo.setBounds(0, 0, 327, 295);
        compo.setLayout(new GridLayout(1, true));

        CLabel icon = new CLabel(compo, SWT.NONE);

        icon.setImage(ImageLoader.getPluginImage("com.intel.tools.utils", "images/intel-logo.png"));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        icon.setLayoutData(gd);
    }
    
  
}
