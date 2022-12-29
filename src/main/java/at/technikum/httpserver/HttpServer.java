package at.technikum.httpserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer extends Thread {
    public void start() {

            try (ServerSocket serverSocket = new ServerSocket(3000)) {
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

                            BufferedWriter w = new BufferedWriter(
                                    new OutputStreamWriter(socket.getOutputStream()));
                            w.write("HTTP/1.1 200 OK");
                            w.newLine();
                            w.newLine();
                            w.flush();
                            Thread.sleep(10000);

                        } catch (InterruptedException e){
                            throw new RuntimeException(e);
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

        String input = "";
        String versionString = bufferedReader.readLine();
        final String[] splitVersionString = versionString.split(" ");
        requestContext.setHttpVerb(splitVersionString[0]);
        requestContext.setPath(splitVersionString[1]);

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
