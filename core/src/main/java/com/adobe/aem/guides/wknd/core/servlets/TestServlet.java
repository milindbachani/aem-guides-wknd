package com.adobe.aem.guides.wknd.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.servlet.Servlet;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;

@Component(service = {Servlet.class}, property = {"sling.servlet.paths=/bin/practice", "sling.servlet.methods=GET", "sling.servlet.extensions=txt"})
public class TestServlet extends SlingAllMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(TestServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        String path = request.getParameter("path");
        if (path == null || path.isEmpty()) {
            out.println("Path parameter is required");
            return;
        }
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource resource = resourceResolver.getResource(path);
        if (resource == null) {
            out.println("Resource not found");
            return;
        }
        try {
            Node node = resource.adaptTo(Node.class);
            if (node != null) {
                //long size = getNodeSize(node);
                int size = getNodeSizeTest(node);
                out.println("Size of the node at " + path + ": " + size + " int");
            } else {
                out.println("Node could not be adapted");
            }
        } catch (RepositoryException e) {
            out.println("Error reading node: " + e.getMessage());
        }
    }

    private int getNodeSizeTest(Node node) throws RepositoryException {
        int size = 0;
        // Iterate through properties and check for binary data
        // Check for binary data in jcr:content node if present
        try {
            if (node.hasNode("jcr:content")) {
                Node contentNode = node.getNode("jcr:content");
                if (contentNode.hasProperty("jcr:data")) {
                    Property dataProperty = contentNode.getProperty("jcr:data");
                    LOG.info("mayank---->{}", dataProperty);
                    size = (int) contentNode.getProperty("jcr:data").getBinary().getSize();
                    LOG.info("pawan size >{}", size);
                }
            }
        } catch (Exception e) {
            LOG.info("error reading file", e.getMessage());
        }
        return size;
    }
}
