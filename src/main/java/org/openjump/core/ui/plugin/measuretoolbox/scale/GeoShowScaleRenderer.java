package org.openjump.core.ui.plugin.measuretoolbox.scale;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.openjump.core.ui.plugin.measuretoolbox.plugins.ToolboxMeasurePlugIn;
import org.openjump.core.ui.plugin.measuretoolbox.utils.CoordinateListMetrics_extended;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.Logger;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.Viewport;
import com.vividsolutions.jump.workbench.ui.renderer.SimpleRenderer;

/**
 *Class modified from ShowScaleRenderer.class to work also with Geographic 
 * coordinates (EPSG 4326) using Vincentine's algorithm<br>
 * [2015-09-15] paint  the scale factor into the view<br>
 * [2023-05-31] paint a simple scale bar into the view
 * @author Giuseppe Aruta - Sept 1th 2015)
*/
public class GeoShowScaleRenderer extends SimpleRenderer {


    public final static String CONTENT_ID = "GEO_SCALE_SHOW";
    /**
     * Height of the increment boxes, in view-space units.
     */
    private final static int BAR_HEIGHT = 13;
    private final static Color FILL = new Color(235,235,235);
    private final static Color LINE_COLOR = Color.GRAY;
    private final static int TEXT_BOTTOM_MARGIN = 1;
    private final static Color TEXT_COLOR = Color.black;
    private static final int MARGIN_X = 5;
    private static final int DESIRED_SCALE_BAR_LENGTH = 150;
    
    /**
     * Distance from the bottom edge, in view-space units.
     */
    private final int FONTSIZE = 14;
    private final static int VERTICAL_MARGIN = 2;
    private final static String ENABLED_KEY = "GEO_SCALE_SHOW_ENABLED";
    private Font FONT = new Font("Dialog", Font.BOLD, FONTSIZE);
    private Stroke stroke = new BasicStroke();
    
    // [mmichaud 2013-03-27, from org.openjump.core.ui.util.ScreenScale class] 
    // Toolkit.getDefaultToolkit().getScreenResolution()
  	// does not return the correct value as it does not know the physical 
  	// screen size.
  	// On modern computers, 96 ppi is a good approximation when screen is 
  	// at full resolution, while resolution returned by Toolkit is 120;
  	// Moreover, it seems that changing the screen resolution does not
  	// change the value returned by Toolkit.getDefaultToolkit().getScreenResolution()
    private static int resolution = 96;

    public GeoShowScaleRenderer(LayerViewPanel panel) {
        super(CONTENT_ID, panel);
    }

    public static double screenScale;

    @Override
	protected void paint(Graphics2D g) {
        if (!isEnabled(panel)) {
            return;
        }
        g.setStroke(stroke);
        screenScale=getScale(panel.getViewport());
        paintScaleFactor(g);
        paintScaleBar(g);
    }

    
    /**
     * Draw a scale factor into the view
     * @param g
     */

    private void paintScaleFactor(Graphics2D g) {
        Integer scaleD =  (int)Math.floor(screenScale);
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();
        String formatPattern = "#,##0";
        decimalFormat.applyPattern(formatPattern);
        String text = "1 : " + decimalFormat.format(scaleD.doubleValue());
        int length = text.length();
        Rectangle2D.Double shape = new Rectangle2D.Double(panel.getWidth()- (length+20)*3.6, 
                    barTop(), (length+20)*3.6-3, barBottom() - barTop());
        g.setColor(FILL);
        g.fill(shape);
        g.setColor(LINE_COLOR);
        g.draw(shape);
        g.setColor(TEXT_COLOR);
        int textBottomMargin = TEXT_BOTTOM_MARGIN;
        g.setFont(FONT);
        g.drawString(text, (float) (panel.getWidth()- (length+18)*3.6),
                (float) (barBottom() - textBottomMargin));
    }
    
    /**
     * get horizontal scale of the view
     * @param port
     * @return
     */
    
