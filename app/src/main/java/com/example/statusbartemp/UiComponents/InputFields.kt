package com.example.statusbartemp.UiComponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.statusbartemp.LogicAndData.Constants.Companion.decimalPattern
import com.example.statusbartemp.LogicAndData.Constants.Companion.textInputNegativePattern

@Composable
fun decimalInputFieldForCoordinates(
    inVal : String,
    label : String,
    maxLength : Int = 9,
    onInput : (String) -> Unit,
    onPastedAfterComma : (String) -> Unit = {  }
) {
    Box(modifier = Modifier, Alignment.CenterEnd) {
        TextField(
            value = inVal,
            onValueChange = {
                if (!it.matches(decimalPattern) && it.isNotEmpty()){
                    //onInput(it.dropLast(1))
                    var input = it
                    while (!input.matches(decimalPattern) && input.isNotEmpty())
                        input = input.dropLast(1)
                    onInput(input)
                }
                else if ((it.isEmpty()) || (it.matches(decimalPattern) && it.length <= maxLength)) {
                    onInput(it)
                }
                else if (it.length > maxLength) onInput(it.take(maxLength))
                if (it.contains(",")) {
                    val splitIt = it.split(",")
                    if (splitIt.lastIndex == 1) {
                        onPastedAfterComma(splitIt[1].trim())
                        
                    }
                }
            },
            label = {
                Text(
                    text = label
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
        )
        Button(
            modifier = Modifier
                .size(22.dp)
                .offset(
                    x = when (inVal == "") {
                        true -> 155.dp
                        false -> -5.dp
                    }
                ),
            onClick = {
                onInput("")
            },
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.45f),
                contentColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.9f)
            )
        ) {
            Icon(Icons.Filled.Clear, "")
        }
    }
}


@Composable
fun stringInputField(
    inVal : String,
    label : String,
    shouldFocus : Boolean = false,
    maxLength : Int = 49,
    onInput : (String) -> Unit,
) {
    val focusRequester = FocusRequester()
    var textFieldLoaded by remember { mutableStateOf(false) }
    Box(modifier = Modifier, Alignment.CenterEnd){
        TextField(
            modifier = when(shouldFocus) {
                true -> Modifier
                    .focusRequester(focusRequester)
                    .onGloballyPositioned {
                        if (!textFieldLoaded) {
                            focusRequester.requestFocus() // IMPORTANT
                            textFieldLoaded = true // stop cyclic recompositions
                        }
                    }
                false -> Modifier
            },
            value = inVal,
            onValueChange = {
                if (it.length<=maxLength && (it.isEmpty() || !it.matches(textInputNegativePattern))) {
                    onInput(it)
                }
            },
            label = {
                Text(
                    text = label
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
            singleLine = true,
    
        )
        Button(
            modifier = Modifier
                .size(22.dp)
                .offset(
                    x = when (inVal == "") {
                        true -> 155.dp
                        false -> -5.dp
                    }
                ),
            onClick = {
                onInput("")
            },
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.45f),
                contentColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.9f)
            )
        ) {
            Icon(Icons.Filled.Clear, "")
        }
    }
}
