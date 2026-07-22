package com.visura.ui.presenter.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.visura.ui.viewmodels.InspectionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomItemsScreen(
    roomName: String,
    savedItems: List<InspectionItem>,
    onBackClick: () -> Unit = {},
    onSaveRoomClick: (List<InspectionItem>) -> Unit = {}
) {
    // Sugestão padrão de itens para cômodos novos
    val defaultItems = remember(roomName) {
        val lower = roomName.lowercase()
        when {
            lower.contains("banheiro") || lower.contains("lavabo") -> listOf(
                "Piso", "Paredes", "Teto", "Pia / Lavatório", "Torneira", "Vaso Sanitário", "Box / Chuveiro", "Espelho"
            )
            lower.contains("cozinha") || lower.contains("copa") -> listOf(
                "Piso", "Paredes", "Teto", "Bancada / Pia", "Torneira", "Armários", "Tomadas"
            )
            else -> listOf(
                "Piso", "Paredes", "Teto", "Porta e Fechadura", "Janela / Vidros", "Iluminação / Tomadas"
            )
        }
    }

    // Recarrega itens salvos previamente ou cria sugestões se for a 1ª vez
    val itemsList = remember {
        mutableStateListOf<InspectionItem>().apply {
            if (savedItems.isNotEmpty()) {
                addAll(savedItems)
            } else {
                defaultItems.forEachIndexed { index, name ->
                    add(InspectionItem(id = index, name = name))
                }
            }
        }
    }

    var newItemName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vistoria: $roomName", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Avalie o estado de conservação de cada item deste ambiente:",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo para adicionar item customizado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("Adicionar item específico (ex: Ar Condicionado)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                IconButton(
                    onClick = {
                        if (newItemName.isNotBlank()) {
                            itemsList.add(
                                InspectionItem(
                                    id = itemsList.size + 1,
                                    name = newItemName.trim()
                                )
                            )
                            newItemName = ""
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(itemsList) { index, item ->
                    ItemCard(
                        item = item,
                        onConditionChange = { newCond ->
                            itemsList[index] = itemsList[index].copy(condition = newCond)
                        },
                        onObsChange = { newObs ->
                            itemsList[index] = itemsList[index].copy(observation = newObs)
                        },
                        onDelete = {
                            itemsList.removeAt(index)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onSaveRoomClick(itemsList.toList()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Salvar Vistoria do Cômodo", fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCard(
    item: InspectionItem,
    onConditionChange: (String) -> Unit,
    onObsChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    val conditions = listOf("Novo", "Bom", "Regular", "Ruim")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remover item",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Estado de Conservação:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                conditions.forEach { cond ->
                    FilterChip(
                        selected = item.condition == cond,
                        onClick = { onConditionChange(cond) },
                        label = { Text(cond, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = item.observation,
                onValueChange = onObsChange,
                placeholder = { Text("Observações ou detalhes de avarias...", fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3
            )
        }
    }
}