package ps.petugas.salam.wakatobi.ui.finishreport

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.finish_report_screen.*
import okhttp3.MediaType
import okhttp3.RequestBody
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.base.BaseActivity
import ps.petugas.salam.wakatobi.ui.mainscreen.VMainScreen
import ps.petugas.salam.wakatobi.utils.AppController.Companion.context
import ps.petugas.salam.wakatobi.utils.SharedKey
import ps.petugas.salam.wakatobi.utils.SharedPref
import ps.petugas.salam.wakatobi.widget.CustomToastView
import ps.salam.wakatobi.ui.DialogImageZoom
import ps.salam.wakatobi.utils.ProgressRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class VFinishReport : BaseActivity(), IFinishReport.View, ProgressRequestBody.UploadCallbacks {


    private val mPresenter = PFinishReport()

    private lateinit var rxPermissions: RxPermissions
    private lateinit var progress: ProgressDialog

    private var imageFile: File? = null
    private lateinit var filePath: String

    private val CAMERA = 1
    private val GALLERY = 2

    override fun getLayoutResource(): Int {
        return R.layout.finish_report_screen
    }

    override fun myCodeHere() {
        title = getString(R.string.send_response)
        rxPermissions = RxPermissions(this)

        setupView()
    }

    override fun setupView() {
        mPresenter.attachView(this)

        progress = ProgressDialog(this)
        progress.setMessage(getString(R.string.send_response))
        progress.setCancelable(false)
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progress.isIndeterminate = false


        btn_camera.setOnClickListener {
            openCamera()
        }
        btn_gallery.setOnClickListener {
            openGallery()
        }
        btn_send_response.setOnClickListener {
            if (tet_value_response.text.isEmpty()) {
                CustomToastView.makeText(this, getString(R.string.error_empty), Toast.LENGTH_SHORT)
            } else {
                val items = HashMap<String, RequestBody>()
                items.put("action", toRequestBody("finish-report"))
                items.put("laporan", toRequestBody(intent.extras.getString("id_report")))
                items.put("pesan_selesai", toRequestBody(tet_value_response.text.toString()))
                items.put("petugas", toRequestBody(SharedPref.getString(SharedKey.id_user)))

                if (imageFile == null) {
                    rxPermissions
                            .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                            .doOnError { e -> CustomToastView.makeText(this, e.toString(), Toast.LENGTH_SHORT) }
                            .subscribe({ permission ->
                                if (permission) {
                                    imageFile = File(getExternalFilesDir(null).toString() +
                                            File.separator + "dummy.png")
//                                    try {
                                    imageFile!!.createNewFile()
//                                    } catch (e: IOException) {
//                                         TODO Auto-generated catch block
//                                        e.printStackTrace()
//                                    }
                                    mPresenter.sendFinishReport(items, imageFile!!, this)
                                } else {
                                    val builder = AlertDialog.Builder(this)
                                    builder.setMessage(getString(R.string.camera_not_allowed))
                                    builder.setPositiveButton(getString(R.string.retry)) { dialog, _ -> dialog.dismiss() }
                                    builder.setCancelable(false)
                                    builder.show()
                                }
                            })
                } else {
                    mPresenter.sendFinishReport(items, imageFile!!, this)
                }
            }
        }
    }

    private fun toRequestBody(value: String): RequestBody {
        val body = RequestBody.create(MediaType.parse("text/plain"), value)
        return body
    }

    override fun onProgressUpdate(percentage: Int) {
        progress.progress = percentage
    }

    override fun onError() {
        progress.dismiss()
    }

    override fun onFinish() {
        progress.progress = 100
    }


    override fun onShowDialog(isShow: Boolean, type: Int) {
        if (isShow)
            progress.show()
        else {
            if (type == 200) {
                finishAffinity()
                startActivity(Intent(applicationContext, VMainScreen::class.java))
            }
            progress.dismiss()
        }
    }

    override fun onShowMessage(message: String) {
        CustomToastView.makeText(this, message, Toast.LENGTH_SHORT)
    }

    private fun openCamera() {
        rxPermissions
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .doOnError { e -> CustomToastView.makeText(this, e.toString(), Toast.LENGTH_SHORT) }
                .subscribe({ permission ->
                    if (permission) {
                        try {
                            imageFile = createImageFile()
                        } catch (ex: IOException) {
                            ex.printStackTrace()
                        }

                        if (imageFile != null) {
                            val photoURI: Uri = FileProvider.getUriForFile(this, "ps.petugas.salam.wakatobi.provider", imageFile)
                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            val resolvedIntentActivities: List<ResolveInfo> = context?.packageManager!!.queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY)
                            resolvedIntentActivities
                                    .map { it.activityInfo.packageName }
                                    .forEach { context?.grantUriPermission(it, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION) }
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            if (cameraIntent.resolveActivity(packageManager) != null)
                                startActivityForResult(cameraIntent, CAMERA)
                        }
                    } else {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(getString(R.string.camera_not_allowed))
                        builder.setPositiveButton(getString(R.string.retry)) { dialog, _ -> dialog.dismiss() }
                        builder.setCancelable(false)
                        builder.show()
                    }
                })
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        filePath = image.absolutePath
        return image
    }

    private fun setPic(imageView: ImageView, imageFile: File) {
        Glide.with(this)
                .load(imageFile)
                .asBitmap()
                .centerCrop()
                .into(imageView)
        imageView.visibility = View.VISIBLE

        iv_response.setOnClickListener {
            val args = Bundle()
            args.putString("image_url", imageFile.absolutePath)
            args.putString("image_title", tet_value_response.text.toString())
            val dialogZoom = DialogImageZoom()
            dialogZoom.arguments = args
            dialogZoom.show(supportFragmentManager, "dialog image")
        }
    }

    private fun openGallery() {
        rxPermissions
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe({ permission ->
                    if (permission) {
                        Logger.d("lacak _camera gallery")
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                        galleryIntent.type = "image/*"
                        startActivityForResult(galleryIntent, GALLERY)
                    } else {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(getString(R.string.camera_not_allowed))
                        builder.setPositiveButton(getString(R.string.retry)) { dialog, _ -> dialog.dismiss() }
                        builder.setCancelable(false)
                        builder.show()

                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA && resultCode == Activity.RESULT_OK) {
            imageFile = File(filePath)
            if (imageFile?.exists()!!) {
                Logger.d("image exist")
                Logger.d("size before compress" + imageFile?.length()!! / 1024)
                //reduce size if file image more than 2048
//                if (imageFile.length() / 1024 > 2048) {
                val bitmap = BitmapFactory.decodeFile(imageFile?.absolutePath)
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, FileOutputStream(imageFile))
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    options.inSampleSize = 5
                    BitmapFactory.decodeFile(imageFile?.absolutePath!!, options)

                    setPic(iv_response, imageFile!!)
                    Logger.d("size after compress" + imageFile?.length()!! / 1024)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
//                }
            }


        } else if (requestCode == GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                val selectedImage = data?.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursorImage = this.contentResolver.query(selectedImage,
                        filePathColumn, null, null, null)
                if (cursorImage != null) {
                    cursorImage.moveToFirst()
                    val columnIndex = cursorImage.getColumnIndex(filePathColumn[0])
                    val picturePath = cursorImage.getString(columnIndex)
                    imageFile = File(picturePath)
                    cursorImage.close()
                }

                Logger.d("size before compress" + imageFile?.length()!! / 1024)

                //reduce size if file image more than 2048
//                if (imageFile.length() / 1024 > 2048) {
                val bitmap = BitmapFactory.decodeFile(imageFile?.absolutePath!!)
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, FileOutputStream(imageFile))
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    options.inSampleSize = 5
                    BitmapFactory.decodeFile(imageFile?.absolutePath!!, options)
                    setPic(iv_response, imageFile!!)
                    Logger.d("size after compress" + imageFile?.length()!! / 1024)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
//                }
//                val bmp = BitmapFactory.decodeFile(imageFile.absolutePath)
//                iv_report.visibility = View.VISIBLE
//                iv_report.setImageBitmap(bmp)

            } catch (e: Exception) {        //for xiaomi device
                val uri = data?.data
                imageFile = File(uri?.path)
                val selectedImage = getImageContentUri(this, imageFile!!)
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                val cursorImage = this.contentResolver.query(selectedImage,
                        filePathColumn, null, null, null)
                if (cursorImage != null) {
                    cursorImage.moveToFirst()
                    val columnIndex = cursorImage.getColumnIndex(filePathColumn[0])
                    val picturePath = cursorImage.getString(columnIndex)
                    imageFile = File(picturePath)
                    cursorImage.close()
                }

                Logger.d("size before compress" + imageFile?.length()!! / 1024)

                //reduce size if file image more than 2048
//                if (imageFile.length() / 1024 > 2048) {
                val bitmap = BitmapFactory.decodeFile(imageFile?.absolutePath!!)
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, FileOutputStream(imageFile))
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    options.inSampleSize = 5
                    BitmapFactory.decodeFile(imageFile?.absolutePath!!, options)
                    setPic(iv_response, imageFile!!)
                    Logger.d("size after compress" + imageFile!!.length() / 1024)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
//                }
            }
            filePath = imageFile?.absolutePath!!
            imageFile = File(imageFile?.absolutePath!!)
        }
    }

    private fun getImageContentUri(context: Context, imageFile: File): Uri? {
        val filePath = imageFile.absolutePath
        val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID),
                MediaStore.Images.Media.DATA + "=? ",
                arrayOf(filePath), null)

        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri = Uri.parse("content://media/external/images/media")
            cursor.close()
            return Uri.withAppendedPath(baseUri, "" + id)
        } else {
            if (imageFile.exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                return context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                return null
            }
        }
    }


    override fun onDetachScreen() {
    }
}
