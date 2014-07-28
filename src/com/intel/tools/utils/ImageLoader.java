package com.intel.tools.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

public class ImageLoader {
    
    private static Map<String, Image> cache = new HashMap<>();

    private static Image getImage(InputStream stream) throws IOException {
        try {
            Display display = Display.getCurrent();
            ImageData data = new ImageData(stream);
            if (data.transparentPixel > 0) {
                return new Image(display, data, data.getTransparencyMask());
            }
            return new Image(display, data);
        } finally {
            stream.close();
        }
    }
    private static Image getPluginImageFromUrl(URL url) {
        try {
            Image image;

            InputStream stream = url.openStream();
            try {
                image = getImage(stream);
            } finally {
                stream.close();
            }

            return image;
        } catch (Throwable e) {
            // Ignore any exceptions
        }
        return null;
    }
    private static URL getPluginImageURL(String symbolicName, String path) {
        Bundle bundle = Platform.getBundle(symbolicName);
        if (bundle != null) {
            return bundle.getEntry(path);
        }
        // no such resource
        return null;
    }
    
    public static Image getPluginImage(String symbolicName, String path) {

        String key = symbolicName + "/" + path;
        Image image = null;
        
        if (cache.containsKey(key)) {
            image = cache.get(key);
        } else {      
            try {
                URL url = getPluginImageURL(symbolicName, path);
                if (url != null) {
                    image = getPluginImageFromUrl(url);
                }
            } catch (Throwable e) {
                // Ignore any exceptions
            }
            if (image != null) {
                cache.put(key, image);
            }
        }
        return image;
    }
}
