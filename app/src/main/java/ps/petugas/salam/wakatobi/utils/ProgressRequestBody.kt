package ps.salam.wakatobi.utils

import android.os.Handler
import android.os.Looper

import java.io.File
import java.io.FileInputStream
import java.io.IOException

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink

/**
 * ----------------------------------------------
 * Created by ukieTux on 2/5/17 with ♥ .

 * @email : ukie.tux@gmail.com
 * *
 * @github : https://www.github.com/tuxkids
 * * ----------------------------------------------
 * * © 2017 | All Rights Reserved
 */

class ProgressRequestBody(private val mFile: File, private val mListener: ProgressRequestBody.UploadCallbacks) : RequestBody() {
    private val mPath: String? = null

    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int)

        fun onError()

        fun onFinish()
    }

    override fun contentType(): MediaType {
        // i want to upload only images
        return MediaType.parse("image/*")
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mFile.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val fileLength = mFile.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fis = FileInputStream(mFile)
        var uploaded: Long = 0

        try {
            try {
                var read: Int = 0
                val handler = Handler(Looper.getMainLooper())
                while (fis.read(buffer).let { read = it;it != -1 }) {

                    // update progress on UI thread
                    handler.post(ProgressUpdater(uploaded, fileLength))

                    uploaded += read.toLong()
                    sink.write(buffer, 0, read)
                }
            } finally {
                fis.close()
            }
        } finally {
            fis.close()
        }
    }

    private inner class ProgressUpdater internal constructor(private val mUploaded: Long, private val mTotal: Long) : Runnable {

        override fun run() {
            mListener.onProgressUpdate((100 * mUploaded / mTotal).toInt())
        }
    }

    companion object {

        private val DEFAULT_BUFFER_SIZE = 2048
    }
}
