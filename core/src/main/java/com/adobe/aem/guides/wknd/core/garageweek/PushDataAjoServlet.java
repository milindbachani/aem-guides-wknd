package com.adobe.aem.guides.wknd.core.garageweek;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.paths=/bin/push-data",
                "sling.servlet.extensions=json"
        }
)
public class PushDataAjoServlet extends SlingAllMethodsServlet {

    @Reference
    private TokenService tokenService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        // Initialize a StringBuilder to collect JSON data
        StringBuilder jsonData = new StringBuilder();
        String line;

        // Read the JSON payload from the request body
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonData.append(line);
            }
        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Error reading request payload: " + e.getMessage());
            return;
        }

        // Convert JSON string to a Map or process it as needed
        try {
            // Assuming you are using org.json (add it to your dependencies)
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsonData.toString());

            // Example: Extract specific fields
            String fieldName1 = jsonObject.optString("fieldName1", "defaultValue");
            String fieldName2 = jsonObject.optString("fieldName2", "defaultValue");

            // Process data (e.g., save to the repository, call an external API)
            Map<String, String> processedData = new HashMap<>();
            processedData.put("fieldName1", fieldName1);
            processedData.put("fieldName2", fieldName2);

            // Respond back to the client
            response.setContentType("application/json");
            response.getWriter().write(new org.json.JSONObject(processedData).toString());
        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing JSON payload: " + e.getMessage());
        }
    }
}
