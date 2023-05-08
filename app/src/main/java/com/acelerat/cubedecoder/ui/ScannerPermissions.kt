package com.acelerat.cubedecoder.ui

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext


@SuppressLint("InflateParams")
@Composable
fun ScannerPermissions(viewModel: MainViewModel) {
    //val context = LocalContext.current
    //val dialogQueue = viewModel.visiblePermissionDialogQueue
    
    val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.onPermissionResult(
                permission = CAMERA,
                isGranted = isGranted
            )
        })

    SideEffect {
        cameraPermissionResultLauncher.launch(CAMERA)
    }

//    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestMultiplePermissions(),
//        onResult = { perms ->
//            perms.keys.forEach { permission ->
//                viewModel.onPermissionResult(
//                    permission = permission,
//                    isGranted = perms[permission] == true
//                )
//
//            }
//        })
//
//    SideEffect {
//        multiplePermissionResultLauncher.launch(
//            arrayOf(CAMERA)
//        )
//    }


//    dialogQueue
//        .reversed()
//        .forEach { permission ->
//            PermissionDialog(
//                permissionTextProvider = when (permission) {
//                    CAMERA -> {
//                        CameraPermissionTextProvider()
//                    }
//                    RECORD_AUDIO -> {
//                        RecordAudioPermissionTextProvider()
//                    }
//                    CALL_PHONE -> {
//                        PhoneCallPermissionTextProvider()
//                    }
//                    else -> {
//                        return@forEach
//                    }
//                },
//                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
//                    context as Activity,
//                    permission
//                ),
//                onDismiss = viewModel::dismissDialog,
//                onOkClick = {
//                    viewModel.dismissDialog()
//                    multiplePermissionResultLauncher.launch(
//                        arrayOf(permission)
//                    )
//                },
//                onGoToAppSettingsClick = context::openAppSettings
//            )
//        }


}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also (::startActivity)
}


