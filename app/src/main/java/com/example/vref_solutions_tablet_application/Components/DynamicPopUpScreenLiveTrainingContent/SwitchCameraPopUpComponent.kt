package com.example.vref_solutions_tablet_application.Components

import androidx.compose.foundation.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.vref_solutions_tablet_application.Models.CameraLinkObject
import com.example.vref_solutions_tablet_application.Models.PopUpModels.SwitchCamera
import com.example.vref_solutions_tablet_application.StylingClasses.PaddingStatic
import com.example.vref_solutions_tablet_application.StylingClasses.RoundedSizeStatic

@Composable
fun SwitchCameraPopUpComponent(targetCamera: Int, cameraSwitchHandler: SwitchCamera) {


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(PaddingStatic.Small)) {

        var selectedVideoToDisplay: State<CameraLinkObject> = cameraSwitchHandler.selectedCameraObject.collectAsState()

        Row(modifier = Modifier
            .height(200.dp)
            .fillMaxWidth().padding(bottom = 8.dp)) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = selectedVideoToDisplay.value.thumbnailDrawableId),
                contentDescription = "Selected camera display",
                modifier = Modifier
                    .weight(4f)
                    .clip(RoundedCornerShape(RoundedSizeStatic.Medium)),
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
                                .padding(PaddingStatic.Tiny)
                                .clip(RoundedCornerShape(RoundedSizeStatic.Small))
                                .border(BorderStroke(if(item.videoDisplayType == selectedVideoToDisplay.value.videoDisplayType) 2.dp else 0.dp, Color.White ))
                                .clickable {
                                    cameraSwitchHandler.ChangeSelectedCamera(item)
                                },
                            painter = painterResource(id = item.thumbnailDrawableId),
                            contentDescription = "Camera display select option",
                            contentScale = ContentScale.FillBounds
                        )
                    //}
            }
        }
    }
}
