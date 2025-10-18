// Create from file
val file = File("/storage/emulated/0/DCIM/Camera/photo.jpg")
val photoItem = PhotoItem(file)

// Create with all parameters
val photoItem2 = PhotoItem(
    file = file,
    name = "photo.jpg",
    path = "/storage/emulated/0/DCIM/Camera/photo.jpg",
    size = 1024000,
    lastModified = System.currentTimeMillis()
)

// Use utility functions
println(photoItem.getFormattedSize()) // "1000 KB"
println(photoItem.getRelativeTime()) // "2 hours ago"
println(photoItem.isImage) // true

// Sorting
val photos = listOf(photoItem, photoItem2)
val sortedByDate = photos.sortedWith(PhotoItem.DATE_COMPARATOR_DESC)
