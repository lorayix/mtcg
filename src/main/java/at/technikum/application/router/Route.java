package at.technikum.application.router;

public class Route {
    private final String path;
    private final String httpVerb;

    public Route(String path, String httpVerb){
        this.path = path;
        this.httpVerb = httpVerb;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVerb() {
        return httpVerb;
    }

}

