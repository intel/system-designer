/* ============================================================================
 * INTEL CONFIDENTIAL
 *
 * Copyright 2015-2016 Intel Corporation All Rights Reserved.
 *
 * The source code contained or described herein and all documents related to
 * the source code ("Material") are owned by Intel Corporation or its suppliers
 * or licensors. Title to the Material remains with Intel Corporation or its
 * suppliers and licensors. The Material contains trade secrets and proprietary
 * and confidential information of Intel or its suppliers and licensors. The
 * Material is protected by worldwide copyright and trade secret laws and
 * treaty provisions. No part of the Material may be used, copied, reproduced,
 * modified, published, uploaded, posted, transmitted, distributed, or
 * disclosed in any way without Intel's prior express written permission.
 *
 * No license under any patent, copyright, trade secret or other intellectual
 * property right is granted to or conferred upon you by disclosure or delivery
 * of the Materials, either expressly, by implication, inducement, estoppel or
 * otherwise. Any license under such intellectual property rights must be
 * express and approved by Intel in writing.
 * ============================================================================
 */
package com.intel.tools.fdk.graphframework.displayer.controller;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;

/** Zoom/Unzoom a ComponentLayoutDisplayer by when CTRL+Scroll is realized by the user */
public class ZoomController {

    private static final int KEY_0 = 224;

    /** Zoom step applied after a zoom action is required */
    private static double zoomStep = 1.1;

    public ZoomController(final GraphDisplayer displayer) {
        displayer.getControl().addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseScrolled(final MouseEvent e) {
                // CTRL + mouse wheel -> zoom
                if ((e.stateMask & SWT.CTRL) == SWT.CTRL) {
                    zoom(displayer, e.count > 0 ? zoomStep : 1 / zoomStep);
                }
            }
        });
        displayer.getControl().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if ((e.stateMask & SWT.CTRL) == SWT.CTRL) {
                    if (e.keyCode == SWT.KEYPAD_ADD) {
                        // Ctrl +
                        zoom(displayer, zoomStep);
                    } else if (e.keyCode == SWT.KEYPAD_SUBTRACT) {
                        // Ctrl -
                        zoom(displayer, 1 / zoomStep);
                    } else if (e.keyCode == KEY_0 || e.keyCode == SWT.KEYPAD_0) {
                        // Ctrl 0
                        FitToScreenController.fitToScreen(displayer);
                    }
                }
            }
        });
    }

    private void zoom(final GraphDisplayer displayer, final double factor) {
        displayer.setScale(displayer.getScale() * factor);
    }

}
