package controller.docs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.JsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DocsController implements HttpHandler {

    private static final String OPENAPI_PATH = "src/docs/openapi.json";
    private static String cachedSpec = null;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        controller.AppServer.setCorsHeaders(exchange);

        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            if ("/api-docs".equals(path) || "/api-docs.json".equals(path)) {
                serveOpenApiSpec(exchange);
            } else if ("/docs".equals(path) || "/docs/".equals(path)) {
                serveSwaggerUi(exchange);
            } else if (path.startsWith("/docs/")) {
                serveSwaggerUiStatic(exchange, path);
            } else {
                sendResponse(exchange, 404, "{\"success\":false,\"message\":\"Endpoint tidak ditemukan\",\"status\":404}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"success\":false,\"message\":\"Internal server error: " + e.getMessage() + "\",\"status\":500}");
        }
    }

    private void serveOpenApiSpec(HttpExchange exchange) throws IOException {
        if (cachedSpec == null) {
            cachedSpec = new String(Files.readAllBytes(Paths.get(OPENAPI_PATH)), StandardCharsets.UTF_8);
        }

        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, cachedSpec.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(cachedSpec.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void serveSwaggerUi(HttpExchange exchange) throws IOException {
        String html = getSwaggerUiHtml();
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, html.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(html.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void serveSwaggerUiStatic(HttpExchange exchange, String path) throws IOException {
        // For Swagger UI static assets (CSS, JS, etc.) - redirect to CDN or return 404
        // Since we're using CDN for Swagger UI assets, we don't need to serve them locally
        sendResponse(exchange, 404, "{\"success\":false,\"message\":\"Static asset not found\",\"status\":404}");
    }

    private String getSwaggerUiHtml() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>SI-Paket API Documentation</title>\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"https://unpkg.com/swagger-ui-dist@5.9.0/swagger-ui.css\" />\n" +
                "    <style>\n" +
                "        html { box-sizing: border-box; overflow: -moz-scrollbars-vertical; overflow-y: scroll; }\n" +
                "        *, *:before, *:after { box-sizing: inherit; }\n" +
                "        body { margin:0; background: #fafafa; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"swagger-ui\"></div>\n" +
                "    <script src=\"https://unpkg.com/swagger-ui-dist@5.9.0/swagger-ui-bundle.js\" crossorigin></script>\n" +
                "    <script src=\"https://unpkg.com/swagger-ui-dist@5.9.0/swagger-ui-standalone-preset.js\" crossorigin></script>\n" +
                "    <script>\n" +
                "        window.onload = function() {\n" +
                "            const ui = SwaggerUIBundle({\n" +
                "                url: '/api-docs',\n" +
                "                dom_id: '#swagger-ui',\n" +
                "                deepLinking: true,\n" +
                "                presets: [\n" +
                "                    SwaggerUIBundle.presets.apis,\n" +
                "                    SwaggerUIStandalonePreset\n" +
                "                ],\n" +
                "                plugins: [\n" +
                "                    SwaggerUIBundle.plugins.DownloadUrl\n" +
                "                ],\n" +
                "                layout: \"StandaloneLayout\",\n" +
                "                requestInterceptor: (req) => {\n" +
                "                    // Auto-add Authorization header if token exists in localStorage\n" +
                "                    const token = localStorage.getItem('authToken');\n" +
                "                    if (token) {\n" +
                "                        req.headers['Authorization'] = 'Bearer ' + token;\n" +
                "                    }\n" +
                "                    return req;\n" +
                "                },\n" +
                "                onComplete: () => {\n" +
                "                    // Add login button to UI\n" +
                "                    addAuthButton();\n" +
                "                }\n" +
                "            });\n" +
                "            window.ui = ui;\n" +
                "        };\n" +
                "\n" +
                "        function addAuthButton() {\n" +
                "            const authContainer = document.querySelector('.swagger-ui .topbar');\n" +
                "            if (authContainer && !document.getElementById('auth-btn')) {\n" +
                "                const btn = document.createElement('button');\n" +
                "                btn.id = 'auth-btn';\n" +
                "                btn.className = 'btn authorize';" +
                "                btn.innerHTML = '🔐 Set Auth Token';\n" +
                "                btn.style.marginLeft = '10px';\n" +
                "                btn.onclick = () => {\n" +
                "                    const token = prompt('Enter JWT Token:');\n" +
                "                    if (token) {\n" +
                "                        localStorage.setItem('authToken', token);\n" +
                "                        alert('Token saved! Requests will now include Authorization header.');\n" +
                "                    }\n" +
                "                };\n" +
                "                authContainer.appendChild(btn);\n" +
                "            }\n" +
                "        }\n" +
                "    };\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        controller.AppServer.setCorsHeaders(exchange);
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}