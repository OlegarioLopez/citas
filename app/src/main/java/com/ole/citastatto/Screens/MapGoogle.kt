package com.ole.citastatto.Screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker


@Composable
fun MapGoogle(){
    val marker = LatLng(51.897107,-8.4704763)
    GoogleMap(Modifier.fillMaxSize(),
        cameraPositionState = CameraPositionState(CameraPosition.fromLatLngZoom(marker,10f))
    ){
        Marker(position = marker, title = "Tattoo Studio", snippet = "Llámanos si tienes problemas para encontrarnos")
    }
}
