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
import java.util.Collection;

import com.lunatech.doclets.jax.JAXConfiguration;
import com.lunatech.doclets.jax.Utils;
import com.lunatech.doclets.jax.jaxrs.model.ResourceMethod;
import com.sun.javadoc.Doc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.formats.html.HtmlDocletWriter;
import java.io.InputStream;

public class SummaryWriter extends com.lunatech.doclets.jax.writers.DocletWriter {

  private Collection<ResourceMethod> methods;

  public SummaryWriter(JAXConfiguration configuration, Collection<ResourceMethod> methods) {
    super(configuration, getWriter(configuration));
    this.methods = methods;
  }

  private static HtmlDocletWriter getWriter(JAXConfiguration configuration) {
    try {
      return new HtmlDocletWriter(configuration.parentConfiguration, "", "index.html", "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void write() {
    printHeader();
    printMenu("Overview");
    printOverview();
    printResources();
    printFooter();
    writer.close();
  }

  private void printOverview() {
    RootDoc root = configuration.parentConfiguration.root;
    if (root.inlineTags().length > 0) {
      around("div", root.getRawCommentText());
    }
  }

  private void printResources() {
    open("table class='info'");
    around("caption class='TableCaption'", "Resources");
    open("tbody");
    open("tr class='subheader'");
    around("th class='TableHeader'", "Methods");
    around("th class='TableHeader'", "Resource");
    around("th class='TableHeader'", "Description");
    close("tr");
    for(ResourceMethod method : methods ) {
        printMethodLine(method);
    }
    close("tbody");
    close("table");
  }

  private void printMethodLine(ResourceMethod method) {
    open("tr");
    String path = Utils.getAbsolutePath(this, method);
    if(path.endsWith("/")) {
      path = path.substring(path.length() - 1);
    }

    String methodsStr = Utils.join(method.getMethods());

    // Print method(s)
    open("td");
    boolean first = true;
    for (String httpMethod : method.getMethods()) {
      if (!first)
        print(", ");
      open("a href='" + path + "/" + methodsStr + ".html'");
      around("tt", httpMethod);
      close("a");
      first = false;
    }
    close("td");

    // Print URL
    open("td");
    if (path.length() == 0)
      path = ".";
    open("a href='" + path + "/" + methodsStr + ".html'");
    around("tt", method.getPath());
    close("a");
    close("td");

    // Print description
    open("td");
    Doc javaDoc = method.getJavaDoc();
    if (javaDoc != null && javaDoc.firstSentenceTags() != null)
      writer.printSummaryComment(javaDoc);
    close("td");
    close("tr");
  }

  protected void printHeader() {
    printHeader("Overview of resources");
  }
}
