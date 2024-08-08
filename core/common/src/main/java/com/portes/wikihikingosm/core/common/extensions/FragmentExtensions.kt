package com.portes.wikihikingosm.core.common.extensions

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment


fun Fragment.multiplePermissionsLauncher(
    allGranted: () -> Unit,
    onReject: () -> Unit,
): ActivityResultLauncher<Array<String>> =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.entries.all { it.value }) {
            allGranted()
        } else {
            onReject()
        }
    }