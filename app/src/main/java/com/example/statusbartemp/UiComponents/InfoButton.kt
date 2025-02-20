package com.example.statusbartemp.UiComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoButton(
    infoText: String,
    size : Dp = 27.dp,
    moreInfoText : String? = null,
    firstContent :  @Composable () -> Unit = {Text(text = infoText)},
    secondContent :  @Composable (() -> Unit) = {Text(text = moreInfoText ?:"")},
    modifier : Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var showMore by remember(showDialog) { mutableStateOf(false) }


    Box(modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Icon(
            modifier = Modifier
                .size(size)
                .clip(shape = CircleShape)
                .clickable(onClick = { showDialog = true })
            ,
            imageVector = Icons.Filled.Info,
            contentDescription = "Info",
            tint = Color.DarkGray
        )
    }
    if (showDialog) {
        AlertDialog(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(25.dp))
                .clickable(onClick = { showDialog = false }),
            onDismissRequest = { showDialog = false },
            confirmButton = {_ConfirmButton(
                isMore = (moreInfoText != null),
                showMoreIn = showMore,
                showMoreUp = {showMore = it}
            )},
            text = {
                if (showMore) {
                    secondContent()
                }
                else {
                    firstContent()
                }

           },
        )
    }
}

@Composable
private fun _ConfirmButton(isMore : Boolean, showMoreIn : Boolean, showMoreUp : (Boolean) -> Unit){
    if (!isMore){
        Icon(
            Icons.Filled.Info,
            contentDescription = "Info",
            tint = Color.LightGray
        )
    }
    else if (!showMoreIn){
        Icon(imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = "More Info",
            tint = Color.LightGray,
            modifier = Modifier.size(40.dp).clip(CircleShape).clickable(
                onClick = {
                    showMoreUp(true)
                }
            )
        )

    }
    else {
        Icon(imageVector = Icons.Filled.KeyboardArrowLeft,
            contentDescription = "More Info",
            tint = Color.LightGray,
            modifier = Modifier.size(40.dp).clip(CircleShape).clickable(
                onClick = {
                    showMoreUp(false)
                }
            )
        )
    }

}


