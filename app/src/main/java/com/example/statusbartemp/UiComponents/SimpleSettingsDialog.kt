package com.example.statusbartemp.UiComponents

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.RESTRICTIONS_DIALOG_TITLE
import com.weeklist.screens.utils.MainViewModel

enum class SettingsType(

)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSettingsDialog(vm : MainViewModel,
                         context : Context,
                         activity : Activity,
                         appSettings: AppSettings,
){
    if (vm.openSimpleSettingsDialog) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth(),
            onDismissRequest = {
                vm.closeSimpleSettingsDialog()
            },
            title = { Text(text = RESTRICTIONS_DIALOG_TITLE )},
            text = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppSettingsDialogContent(context, activity, vm, appSettings)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.closeSimpleSettingsDialog()
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

data class AppSettings(
    val useSavedLocation : Boolean,
)