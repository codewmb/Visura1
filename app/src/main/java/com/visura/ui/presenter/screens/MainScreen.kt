package com.visura.ui.presenter.screens

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.toRoute
import com.visura.ui.viewmodels.InspectionViewModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Menu(modifier = modifier)
    }
}

// --- ROTAS DA NAVEGAÇÃO ---
@Serializable
@SerialName("Home")
object Home

@Serializable
@SerialName("Register")
object Register

@Serializable
@SerialName("PropertyDetails")
object PropertyDetails

@Serializable
@SerialName("Rooms")
object Rooms

@Serializable
@SerialName("CompletedInspections")
object CompletedInspections

@Serializable
data class InspectionDetail(val inspectionId: String)

@Serializable
data class RoomItems(val roomName: String)

enum class Destination(
    val route: Any,
    val label: String,
    val icon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(
        route = Home,
        label = "Home",
        icon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    REGISTER(
        route = Register,
        label = "Register",
        icon = Icons.Filled.AddCircle,
        unselectedIcon = Icons.Outlined.AddCircleOutline
    )
}

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun Menu(
    modifier: Modifier = Modifier,
    inspectionViewModel: InspectionViewModel = hiltViewModel()
) {

    val navController = rememberNavController()
    val startDestination = Destination.HOME
    var selected by remember { mutableIntStateOf(startDestination.ordinal) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val current = navBackStackEntry?.destination?.route

    val roomsList by inspectionViewModel.rooms.collectAsState()
    val completedList by inspectionViewModel.completedInspections.collectAsState()

    LaunchedEffect(current) {
        Destination.entries.forEachIndexed { index, destination ->
            if (current == destination.label) {
                selected = index
            }
        }
    }

    val graph = navController.createGraph(startDestination = startDestination.route) {
        composable<Home> {
            HomeScreen(
                onHistoryClick = {
                    navController.navigate(CompletedInspections)
                }
            )
        }

        composable<Register> {
            Register(
                onConfirmClick = {
                    navController.navigate(PropertyDetails)
                }
            )
        }

        composable<PropertyDetails> {
            PropertyDetailsScreen(
                onNextClick = { owner, ownerCpf, tenant, tenantCpf, inspector, obs ->
                    inspectionViewModel.updatePropertyDetails(owner, ownerCpf, tenant, tenantCpf, inspector, obs)
                    navController.navigate(Rooms)
                }
            )
        }

        composable<Rooms> {
            RoomsScreen(
                rooms = roomsList,
                onAddRoom = { roomName -> inspectionViewModel.addRoom(roomName) },
                onRemoveRoom = { roomName -> inspectionViewModel.removeRoom(roomName) },
                onRoomSelect = { roomName ->
                    navController.navigate(RoomItems(roomName = roomName))
                },
                onFinishAllClick = {
                    inspectionViewModel.finishCurrentInspection()
                    navController.navigate(CompletedInspections)
                }
            )
        }

        composable<RoomItems> { backStackEntry ->
            val args = backStackEntry.toRoute<RoomItems>()
            val currentRoom = inspectionViewModel.getRoom(args.roomName)

            RoomItemsScreen(
                roomName = args.roomName,
                savedItems = currentRoom?.items ?: emptyList(),
                onBackClick = { navController.popBackStack() },
                onSaveRoomClick = { items ->
                    inspectionViewModel.saveRoomItems(args.roomName, items)
                    navController.popBackStack()
                }
            )
        }

        composable<CompletedInspections> {
            CompletedInspectionsScreen(
                inspections = completedList,
                onInspectionClick = { inspectionId ->
                    navController.navigate(InspectionDetail(inspectionId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<InspectionDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<InspectionDetail>()
            val item = inspectionViewModel.getCompletedInspection(args.inspectionId)

            InspectionDetailScreen(
                inspection = item,
                onBackClick = { navController.popBackStack() },
                onSaveUpdate = { updatedInspection ->
                    inspectionViewModel.updateCompletedInspection(updatedInspection)
                }
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {},
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selected == index,
                        onClick = {
                            if (selected != index) {
                                navController.navigate(destination.route)
                                selected = index
                            }
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(28.dp),
                                imageVector = if (selected == index) destination.icon else destination.unselectedIcon,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(if (destination.route == Home) "Home" else "Nova Vistoria")
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController,
            graph,
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
        )
    }
}

@Composable
@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@Preview(showBackground = true, showSystemUi = true)
fun MainScreenPreview() {
    MainScreen()
}