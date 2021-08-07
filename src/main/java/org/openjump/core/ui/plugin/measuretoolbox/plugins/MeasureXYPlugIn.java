package org.openjump.core.ui.plugin.measuretoolbox.plugins;

import javax.swing.ImageIcon;

import com.vividsolutions.jump.I18N;
import org.openjump.core.ui.plugin.measuretoolbox.cursortools.MeasureXYTool;
import org.openjump.core.ui.plugin.measuretoolbox.icons.IconLoader;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.cursortool.QuasimodeTool;

/**
 * Computes coordinates
 *
 * @author Giuseppe Aruta - Sept 1th 2015
 */
public class MeasureXYPlugIn extends AbstractPlugIn {

  private static final I18N i18n = I18N.getInstance("org.openjump.core.ui.plugin.measuretoolbox");
  private static final ImageIcon ICON = IconLoader.icon("Ruler_XY.gif");
  public static final String NAME = i18n
      .get("MeasureToolbox.MeasureTools.Coordinates");

  @Override
  public boolean execute(PlugInContext context) throws Exception {
    reportNothingToUndoYet(context);

    context.getLayerViewPanel().setCurrentCursorTool(QuasimodeTool.createWithDefaults(new MeasureXYTool(context)));

    return true;
  }

  public ImageIcon getIcon() {
    return ICON;
  }

  @Override
  public String getName() {
    return NAME;
  }

  public static MultiEnableCheck createEnableCheck(
      WorkbenchContext workbenchContext) {
    EnableCheckFactory checkFactory = workbenchContext.createPlugInContext().getCheckFactory();

    return new MultiEnableCheck().add(checkFactory
        .createWindowWithSelectionManagerMustBeActiveCheck());
  }
}
