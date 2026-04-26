package com.example.pairingapp.data.ini

import java.io.File

object StoragePaths {
    val sharedStorageRoot = File("/storage/emulated/0")
    val sharedStorageAndroidData = File(sharedStorageRoot, "Android/data")

    fun appExternalFilesDir(packageName: String): File {
        return File(sharedStorageAndroidData, "$packageName/files")
    }
}