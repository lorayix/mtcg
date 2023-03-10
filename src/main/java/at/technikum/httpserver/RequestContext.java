package at.technikum.httpserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class RequestContext {

    private static final String CONTENT_LENGTH_HEADER_NAME = "Content-Length";
    private String httpVerb;
    private String path;
    private List<Header> headers;
    private String body;

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    private String identifier = "";

    public String getIdentifier() {
        return identifier;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        System.out.println("Identifier: " + identifier);
    }

    public <T> T getBodyAs(Class<T> clazz) {
        try {
            return objectMapper.readValue(body, clazz);
        } catch (JsonProcessingException e){
            System.err.println(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    public String getToken(){
        String tokenValue = "";
        for (Header header: headers){
            if(header.getName().equals("Authorization")){
                tokenValue = header.getValue();
            }
        }
        if(!tokenValue.isBlank()) {
            final String[] tokenArray = tokenValue.split("Basic ");
            String token = tokenArray[1];
            return token;
        }
        throw new BadRequestException("No token was delivered");
    }
}
