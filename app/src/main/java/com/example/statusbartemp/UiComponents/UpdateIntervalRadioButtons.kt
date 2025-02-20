package com.example.statusbartemp.UiComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.statusbartemp.LogicAndData.StringConstants
import com.weeklist.screens.utils.MainViewModel

@Composable
fun UpdateIntervalRadioButtons(vm : MainViewModel, updateInterval : String, enabled : Boolean){
    val radioOptions = listOf("Disabled", "15 minutes", "30 minutes", "1 hour")
    val selectedOption by remember(updateInterval) { mutableStateOf(updateInterval) }
    //val (selectedOption, onOptionSelected) = remember(updateInterval) { mutableStateOf(updateInterval) }
    Row(verticalAlignment = Alignment.CenterVertically){
        Spacer(modifier = Modifier
            .width(15.dp)
            .weight(1f)
        )
        Text("How often to update?")
        Row(
            modifier = Modifier
                .width(15.dp)
                .weight(1f)
                .offset(x = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            InfoButton(modifier = Modifier
                .offset(x = 15.dp),
                infoText = StringConstants.UPDATE_INTERVAL_INFO,
                size = 23.dp
            )
        }
    }
    Row {
        radioOptions.forEach { text ->
            Column(
                Modifier
                    .weight(1f)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            //onOptionSelected(text)
                            vm.saveData(updateInterval = text)
                        }
                    )
            ) {
                RadioButton(
                    enabled = enabled,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    selected = (text == selectedOption),
                    onClick = {
                        //onOptionSelected(text)
                        vm.saveData(updateInterval = text)
                    }
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = text,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}