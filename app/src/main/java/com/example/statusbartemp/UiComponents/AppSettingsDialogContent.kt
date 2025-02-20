package com.example.statusbartemp.UiComponents

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ComponentActivity
import com.example.statusbartemp.LogicAndData.StringConstants
import com.example.statusbartemp.Permissions.checkPermissions
import com.example.statusbartemp.Permissions.initializePermissionHelper
import com.example.statusbartemp.Permissions.obtainBackGroundPermissions
import com.weeklist.screens.utils.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun AppSettingsDialogContent(
    context: Context,
    activity : Activity,
    vm: MainViewModel,
    appSettings: AppSettings
){
    val scope = rememberCoroutineScope()
    Button(
        onClick = {
            scope.launch{
                val hasPermissions = checkPermissions(context, activity, !(appSettings.useSavedLocation))
                if (hasPermissions){
                    Toast.makeText(context, StringConstants.PERMISSIONS_OK, Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(context, StringConstants.PERMISSIONS_NOT_OK, Toast.LENGTH_LONG).show()
                }
            }
        }
    ){
        Text("Check permissions")
    }
    Spacer(modifier = Modifier.height(5.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier
            .width(15.dp)
            .weight(1f))
        Button(
            onClick = {
                scope.launch{
                    initializePermissionHelper(context, activity, !(appSettings.useSavedLocation))
                }
                scope.launch{
                    obtainBackGroundPermissions(context, activity)
                }
                
            }
        ){
            Text("Request permissions ")
        }
        InfoButton(
            modifier = Modifier
                .width(15.dp)
                .weight(1f)
                .offset(x = 0.dp),
            infoText = StringConstants.PERMISSIONS_INFO,
            size = 23.dp
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(verticalAlignment = Alignment.CenterVertically){
        Spacer(modifier = Modifier
            .width(15.dp)
            .weight(1f))
        Button(
            onClick = {
                scope.launch{
                    val pm = context.getSystemService(ComponentActivity.POWER_SERVICE) as PowerManager
                    if (pm.isIgnoringBatteryOptimizations(context.packageName)){
                        Toast.makeText(context, StringConstants.NO_OPTIMIZATIONS, Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(context, StringConstants.OPTIMIZATIONS_ENFORCED, Toast.LENGTH_LONG).show()
                    }
                }
                
            }
        ){
            Text("Check optimization")
        }
        InfoButton(
            modifier = Modifier
                .width(15.dp)
                .weight(1f)
                .offset(x = 0.dp),
            infoText = StringConstants.RESTRICTIONS_INFO,
            size = 23.dp
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Button(
        onClick = {
            scope.launch{
                val intent = Intent()
                val pm = context.getSystemService(ComponentActivity.POWER_SERVICE) as PowerManager
                if (pm.isIgnoringBatteryOptimizations(context.packageName)) {
                    intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                } else {
                    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    intent.data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            }
            
        }
    ){
        Text("Manage optimization settings")
    }
    
    Spacer(modifier = Modifier.height(22.dp))
    Button(
        onClick = {
            scope.launch{
                val backgroundProcessRunning = vm.checkBackGroundProcessState()
                if (backgroundProcessRunning){
                    Toast.makeText(context, "Background process is running", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(context, "Background process is not running", Toast.LENGTH_SHORT).show()
                }
            }

        }
    ){
        Text("Check if a background process is currently running")
    }
    
}

