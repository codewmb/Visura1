package com.visura.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

data class InspectionItem(
    val id: Int,
    val name: String,
    val condition: String = "Bom",
    val observation: String = ""
)

data class RoomInspection(
    val roomName: String,
    val isInspected: Boolean = false,
    val items: List<InspectionItem> = emptyList()
)

data class PropertyDetails(
    val ownerName: String = "",
    val ownerCpfCnpj: String = "",
    val tenantName: String = "",
    val tenantCpfCnpj: String = "",
    val inspectorName: String = "",
    val observations: String = ""
)

data class CompletedInspection(
    val id: String = UUID.randomUUID().toString(),
    val ownerName: String,
    val ownerCpfCnpj: String = "",
    val tenantName: String,
    val tenantCpfCnpj: String = "",
    val inspectorName: String,
    val observations: String,
    val date: String,
    val rooms: List<RoomInspection>
)

@HiltViewModel
class InspectionViewModel @Inject constructor() : ViewModel() {

    private val _rooms = MutableStateFlow<List<RoomInspection>>(
        listOf(
            RoomInspection("Sala de Estar"),
            RoomInspection("Cozinha"),
            RoomInspection("Banheiro Social")
        )
    )
    val rooms: StateFlow<List<RoomInspection>> = _rooms.asStateFlow()

    private val _propertyDetails = MutableStateFlow(PropertyDetails())
    val propertyDetails: StateFlow<PropertyDetails> = _propertyDetails.asStateFlow()

    private val _completedInspections = MutableStateFlow<List<CompletedInspection>>(emptyList())
    val completedInspections: StateFlow<List<CompletedInspection>> = _completedInspections.asStateFlow()

    fun updatePropertyDetails(
        owner: String,
        ownerCpf: String,
        tenant: String,
        tenantCpf: String,
        inspector: String,
        obs: String
    ) {
        _propertyDetails.value = PropertyDetails(owner, ownerCpf, tenant, tenantCpf, inspector, obs)
    }

    fun addRoom(roomName: String) {
        val current = _rooms.value
        if (roomName.isNotBlank() && current.none { it.roomName.equals(roomName, ignoreCase = true) }) {
            _rooms.value = current + RoomInspection(roomName = roomName)
        }
    }

    fun removeRoom(roomName: String) {
        _rooms.value = _rooms.value.filter { it.roomName != roomName }
    }

    fun saveRoomItems(roomName: String, items: List<InspectionItem>) {
        _rooms.value = _rooms.value.map { room ->
            if (room.roomName == roomName) {
                room.copy(isInspected = true, items = items)
            } else {
                room
            }
        }
    }

    fun getRoom(roomName: String): RoomInspection? {
        return _rooms.value.find { it.roomName == roomName }
    }

    fun finishCurrentInspection() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR"))
        val currentDate = dateFormat.format(Date())

        val details = _propertyDetails.value

        val finished = CompletedInspection(
            ownerName = details.ownerName.ifBlank { "Proprietário não informado" },
            ownerCpfCnpj = details.ownerCpfCnpj,
            tenantName = details.tenantName,
            tenantCpfCnpj = details.tenantCpfCnpj,
            inspectorName = details.inspectorName,
            observations = details.observations,
            date = currentDate,
            rooms = _rooms.value
        )

        _completedInspections.value = _completedInspections.value + finished

        // Reseta o estado para uma nova vistoria
        _propertyDetails.value = PropertyDetails()
        _rooms.value = listOf(
            RoomInspection("Sala de Estar"),
            RoomInspection("Cozinha"),
            RoomInspection("Banheiro Social")
        )
    }

    fun getCompletedInspection(id: String): CompletedInspection? {
        return _completedInspections.value.find { it.id == id }
    }

    fun updateCompletedInspection(updated: CompletedInspection) {
        _completedInspections.value = _completedInspections.value.map {
            if (it.id == updated.id) updated else it
        }
    }
}