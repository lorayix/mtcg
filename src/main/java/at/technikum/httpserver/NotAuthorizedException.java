package at.technikum.httpserver;

public class NotAuthorizedException extends RuntimeException {

    public NotAuthorizedException(String message){
        super(message);
    }
}
