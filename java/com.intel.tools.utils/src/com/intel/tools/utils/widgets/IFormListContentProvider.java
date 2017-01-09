/* ============================================================================
 * INTEL CONFIDENTIAL
 *
 * Copyright 2015 Intel Corporation All Rights Reserved.
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
package com.intel.tools.utils.widgets;

import java.util.List;

public interface IFormListContentProvider<I, S> {
    /**
     * Listener interface
     */
    public static interface IFormListContentProviderListener {
        /**
         * Method fired when the content of the content provider has been updated.
         */
        public void contentUpdated();
    }

    /**
     * Add a listener to be notified when the content of the content provider is updated.
     *
     * @param listener
     *            The listener to notify
     */
    public void addListener(IFormListContentProviderListener listener);

    /**
     * Remove a listener from the list of notified listeners
     *
     * @param listener
     *            The listener to remove
     */
    public void removeListener(IFormListContentProviderListener listener);

    /**
     * get the list of main items to display
     *
     * @return
     */
    public List<I> getItemList();

    /**
     * Get the list of subItems to display
     *
     * @param item
     *            The parent item to get sub items for.
     * @return
     */
    public List<S> getSubItemList(I item);

    /**
     * Set the input object used as a source of data.
     * 
     * @param input
     */
    public void setInput(Object input);
}
