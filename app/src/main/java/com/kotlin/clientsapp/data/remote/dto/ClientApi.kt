package com.kotlin.clientsapp.data.remote.dto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Response

interface ClientApi{

    @GET("/api/Client")
    suspend fun getClient(): List<ClientDto>

    @POST("/api/Client")
    suspend fun postClient(@Body clientDto: ClientDto): Response<ClientDto>

    @GET("/api/Client/{id}")
    suspend fun getClientById(@Path("id") id: Int): ClientDto

    @DELETE("/api/Client/{id}")
    suspend fun deleteClient(@Path("id") id: Int, @Body clientDto: ClientDto): Response<Unit>
}