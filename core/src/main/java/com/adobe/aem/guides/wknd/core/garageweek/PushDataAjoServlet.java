package com.adobe.aem.guides.wknd.core.garageweek;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.paths=/bin/pushdata"
        },
        immediate = true
)
public class PushDataAjoServlet extends SlingAllMethodsServlet {

    @Reference
    private TokenService tokenService;

    private static final Gson gson = new Gson();


     protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        StringBuilder sbJsonData = new StringBuilder();
        String line;
        // Read the JSON payload from the request body
        try (InputStream inputStream = request.getInputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            while ((line = reader.readLine()) != null) {
                sbJsonData.append(line);
            }
            // Log the received data
            System.out.println("Received JSON: " + sbJsonData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert JSON string to a Map or process it as needed
        try {
            // Assuming you are using org.json (add it to your dependencies)
            JsonObject jsonFormData = gson.fromJson(sbJsonData.toString(), JsonObject.class);
            Map<String, Object> jsonData = prepareBodyForAJO(jsonFormData);
            // Convert to JSON string
            String jsonString = gson.toJson(jsonData);
            String apiSubmitResp = tokenService.sendData(jsonString);
            // Respond back to the client
            response.getWriter().write(apiSubmitResp);
            response.setStatus(SlingHttpServletResponse.SC_OK);
            response.setContentType("application/json");
        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing JSON payload: " + e.getMessage());
        }
    }

    private static Map<String, Object> prepareBodyForAJO(JsonObject jsonFormData) {

        // Form Input Fields
        String ipName = jsonFormData.get("name").getAsString();
        String ipEmail = jsonFormData.get("email").getAsString();
        String ipProduct = jsonFormData.get("product").getAsString();
        String ipLocation = jsonFormData.get("location").getAsString();
        String ipFormId = jsonFormData.get("formId").getAsString();

        // Build JSON data structure
        Map<String, Object> jsonData = new HashMap<>();

        // Header section
        Map<String, Object> header = new HashMap<>();
        Map<String, Object> schemaRefHeader = new HashMap<>();
        schemaRefHeader.put("id", "https://ns.adobe.com/psc/schemas/a9867ac9ff57748882aaed20e53e59fd83513cd9129fc493");
        schemaRefHeader.put("contentType", "application/vnd.adobe.xed-full-notext+json; version=1.0");
        header.put("schemaRef", schemaRefHeader);
        header.put("flowId", "160de096-23a7-487d-88f9-6d5e413797e1");
        header.put("datasetId", "6757febf623f9d2aee3939ee");
        jsonData.put("header", header);

        // Body section
        Map<String, Object> body = new HashMap<>();

        // xdmMeta inside body
        Map<String, Object> xdmMeta = new HashMap<>();
        Map<String, Object> schemaRefBody = new HashMap<>();
        schemaRefBody.put("id", "https://ns.adobe.com/psc/schemas/a9867ac9ff57748882aaed20e53e59fd83513cd9129fc493");
        schemaRefBody.put("contentType", "application/vnd.adobe.xed-full-notext+json; version=1.0");
        xdmMeta.put("schemaRef", schemaRefBody);
        body.put("xdmMeta", xdmMeta);

        // xdmEntity inside body
        Map<String, Object> xdmEntity = new HashMap<>();

        // _psc inside xdmEntity
        Map<String, Object> _psc = new HashMap<>();
        _psc.put("formId", ipFormId);
        _psc.put("location", ipLocation);
        _psc.put("product", ipProduct);
        xdmEntity.put("_psc", _psc);

        // person inside xdmEntity
        Map<String, Object> person = new HashMap<>();
        Map<String, Object> name = new HashMap<>();
        name.put("fullName", ipName);
        person.put("name", name);
        xdmEntity.put("person", person);

        // personalEmail inside xdmEntity
        Map<String, Object> personalEmail = new HashMap<>();
        personalEmail.put("address", ipEmail);
        xdmEntity.put("personalEmail", personalEmail);

        // Add xdmEntity to body
        body.put("xdmEntity", xdmEntity);

        // Add body to jsonData
        jsonData.put("body", body);
        return jsonData;
    }
}
