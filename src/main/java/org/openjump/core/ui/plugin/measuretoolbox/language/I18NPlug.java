package org.openjump.core.ui.plugin.measuretoolbox.language;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.vividsolutions.jump.I18N;

public class I18NPlug {

  private static final ResourceBundle I18N_RESOURCE = ResourceBundle
      .getBundle(
          "org/openjump/core/ui/plugin/measuretoolbox/language/measure",
          new Locale(I18N.getLocale()));

  public static String getI18N(String key) {
    try {
      return I18N_RESOURCE.getString(key);
    } catch (MissingResourceException ex) {
      String[] labelpath = key.split("\\.");
      ex.printStackTrace();
      return labelpath[(labelpath.length - 1)];
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return "";
  }

}
