package org.openjump.core.ui.plugin.measuretoolbox.plugins;

import javax.swing.Icon;
import javax.swing.JPopupMenu;

import com.vividsolutions.jump.I18N;
import org.openjump.core.ui.plugin.measuretoolbox.icons.IconLoader;

import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.StandardCategoryNames;
import com.vividsolutions.jump.workbench.model.UndoableCommand;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;

/**
 * clean measurements
 *
 * @author Giuseppe Aruta - Sept 1th 2015
 */
public class CleanMeasurePlugIn extends AbstractPlugIn {

  private static final I18N i18n = I18N.getInstance("org.openjump.core.ui.plugin.measuretoolbox");

  private static final String NAME = i18n
      .get("MeasureToolbox.MeasurePlugin.CleanMeasurePlugIn.name");

  public static final Icon ICON = IconLoader.icon("cross.png");

  public static final String LAYER_NAME = i18n.get("MeasureToolbox.layer");

  //Geometry measureGeometry = null;
  //double area;
  //double distance;

  @Override
  public void initialize(PlugInContext context) {

    JPopupMenu popupMenu = LayerViewPanel.popupMenu();
    context.getFeatureInstaller().addPopupMenuPlugin(popupMenu, this,
        getName(), false, null, // to do: add icon
        getEnableCheck(context));
  }

  @Override
  public boolean execute(final PlugInContext context) throws Exception {
    reportNothingToUndoYet(context);
    final Layer measureLayer = context.getLayerManager().getLayer(
        LAYER_NAME);
    final String catName = StandardCategoryNames.SYSTEM;
    if (measureLayer == null) {
      return false;

    } else {


      UndoableCommand cmd = new UndoableCommand(getName()) {
        @Override
        public void execute() {

          context.getLayerManager().remove(measureLayer);
        }

        @Override
        public void unexecute() {
          context.getLayerManager().addLayerable(catName,
              measureLayer);


        }
      };
      execute(cmd, context);
      return true;

    }

  }

  public MultiEnableCheck getEnableCheck(PlugInContext context) {

    EnableCheckFactory checkFactory = context.getCheckFactory();

    return new MultiEnableCheck()
        .add(checkFactory
            .createWindowWithSelectionManagerMustBeActiveCheck())
        .add(checkFactory.createExactlyNFeaturesMustBeSelectedCheck(1))
        .add(checkFactory
            .createExactlyOneSelectedLayerMustBeEditableCheck());
  }

  public Icon getIcon() {
    return ICON;
  }

  @Override
  public String getName() {
    return NAME;
  }

}
