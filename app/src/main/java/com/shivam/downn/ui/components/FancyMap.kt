package com.shivam.downn.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.shivam.downn.R

/**
 * A stylized Google Map component with a "Snapchat-like" clean look.
 *
 * @param modifier The modifier to be applied to the map.
 * @param initialCameraPosition The initial position of the camera.
 * @param showMyLocation Whether to show the user's current location on the map.
 */
@Composable
fun FancyMap(
    modifier: Modifier = Modifier,
    initialCameraPosition: LatLng = LatLng(37.4219999, -122.0840575), // Default to Googleplex
    showMyLocation: Boolean = false,
    cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialCameraPosition, 10f)
    },
    onMapClick: (LatLng) -> Unit = {},
    gesturesEnabled: Boolean = true,
    content: @Composable () -> Unit = {}
) {
    val context = LocalContext.current

    // Load the custom map style from resources
    val mapStyle = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    }

    val properties = remember(showMyLocation) {
        MapProperties(
            isMyLocationEnabled = showMyLocation,
            mapStyleOptions = mapStyle,
            isBuildingEnabled = false,
            isIndoorEnabled = false,
            isTrafficEnabled = false
        )
    }

    val uiSettings = remember(gesturesEnabled) {
        MapUiSettings(
            myLocationButtonEnabled = showMyLocation,
            zoomControlsEnabled = false,
            mapToolbarEnabled = false,
            compassEnabled = true,
            scrollGesturesEnabled = gesturesEnabled,
            zoomGesturesEnabled = gesturesEnabled,
            tiltGesturesEnabled = gesturesEnabled,
            rotationGesturesEnabled = gesturesEnabled
        )
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapClick = onMapClick,
        content = content
    )
}
