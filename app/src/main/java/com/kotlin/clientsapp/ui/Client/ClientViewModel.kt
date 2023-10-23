package com.kotlin.clientsapp.ui.Client
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.clientsapp.data.remote.dto.ClientDto
import com.kotlin.clientsapp.data.repository.ClientRepository
import com.kotlin.clientsapp.util.ClientListState
import com.kotlin.clientsapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    var nombres by mutableStateOf("")
    var rnc by mutableStateOf("")
    var direccion by mutableStateOf("")
    var limiteCredito by mutableStateOf(0)

    var isValidNombre by mutableStateOf(true)
    var isValidRnc by mutableStateOf(true)
    var isValidDireccion by mutableStateOf(true)
    var isValidLimiteCredito by mutableStateOf(true)

    private var _state = mutableStateOf(ClientListState())
    val state: State<ClientListState> = _state

    fun isValid(): Boolean {
        isValidNombre = nombres.isNotBlank()
        isValidRnc = rnc.isNotBlank()
        isValidDireccion = direccion.isNotBlank()
        isValidLimiteCredito = limiteCredito > 0
        return isValidNombre && isValidRnc && isValidDireccion && isValidLimiteCredito
    }

    var uiState = MutableStateFlow(ClientListState())

    private val _isMessageShown = MutableSharedFlow<Boolean>()
    val isMessageShownFlow = _isMessageShown.asSharedFlow()
    fun setMessageShown() {
        viewModelScope.launch {
            _isMessageShown.emit(true)
        }
    }

    val clients: StateFlow<Resource<List<ClientDto>>> = clientRepository.getClients().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Resource.Loading()
    )

    init {
        clientRepository.getClients().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = ClientListState(isLoading = true)
                }

                is Resource.Success -> {
                    _state.value = ClientListState(clients = result.data ?: emptyList())
                }

                is Resource.Error -> {
                    _state.value = ClientListState(error = result.message ?: "Error desconocido")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun saveClient() {
        viewModelScope.launch {
            if (isValid()) {
                val clientDto = ClientDto(
                    nombres = nombres,
                    rnc = rnc,
                    direccion = direccion,
                    limiteCredito = limiteCredito
                )
                clientRepository.postClient(clientDto)
                limpiar()
            }
        }
    }

    fun deleteClient(clientId: Int, clientDto: ClientDto) {
        viewModelScope.launch {
            clientRepository.deleteClient(clientId, clientDto)
        }
    }

    fun limpiar() {
        nombres = ""
        rnc = ""
        direccion = ""
        limiteCredito = 0
    }
}
