/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */

package com.intel.audioviz.example.handlers;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.intel.audioviz.AudioTrack;
import com.intel.audioviz.file.FileAudioTrackFactoryException;
import com.intel.audioviz.file.RawFileAudioTrack;
import com.intel.audioviz.file.RawFileAudioTrackProvider;

public abstract class OpenAudioTrackHandler {

    @Execute
    public void execute(
            final Shell shell,
            final RawFileAudioTrackProvider rawFileAudioTrackProvider,
            final EModelService modelService,
            final MApplication application,
            final EPartService partService,
            final IEclipseContext context) {
        try {
            /*
             * Request a RawFileAudioTrack without file specified: user will be prompted for file selection.
             */
            final RawFileAudioTrack fileAudioTrack = rawFileAudioTrackProvider.getAudioTrack();

            if (fileAudioTrack != null) {
                // Create an AudioTrackPart for the RawFileAudioTrack
                final MPart trackPart = MBasicFactory.INSTANCE.createPart();
                trackPart.setLabel(fileAudioTrack.getName());
                trackPart.setContributionURI(AUDIO_TRACK_PART_URI);
                trackPart.setCloseable(true);
                trackPart.setElementId(fileAudioTrack.getAbsolutePath());

                // Inject AudioTrack into the AudioTrackPart's private context
                final IEclipseContext partContext = context.createChild();
                partContext.set(AudioTrack.class, fileAudioTrack);
                // Let subclasses do their customization on the AudioTrackPart's private context
                setupAudioTrackPart(partContext);
                trackPart.setContext(partContext);

                // Add and activate Part to the com.intel.audioviz.example.audiotrackstack
                final MPartStack trackPartStacks = (MPartStack) modelService
                        .find("com.intel.audioviz.example.audiotrackstack", application);
                trackPartStacks.getChildren().add(trackPart);
                partService.showPart(trackPart, PartState.ACTIVATE);
            }
        } catch (final FileAudioTrackFactoryException e) {
            MessageDialog.openError(shell, "Error", "Error occured: " + e.getMessage());
        }
    }

    protected abstract void setupAudioTrackPart(IEclipseContext partContext);

    private static final String AUDIO_TRACK_PART_URI = "bundleclass://com.intel.audioviz.widgets"
            + "/com.intel.audioviz.widgets.AudioTrackPart";
}
