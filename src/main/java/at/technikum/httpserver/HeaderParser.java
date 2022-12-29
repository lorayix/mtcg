package at.technikum.httpserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeaderParser {
    public Header parseHeader(String rawHeader) {
        if (rawHeader == null) {
            return null;
        }
        String [] split = rawHeader.split(":", 2);
        final String name = split[0];
        final String value = split[1].trim();
        final Header header = new Header();
        header.setName(name);
        header.setValue(value);
        return header;
    }

    public Map<String, String> parseHeadersToMap(List<String> rawHeaders) {
        if (rawHeaders == null) {
            return Map.of();
        }

        final Map<String, String> headerMap = new HashMap<>(rawHeaders.size());
        for (String rawHeader: rawHeaders) {
            final Header header = parseHeader(rawHeader);
            headerMap.put(header.getName(), header.getValue());
        }
        return headerMap;
    }

}
