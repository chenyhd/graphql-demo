package com.chenyh.client

import com.netflix.graphql.dgs.client.codegen.BaseProjectionNode
import com.netflix.graphql.dgs.client.codegen.GraphQLQuery
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest

class Test {


    fun testSerializeListOfStringsAsInput() {
        val query = TestGraphQLQuery().apply {
            input["actors"] = "actorA"
            input["movies"] = listOf("movie1", "movie2")
        }
        val request = GraphQLQueryRequest(query)
        val result = request.serialize()
    }

    class TestGraphQLQuery : GraphQLQuery() {
        override fun getOperationName(): String {
            return "test"
        }
    }

    class MovieProjection : BaseProjectionNode() {
        fun movieId(): MovieProjection {
            fields["movieId"] = null
            return this
        }

        fun name(): MovieProjection {
            fields["name"] = null
            return this
        }
    }
}