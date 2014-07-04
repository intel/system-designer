/* ============================================================================
 * INTEL CONFIDENTIAL
 * 
 * Copyright 2013 - 2014 Intel Corporation All Rights Reserved.
 * 
 * The source code contained or described herein and all documents related to
 * the source code ("Material") are owned by Intel Corporation or its suppliers
 * or licensors. Title to the Material remains with Intel Corporation or its
 * suppliers and licensors. The Material contains trade secrets and proprietary
 * and confidential information of Intel or its suppliers and licensors. The
 * Material is protected by worldwide copyright and trade secret laws and treaty
 * provisions. No part of the Material may be used, copied, reproduced,
 * modified, published, uploaded, posted, transmitted, distributed, or disclosed
 * in any way without Intel's prior express written permission.
 * 
 * No license under any patent, copyright, trade secret or other intellectual
 * property right is granted to or conferred upon you by disclosure or delivery
 * of the Materials, either expressly, by implication, inducement, estoppel or
 * otherwise. Any license under such intellectual property rights must be
 * express and approved by Intel in writing.
 * ============================================================================ */
/* -------------------------------------------------------------------------
 * Copyright (C) 2013 - 2014 Intel Mobile Communications GmbH
 * 
 * Sec Class: Intel Confidential (IC)
 * ---------------------------------------------------------------------- */
package com.intel.tools.utils.logging;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.intel.tools.utils.DirectoryUtils;

// import com.intel.tools.tfw.core.exceptions.TfwRuntimeException;

/**
 * Configuration for {@link org.apache.log4j.Logger log4j.Logger}. This class
 * configures the log properties and provides special behaviour, such as
 * throwing an exception on error and collecting usage statistics.
 * 
 * <P>
 * Note that when error logs are generated during unit testing, the
 * {@link TfwRuntimeException} (indeed any runtime exception) can be caught and
 * handled by the unit test. Unit tests are expected to trigger error logs.
 * 
 * <P>
 * Logging level can be controlled via the log4j.properties file, including
 * enabling and disabling of logging altogether. The deployed Tools Framework
 * application shall have logging of ERROR, WARN and INFO turned on - these logs
 * must be of low bandwidth - and this is not designed to be configurable by the
 * user.
 * 
 * <P>
 * A developer may enable DEBUG level logging on his local system, and this must
 * be of reasonably low bandwidth - for example, it may not continuously add to
 * the log file as the Tools Framework runs. A developer may enable TRACE level
 * logging on his local system, but this may result in high bandwidth trace
 * output.
 * 
 * <P>
 * These standard Logger methods are used in the Tools Framework:
 * 
 * <UL>
 * <LI>error() = ERROR level log: A <B>programming error</B> or other condition
 * that should be <B>called to the attention of the developers</B>. Logs of this
 * level <B>must not be able to be triggered by user action</B>. There must
 * never be logs of this type in released code and <B>further Tools Framework
 * operation is not to be expected following this log</B>. As these logs are for
 * Tools Framework developers the logged text does not need to be descriptive,
 * but it must be enough to localize the line of code that caused the error.
 * Examples: A bug, overflow of a buffer that should never overflow.
 * 
 * <LI>warning() = WARN level log: A warning that an unexpected condition has
 * occurred. There should never be logs of this type with normal usage of
 * released code. Logs of this level <B>may be triggered by user action</B>.
 * Examples: Corrupted message data, missing file.
 * 
 * <LI>info() = INFO level log: Log information about a normal event which
 * describes program usage. The INFO level logs must be kept to a reasonably low
 * bandwidth such that it is <B>easy to follow Tools Framework usage from
 * reading the log file</B>. Care must be used not to have INFO level logs
 * produced from performance critical parts of the code, such as in the capture
 * and decode path. Example: User selects a function.
 * 
 * <LI>debug() = DEBUG level log: Log information which is only useful to the
 * developer working on the code. These logs are suppressed outside of the
 * development environment. Example: An internal function is called.
 * 
 * <LI>trace() = TRACE level log: Log information which is only useful to the
 * developer working on the code and includes logging of potentially
 * high-bandwidth data. These logs are suppressed outside of the development
 * environment. Example: Logging of decoded message data.
 * </UL>
 */
public final class Logging {

    /**
     * Default log4j configuration file.
     */
    private static final String LOG4J_PROPERTY_FILE = "log4j.properties";

    /**
     * Log4j configuration property.
     */
    private static final String LOG4J_CONFIGURATION_PROPERTY = "log4j.configuration";

    /**
     * Flag to indicate whether already initialized.
     */
    private static boolean initialized = false;

    /**
     * Private constructor.
     */
    private Logging() {
    }

    /**
     * Static initializer for the TfwLogger class. Reads the log4j.properties
     * file and configures log4j from it.
     * <P>
     * 
     * <UL>
     * <LI>1st, look for a -Dlog4j.configuration setting pointing to a
     * log4j.properties file. An example is by using option
     * -Dlog4j.configuration=file:/C:/Users/stefan/workspace/log4j.properties in
     * the VM arguments of your Eclipse run configuration.
     * <LI>2nd, take a log4j.properties file in the current working directory.
     * <LI>3rd, use the log4j.properties file in the {@link Bundle} bundle
     * </UL>
     * 
     * @param bundle
     *            The bundle where to look for a log4j.properties file.
     */
    public static void start(Bundle bundle) {
        if (initialized) {
            return;
        }

        String log4jPropertyFile = null;

        // first, look for a -Dlog4j.configuration setting
        String log4jSysProp = System.getProperty(LOG4J_CONFIGURATION_PROPERTY);
        if (null != log4jSysProp && new File(log4jSysProp).exists()) {
            log4jPropertyFile = log4jSysProp;

        } else if (new File(LOG4J_PROPERTY_FILE).exists()) {
            log4jPropertyFile = LOG4J_PROPERTY_FILE;

        } else {
            String file = null;

            if (bundle != null) {
                URL url = bundle.getEntry(LOG4J_PROPERTY_FILE);
                try {
                    file = FileLocator.toFileURL(url).getFile();
                    if ((new File(file)).exists()) {
                        log4jPropertyFile = file;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (null != log4jPropertyFile) {
            PropertyConfigurator.configure(log4jPropertyFile);

        } else {
            // Exceptionally, write to the console
            System.err.println("Could not find initialize log4j correctly");
        }

        Logger logger = Logger.getLogger(Logging.class);
        logger.info("Log4j logger started");
        logger.info("Selected log4j.configuration file is " + log4jPropertyFile);

        initialized = true;
    }

    /**
     * Set the log file path if a FileAppender is available, else does nothing.
     */
    public static void setLogFilePath(String path) {
        FileAppender fileAppender = (FileAppender) Logger.getRootLogger().getAppender("FILE");
        
        if (fileAppender != null) {
        	fileAppender.setFile(path);
        	fileAppender.activateOptions();
        	Logger.getLogger(Logging.class).info("Log file set to : " + path);
        }
    }
    
    /**
     * same as <code>start(bundle)</code> where the bundle is utils bundle.
     */
    public static void start() {
        Bundle bundle = FrameworkUtil.getBundle(Logging.class);
        start(bundle);
    }
}
