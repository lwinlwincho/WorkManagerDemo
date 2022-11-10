package com.lc.workmanagerdemo.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.lc.workmanagerdemo.FileApi
import com.lc.workmanagerdemo.R
import com.lc.workmanagerdemo.worker.WorkerKeys.CHANNEL_ID
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ImageDownloadWorker(
    private val context: Context, workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        startForegroundService()
        delay(3000L)
        val response = FileApi.instance.downloadImage()
        when {
            response.isSuccessful -> {
                response.body()?.let { body ->
                    return withContext(Dispatchers.IO) {
                        val file = File(context.cacheDir, "image.jpg")
                        val outputStream = FileOutputStream(file)
                        outputStream.use { stream ->
                            try {
                                stream.write(body.bytes())
                            } catch (e: IOException) {
                                return@withContext Result.failure(workDataOf(WorkerKeys.ERROR_MSG to e.localizedMessage))
                            }
                        }
                        Result.success(workDataOf(WorkerKeys.IMAGE_URI to file.toUri().toString()))
                    }
                }
            }

            response.code().toString().startsWith("5") -> return Result.retry()

            else -> return Result.failure(workDataOf(WorkerKeys.ERROR_MSG to "Network error"))
        }

        return Result.failure(workDataOf(WorkerKeys.ERROR_MSG to "Unknown error"))
    }

    private suspend fun startForegroundService() {
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentText("Downloading...")
                    .setContentTitle("Download in progress")
                    .build()
            )
        )
    }
}