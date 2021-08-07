package org.openjump.core.ui.plugin.extension;

import org.openjump.core.ui.plugin.measuretoolbox.plugins.ToolboxMeasurePlugIn;
import org.openjump.core.ui.plugin.measuretoolbox.scale.G_InstallShowScalePlugIn;

import com.vividsolutions.jump.workbench.plugin.Extension;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;

public class MeasureExtension extends Extension {

  private static final String NAME = "Measure Extension for OpenJUMP(Giuseppe Aruta)";
  private static final String VERSION = "2.1.0 (2021-08-07)";

  @Override
	public String getName() {
        return NAME;
    }

  @Override
	public String getVersion() {
        return VERSION;
    }

  @Override
	public void configure(PlugInContext context) throws Exception {

        new G_InstallShowScalePlugIn().initialize(context);
      
        new ToolboxMeasurePlugIn().initialize(context);

    }
}
