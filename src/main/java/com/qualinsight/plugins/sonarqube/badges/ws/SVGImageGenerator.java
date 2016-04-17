/*
 * qualinsight-plugins-sonarqube-badges
 * Copyright (c) 2015-2016, QualInsight
 * http://www.qualinsight.com/
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program. If not, you can retrieve a copy
 * from <http://www.gnu.org/licenses/>.
 */
package com.qualinsight.plugins.sonarqube.badges.ws;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.ServerSide;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * Generates SVG images.
 *
 * @author Michel Pawlak
 */
@ServerSide
public final class SVGImageGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SVGImageGenerator.class);

    private static final String SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg";

    private static final String QUALIFIED_NAME = "svg";

    private static final String COMMENT_STRING = "Generated by QualInsight SVG Badge Generator";

    private static final int FONT_SIZE = 11;

    /**
     * Ugly hack but I could not find a better way
     */
    private static final String FONT_NAME = "THIS_FONT_DOES_NOT_EXIST_ON_PURPOSE";

    private static final Font FONT = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE);

    private static final int X_MARGIN = 4;

    private static final int CANVAS_HEIGHT = 20;

    private static final int BACKGROUND_CORNER_ARC_DIAMETER = 6;

    private static final Color COLOR_BACKGROUND_LABEL = new Color(85, 85, 85, 255);

    private static final Color COLOR_SHADOW = new Color(0, 0, 0, 85);

    private static final Color COLOR_TEXT = new Color(255, 255, 255, 255);

    private static final int Y_OFFSET_SHADOW = 14;

    private static final int Y_OFFSET_TEXT = 14;

    private SVGGeneratorContext svgGeneratorContext;

    public static class Data {

        private String labelText;

        private int labelWidth;

        private String contentText;

        private int contentWidth;

        private Color contentBackgroundColor;

        private Data() {
        }

        public Data withLabelText(final String labelText) {
            this.labelText = labelText;
            return this;
        }

        public Data withLabelWidth(final int labelWidth) {
            this.labelWidth = labelWidth;
            return this;
        }

        public Data withContentText(final String contentText) {
            this.contentText = contentText;
            return this;
        }

        public Data withContentWidth(final int contentWidth) {
            this.contentWidth = contentWidth;
            return this;
        }

        public Data withContentBackgroundColor(final Color contentBackgroundColor) {
            this.contentBackgroundColor = contentBackgroundColor;
            return this;
        }

        public static Data create() {
            return new Data();
        }

    }

    /**
     * {@link SVGImageGenerator} IoC constructor.
     *
     */
    public SVGImageGenerator() {
        final DOMImplementation domImplementation = GenericDOMImplementation.getDOMImplementation();
        final Document document = domImplementation.createDocument(SVG_NAMESPACE_URI, QUALIFIED_NAME, null);
        this.svgGeneratorContext = SVGGeneratorContext.createDefault(document);
        this.svgGeneratorContext.setComment(COMMENT_STRING);
        LOGGER.info("SVGImageGenerator is now ready.");
    }

    public SVGGraphics2D generateFor(final Data data) {
        // new SVG graphics
        final SVGGraphics2D svgGraphics2D = new SVGGraphics2D(this.svgGeneratorContext, false);
        // set SVG canvas size
        svgGraphics2D.setSVGCanvasSize(new Dimension(data.labelWidth + data.contentWidth, CANVAS_HEIGHT));
        // set font
        svgGraphics2D.setFont(FONT);
        // draw Label background
        svgGraphics2D.setColor(COLOR_BACKGROUND_LABEL);
        svgGraphics2D.fillRoundRect(0, 0, data.labelWidth, CANVAS_HEIGHT, BACKGROUND_CORNER_ARC_DIAMETER, BACKGROUND_CORNER_ARC_DIAMETER);
        svgGraphics2D.fillRect(data.labelWidth - BACKGROUND_CORNER_ARC_DIAMETER, 0, BACKGROUND_CORNER_ARC_DIAMETER, CANVAS_HEIGHT);
        // draw Label text shadow
        svgGraphics2D.setColor(COLOR_SHADOW);
        svgGraphics2D.drawString(data.labelText, X_MARGIN, Y_OFFSET_SHADOW);
        // draw Label text
        svgGraphics2D.setColor(COLOR_TEXT);
        svgGraphics2D.drawString(data.labelText, X_MARGIN, Y_OFFSET_TEXT);
        // draw result background
        svgGraphics2D.setColor(data.contentBackgroundColor);
        svgGraphics2D.fillRoundRect(data.labelWidth, 0, data.contentWidth, CANVAS_HEIGHT, BACKGROUND_CORNER_ARC_DIAMETER, BACKGROUND_CORNER_ARC_DIAMETER);
        svgGraphics2D.fillRect(data.labelWidth, 0, BACKGROUND_CORNER_ARC_DIAMETER, CANVAS_HEIGHT);
        // draw result text shadow
        svgGraphics2D.setColor(COLOR_SHADOW);
        svgGraphics2D.drawString(data.contentText, data.labelWidth + X_MARGIN, 15);
        // draw result text
        svgGraphics2D.setColor(COLOR_TEXT);
        svgGraphics2D.drawString(data.contentText, data.labelWidth + X_MARGIN, 14);
        return svgGraphics2D;
    }
}
