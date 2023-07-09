package com.ddup.chat.chatapi.util;


import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestToStringConverter {

    public static String convertToString(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("HttpServletRequest {\n");
        sb.append("  method=").append(req.getMethod()).append(",\n");
        sb.append("  requestURL=").append(req.getRequestURL()).append(",\n");
        sb.append("  queryString=").append(req.getQueryString()).append(",\n");
        sb.append("  protocol=").append(req.getProtocol()).append(",\n");
        sb.append("  remoteAddr=").append(req.getRemoteAddr()).append(",\n");
        sb.append("  remotePort=").append(req.getRemotePort()).append(",\n");
        sb.append("  headers={\n");
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = req.getHeaders(headerName);
            sb.append("    ").append(headerName).append(": ");
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                sb.append(headerValue);
                if (headerValues.hasMoreElements()) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }
        sb.append("  },\n");
        sb.append("  parameters={\n");
        Enumeration<String> paramNames = req.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] paramValues = req.getParameterValues(paramName);
            sb.append("    ").append(paramName).append(": ");
            if (paramValues != null && paramValues.length > 0) {
                for (int i = 0; i < paramValues.length; i++) {
                    sb.append(paramValues[i]);
                    if (i < paramValues.length - 1) {
                        sb.append(", ");
                    }
                }
            }
            sb.append("\n");
        }
        sb.append("  }\n");
        sb.append("}");
        return sb.toString();
    }
}
