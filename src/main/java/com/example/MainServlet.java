package com.example;

import com.example.AmazonS3.API.S3Api;
import com.example.AmazonS3.util.HttpUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@WebServlet(urlPatterns = {"/"}, loadOnStartup = 1)
public class MainServlet extends HttpServlet {

    String result;
    String region;
    String access_key;
    String secret_key;
    HashMap<String, String> responseHeaders = new HashMap<>();
    Map<String, String> headersMap = new HashMap<>();
    Enumeration<String> hearderNames;

    public void credentials() {
        JSONParser parser = new JSONParser();
        Object obj = null;

        try {
            obj = parser.parse(new FileReader
                    ("src/main/java/com/example/AmazonS3/main/credentials.json"));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = (JSONObject) obj;
        region = (String) jsonObject.get("region");
        access_key = (String) jsonObject.get("aws_access_key");
        secret_key = (String) jsonObject.get("aws_secret_key");
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("***************PUTTING OBJECTS********************");

        credentials();
        hearderNames = request.getHeaderNames();
        while (hearderNames.hasMoreElements()) {
            String headerName = hearderNames.nextElement();
            headersMap.put(headerName, request.getHeader(headerName));

        }
        System.out.println("request headers: " + headersMap);

        String uri = request.getRequestURI();
        String[] uriInfo = uri.split("/");
        String key = uriInfo[2];
        String bucket = uriInfo[1];

        String request_parameter;
        if (request.getQueryString() == null) {
            request_parameter = "";
        } else {
            request_parameter = request.getQueryString() + "=";
        }

        StringBuffer content;
        try (BufferedReader reader = request.getReader()) {
            content = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append('\n');
            }
        }
        try {
            S3Api.putS3Object(key, bucket, content.toString(),
                    region, access_key, secret_key, headersMap, request_parameter);
            responseHeaders = HttpUtils.ResponseHeaders;
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String i : responseHeaders.keySet()) {
            response.setHeader(i, responseHeaders.get(i));
        }
        response.getOutputStream();

        System.out.println("****************UPLOADED******************");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("***************GETTING OBJECTS********************");
        credentials();
        hearderNames = request.getHeaderNames();
        while (hearderNames.hasMoreElements()) {
            String headerName = hearderNames.nextElement();
            headersMap.put(headerName, request.getHeader(headerName));
        }
        System.out.println("request headers: " + headersMap);

        String uri = request.getRequestURI();
        String[] uriInfo = uri.split("/");
        String key = uriInfo[2];
        String bucket = uriInfo[1];

        String request_parameter;
        if (request.getQueryString() == null) {
            request_parameter = "";
        } else {
            request_parameter = request.getQueryString() + "=";
        }

        try {
            result = S3Api.getS3Object(key, bucket,
                    region, access_key, secret_key, headersMap, request_parameter);
            responseHeaders = HttpUtils.ResponseHeaders;
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseHeaders.remove("Content-Length");
        responseHeaders.put("Content-Length", String.valueOf(result.length()));
        for (String i : responseHeaders.keySet()) {
            response.setHeader(i, responseHeaders.get(i));
        }
        response.getOutputStream().print(result);
        System.out.println("****************Recieved******************");
    }
}