    public static   double getScale(Viewport port) {
        port=JUMPWorkbench.getInstance().getContext().getLayerViewPanel().getViewport();
        double horizontalScale = 0;
        double INCHTOCM = 2.54;
        double modelWidth;
        double panelWidth = port.getPanel().getWidth(); 
        if (ToolboxMeasurePlugIn.coordinateCheck.isSelected()) {
            Envelope envelope = port.getEnvelopeInModelCoordinates();
            double minx = envelope.getMinX();
            double maxx = envelope.getMaxX();
            double miny = envelope.getMinY();
            double maxy = envelope.getMaxY();
            modelWidth =  CoordinateListMetrics_extended.computeGeographicLength(maxy, maxx, miny, minx);
        } else {
            modelWidth = port.getEnvelopeInModelCoordinates().getWidth();
        }
        horizontalScale = modelWidth * 100 / (INCHTOCM / resolution * panelWidth);
        return horizontalScale;
    }
    

    /**
     * Draw a simple scale bar into the view
     * @param g
     */
    private void paintScaleBar(Graphics2D g)  {
        final int x = MARGIN_X-2;
        double horizontalScale = 0;
        horizontalScale = screenToRealWorldSize(DESIRED_SCALE_BAR_LENGTH);
        double round=roundScale(horizontalScale);
        final int width = (int)(DESIRED_SCALE_BAR_LENGTH * (round/ horizontalScale));
        g.setStroke(stroke);
        g.setColor(FILL);
        g.fillRect(x, barTop() - 2, width, barBottom() - barTop());
        g.setColor(Color.BLACK);
        g.drawRect(x, barTop() - 2, width, barBottom() - barTop());
        String str;
        if (horizontalScale < 1000.0) {
            str = "m";
        } else {
            str = "km";
            round /= 1000.0;
        }
        int i = (int)round;
        if (i == 0) {
            i = 1;
        }
        final String string = i + " " + str;
        g.setFont(FONT);
        g.drawString(string, x + 10, barTop() +10);
    }
 
    /**
     * Round the scale of the bar to nearest
     * 10 or multiple.<br> Example:<br>
     * if scale is 453 ->500<br>
     * if scale is 1134 ->1000<br>
     * @param double number
     * @return
     */
    public static double roundScale(final double n) {
        if (n < 1.0) {
            return Math.ceil(n);
        }
        final int n2 = (int)Math.pow(10.0, Math.floor(Math.log10(n)));
        return Math.round(n / n2) * (double)n2;
    }

    /**
     * Giving a length in pixel, it returns the real size in the real World
     * @param n
     * @return
     */
    public  double screenToRealWorldSize(final int n)  {
        final int x = panel.getWidth() / 2;
        final int n2 = panel.getHeight() / 2;
        final Point point = new Point(x, n2);
        final Point point2 = new Point(x + n, n2);
        Coordinate position = null, position2 = null;
        try {
            position = panel.getViewport().toModelCoordinate(point);
            position2 = panel.getViewport().toModelCoordinate(point2);
        } catch (NoninvertibleTransformException e) {
            Logger.error(e);
        }
        if (ToolboxMeasurePlugIn.coordinateCheck.isSelected()) {
            return  CoordinateListMetrics_extended.computeGeographicLength(position.y, position.x, 
                    position2.y, position2.x);
        } else {
            return position.distance(position2);
        }
        
    }
    private int barBottom() {
        return panel.getHeight() - VERTICAL_MARGIN;
    }

    private int barTop() {
        return barBottom() - BAR_HEIGHT;
    }


    public String text;

 
   
    
    
    /*********** getters and setters ******************/

    /**
     * 
     * @param panel
     * @return true if the scale is enabled in the LayerViewPanel
     */
    public static boolean isEnabled(LayerViewPanel panel) {
        return panel.getBlackboard().get(ENABLED_KEY, false);
    }

    public static void setEnabled(boolean enabled, LayerViewPanel panel) {
        panel.getBlackboard().put(ENABLED_KEY, enabled);
    }

    /**
     * @param myPlugInContext
     *            The myPlugInContext to set.
     */

}

