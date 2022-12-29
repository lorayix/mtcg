package at.technikum.httpserver;

import java.util.List;

public class RequestContext {

    private static final String CONTENT_LENGTH_HEADER_NAME = "Content-Length";

    private String httpVerb;
    private String path;
    private List<Header> headers;
    private String body;

    public String getHttpVerb() {
        return httpVerb;
    }

    public void setHttpVerb(String httpVerb) {
        this.httpVerb = httpVerb;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getContentLength() {
        /*
        final Optional<Header> optionalHeader = headers.stream()
                .filter(header -> header.getName().equals("Content-Length")).findFirst();
        final Optional<String> optionalHeaderValue = optionalHeader.map(Header::getValue);
        final Optional<Integer> optionalInteger = optionalHeaderValue.map(Integer::parseInt);
        return optionalInteger.orElse(0);
        */
        return headers.stream()
                .filter(header -> header.getName().equals(CONTENT_LENGTH_HEADER_NAME)).findFirst()
                .map(Header::getValue)
                .map(Integer::parseInt)
                .orElse(0);
    }

    public void print() {
        System.out.println("HTTP-Verb: " + httpVerb);
        System.out.println("Path: " + path);
        System.out.println("Headers: "+ headers);
        System.out.println("Body: " + body);
    }
}
