package com.visura.ui.presenter.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PropertyDetailsScreen(
    onNextClick: (owner: String, ownerCpf: String, tenant: String, tenantCpf: String, inspector: String, obs: String) -> Unit = { _, _, _, _, _, _ -> }
) {
    var ownerName by remember { mutableStateOf("") }
    var ownerCpfCnpj by remember { mutableStateOf("") }
    var tenantName by remember { mutableStateOf("") }
    var tenantCpfCnpj by remember { mutableStateOf("") }
    var inspectorName by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Dados das Partes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Preencha os dados dos envolvidos na vistoria para o laudo final.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = ownerName,
            onValueChange = { ownerName = it },
            label = { Text("Nome do Proprietário / Locador") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text
            )
        )

        OutlinedTextField(
            value = ownerCpfCnpj,
            onValueChange = { ownerCpfCnpj = it },
            label = { Text("CPF ou CNPJ do Proprietário") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = tenantName,
            onValueChange = { tenantName = it },
            label = { Text("Nome do Inquilino / Locatário") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text
            )
        )

        OutlinedTextField(
            value = tenantCpfCnpj,
            onValueChange = { tenantCpfCnpj = it },
            label = { Text("CPF ou CNPJ do Inquilino") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = inspectorName,
            onValueChange = { inspectorName = it },
            label = { Text("Vistoriador Responsável") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text
            )
        )

        OutlinedTextField(
            value = observations,
            onValueChange = { observations = it },
            label = { Text("Observações do Imóvel (Opcional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onNextClick(ownerName, ownerCpfCnpj, tenantName, tenantCpfCnpj, inspectorName, observations) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Avançar para Adicionar Cômodos", fontSize = 16.sp)
        }
    }
}