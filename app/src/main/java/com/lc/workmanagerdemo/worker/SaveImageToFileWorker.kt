package com.lc.workmanagerdemo.worker

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.lc.workmanagerdemo.worker.WorkerKeys.IMAGE_URI
import java.text.SimpleDateFormat
import java.util.*

class SaveImageToFileWorker(context: Context, workerParams: WorkerParameters) :Worker(context, workerParams) {
    private val title = "Saved Image"
    private val dateFormatter = SimpleDateFormat(
        "yyyy.MM.dd 'at' HH:mm:ss z",
        Locale.getDefault()
    )

    override fun doWork(): Result {
       // makeStatusNotification("Saving image", applicationContext)
        //sleep()

        val resolver = applicationContext.contentResolver
        return try {
            val resourceUri = inputData.getString(IMAGE_URI)

            val bitmap = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri)))

            val imageUrl = MediaStore.Images.Media.insertImage(
                resolver, bitmap, title, dateFormatter.format(Date()))

            if (!imageUrl.isNullOrEmpty()) {
                val output = workDataOf(IMAGE_URI to imageUrl)

                Result.success(output)
            } else {
                Result.failure()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            Result.failure()
        }
    }
}