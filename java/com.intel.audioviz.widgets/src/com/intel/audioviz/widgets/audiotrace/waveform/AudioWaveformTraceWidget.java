/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */

package com.intel.audioviz.widgets.audiotrace.waveform;

import java.util.Calendar;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.visualization.xygraph.figures.Axis;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.intel.audioviz.AudioTrack;
import com.intel.audioviz.trace.AudioTrace;
import com.intel.audioviz.trace.AudioTraceController;
import com.intel.audioviz.trace.AudioWaveformTrace;
import com.intel.audioviz.widgets.AudioTraceWidget;

/**
 * AudioWaveformTraceWidget uses a nebula XYGraph to render an AudioWaveformTrace.
 */
public class AudioWaveformTraceWidget extends AudioTraceWidget
implements Listener, MouseWheelListener, MouseMotionListener, FocusListener {

    enum AxisUnit {
        NONE, TIME, SAMPLE, ALL
    }

    public AudioWaveformTraceWidget(final Composite parent, final int style) {
        super(parent, style);
        mAudioWaveformTrace = null;
        mGraphAudioWaveformTrace = null;
        mXAxisUnit = AxisUnit.TIME;
        mCursorX = 0;
        mCursorFigure = null;

        super.setLayout(new FillLayout());

        mGraphCanvas = new Canvas(this, SWT.NONE);
        final LightweightSystem lws = new LightweightSystem(mGraphCanvas);
        createPopupMenu();

        mGraph = new XYGraph();
        mGraph.getPrimaryYAxis().setTitle("");
        mGraph.getPrimaryYAxis().setShowMajorGrid(true);
        mGraph.getPrimaryYAxis().setRange(DEFAULT_Y_AXIS_RANGE);
        mGraph.getPrimaryXAxis().setTitle("");
        mGraph.getPrimaryXAxis().setFormatPattern(DECIMAL_PATTERN);
        mGraph.getPrimaryXAxis().setVisible(false);
        mGraph.setShowLegend(false);
        mGraph.setTransparent(true);

        final Axis timeAxis = new Axis("", false);
        timeAxis.setTimeUnit(Calendar.MILLISECOND);
        timeAxis.setDateEnabled(true);
        timeAxis.setVisible(false);
        mGraph.addAxis(timeAxis);

        lws.setContents(mGraph);

        mGraphCanvas.addListener(SWT.Resize, this);
        mGraphCanvas.addFocusListener(this);
        mGraphCanvas.addMouseWheelListener(this);
        mGraph.getPlotArea().addMouseMotionListener(this);

        mGraph.setVisible(true);
    }

    public void setXAxisUnit(final AxisUnit xAxisUnit) {
        mXAxisUnit = xAxisUnit;
        update();
    }

    @Override
    public void setLayout(final Layout layout) {
        // Internally control the layout of widget required to render the AudioWaveformTrace.
    }

    @Override
    public void update() {
        super.update();

        mGraph.getXAxisList().get(X_SAMPLE_AXIS_INDEX).setVisible(
                mXAxisUnit == AxisUnit.SAMPLE || mXAxisUnit == AxisUnit.ALL);

        mGraph.getXAxisList().get(X_TIME_AXIS_INDEX).setVisible(
                mXAxisUnit == AxisUnit.TIME || mXAxisUnit == AxisUnit.ALL);

        updateCursor();

        if (getAudioTraceController().getUnit() != AudioTraceController.INVALID_UNIT) {
            final int pixel = mGraph.getPlotArea().getClientArea().width;
            final Range sampleRange = new Range(
                    getAudioTraceController().getAudioSampleIndex(),
                    getAudioTraceController().getAudioSampleIndex()
                    + ((pixel - 1) * getAudioTraceController().getUnit()));

            if (mAudioWaveformTrace != null) {
                assert (mGraphAudioWaveformTrace != null);

                mGraphAudioWaveformTrace.setVisible(true);

                final double pixelPerTraceSample = 1 / getAudioTraceController().getUnit();

                if (pixelPerTraceSample > MINIMUM_PIXEL_PER_SAMPLE_TO_DISPLAY_POINT) {
                    mGraphAudioWaveformTrace.setAntiAliasing(true);
                    mGraphAudioWaveformTrace.setPointStyle(PointStyle.CIRCLE);
                    mGraphAudioWaveformTrace.setPointSize(
                            Math.min(
                                    (int) pixelPerTraceSample / MINIMUM_PIXEL_PER_SAMPLE_TO_DISPLAY_POINT,
                                    MAX_GRAPH_TRACE_POINT_SIZE));
                } else {
                    mGraphAudioWaveformTrace.setPointStyle(PointStyle.NONE);
                    mGraphAudioWaveformTrace.setAntiAliasing(false);
                }

                mGraph.getXAxisList().get(X_SAMPLE_AXIS_INDEX).setRange(sampleRange);

                final Range millisecondsRange = getMillisecondsRange(sampleRange);
                mGraph.getXAxisList().get(X_TIME_AXIS_INDEX).setRange(getMillisecondsRange(sampleRange));
                mGraph.getXAxisList().get(X_TIME_AXIS_INDEX).setFormatPattern(getTimePattern(millisecondsRange));
            }
        }
    }

    @Override
    public void handleEvent(final Event event) {
        if (mAudioWaveformTrace != null) {
            assert (mGraphAudioWaveformTrace != null);
            mGraphAudioWaveformTrace.setVisible(false);
            getAudioTraceController().setVisibleTraceSampleCount(mAudioWaveformTrace, getVisibleTraceSampleCount());
        }
    }

    @Override
    public void mouseScrolled(final MouseEvent mouseEvent) {
        if ((mouseEvent.stateMask & SWT.CONTROL) == SWT.CONTROL) {

            deleteCursor();

            final int plotAreaX = mCursorX - mGraph.getPlotArea().getClientArea().x;
            final long sampleIndex = getAudioTraceController().getAudioSampleIndex()
                    + (long) Math.floor(plotAreaX * getAudioTraceController().getUnit());

            if (mouseEvent.count > 0) {
                getAudioTraceController().zoomIn(sampleIndex);
            } else if (mouseEvent.count < 0) {
                getAudioTraceController().zoomOut(sampleIndex);
            }
        }
    }

    @Override
    public void focusGained(final FocusEvent e) {
        updateCursor();
    }

    @Override
    public void focusLost(final FocusEvent e) {
        deleteCursor();
    }

    @Override
    public void mouseDragged(final org.eclipse.draw2d.MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(final org.eclipse.draw2d.MouseEvent arg0) {
    }

    @Override
    public void mouseExited(final org.eclipse.draw2d.MouseEvent arg0) {
    }

    @Override
    public void mouseHover(final org.eclipse.draw2d.MouseEvent arg0) {
    }

    @Override
    public void mouseMoved(final org.eclipse.draw2d.MouseEvent arg0) {
        mCursorX = arg0.x;
        updateCursor();
    }

    @Override
    protected int getVisibleTraceSampleCount() {
        if (mAudioWaveformTrace != null) {
            assert (mGraphAudioWaveformTrace != null);
            return Math.max(0, mGraph.getPlotArea().getClientArea().width);
        } else {
            return 0;
        }
    }

    @Override
    protected String getAudioTraceWidgetFriendlyName() {
        return AUDIO_TRACE_WIDGET_FRIENDLY_NAME;
    }

    @Override
    protected AudioTrace doSetAudioTrace(final AudioTrack audioTrack, final int channel) {
        final AudioWaveformTrace audioWaveformTrace = audioTrack.getAudioWaveformTrace(channel);

        if (mAudioWaveformTrace != null) {
            mAudioWaveformTrace = null;
            mGraph.removeTrace(mGraphAudioWaveformTrace);
            mGraphAudioWaveformTrace = null;
        }
        if (audioWaveformTrace != null) {
            mAudioWaveformTrace = audioWaveformTrace;

            mGraphAudioWaveformTrace = new Trace(
                    "",
                    mGraph.getPrimaryXAxis(),
                    mGraph.getPrimaryYAxis(),
                    new AudioWaveformTraceAdapter(mAudioWaveformTrace));
            mGraphAudioWaveformTrace.setAntiAliasing(false);
            mGraphAudioWaveformTrace.setLineWidth(1);
            /** @todo Consider add a preference for trace color */
            mGraphAudioWaveformTrace.setTraceColor(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
            mGraph.addTrace(mGraphAudioWaveformTrace);
        } else {
            mGraph.getXAxisList().get(X_SAMPLE_AXIS_INDEX).setVisible(false);
            mGraph.getXAxisList().get(X_TIME_AXIS_INDEX).setVisible(false);
        }

        return mAudioWaveformTrace;
    }

    private void updateCursor() {
        deleteCursor();
        if (mGraphCanvas.isFocusControl()) {
            final int plotAreaOffset = mGraph.getPlotArea().getClientArea().x;
            final int plotAreaZeroX = mCursorX - plotAreaOffset;
            final int sampleAlignedX = (int) (Math.floor(plotAreaZeroX * getAudioTraceController().getUnit())
                    / getAudioTraceController().getUnit());
            final int sampleAlignedCursorX = Math.max(sampleAlignedX, 1) + plotAreaOffset - 1;

            mCursorFigure = new RectangleFigure();
            mCursorFigure.setForegroundColor(Display.getDefault().getSystemColor(SWT.COLOR_RED));
            mCursorFigure.setBounds(new Rectangle(sampleAlignedCursorX, mGraph.getPlotArea().getClientArea().y, 1,
                    mGraph.getPlotArea().getClientArea().height));
            mGraph.getPlotArea().add(mCursorFigure);
        }
    }

    private void deleteCursor() {
        if (mCursorFigure != null) {
            mGraph.getPlotArea().remove(mCursorFigure);
            mCursorFigure = null;
        }
    }

    private void createPopupMenu() {
        final Menu waveformMenu = new Menu(mGraphCanvas);

        final MenuItem setXUnitMenu = new MenuItem(waveformMenu, SWT.CASCADE);
        setXUnitMenu.setText("X Axis Unit");

        final Menu xUnitMenu = new Menu(waveformMenu);
        setXUnitMenu.setMenu(xUnitMenu);

        final MenuItem noUnitMenu = new MenuItem(xUnitMenu, SWT.RADIO);
        noUnitMenu.setText("None");
        final MenuItem sampleUnitMenu = new MenuItem(xUnitMenu, SWT.RADIO);
        sampleUnitMenu.setText("Sample Index");
        final MenuItem timeUnitMenu = new MenuItem(xUnitMenu, SWT.RADIO);
        timeUnitMenu.setText("Time");
        final MenuItem allUnitMenu = new MenuItem(xUnitMenu, SWT.RADIO);
        allUnitMenu.setText("All");

        noUnitMenu.setSelection(mXAxisUnit == AxisUnit.NONE);
        timeUnitMenu.setSelection(mXAxisUnit == AxisUnit.TIME);
        sampleUnitMenu.setSelection(mXAxisUnit == AxisUnit.SAMPLE);
        allUnitMenu.setSelection(mXAxisUnit == AxisUnit.ALL);

        noUnitMenu.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final MenuItem item = (MenuItem) e.widget;
                if (item.getSelection()) {
                    AudioWaveformTraceWidget.this.setXAxisUnit(AxisUnit.NONE);
                }
            }
        });
        timeUnitMenu.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final MenuItem item = (MenuItem) e.widget;
                if (item.getSelection()) {
                    AudioWaveformTraceWidget.this.setXAxisUnit(AxisUnit.TIME);
                }
            }
        });
        sampleUnitMenu.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final MenuItem item = (MenuItem) e.widget;
                if (item.getSelection()) {
                    AudioWaveformTraceWidget.this.setXAxisUnit(AxisUnit.SAMPLE);
                }
            }
        });
        allUnitMenu.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final MenuItem item = (MenuItem) e.widget;
                if (item.getSelection()) {
                    AudioWaveformTraceWidget.this.setXAxisUnit(AxisUnit.ALL);
                }
            }
        });

        mGraphCanvas.setMenu(waveformMenu);
    }

    private Range getMillisecondsRange(final Range sampleRange) {
        final long sampleFrequency = mAudioWaveformTrace.getAudioTrack().getAudioFormat().getSampleFrequency();

        return new Range(
                sampleRange.getLower() * 1000 / sampleFrequency,
                sampleRange.getUpper() * 1000 / sampleFrequency);
    }

    private String getTimePattern(final Range millisecondsRange) {
        if (millisecondsRange.getLower() < SECOND_IN_MILLISECONDS
                && millisecondsRange.getUpper() < SECOND_IN_MILLISECONDS) {
            return TIME_PATTERN_MILLISECONDS;
        } else if (millisecondsRange.getLower() < MINUTE_IN_MILLISECONDS
                && millisecondsRange.getUpper() < MINUTE_IN_MILLISECONDS) {
            return TIME_PATTERN_SECONDS;
        } else if (millisecondsRange.getLower() < HOUR_IN_MILLISECONDS
                && millisecondsRange.getUpper() < HOUR_IN_MILLISECONDS) {
            return TIME_PATTERN_MINUTES;
        } else {
            return TIME_PATTERN_HOURS;
        }
    }

    private AudioWaveformTrace mAudioWaveformTrace;
    private Trace mGraphAudioWaveformTrace;
    private final XYGraph mGraph;
    private AxisUnit mXAxisUnit;
    private final Canvas mGraphCanvas;
    private int mCursorX;
    private RectangleFigure mCursorFigure;

    private static final int X_SAMPLE_AXIS_INDEX = 0;
    private static final int X_TIME_AXIS_INDEX = 1;

    private static final String TIME_PATTERN_MILLISECONDS = "S'ms'";
    private static final String TIME_PATTERN_SECONDS = "ss.SSS's'";
    private static final String TIME_PATTERN_MINUTES = "mm:ss";
    /**
     * @todo check why this HOURS pattern makes the X axis bug shifting +1 hour
     * Might be a bug in Graph ?
     */
    private static final String TIME_PATTERN_HOURS = "hh'h'mm:ss";
    private static final String DECIMAL_PATTERN = "#########0";

    private static final double SECOND_IN_MILLISECONDS = 1000d;
    private static final double MINUTE_IN_MILLISECONDS = 1000d * 60d;
    private static final double HOUR_IN_MILLISECONDS = 1000d * 60d * 60d;

    private static final int MAX_GRAPH_TRACE_POINT_SIZE = 20;
    private static final int MINIMUM_PIXEL_PER_SAMPLE_TO_DISPLAY_POINT = 5;

    private static final Range DEFAULT_Y_AXIS_RANGE = new Range(-1.0d, 1.0d);

    private static final String AUDIO_TRACE_WIDGET_FRIENDLY_NAME = "Waveform";
}
