package com.example.statusbartemp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.statusbartemp.Permissions.initializePermissionHelper
import com.example.statusbartemp.ui.theme.StatusBarTempTheme
import com.weeklist.screens.utils.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StatusBarTempTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val vm : MainViewModel = hiltViewModel()
                    var initialized by rememberSaveable{ mutableStateOf(false) }
                    var startLoading by rememberSaveable{ mutableStateOf(false) }
                    LaunchedEffect(Unit){
                        vm.setStatusMessage("")
                        initializePermissionHelper(
                            context = this@MainActivity,
                            activity = this@MainActivity,
                            false
                        )
                        delay(30)
                        startLoading = true
                        delay(200)
                        initialized=true
                    }
                    if (startLoading) {
                        AnimatedVisibility(
                            visible = initialized,
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 150,
                                    delayMillis = 150,
                                    easing = EaseIn
                                )
                            )
                        ) {
                            MainApp(
                                vm,
                                activity = this@MainActivity,
                                context = this@MainActivity
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StatusBarTempTheme {
        Greeting("Android")
    }
}