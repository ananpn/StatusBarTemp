package com.example.statusbartemp.UiComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.statusbartemp.LogicAndData.StringConstants
import com.example.statusbartemp.LogicAndData.TimeFunctions
import com.example.statusbartemp.LogicAndData.formatTempToDisplay
import com.example.statusbartemp.UpdateWorker.toHistoryDataPointList
import com.weeklist.screens.utils.MainViewModel

@Composable
fun TemperatureHistoryDialog(vm : MainViewModel,
                         historyString : String,
){
    if (vm.openTemperatureHistoryDialog) {
        
        val historyDataPoints = historyString.toHistoryDataPointList().reversed()
        var activateDelete by remember{ mutableStateOf(false) }
        
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth(),
            onDismissRequest = {
                vm.plsOpenTemperatureHistoryDialog(false)
            },
            title = { Text(text = StringConstants.TEMPERATURE_HISTORY_DIALOG_TITLE ) },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (historyDataPoint in historyDataPoints){
                        item{
                            Row(){
                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally){
                                    historyDataPoint.temp.toDoubleOrNull()?.let{
                                        Text(formatTempToDisplay(it) +"°C", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                Column(modifier = Modifier.weight(2.5f), horizontalAlignment = Alignment.CenterHorizontally){
                                    Text(
                                        text = TimeFunctions.formatISOTimeToFinnishTime(
                                            input = historyDataPoint.time,
                                            seconds = false
                                        ),
                                        style = MaterialTheme.typography.bodySmall)
                                }
                                Column(modifier = Modifier.weight(1.5f), horizontalAlignment = Alignment.CenterHorizontally){
                                    Text(historyDataPoint.locationString, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        when(activateDelete){
                            true -> vm.deleteHistory()
                            false -> activateDelete = true
                        }
                    }
                ) {
                    Text(
                        text = "Delete history",
                        color = when(activateDelete){
                            true -> MaterialTheme.colorScheme.primary
                            false -> Color.DarkGray
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.plsOpenTemperatureHistoryDialog(false)
                    }
                ) {
                    Text(
                        text = "Close"
                    )
                }
            },
        )
    }
}