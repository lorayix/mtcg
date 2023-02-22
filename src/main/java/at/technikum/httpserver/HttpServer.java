package at.technikum.httpserver;

import at.technikum.application.router.Route;
import at.technikum.application.router.RouteIdentifier;
import at.technikum.application.router.Router;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer extends Thread {

    private final Router router;

    public HttpServer(Router router) { this.router = router; }
    public void start() {

            try (ServerSocket serverSocket = new ServerSocket(10001)) {
                ExecutorService executorService = Executors.newFixedThreadPool(10);

                while(true) {
                    final Socket socket = serverSocket.accept();
                    executorService.submit(() -> {
                        System.out.println("Thread: " + Thread.currentThread().getName());
                        try {
                            BufferedReader br = new BufferedReader(
                                    new InputStreamReader
                                            (socket.getInputStream()));

                            final RequestContext requestContext = parseInput(br);
                            requestContext.print();

                            final RouteIdentifier routeIdentifier = new RouteIdentifier(
                                    requestContext.getPath(),
                                    requestContext.getHttpVerb()
                            );

                            final Route route = router.findRoute(routeIdentifier);
                            Response response;
                            try {
                                response = route.process(requestContext);
                            } catch (BadRequestException badRequestException) {
                                response = new Response();
                                response.setBody(badRequestException.getMessage());
                                response.setHttpStatus(HttpStatus.BAD_REQUEST);
                            } catch (IllegalStateException | InterruptedException e) {
                                response = new Response();
                                response.setBody(e.getMessage());
                                response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                            }

                            BufferedWriter w = new BufferedWriter(
                                    new OutputStreamWriter(socket.getOutputStream()));
                            w.write("HTTP/1.1 " + response.getHttpStatus().getStatusCode() + " " + response.getHttpStatus().getStatusMessage());
                            w.newLine();
                            // write headers
                            w.newLine();
                            w.write(response.getBody());
                            w.newLine();
                            w.newLine();
                            w.close();
                            w.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }

            } catch (IOException e) {
                System.err.println(e);
            }
    }
    public RequestContext parseInput(BufferedReader bufferedReader) throws IOException {
        List<Header> headerList = new ArrayList<>();
        HeaderParser headerParser = new HeaderParser();
        RequestContext requestContext = new RequestContext();

        String input;
        String versionString = bufferedReader.readLine();
        final String[] splitVersionString = versionString.split(" ");
        requestContext.setHttpVerb(splitVersionString[0]);
        requestContext.setPath(splitVersionString[1]);
        String path = requestContext.getPath();
        int lastIndex = path.lastIndexOf("/");
        if(lastIndex > 0){
            final String[] splitPath = path.split("/");
            if((path.indexOf("users") > 0) || (path.indexOf("tradings") > 0)){
                requestContext.setPath("/" + splitPath[1]);
                requestContext.setIdentifier(splitPath[2]);
            }
        }
        lastIndex = path.lastIndexOf("?");
        if(lastIndex > 0){
            final String[] splitPath = path.split("\\?");
            requestContext.setPath(splitPath[0]);
            requestContext.setIdentifier(splitPath[1]);
        }

        do {
            input = bufferedReader.readLine();
            if (input.equals("")) {
                break;
            }
            headerList.add(headerParser.parseHeader(input));
        } while (true);
        requestContext.setHeaders(headerList);

        int contentLength = requestContext.getContentLength();
        char [] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        requestContext.setBody(new String(buffer));
        return requestContext;
    }
}
