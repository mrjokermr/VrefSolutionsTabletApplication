package com.example.vref_solutions_tablet_application.components

import androidx.compose.foundation.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.R
import com.example.vref_solutions_tablet_application.models.CameraLinkObject
import com.example.vref_solutions_tablet_application.models.popUpModels.SwitchCamera
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.padding
import com.example.vref_solutions_tablet_application.ui.theme.stylingClasses.roundedCornerShape

@Composable
fun SwitchCameraPopUpComponent(targetCamera: Int, cameraSwitchHandler: SwitchCamera) {


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(MaterialTheme.padding.small)) {

        var selectedVideoToDisplay: State<CameraLinkObject> = cameraSwitchHandler.selectedCameraObject.collectAsState()

        Row(modifier = Modifier
            .height(200.dp)
            .fillMaxWidth().padding(bottom = 8.dp)) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = selectedVideoToDisplay.value.thumbnailDrawableId),
                contentDescription = stringResource(R.string.selected_camera_display),
                modifier = Modifier
                    .weight(4f)
                    .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.medium)),
                contentScale = ContentScale.FillBounds
            )

            Spacer(modifier = Modifier.weight(1f))
        }
        
        var allCameraSwitchObjects = cameraSwitchHandler.allSwitchObjects.collectAsState()
        //big currently selected camera display
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
            items(allCameraSwitchObjects.value) {
                item ->
                    //if(item.videoDisplayType != selectedVideoToDisplay.value.videoDisplayType) {
                        Image(
                            modifier = Modifier
                                .height(120.dp)
                                .fillMaxWidth()
                                .padding(MaterialTheme.padding.tiny)
                                .clip(RoundedCornerShape(MaterialTheme.roundedCornerShape.small))
                                .border(BorderStroke(if(item.videoDisplayType == selectedVideoToDisplay.value.videoDisplayType) 2.dp else 0.dp, Color.White ))
                                .clickable {
                                    cameraSwitchHandler.changeSelectedCamera(item)
                                },
                            painter = painterResource(id = item.thumbnailDrawableId),
                            contentDescription = stringResource(R.string.camera_display_select_option),
                            contentScale = ContentScale.FillBounds
                        )
                    //}
            }
        }
    }
}
