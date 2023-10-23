package com.kotlin.clientsapp.util
import com.kotlin.clientsapp.data.remote.dto.ClientDto

data class ClientListState(
    val isLoading: Boolean = false,
    val clients: List<ClientDto> = emptyList(),
    val error: String = ""
)