package com.lc.workmanagerdemo

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.lc.workmanagerdemo.databinding.ActivityMainBinding
import com.lc.workmanagerdemo.worker.ImageDownloadWorker
import com.lc.workmanagerdemo.worker.SaveImageToFileWorker
import com.lc.workmanagerdemo.worker.WorkerKeys
import com.lc.workmanagerdemo.worker.WorkerKeys.UNIQUE_WORKER_NAME
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val workManager by lazy {
        WorkManager.getInstance(applicationContext)
    }

    private lateinit var downloadRequest: OneTimeWorkRequest

    private lateinit var saveRequest: OneTimeWorkRequest

    /*private var constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initWorker()
        setUpActions()
        observeWorkResult()
    }

    private fun initWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        downloadRequest = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
            .setConstraints(constraints)
            .build()
    }


    private fun setUpActions() {
        binding.btnDownlaod.setOnClickListener {
            //for one task so no need chain
            /*workManager.beginUniqueWork(
                 UNIQUE_WORKER_NAME,
                 ExistingWorkPolicy.KEEP,
                 downloadRequest
             ).enqueue()*/

            var continuation = workManager.beginUniqueWork(
                UNIQUE_WORKER_NAME,
                ExistingWorkPolicy.KEEP,
                downloadRequest
            )

            saveRequest = OneTimeWorkRequestBuilder<SaveImageToFileWorker>()
                .addTag("Output")
                .build()

            continuation = continuation.then(saveRequest)

            // Actually start the work
            continuation.enqueue()
        }

        binding.btnCancle.setOnClickListener{
            workManager.cancelUniqueWork(UNIQUE_WORKER_NAME)
        }
    }

    private fun observeWorkResult() {
        workManager
            .getWorkInfosForUniqueWorkLiveData(UNIQUE_WORKER_NAME)
            .observe(this) { workInfos ->

                val downloadInfo: WorkInfo? = workInfos?.find { it.id == downloadRequest.id }
                val downloadUri = downloadInfo?.outputData?.getString(WorkerKeys.IMAGE_URI)?.toUri()

                val text = when (downloadInfo?.state) {
                    WorkInfo.State.RUNNING -> "Downloading..."
                    WorkInfo.State.SUCCEEDED -> "Download succeeded"
                    WorkInfo.State.FAILED -> "Download failed"
                    WorkInfo.State.CANCELLED -> "Download cancelled"
                    WorkInfo.State.ENQUEUED -> "Download enqueued"
                    WorkInfo.State.BLOCKED -> "Download blocked"
                    else -> ""
                }

                binding.tvState.text = text

                if (downloadInfo?.state == WorkInfo.State.SUCCEEDED) {
                    binding.imageView.loadFromUrl(downloadUri)
                }
            }
    }
}