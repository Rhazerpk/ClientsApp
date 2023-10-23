import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kotlin.clientsapp.data.remote.dto.ClientDto
import com.kotlin.clientsapp.ui.Client.ClientViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ClientScreen(viewModel: ClientViewModel = hiltViewModel()) {

    val clients by viewModel.clients.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.isMessageShownFlow.collectLatest {
            if (it) {
                snackbarHostState.showSnackbar(
                    message = "Cliente guardado",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            RefreshAppBar(title = "Clientes") {
                viewModel.limpiar()
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(8.dp)
        ) {
            val keyboardController = LocalSoftwareKeyboardController.current
            Text(text = "Cliente detalles", style = MaterialTheme.typography.titleMedium)

            CustomOutlinedTextField(
                value = viewModel.nombres,
                onValueChange = { viewModel.nombres = it },
                label = "Nombres",
                isError = viewModel.isValidNombre,
                imeAction = ImeAction.Next
            )
            CustomOutlinedTextField(
                value = viewModel.rnc,
                onValueChange = { viewModel.rnc = it },
                label = "Rnc",
                isError = viewModel.isValidRnc,
                imeAction = ImeAction.Next
            )
            CustomOutlinedTextField(
                value = viewModel.direccion,
                onValueChange = { viewModel.direccion = it },
                label = "Dirección",
                isError = viewModel.isValidDireccion,
                imeAction = ImeAction.Next
            )
            OutlinedTextField(
                value = viewModel.limiteCredito.toString(),
                label = { Text(text = "Limite crédito") },
                singleLine = true,
                onValueChange = {
                    val newValue = it.toIntOrNull()
                    if (newValue != null) {
                        viewModel.limiteCredito = newValue
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                )
            )

            OutlinedButton(onClick = {
                keyboardController?.hide()
                if (viewModel.isValid()) {
                    viewModel.saveClient()
                    viewModel.setMessageShown()
                }
            }, modifier = Modifier.fillMaxWidth())
            {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Guardar")
                Text(text = "Guardar")
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefreshAppBar(
    title: String,
    onRefreshClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = title) },
        actions = {
            IconButton(onClick = { onRefreshClick() }) {
                Icon(
                    imageVector = Icons.Default.Refresh, contentDescription = "Refresh"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    imeAction: ImeAction
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = label) },
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = if (isError) Color.Gray else Color.Red,
            unfocusedBorderColor = if (isError) Color.Gray else Color.Red
        ),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction)
    )
}

@Composable
fun Consult(clients: List<ClientDto>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Lista de clientes", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(clients) { client ->
                ClientItem(client)
            }
        }
    }
}

@Composable
fun ClientItem(client: ClientDto, viewModel: ClientViewModel = hiltViewModel()) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = client.nombres, style = MaterialTheme.typography.titleMedium)
            Text(text = client.direccion, style = MaterialTheme.typography.titleMedium)
            Text(text = client.rnc, style = MaterialTheme.typography.titleMedium)
            Text(text = client.limiteCredito.toString(), style = MaterialTheme.typography.titleMedium)

            Button(
                onClick = {
                    client.clienteId?.let { viewModel.deleteClient(it, client) }
                }
            ) {
                Text(text = "Eliminar")
            }
        }
    }
}
