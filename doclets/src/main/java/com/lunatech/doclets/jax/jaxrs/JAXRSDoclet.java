/*
    Copyright 2009 Lunatech Research
    
    This file is part of jax-doclets.

    jax-doclets is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    jax-doclets is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with jax-doclets.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.lunatech.doclets.jax.jaxrs;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import javax.ws.rs.Path;

import com.lunatech.doclets.jax.JAXDoclet;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.model.ResourceClass;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.lunatech.doclets.jax.jaxrs.tags.*;
import com.lunatech.doclets.jax.jaxrs.writers.MethodWriter;
import com.lunatech.doclets.jax.jaxrs.writers.SummaryWriter;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.formats.html.HtmlDoclet;
import com.sun.tools.doclets.internal.toolkit.AbstractDoclet;
import com.sun.tools.doclets.internal.toolkit.taglets.LegacyTaglet;

public class JAXRSDoclet extends JAXDoclet<JAXRSConfiguration> {

  public final HtmlDoclet htmlDoclet = new HtmlDoclet();

  private static final Class<?>[] jaxrsAnnotations = new Class<?>[] { Path.class };

  public static int optionLength(final String option) {
    if ("-jaxrscontext".equals(option)) {
      return 2;
    }
    if ("-disablehttpexample".equals(option)
        || "-disablejavascriptexample".equals(option)) {
      return 1;
    }
    return HtmlDoclet.optionLength(option);
  }

  public static boolean validOptions(final String[][] options, final DocErrorReporter reporter) {
    return HtmlDoclet.validOptions(options, reporter);
  }

  public static LanguageVersion languageVersion() {
    return AbstractDoclet.languageVersion();
  }

  private List<ResourceMethod> jaxrsMethods = new ArrayList<ResourceMethod>();

  public static boolean start(final RootDoc rootDoc) {
    new JAXRSDoclet(rootDoc).start();
    return true;
  }

  public JAXRSDoclet(RootDoc rootDoc) {
    super(rootDoc);
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new ResponseHeaderTaglet()));
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new RequestHeaderTaglet()));
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new HTTPTaglet()));
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new ReturnWrappedTaglet()));
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new InputWrappedTaglet()));
    htmlDoclet.configuration.tagletManager.addCustomTag(new LegacyTaglet(new IncludeTaglet()));
    htmlDoclet.configuration.tagletManager.addCustomTag(new ExcludeTaglet());
  }

  @Override
  protected JAXRSConfiguration makeConfiguration(ConfigurationImpl configuration) {
    return new JAXRSConfiguration(configuration);
  }

  public void start() {
    final ClassDoc[] classes = conf.parentConfiguration.root.classes();
    for (final ClassDoc klass : classes) {
      if (Utils.findAnnotatedClass(klass, jaxrsAnnotations) != null) {
        handleJAXRSClass(klass);
      }
    }
    for(ResourceMethod method : jaxrsMethods) {
        new MethodWriter(conf, method, this).print();
    }
    Collections.sort(jaxrsMethods);
    new SummaryWriter(conf, jaxrsMethods).write();
    Utils.copyResources(conf);
  }

  private void handleJAXRSClass(final ClassDoc klass) {
    jaxrsMethods.addAll(new ResourceClass(klass, null).getMethods());
  }

  public void warn(String warning) {
    conf.parentConfiguration.root.printWarning(warning);
  }

  public void printError(SourcePosition position, String error) {
    conf.parentConfiguration.root.printError(position, error);
  }
}
