package com.intel.tools.fdk.graphframework.displayer;

import org.eclipse.draw2d.FreeformFigure;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.geometry.Rectangle;

public class FDKViewPort extends FreeformViewport {

    private int margin;

    public FDKViewPort() {
        super();
    }

    /**
     * This method readjusts the scroll bars from the viewport including some margins.
     */
    @Override
    protected void readjustScrollBars() {
        if ((getContents() != null) && (getContents() instanceof FreeformFigure)) {
            final Rectangle clientArea = getClientArea();
            final Rectangle freeformExtent = ((FreeformFigure) getContents()).getFreeformExtent().getCopy();
            freeformExtent.union(0, 0, clientArea.width, clientArea.height);
            freeformExtent.setBounds(freeformExtent.x - margin, freeformExtent.y - margin,
                    freeformExtent.width + 2 * margin, freeformExtent.height + 2 * margin);
            ((FreeformFigure) getContents()).setFreeformBounds(freeformExtent);

            getVerticalRangeModel().setAll(freeformExtent.y, clientArea.height,
                    freeformExtent.bottom());
            getHorizontalRangeModel().setAll(freeformExtent.x, clientArea.width,
                    freeformExtent.right());
        }
    }

    public void setMargin(final int margin) {
        this.margin = margin;
    }
}
