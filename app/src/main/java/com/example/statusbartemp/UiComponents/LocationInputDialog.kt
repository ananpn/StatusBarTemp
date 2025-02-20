package com.example.statusbartemp.UiComponents

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.CANCEL_BUTTON
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.CANNOT_LOAD_CURRENT
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.CANNOT_LOAD_SAVED
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.GIVE_LATITUDE
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.GIVE_LOCATION_NAME
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.GIVE_LONGITUDE
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.INVALID_VALUES_ERROR
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.LOAD_CURRENT_LOCATION
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.LOAD_SAVED_LOCATION
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.SAVE_LOCATION_BUTTON
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.SAVE_LOCATION_TITLE
import com.weeklist.screens.utils.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationInputDialog(vm : MainViewModel, context : Context){
    if (vm.openLocationInputDialog) {
        val coroutineScope = rememberCoroutineScope()

        var title by remember { mutableStateOf("") }
        var latitude by remember { mutableStateOf("") }
        var longitude by remember { mutableStateOf("") }


        AlertDialog(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .offset(x = 30.dp),
            onDismissRequest = { vm.closeLocationInputDialog() },
            title = { Text(text = SAVE_LOCATION_TITLE
            ) },
            text = {
                Column {
                    Spacer(
                        modifier = Modifier.height(9.dp)
                    )
                    stringInputField(
                        inVal = title,
                        label = GIVE_LOCATION_NAME,
                        onInput = {title = it}
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    decimalInputFieldForCoordinates(
                        inVal = latitude,
                        label = GIVE_LATITUDE,
                        onInput = { latitude = it },
                        onPastedAfterComma = {
                            longitude = it
                        }
                    )
                    Spacer(modifier = Modifier.height(9.dp))
                    decimalInputFieldForCoordinates(
                        inVal = longitude,
                        label = GIVE_LONGITUDE,
                        onInput = {longitude = it}
                    )
                    Row(){
                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = {coroutineScope.launch{
                                val savedLocation = vm.getOfflineLocation()
                                if (savedLocation != null) {
                                    longitude = savedLocation.longitude.toString().take(7)
                                    latitude = savedLocation.latitude.toString().take(7)
                                    title = savedLocation.placeName
                                }
                                else{
                                    Toast.makeText(
                                        context,
                                        CANNOT_LOAD_SAVED,
                                        Toast.LENGTH_LONG
                                    ).show()

                                }
                            }
                            }
                        ) {
                            Text(
                                text = LOAD_SAVED_LOCATION
                            )
                        }
                        TextButton(
                            modifier = Modifier.weight(1f),
                            //colors = getTextButtonColors(),
                            onClick = {coroutineScope.launch{
                                val currentLocation = vm.getCurrentLocation()
                                if (currentLocation != null) {
                                    longitude = currentLocation.longitude.toString().take(7)
                                    latitude = currentLocation.latitude.toString().take(7)
                                    title = currentLocation.placeName
                                }
                                else{
                                    Toast.makeText(
                                        context,
                                        CANNOT_LOAD_CURRENT,
                                        Toast.LENGTH_LONG
                                    ).show()

                                }
                            }
                            }
                        ) {
                            Text(
                                text = LOAD_CURRENT_LOCATION
                            )
                        }
                        TextButton(
                            modifier = Modifier.weight(1f),
                            //colors = getTextButtonColors(),
                            onClick = {coroutineScope.launch{
                                try{
                                    val latDouble = latitude.toDouble()
                                    val lonDouble = longitude.toDouble()
                                    vm.applyLocation(
                                        title = when(title){
                                            "" -> vm.obtainPlaceName(
                                                    latDouble,
                                                    lonDouble,
                                                )
                                            else -> title
                                        },
                                        lat = latDouble,
                                        lon = lonDouble
                                    )
                                }
                                catch(e : Exception){
                                    Toast.makeText(
                                        context,
                                        INVALID_VALUES_ERROR,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            }
                        ) {
                            Text(
                                text = "Apply"
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch{
                            try{
                                val latDouble = latitude.toDouble()
                                val lonDouble = longitude.toDouble()
                                vm.saveLocation(
                                    title = when(title){
                                        "" -> vm.obtainPlaceName(
                                            latDouble,
                                            lonDouble,
                                        )
                                        else -> title
                                    },
                                    lat = latDouble,
                                    lon = lonDouble
                                )
                                delay(80)
                                vm.closeLocationInputDialog()
                            }
                            catch(e : Exception){
                                Toast.makeText(
                                    context,
                                    INVALID_VALUES_ERROR,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                ) {
                    Text(
                        text = SAVE_LOCATION_BUTTON
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { vm.closeLocationInputDialog() }
                ) {
                    Text(
                        text = CANCEL_BUTTON
                    )
                }
            }
        )
    }
}