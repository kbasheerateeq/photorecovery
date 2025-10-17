package com.example.photorecovery.utils

import android.content.Context
import android.os.Environment
import java.io.File

object FileUtils {
    
    fun scanForPhotos(context: Context): List<File> {
        val photoFiles = mutableListOf<File>()
        
        // Common directories where photos might be stored
        val directories = arrayOf(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            File(Environment.getExternalStorageDirectory(), "WhatsApp/Media/WhatsApp Images"),
            File(Environment.getExternalStorageDirectory(), "Download"),
            File(Environment.getExternalStorageDirectory(), "Pictures"),
            File(Environment.getExternalStorageDirectory(), "Camera")
        )
        
        // Image extensions to look for
        val imageExtensions = arrayOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
        
        directories.forEach { directory ->
            if (directory.exists() && directory.isDirectory) {
                scanDirectory(directory, imageExtensions, photoFiles)
            }
        }
        
        return photoFiles
    }
    
    private fun scanDirectory(
        directory: File,
        extensions: Array<String>,
        result: MutableList<File>
    ) {
        try {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    // Recursively scan subdirectories
                    scanDirectory(file, extensions, result)
                } else if (file.isFile && isImageFile(file, extensions)) {
                    result.add(file)
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
    
    private fun isImageFile(file: File, extensions: Array<String>): Boolean {
        val fileName = file.name.lowercase()
        return extensions.any { fileName.endsWith(".$it") }
    }
    
    fun deletePhoto(file: File): Boolean {
        return try {
            file.delete()
        } catch (e: SecurityException) {
            e.printStackTrace()
            false
        }
    }
    
    fun formatFileSize(size: Long): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            else -> "${size / (1024 * 1024)} MB"
        }
    }
}
