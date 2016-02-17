/* ============================================================================
 * INTEL CONFIDENTIAL
 *
 * Copyright 2016 Intel Corporation All Rights Reserved.
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
package com.intel.tools.utils.widgets.viewerfiltercomposite;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filter for Structured content viewers
 */
public abstract class ExtendedViewerFilter extends ViewerFilter {
    /**
     * The filter pattern
     */
    protected String filterString = "";

    /**
     * The flag that indicates the filter has been modified
     */
    protected boolean isModified = false;

    /**
     * The case sensitive option
     */
    protected boolean isCaseSensitive = false;

    /**
     * The regular expression option
     */
    protected boolean isRegex = false;

    /**
     * The whole word option
     */
    protected boolean isWholeWord = false;

    /**
     * The number of filtered elements
     */
    protected int filteredElementCount;

    /**
     * The total number of elements
     */
    protected int totalElementCount;

    @Override
    public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
        boolean result = false;

        if ((filterString == null) || (filterString.isEmpty())) {
            clear();
            result = true;
        } else {
            try {
                final Pattern pattern = compilePattern();
                result = checkElement(viewer, pattern, parentElement, element);
            } catch (final PatternSyntaxException pse) {
            }
        }

        return result;
    }

    /**
     * @return the current {@link Pattern} instance
     * @throws PatternSyntaxException
     */
    public Pattern compilePattern() {
        final String pattern = getPatternString();
        return isCaseSensitive ? Pattern.compile(pattern) : Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    /**
     * @return the number of filtered elements
     */
    public int getFilteredElementCount() {
        return filteredElementCount;
    }

    /**
     * Define the number of filtered elements.
     *
     * @param filteredElementCount
     *            the number of filtered elements
     */
    public void setFilteredElementCount(final int filteredElementCount) {
        this.filteredElementCount = filteredElementCount;
    }

    /**
     * @return the total number of elements
     */
    public int getTotalElementCount() {
        return totalElementCount;
    }

    /**
     * Define the total number of elements.
     *
     * @param totalElementCount
     *            the total number of elements
     */
    public void setTotalElementCount(final int totalElementCount) {
        this.totalElementCount = totalElementCount;
    }

    /**
     * @return the modification flag
     */
    public boolean isModified() {
        return isModified;
    }

    /**
     * Define the modification flag
     *
     * @param isModified
     *            the modification flag to set
     */
    public void setModified(final boolean isModified) {
        this.isModified = isModified;
    }

    /**
     * @return the case sensitive flag
     */
    public boolean isCaseSensitive() {
        return isCaseSensitive;
    }

    /**
     * Define the case sensitive flag
     *
     * @param caseSensitive
     *            case sensitive flag
     */
    public void enableCaseSensitive(final boolean isCaseSensitive) {
        if (this.isCaseSensitive == isCaseSensitive) {
            return;
        }

        this.isCaseSensitive = isCaseSensitive;
        this.isModified = true;
    }

    /**
     * @return the RegEx activation flag
     */
    public boolean isRegEx() {
        return isRegex;
    }

    /**
     * Enabled or disable the regular expression.
     *
     * @param enabled
     */
    public void enableRegEx(final boolean enabled) {
        if (this.isRegex == enabled) {
            return;
        }

        this.isRegex = enabled;
        this.isModified = true;
    }

    /**
     * @return the Whole Word flag
     */
    public boolean isWholeWord() {
        return isWholeWord;
    }

    /**
     * Define the Whole Word flag.
     *
     * @param isWholeWord
     *            the Whole Word flag
     */
    public void enableWholeWord(final boolean isWholeWord) {
        if (this.isWholeWord == isWholeWord) {
            return;
        }

        this.isWholeWord = isWholeWord;
        this.isModified = true;
    }

    public void setFilterString(final String filterString) {
        if (this.filterString.equals(filterString)) {
            return;
        }

        clear();
        this.filterString = filterString;
        this.isModified = true;
    }

    public String getFilterString() {
        return filterString;
    }

    protected abstract boolean checkElement(Viewer viewer, Pattern pattern, Object parentElement, Object element);

    protected void clear() {
        filteredElementCount = 0;
        totalElementCount = 0;
    }

    /**
     * Convert the filter string to a regular expression pattern
     *
     * @return a regular expression
     */
    private String getPatternString() {
        String patternString;

        if (isRegEx()) {
            patternString = filterString;
        } else {
            // Convert all characters that need to be escaped: [](){}+^$.|\
            patternString = filterString.replaceAll("([\\[\\]\\(\\)\\{\\}\\+\\^\\$\\.\\|\\\\])", "\\\\$1");

            // Convert all wildcards into a standard regular expression: *?
            patternString = patternString.replaceAll("(?<!\\\\)(\\*)", ".$1").replaceAll("(?<!\\\\)(\\?)", ".");

            // Convert the "Whole word" option into a regular expression
            if (isWholeWord()) {
                patternString = "\\b(" + patternString + ")\\b";
            }
        }

        return patternString;
    }


}
