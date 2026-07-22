package com.visura.ui.presenter.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.visura.ui.presenter.PdfReportExporter
import com.visura.ui.viewmodels.CompletedInspection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionDetailScreen(
    inspection: CompletedInspection?,
    onBackClick: () -> Unit = {},
    onSaveUpdate: (CompletedInspection) -> Unit = {}
) {
    if (inspection == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Vistoria não encontrada.")
        }
        return
    }

    val context = LocalContext.current
    var isEditing by remember { mutableStateOf(false) }

    var ownerName by remember(inspection) { mutableStateOf(inspection.ownerName) }
    var ownerCpfCnpj by remember(inspection) { mutableStateOf(inspection.ownerCpfCnpj) }
    var tenantName by remember(inspection) { mutableStateOf(inspection.tenantName) }
    var tenantCpfCnpj by remember(inspection) { mutableStateOf(inspection.tenantCpfCnpj) }
    var inspectorName by remember(inspection) { mutableStateOf(inspection.inspectorName) }
    var observations by remember(inspection) { mutableStateOf(inspection.observations) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Vistoria" else "Detalhes da Vistoria", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                            contentDescription = "Editar / Salvar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Dados do Imóvel & Partes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (isEditing) {
                        OutlinedTextField(
                            value = ownerName,
                            onValueChange = { ownerName = it },
                            label = { Text("Proprietário") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = ownerCpfCnpj,
                            onValueChange = { ownerCpfCnpj = it },
                            label = { Text("CPF/CNPJ do Proprietário") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = tenantName,
                            onValueChange = { tenantName = it },
                            label = { Text("Inquilino") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = tenantCpfCnpj,
                            onValueChange = { tenantCpfCnpj = it },
                            label = { Text("CPF/CNPJ do Inquilino") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = inspectorName,
                            onValueChange = { inspectorName = it },
                            label = { Text("Vistoriador") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = observations,
                            onValueChange = { observations = it },
                            label = { Text("Observações") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    } else {
                        Text(text = "Proprietário: $ownerName ${if (ownerCpfCnpj.isNotBlank()) "($ownerCpfCnpj)" else ""}", fontWeight = FontWeight.Medium)
                        Text(text = "Inquilino: ${tenantName.ifBlank { "Não informado" }} ${if (tenantCpfCnpj.isNotBlank()) "($tenantCpfCnpj)" else ""}")
                        Text(text = "Vistoriador: ${inspectorName.ifBlank { "Não informado" }}")
                        Text(text = "Data da Vistoria: ${inspection.date}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (observations.isNotBlank()) {
                            Text(text = "Obs: $observations", fontSize = 13.sp)
                        }
                    }
                }
            }

            Text(
                text = "Cômodos Vistoriados",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            inspection.rooms.forEach { room ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = room.roomName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        if (room.items.isEmpty()) {
                            Text("Nenhum item avaliado", fontSize = 13.sp, color = Color.Gray)
                        } else {
                            room.items.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "• ${item.name}", fontSize = 14.sp)
                                    Text(
                                        text = item.condition,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.condition in listOf("Novo", "Bom")) Color(0xFF2E7D32) else Color(0xFFC62828)
                                    )
                                }
                                if (item.observation.isNotBlank()) {
                                    Text(
                                        text = "   Avaria/Obs: ${item.observation}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isEditing) {
                Button(
                    onClick = {
                        val updated = inspection.copy(
                            ownerName = ownerName,
                            ownerCpfCnpj = ownerCpfCnpj,
                            tenantName = tenantName,
                            tenantCpfCnpj = tenantCpfCnpj,
                            inspectorName = inspectorName,
                            observations = observations
                        )
                        onSaveUpdate(updated)
                        isEditing = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salvar Alterações", fontSize = 16.sp)
                }
            } else {
                Button(
                    onClick = { PdfReportExporter.exportAndPrint(context, inspection) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Imprimir / Salvar em PDF", fontSize = 16.sp)
                }
            }
        }
    }
}