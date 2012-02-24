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
package com.lunatech.doclets.jax.jaxrs.writers;

import java.io.IOException;
import java.util.List;

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.JAXRSDoclet;
import com.lunatech.doclets.jax.jaxrs.model.Resource;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;

public class ResourceWriter extends DocletWriter {

  public ResourceWriter(JAXConfiguration configuration, Resource resource, JAXRSDoclet doclet) {
    super(configuration, getWriter(configuration, resource), resource, doclet);
  }

  private static HtmlDocletWriter getWriter(JAXConfiguration configuration, Resource resource) {
    String pathName = Utils.urlToSystemPath(resource);
    try {
      return new HtmlDocletWriter(configuration.parentConfiguration, pathName, "index.html", Utils.urlToRoot(resource));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void write() {
    boolean isRoot = resource.getParent() == null;
    String selected = isRoot ? "Root resource" : "";
    printHeader(isRoot);
    printMenu(selected);
    printMethods();
    printFooter();
    writer.flush();
  }

  private void printMethods() {
    if (!resource.hasRealMethods())
      return;
    List<ResourceMethod> methods = resource.getMethods();
    printMethodDetails(methods);
  }

  private void printMethodDetails(List<ResourceMethod> methods) {
    tag("hr");
    for (ResourceMethod method : methods) {
      // skip resource locator methods
      if (method.isResourceLocator())
        continue;
      new MethodWriter(method, this, doclet).print();
    }
  }

  private void printHeader(boolean isRoot) {
    if (isRoot)
      printHeader("Root Resource");
    else
      printHeader("Resource " + resource.getName());
  }
}
