/*
 * Copyright (C) 2017 Intel Corporation
 *
 * This Program is subject to the terms of the Eclipse Public License, v. 1.0.
 * If a copy of the license was not distributed with this file,
 * you can obtain one at <http://www.eclipse.org/legal/epl-v10.html>
 *
 * SPDX-License-Identifier: EPL-1.0
 */

package com.intel.audioviz.trace;


import java.security.InvalidParameterException;
import java.util.HashSet;

import com.intel.audioviz.AudioTrack;
import com.intel.audioviz.AudioVizException;

/**
 * An AudioTrace is an abstract representation of an AudioTrack's channel part. The part exposed by the AudioTrace is
 * controlled through an AudioTraceView.
 */
public abstract class AudioTrace {

    /**
     * Instantiate an AudioTrace for a given channel number of an AudioTrack.
     *
     * @param audioTrack
     *            The AudioTrack
     * @param channel
     *            The channel number of the AudioTrack
     */
    public AudioTrace(final AudioTrack audioTrack, final int channel) {
        if (channel < 0 || channel >= audioTrack.getAudioFormat().getChannelCount()) {
            throw new InvalidParameterException("Invalid channel number");
        }

        mAudioTrack = audioTrack;
        mChannel = channel;
        mSetTraceViewThread = null;
        mAudioTraceListeners = new HashSet<IAudioTraceListener>();
        mIsDisposed = false;

        clear();
    }

    /**
     * @return the AudioTrack this AudioTrace belongs to.
     */
    public final AudioTrack getAudioTrack() {
        return mAudioTrack;
    }

    /**
     * @return the AudioTrack's channel number this AudioTrace belongs to.
     */
    public final int getChannel() {
        return mChannel;
    }

    /**
     * Dispose AudioTrace. Any ongoing setTraceView is interrupted.
     */
    public void dispose() {
        if (!mIsDisposed) {
            synchronized (mAudioTraceListeners) {
                mAudioTraceListeners.clear();
            }
            mIsDisposed = true;
            clear();
        }
    }

    /**
     * Set the desired range and unit of the trace. Since the computation of the requested range may require a lot of
     * time, the trace is updated in a background Thread. In order to know when the trace is completed, the caller may
     * register itself as IAudioTraceListener.
     *
     * @param audioTraceView
     *            The range to be applied
     * @param strict
     *            If true, the requested view is strictly applied, if false, the view of the trace may be larger than
     *            the requested one.
     * @note The set view is always limited by the AudioTrack channel's length.
     * @see addAudioTraceListener()
     */
    public final void setTraceView(final AudioTraceView audioTraceView, final boolean strict) {
        if (!audioTraceView.isValid()) {
            throw new InvalidParameterException("Invalid AudioTraceView");
        }

        /* Stop on going thread if it is still computing the last and now obsolete setTraceView() */
        stopDoSetTraceViewThread();
        if (mIsDisposed) {
            throw new AudioVizException("AudioTrace is disposed");
        }
        // According to the 'strict' argument, is the requested AudioTraceView different that the last one ?
        final boolean isAudioTraceViewDifferent = strict ? !mAudioTraceView.equals(audioTraceView)
                : !mAudioTraceView.include(audioTraceView);
        /*
         * Execute the doSetTraceView() only if the last request was not completed or if the new AudioTraceView is
         * different.
         */
        if (!isAudioTraceViewRequestCompleted() || isAudioTraceViewDifferent) {
            mAudioTraceView = audioTraceView;

            /* Spawn a thread to compute the last setTraceView() */
            mSetTraceViewThread = new Thread(() -> {
                int delay = INITIAL_DELAY;
                try {
                    do {
                        synchronized (AudioTrace.this) {
                            doSetTraceView(mAudioTraceView);
                        }
                        notifyListenner();
                        Thread.sleep(delay);
                        delay = Math.min(delay * 2, MAXIMUM_DELAY);
                    } while (!isAudioTraceViewRequestCompleted());
                    notifyListenner();
                } catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            });
            mSetTraceViewThread.start();
        } else {
            notifyListenner();
        }
    }

    /**
     * @return The last view set.
     */
    public final AudioTraceView getAudioTraceView() {
        return mAudioTraceView;
    }

    /**
     * Clear the trace. The trace will remain empty until a subsequent call to setTraceView() is made.
     *
     * @see setTraceRange()
     */
    public final synchronized void clear() {
        stopDoSetTraceViewThread();
        mAudioTraceView = AudioTraceView.INVALID_AUDIO_TRACE_RANGE;
        doClear();
        notifyListenner();
    }

    /**
     * Add a listener to the Trace. The listener will be notified each time the AudioTrace data may have changed.
     *
     * @param audioTraceListener
     *            The listener to be registered
     * @return true if the listener was not yet registered, false otherwise
     */
    public final boolean addAudioTraceListener(final IAudioTraceListener audioTraceListener) {
        synchronized (mAudioTraceListeners) {
            return mAudioTraceListeners.add(audioTraceListener);
        }
    }

    /**
     * Remove a listener to the Trace. The listener will no more be notified each time the AudioTrace data may have
     * changed once the method has returned.
     *
     * @param audioTraceListener
     *            The listener to be unregistered
     * @return true if the listener was registered before this call
     */
    public final boolean removeAudioTraceListener(final IAudioTraceListener audioTraceListener) {
        synchronized (mAudioTraceListeners) {
            return mAudioTraceListeners.remove(audioTraceListener);
        }
    }

    private void notifyListenner() {
        synchronized (mAudioTraceListeners) {
            for (final IAudioTraceListener audioTraceListener : mAudioTraceListeners) {
                audioTraceListener.traceChanged(this);
            }
        }
    }

    private void stopDoSetTraceViewThread() {
        if (mSetTraceViewThread != null) {
            mSetTraceViewThread.interrupt();

            boolean joined = false;
            do {
                try {
                    mSetTraceViewThread.join();
                    joined = true;
                } catch (final InterruptedException e) {
                }
            } while (!joined);
        }
    }

    /**
     * @return true if the last AudioTraceRangeRequest has been completed.
     */
    public abstract boolean isAudioTraceViewRequestCompleted();

    /**
     * Do the computation of the AudioTraceRange requested. This subclass method is safely called from a dedicated
     * Thread spawned by the AudioTrace abstract class.
     */
    protected abstract void doSetTraceView(AudioTraceView audioTraceView);

    /**
     * Do the AudioTrace data clear. This subclass method is called with the guarantee to not be called while
     * doSetTraceView() is.
     */
    protected abstract void doClear();

    private final int mChannel;
    private final AudioTrack mAudioTrack;
    private final HashSet<IAudioTraceListener> mAudioTraceListeners;
    private Thread mSetTraceViewThread;
    private AudioTraceView mAudioTraceView;
    private boolean mIsDisposed;

    /**
     * The thread which handles execution of the doSetTraceView() will wait before recall it in case the trace has not
     * been fully computed. The first time, the thread will wait for INITIAL_DELAY milliseconds. Then for each
     * subsequent recall of doSetTraceView(), the thread doubles the time to wait before next recall if the trace is not
     * fully computed again. The delay is doubled each iteration up to MAXIMUM_DELAY milliseconds which is the maximum
     * wait duration between two subsequent calls to doSetTraceView().
     */
    private static final int INITIAL_DELAY = 25;
    private static final int MAXIMUM_DELAY = 1000;
}
