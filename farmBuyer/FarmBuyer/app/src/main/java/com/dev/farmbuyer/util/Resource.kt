package com.dev.farmbuyer.util

/* Resource class copied from Phillips Lackner: https://github.com/philipplackner/KtorNoteApp/blob/VersionUpdate/app/src/main/java/com/androiddevs/ktornoteapp/other/Resource.kt*/
sealed class Resource<out T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>() : Resource<T>()
}
