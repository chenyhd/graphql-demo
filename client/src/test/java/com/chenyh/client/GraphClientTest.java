package com.chenyh.client;

import com.netflix.graphql.dgs.client.DefaultGraphQLClient;
import com.netflix.graphql.dgs.client.GraphQLResponse;
import com.netflix.graphql.dgs.client.HttpResponse;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

public class GraphClientTest {
    private RestTemplate dgsRestTemplate = new RestTemplate();

    private static final String URL = "http://127.0.0.1:8080/graphql";

    private static final String QUERY = "{\n" +
            "    shows (titleFilter : \"the\") {\n" +
            "        title\n" +
            "                releaseYear\n" +
            "    }\n" +
            "}";

    public List<Object> getData() {
        DefaultGraphQLClient graphQLClient = new DefaultGraphQLClient(URL);
        GraphQLResponse response = graphQLClient.executeQuery(QUERY, new HashMap<>(), "", (url, headers, body) -> {
            /**
             * The requestHeaders providers headers typically required to call a GraphQL endpoint, including the Accept and Content-Type headers.
             * To use RestTemplate, the requestHeaders need to be transformed into Spring's HttpHeaders.
             */
            HttpHeaders requestHeaders = new HttpHeaders();
            headers.forEach(requestHeaders::put);

            /**
             * Use RestTemplate to call the GraphQL service.
             * The response type should simply be String, because the parsing will be done by the GraphQLClient.
             */
            ResponseEntity<String> exchange = dgsRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity(body, requestHeaders), String.class);

            /**
             * Return a HttpResponse, which contains the HTTP status code and response body (as a String).
             * The way to get these depend on the HTTP client.
             */
            return new HttpResponse(exchange.getStatusCodeValue(), exchange.getBody());
        });
        System.out.print(response.getJson());

        return null;
    }
    @Test
    public void showsWithQueryApi() {
        getData();
    }
}
