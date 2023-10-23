package com.kotlin.clientsapp.data.remote.dto
import androidx.room.Entity
import androidx.room.PrimaryKey

data class ClientDto (
    var clienteId : Int?=null,
    var nombres: String = "",
    var rnc: String = "",
    var direccion: String = "",
    var limiteCredito: Int = 0
)

