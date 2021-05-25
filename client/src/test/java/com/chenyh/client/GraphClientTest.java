package com.chenyh.client;

import com.chenyh.graph.client.Show;
import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.client.DefaultGraphQLClient;
import com.netflix.graphql.dgs.client.GraphQLResponse;
import com.netflix.graphql.dgs.client.HttpResponse;
import com.netflix.graphql.dgs.client.RequestExecutor;
import com.netflix.graphql.dgs.client.codegen.BaseProjectionNode;
import com.netflix.graphql.dgs.client.codegen.GraphQLQuery;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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

        GraphQLResponse response = graphQLClient.executeQuery(QUERY, new HashMap<>(), "", executor);

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
    public void showsWithQueryApi2() {
        ShowGraphQLQuery query = new ShowGraphQLQuery();

        GraphQLQueryRequest request = new GraphQLQueryRequest(query,new ShowProjection().releaseYear().title());

        DefaultGraphQLClient graphQLClient = new DefaultGraphQLClient(URL);

        Map<String, Object> vars = new HashMap<>();

        vars.put("var1", "v1");

        GraphQLResponse response = graphQLClient.executeQuery(request.serialize(), vars, "shows", executor);

        System.out.println(response.getJson());

    }

    @Test
    public void showsWithQueryApi3() {

        String queryStr = "query  shows (titleFilter : $title) { releaseYear title } ";

        DefaultGraphQLClient graphQLClient = new DefaultGraphQLClient(URL);

        Map<String, Object> vars = new HashMap<>();

        vars.put("title", "the");

        GraphQLResponse response = graphQLClient.executeQuery(queryStr,vars, "MyQuery", executor);

        System.out.println(response.getJson());

    }

    RequestExecutor executor = (url, headers, body) -> {
        HttpHeaders requestHeaders = new HttpHeaders();
        headers.forEach(requestHeaders::put);
        ResponseEntity<String> exchange = dgsRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity(body, requestHeaders), String.class);
        return new HttpResponse(exchange.getStatusCodeValue(), exchange.getBody());
    };


}

class ShowProjection extends BaseProjectionNode {

    private final Map<String, Object> fields = super.getFields();

    public ShowProjection releaseYear () {
        fields.put("releaseYear", null);
        return this;
    }

    public ShowProjection title() {
        fields.put("title", null);
        return this;
    }

}

class ShowGraphQLQuery extends GraphQLQuery {

    public ShowGraphQLQuery() {
        Map<String, Object> input = super.getInput();
    }

    @NotNull
    @Override
    public String getOperationName() {
        return "shows";
    }
}

