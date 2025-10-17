package com.example.photorecovery

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.photorecovery.adapter.PhotoAdapter
import com.example.photorecovery.databinding.ActivityMainBinding
import com.example.photorecovery.model.PhotoItem
import com.example.photorecovery.utils.FileUtils
import java.io.File
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var photoAdapter: PhotoAdapter
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            // Permission granted
            scanForPhotos()
        } else {
            // Permission denied
            showPermissionRequired()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        checkPermissions()
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter(emptyList()) { photo ->
            showDeleteConfirmation(photo)
        }
        
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = photoAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnGrantPermission.setOnClickListener {
            requestPermissions()
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permissions.all { 
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED 
        }) {
            scanForPhotos()
        } else {
            showPermissionRequired()
        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        permissionLauncher.launch(permissions)
    }

    private fun scanForPhotos() {
        showLoading()
        
        executor.execute {
            try {
                val photoFiles = FileUtils.scanForPhotos(this@MainActivity)
                val photoItems = photoFiles.map { file ->
                    PhotoItem(
                        file = file,
                        name = file.name,
                        path = file.absolutePath,
                        size = file.length(),
                        lastModified = file.lastModified()
                    )
                }.sortedByDescending { it.lastModified }

                handler.post {
                    if (photoItems.isEmpty()) {
                        showEmptyState()
                    } else {
                        showPhotos(photoItems)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handler.post {
                    showEmptyState()
                    Toast.makeText(this, "Error scanning photos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDeleteConfirmation(photo: PhotoItem) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_delete))
            .setMessage("Are you sure you want to delete ${photo.name}?")
            .setPositiveButton(R.string.delete) { dialog, _ ->
                deletePhoto(photo)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deletePhoto(photo: PhotoItem) {
        executor.execute {
            val success = FileUtils.deletePhoto(photo.file)
            
            handler.post {
                if (success) {
                    Toast.makeText(this, R.string.photo_deleted, Toast.LENGTH_SHORT).show()
                    // Refresh the photo list
                    scanForPhotos()
                } else {
                    Toast.makeText(this, R.string.error_deleting, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.permissionLayout.visibility = View.GONE
    }

    private fun showPhotos(photos: List<PhotoItem>) {
        binding.progressBar.visibility = View.GONE
        binding.tvEmpty.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.permissionLayout.visibility = View.GONE
        
        photoAdapter.updatePhotos(photos)
    }

    private fun showEmptyState() {
        binding.progressBar.visibility = View.GONE
        binding.tvEmpty.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.permissionLayout.visibility = View.GONE
    }

    private fun showPermissionRequired() {
        binding.progressBar.visibility = View.GONE
        binding.tvEmpty.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.permissionLayout.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
}
