/*
 * SVGTraitsScreen.java
 *
 * Copyright � 1998-2011 Research In Motion Limited
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

package com.rim.samples.device.svgtraitsdemo;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.m2g.SVGImage;
import javax.microedition.m2g.ScalableGraphics;
import javax.microedition.m2g.ScalableImage;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.MainScreen;

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * Screen that displays an svg red rectangle with rounded corners. The user can
 * adjust the size of the rectangle with the trackball
 */
final class SVGTraitsScreen extends MainScreen {
    private static final String SVG_NAMESPACE_URI =
            "http://www.w3.org/2000/svg";
    private static final int MAX_WIDTH = Display.getWidth();
    private static final int MAX_HEIGHT = Display.getHeight();
    private static final int SCROLL_INCREMENT = 20;

    private SVGImage _image;
    private final ScalableGraphics _scalableGraphics;
    private SVGSVGElement _svgElement;
    private final SVGElement _rect1, _widthText, _heightText;

    /**
     * Constructor.
     */
    SVGTraitsScreen() {
        Document document = null;
        try {
            // Load the input stream.
            final InputStream inputStream =
                    getClass().getResourceAsStream("/sample.svg");

            // Load the render model image. Parameter value 'null' means no
            // external resource handler.
            _image = (SVGImage) ScalableImage.createImage(inputStream, null);

            // Get the document from the image.
            document = _image.getDocument();

            // Get the root svg element. <svg/>
            _svgElement = (SVGSVGElement) document.getDocumentElement();
        } catch (final IOException e) {
            System.exit(1);
        }

        // Extract id of rectangle from svg
        _rect1 = (SVGElement) document.getElementById("rect1");

        // Create text field to represent Width/Height labels.
        _widthText =
                (SVGElement) document
                        .createElementNS(SVG_NAMESPACE_URI, "text");
        _widthText.setTrait("font-family", "BBAlpha Sans");
        _widthText.setFloatTrait("font-size", 14);
        _widthText.setTrait("id", "width");
        _heightText =
                (SVGElement) document
                        .createElementNS(SVG_NAMESPACE_URI, "text");
        _heightText.setTrait("font-family", "BBAlpha Sans");
        _heightText.setFloatTrait("font-size", 14);
        _heightText.setTrait("id", "height");

        _svgElement.appendChild(_widthText);
        _svgElement.appendChild(_heightText);

        // Create our graphics context.
        _scalableGraphics = ScalableGraphics.createInstance();

        // Update current size of the rectangle
        update(0, 0);
    }

    /**
     * @see Manager#navigationMovement(int,int,int,int)
     */
    protected boolean navigationMovement(final int dx, final int dy,
            final int status, final int time) {
        int horizontal = 0, vertical = 0;

        if (dx < 0) {
            horizontal = -SCROLL_INCREMENT;
        } else if (dx > 0) {
            horizontal = SCROLL_INCREMENT;
        }

        if (dy < 0) {
            vertical = -SCROLL_INCREMENT;
        } else if (dy > 0) {
            vertical = SCROLL_INCREMENT;
        }

        update(horizontal, vertical);

        return true;
    }

    /**
     * Updates current size of the Rectangle and position and value of text
     * fields.
     * 
     * @param horizontal
     *            width update for rectangle (negative = decrease, positive =
     *            increase)
     * @param vertical
     *            height update for rectangle (negative = decrease, positive =
     *            increase)
     */
    private void update(final int horizontal, final int vertical) {
        if (_rect1.getFloatTrait("width") + horizontal >= SCROLL_INCREMENT
                && _rect1.getFloatTrait("height") + vertical >= SCROLL_INCREMENT
                && _rect1.getFloatTrait("width") + horizontal <= MAX_WIDTH
                && _rect1.getFloatTrait("height") + vertical <= MAX_HEIGHT) {
            // Update width and height of rectangle on trackball movement
            _rect1.setFloatTrait("width", _rect1.getFloatTrait("width")
                    + horizontal);
            _rect1.setFloatTrait("height", _rect1.getFloatTrait("height")
                    + vertical);

            // Update position and value of text fields
            _widthText.setFloatTrait("x", _rect1.getFloatTrait("x")
                    + _rect1.getFloatTrait("width") / 2);
            _widthText.setFloatTrait("y", _rect1.getFloatTrait("y")
                    + _rect1.getFloatTrait("height") + 20);
            _widthText.setTrait("#text", "" + _rect1.getFloatTrait("width"));

            _heightText.setFloatTrait("x", _rect1.getFloatTrait("x")
                    + _rect1.getFloatTrait("width") + 10);
            _heightText.setFloatTrait("y", _rect1.getFloatTrait("y")
                    + _rect1.getFloatTrait("height") / 2);
            _heightText.setTrait("#text", "" + _rect1.getFloatTrait("height"));

            // Repaint screen
            invalidate();
        }
    }

    /**
     * @see Field#paint
     */
    public void paint(final Graphics graphics) {
        super.paint(graphics);

        // Make sure image is non-null
        if (_image == null) {
            return;
        }

        // Bind target Graphics
        _scalableGraphics.bindTarget(graphics);

        // Set the viewport dimensions
        _image.setViewportWidth(MAX_WIDTH);
        _image.setViewportHeight(MAX_HEIGHT);

        // Render the svg image/ model
        _scalableGraphics.render(0, 0, _image);

        // Release bindings on Graphics
        _scalableGraphics.releaseTarget();
    }
}