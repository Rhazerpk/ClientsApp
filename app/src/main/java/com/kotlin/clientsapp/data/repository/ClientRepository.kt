package com.kotlin.clientsapp.data.repository
import com.kotlin.clientsapp.data.remote.dto.ClientApi
import com.kotlin.clientsapp.data.remote.dto.ClientDto
import com.kotlin.clientsapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import retrofit2.HttpException
import javax.inject.Inject

class ClientRepository @Inject constructor(
    private val api: ClientApi
) {
    fun getClients(): Flow<Resource<List<ClientDto>>> = flow {
        try {
            emit(Resource.Loading())

            val client = api.getClient()

            emit(Resource.Success(client))
        } catch (e: HttpException) {
            emit(Resource.Error(e.message ?: "Error HTTP GENERAL"))
        } catch (e: IOException) {
            emit(Resource.Error(e.message ?: "verificar tu conexion a internet"))
        }
    }

    fun getClientById(id: Int): Flow<Resource<ClientDto>> = flow {
        try {
            emit(Resource.Loading())

            val client= api.getClientById(id)

            emit(Resource.Success(client))
        } catch (e: HttpException) {
            emit(Resource.Error(e.message ?: "Error HTTP GENERAL"))
        } catch (e: IOException) {
            emit(Resource.Error(e.message ?: "verificar tu conexion a internet"))
        }
    }

    suspend fun postClient(client: ClientDto) = api.postClient(client)
    suspend fun deleteClient(id: Int) = api.deleteClient(id)
}
