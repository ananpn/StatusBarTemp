package com.example.statusbartemp


import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.statusbartemp.LogicAndData.Constants.Companion.emptyDataStoreData
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.STOP_BACKGROUND_PROCESS
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.STOP_UPDATING
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.UPDATE_BACKGROUND_PROCESS
import com.example.statusbartemp.LogicAndData.TimeFunctions.Companion.formatISOTimeToFinnishTime
import com.example.statusbartemp.LogicAndData.formatSourceToDisplay
import com.example.statusbartemp.LogicAndData.formatTempToDisplay
import com.example.statusbartemp.Permissions.initializePermissionHelper
import com.example.statusbartemp.UiComponents.AppSettings
import com.example.statusbartemp.UiComponents.LocationInputDialog
import com.example.statusbartemp.UiComponents.SimpleSettingsDialog
import com.example.statusbartemp.UiComponents.TemperatureHistoryDialog
import com.example.statusbartemp.UiComponents.UpdateIntervalRadioButtons
import com.weeklist.screens.utils.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun MainApp(
    vm : MainViewModel,
    activity : Activity,
    context : Context
) {
    val dataFlow = vm.dataFlow.collectAsState(initial = emptyDataStoreData)
    val latitude = dataFlow.value.locationToUse.latitude
    val longitude = dataFlow.value.locationToUse.longitude
    val placeName = dataFlow.value.locationToUse.placeName
    val measureTime = dataFlow.value.measureTime
    val measureTemp = dataFlow.value.prevTemp
    val measureSource = dataFlow.value.measureSource
    val useSavedLocation = dataFlow.value.useSavedLocation
    val updateInterval = dataFlow.value.updateInterval
    val lastRun = dataFlow.value.lastRun
    val historyString = dataFlow.value.historyString
    val statusMessage = vm.statusMessage.collectAsState(initial = "")


    val scope = rememberCoroutineScope()
    var isUpdating = vm.isUpdating.collectAsState(initial = false)

    //var isUpdating.value by remember{ mutableStateOf(false) }
    LaunchedEffect(Unit){
        scope.launch{
            initializePermissionHelper(context, activity, !(useSavedLocation))
        }
        vm.checkSecretOffset()
    }

    //var imageFile : String by remember{ mutableStateOf("") }
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Location: "+placeName)
        Text(text = "Coordinates: ${latitude}, ${longitude} ")
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Temperature: ${formatTempToDisplay(measureTemp)}°C ")
        Text(text = "Time: ${formatISOTimeToFinnishTime(measureTime)} ")
        Text(text = "Source: ${formatSourceToDisplay(measureSource)}",
             style = MaterialTheme.typography.bodySmall)
        Text(
            text = "${statusMessage.value}",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Last run: $lastRun",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            /*
            Spacer(modifier = Modifier
                .width(15.dp)
                .weight(1f))
            */
            Button(
                enabled = true,
                onClick = {
                    if (!(isUpdating.value)) {
                        vm.initializeTemperatureUpdater()
                        if (updateInterval != "Disabled")
                            vm.initCheckWorker(true)
                        else vm.initCheckWorker(false)
                    }
                    else {
                        vm.stopUpdate()
                    }
                }
            ) {
                Text(text = when(updateInterval){
                    "Disabled" -> STOP_BACKGROUND_PROCESS
                    else ->
                        if (isUpdating.value) STOP_UPDATING
                        else UPDATE_BACKGROUND_PROCESS
                })
            }

        }
        Spacer(modifier = Modifier.height(20.dp))
        UpdateIntervalRadioButtons(vm = vm, updateInterval = updateInterval, enabled = !(isUpdating.value))
        Spacer(modifier = Modifier.height(20.dp))
        
        Row(horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically){
            Column(
                //modifier = Modifier.weight(0.6f),
                horizontalAlignment = Alignment.Start
            ){
                Text(
                    modifier = Modifier.offset(x=-20.dp),
                    text = "Do not update location"
                )
            }
            Column(
                //modifier = Modifier.weight(0.4f),
            ){
                Switch(
                    modifier = Modifier.offset(x=10.dp),
                    enabled = !(isUpdating.value),
                    checked = useSavedLocation,
                    onCheckedChange ={
                        vm.saveData(useSavedLocation = it)
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))

        Button(
            enabled = !(isUpdating.value),
            onClick = {
                //vm.generateSecretKey()
                vm.plsOpenLocationInputDialog()
            }
        ){
            Text(
                text =
                    "Manual location input"
            )
        }
        LocationInputDialog(vm = vm, context = context)
        Spacer(modifier = Modifier.height(15.dp))
        Button(
            enabled = !(isUpdating.value),
            onClick = {
                vm.plsOpenTemperatureHistoryDialog()
            }
        ){
            
            Text(
                "History"
            )
            Spacer(modifier = Modifier.width(5.dp))
            Icon(Icons.Filled.List, "list")
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            /*
            Spacer(modifier = Modifier
                .width(30.dp)
                .weight(1f))
            */
            Button(
                enabled = !(isUpdating.value),
                onClick = {
                    vm.plsOpenSimpleSettingsDialog()

                }
            ){
                Text("Settings")
                Spacer(modifier = Modifier.width(5.dp))
                Icon(imageVector = Icons.Filled.Settings, "", tint = Color.DarkGray)
            }


        }
        Spacer(modifier = Modifier.height(30.dp))


        SimpleSettingsDialog(
            vm = vm,
            context = context,
            activity = activity,
            appSettings = AppSettings(
                useSavedLocation = useSavedLocation,
            )
        )
        
        TemperatureHistoryDialog(
            vm = vm,
            historyString = historyString
        )

    }
}

