package com.chenyh.client;

import com.chenyh.graph.client.Show;
import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.client.DefaultGraphQLClient;
import com.netflix.graphql.dgs.client.GraphQLResponse;
import com.netflix.graphql.dgs.client.HttpResponse;
import com.netflix.graphql.dgs.client.codegen.BaseProjectionNode;
import com.netflix.graphql.dgs.client.codegen.EntitiesGraphQLQuery;
import com.netflix.graphql.dgs.client.codegen.GraphQLQuery;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphClientTest {
    private RestTemplate dgsRestTemplate = new RestTemplate();

    private static final String URL = "http://127.0.0.1:8080/graphql";

    private static final String QUERY = "query {\n" +
            "    shows (titleFilter : \"the\") {\n" +
            "        title \n" +
            "        releaseYear\n" +
            "    }\n" +
            "}";

    public List<Object> getData() {

        DefaultGraphQLClient graphQLClient = new DefaultGraphQLClient(URL);

        Map<String, String> map = new HashMap<>();

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

        List<Show> shows = response.extractValueAsObject("shows", new TypeRef<List<Show>>() {
        });

        System.out.print(response.getJson());


        return null;
    }
    @Test
    public void showsWithQueryApi() {
        getData();
    }


    @Test
    public void showsWithQueryApi2(){

        GraphQLQuery query = new GraphQLQuery() {
            @NotNull
            @Override
            public String getOperationName() {
                return "shows";
            }
        };



        GraphQLQueryRequest request = new GraphQLQueryRequest(query , null);

    }


}

class ShowNode extends BaseProjectionNode {

    private String title;
    private Integer releaseYear;

    public String getTitle() {
        return title;
    }

    public ShowNode setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public ShowNode setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
        return this;
    }
}
