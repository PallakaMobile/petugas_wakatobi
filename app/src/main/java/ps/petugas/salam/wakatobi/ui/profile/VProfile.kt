package ps.petugas.salam.wakatobi.ui.profile

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
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.iid.FirebaseInstanceId
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.action_screen.*
import kotlinx.android.synthetic.main.layer_toolbar.*
import kotlinx.android.synthetic.main.profile_layer_email.*
import kotlinx.android.synthetic.main.profile_layer_info.*
import kotlinx.android.synthetic.main.profile_layer_password.*
import kotlinx.android.synthetic.main.profile_screen.*
import okhttp3.MediaType
import okhttp3.RequestBody
import ps.petugas.petugas.salam.wakatobi.ui.profile.IProfile
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.adapter.AdapterAction
import ps.petugas.salam.wakatobi.base.BaseActivity
import ps.petugas.salam.wakatobi.model.DataAction
import ps.petugas.salam.wakatobi.model.DataLogin
import ps.petugas.salam.wakatobi.support.cropper.CropImage
import ps.petugas.salam.wakatobi.support.cropper.CropImageView
import ps.petugas.salam.wakatobi.support.xrecyclerview.XRecyclerView
import ps.petugas.salam.wakatobi.ui.editprofile.VEditProfile
import ps.petugas.salam.wakatobi.utils.AppController
import ps.petugas.salam.wakatobi.utils.RxHelperEditText
import ps.petugas.salam.wakatobi.utils.SharedKey
import ps.petugas.salam.wakatobi.utils.SharedPref
import ps.petugas.salam.wakatobi.widget.CustomToastView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.collections.HashMap

class VProfile : BaseActivity(), IProfile.View {
    private val mPresenter = PProfile()
    private val composite = CompositeDisposable()
    private lateinit var viewProfile: View
    private lateinit var viewPassword: View
    private lateinit var viewEmail: View
    private lateinit var viewHistory: View


    private val items = HashMap<String, String>()

    private lateinit var dialog: ProgressDialog

    private lateinit var rxPermissions: RxPermissions
    private var imageFile: File? = null
    private lateinit var filePath: String

    private var passwordValid = false
    private var emailValid = false

    private val CAMERA = 1
    private val GALLERY = 2

    private var page = 1
    private var totalPage = 0

    private var isRefresh = false
    private var isLoadMore = false

    private lateinit var adapter: AdapterAction
    private var listResponse = ArrayList<DataAction.Response>()

    override fun getLayoutResource(): Int {
        return R.layout.profile_screen
    }

    override fun myCodeHere() {
        title = getString(R.string.profile_settings)
        toolbar.showOverflowMenu()

        setupView()
    }


    override fun setupView() {
        rxPermissions = RxPermissions(this)

        dialog = ProgressDialog(this)
        dialog.setMessage(getString(R.string.please_wait))
        dialog.setCancelable(false)

        mPresenter.attachView(this)

        //setup view tab
        viewProfile = LayoutInflater.from(this).inflate(R.layout.profile_layer_info, null, true)
        viewPassword = LayoutInflater.from(this).inflate(R.layout.profile_layer_password, null, true)
        viewEmail = LayoutInflater.from(this).inflate(R.layout.profile_layer_email, null, true)
        viewHistory = LayoutInflater.from(this).inflate(R.layout.action_screen, null, true)


        //get Login Data
        items.put("action", "check-login")
        items.put("username", SharedPref.getString(SharedKey.username))
        items.put("password", SharedPref.getString(SharedKey.password))
        Logger.d(SharedPref.getString(SharedKey.username) + ":" + SharedPref.getString(SharedKey.password))
        mPresenter.getProfile(items)


        //load view profile first
        fl_profile.removeAllViews()
        fl_profile.addView(viewProfile)
        rg_profile.setOnCheckedChangeListener { _, checkedId ->

            fl_profile.removeAllViews()
            fl_profile.invalidate()
            fl_profile.requestLayout()

            when (checkedId) {
                R.id.rb_profile -> {
                    fl_profile.addView(viewProfile)
                }
                R.id.rb_password -> {
                    fl_profile.addView(viewPassword)
                    //update password
                    onUpdatePassword()
                }
                R.id.rb_email -> {
                    fl_profile.addView(viewEmail)
                    //update email
                    onUpdateEmail()
                }
                R.id.rb_history_action -> {
                    fl_profile.addView(viewHistory)
                    //update email
                    onGetHistory()
                }
            }
        }

        iv_change_profile_picture.setOnClickListener {
            val choose = arrayOf<CharSequence>(getString(R.string.camera), getString(R.string.gallery))
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.choose_source))
            builder.setItems(choose) { _, i ->
                when (i) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            builder.show()
        }
    }

    private fun onGetHistory() {
        mPresenter.getActionHistory(page, SharedPref.getString(SharedKey.id_user))

        //setup recylcerview
        val layoutManager = LinearLayoutManager(this)
        adapter = AdapterAction(listResponse, "history")
        rv_action_report.adapter = adapter
        rv_action_report.setHasFixedSize(true)
        rv_action_report.layoutManager = layoutManager
        rv_action_report.setLoadingListener(object : XRecyclerView.LoadingListener {
            override fun onRefresh() {
                page = 1
                isRefresh = true
                mPresenter.getActionHistory(page, SharedPref.getString(SharedKey.id_user))
            }

            override fun onLoadMore() {
                page++
                if (page <= totalPage) {
                    isLoadMore = true
                    mPresenter.getActionHistory(page, SharedPref.getString(SharedKey.id_user))
                } else
                    rv_action_report.loadMoreComplete()
            }
        })
    }

    override fun onGetActionHistory(dataAction: DataAction) {
        if (page == 1 && dataAction.response.isEmpty()) {
            tv_no_data_action.visibility = View.VISIBLE
            rv_action_report.visibility = View.GONE
        } else {
            tv_no_data_action.visibility = View.GONE
            rv_action_report.visibility = View.VISIBLE
        }

        if (isRefresh || page == 1) {
            if (listResponse.isNotEmpty())
                listResponse.clear()
            isRefresh = false
            rv_action_report.refreshComplete()
        }
        if (isLoadMore) {
            isLoadMore = false
            rv_action_report.loadMoreComplete()
        }

        page = dataAction.pagination.active_page
        totalPage = dataAction.pagination.total_page
        listResponse.addAll(dataAction.response)
        adapter.notifyDataSetChanged()
    }

    override fun onUpdateProfilePicture(imageUrl: String) {
        Glide.with(this)
                .load(imageUrl)
                .crossFade()
                .error(R.drawable.ic_default_profile)
                .into(iv_profile)
        Logger.d("url gambar : $imageUrl")

        progressBar.visibility = View.GONE
    }

    override fun onDialogShow(isShow: Boolean) {
        if (isShow)
            dialog.show()
        else dialog.dismiss()
    }

    override fun onMessage(message: String) {
        CustomToastView.makeText(this, message, Toast.LENGTH_SHORT)
    }

    @Suppress("DEPRECATION")
    override fun onGetProfile(response: DataLogin.Response) {
        Glide.with(this)
                .load(response.foto)
                .crossFade()
                .error(R.drawable.ic_default_profile)
                .into(iv_profile)

        Glide.with(this)
                .load(response.foto_instansi)
                .crossFade()
                .error(R.drawable.ic_default_profile)
                .into(iv_instance_image)

        tv_profile_name.text = response.nama
        tv_instance.text = response.instansi
        tv_total_finish.text = response.aksi
        tv_total_point.text = Html.fromHtml(getString(R.string.total_point_dot) + "<b> " + response.poin + "</b>")
        tv_rank.text = Html.fromHtml(getString(R.string.rank_dot) + "<b> " + response.peringkat + "</b>")

        tv_name.text = response.nama
        tv_phone_number.text = response.telp
        tv_work_name.text = response.instansi
        tv_address.text = response.alamat
        btn_change_profile.setOnClickListener {
            val editProfile = Intent(applicationContext, VEditProfile::class.java)
            editProfile.putExtra("response", response)
            startActivity(editProfile)
        }

        //update id_user,id_instansi
        SharedPref.saveBol(SharedKey.is_login, true)
        SharedPref.saveString(SharedKey.id_user, response.id_user.toString())
        SharedPref.saveString(SharedKey.id_instansi, response.id_instansi.toString())
    }


    private fun updatePassword() {
        composite.add(checkOldPassword().subscribe({ isValid ->
            if (!isValid) {
                tet_old_password.error = getString(R.string.password_not_valid)
                tet_old_password.requestFocus()
            } else tet_old_password.error = null
        }))

        composite.add(checkNewPassword().subscribe({ isValid ->
            if (!isValid) {
                tet_new_password.error = getString(R.string.password_not_valid)
                tet_new_password.requestFocus()
            } else tet_new_password.error = null
        }))

        composite.add(checkRetypeNewPassword().subscribe({ isValid ->
            if (!isValid) {
                tet_repeat_new_password.error = getString(R.string.password_not_match)
                tet_repeat_new_password.requestFocus()
            } else tet_repeat_new_password.error = null
        }))

        composite.add(checkFormPassword().subscribe({ isValid -> this.passwordValid = isValid }))

        btn_update_password.setOnClickListener {
            if (passwordValid) {
                val items = HashMap<String, String>()
                items.put("action", "change-password")
                items.put("id_user", SharedPref.getString(SharedKey.id_user))
                items.put("pass_lama", tet_old_password.text.toString())
                items.put("pass_baru", tet_new_password.text.toString())
                mPresenter.updatePassword(items)
            } else CustomToastView.makeText(this, getString(R.string.error_empty), Toast.LENGTH_SHORT)
        }
    }

    override fun onUpdatePassword() {
        composite.clear()
        tet_old_password.text.clear()
        tet_new_password.text.clear()
        tet_repeat_new_password.text.clear()
        tet_old_password.clearFocus()
        tet_new_password.clearFocus()
        tet_repeat_new_password.clearFocus()
        updatePassword()
    }


    private fun updateEmail() {
        composite.add(oldEmail().subscribe({ isValid ->
            if (!isValid) {
                tet_old_email.error = getString(R.string.email_not_valid)
                tet_old_email.requestFocus()
            } else tet_old_email.error = null
        }))

        composite.add(newEmail().subscribe({ isValid ->
            if (!isValid) {
                tet_new_email.error = getString(R.string.email_not_valid)
                tet_new_email.requestFocus()
            } else tet_new_email.error = null
        }))

        composite.add(newRetypeEmail().subscribe({ isValid ->
            if (!isValid) {
                tet_repeat_new_email.error = getString(R.string.email_not_match)
                tet_repeat_new_email.requestFocus()
            } else tet_repeat_new_email.error = null
        }))

        composite.add(checkFormEmail().subscribe({ isValid -> this.emailValid = isValid }))

        btn_update_email.setOnClickListener {
            if (emailValid) {
                val items = HashMap<String, String>()
                items.put("action", "change-email")
                items.put("id_user", SharedPref.getString(SharedKey.id_user))
                items.put("email_lama", tet_old_email.text.toString())
                items.put("email_baru", tet_new_email.text.toString())
                mPresenter.updateEmail(items)
            } else CustomToastView.makeText(this, getString(R.string.error_empty), Toast.LENGTH_SHORT)
        }
    }

    override fun onUpdateEmail() {
        composite.clear()
        tet_old_email.text.clear()
        tet_new_email.text.clear()
        tet_repeat_new_email.text.clear()
        tet_old_email.clearFocus()
        tet_new_email.clearFocus()
        tet_repeat_new_email.clearFocus()
        updateEmail()
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
                            val resolvedIntentActivities: List<ResolveInfo> = AppController.context?.packageManager!!.queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY)
                            resolvedIntentActivities
                                    .map { it.activityInfo.packageName }
                                    .forEach { AppController.context?.grantUriPermission(it, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION) }
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

    private fun setPic(imageFile: File) {
        CropImage.activity(Uri.parse("file://" + imageFile.absolutePath))
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this)
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
        //get result from crop image
        Logger.d("result code" + resultCode)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                imageFile = File(resultUri.path)

                //update profile picture
                val items = HashMap<String, RequestBody>()
                items.put("action", toRequestBody("update-profile-picture"))
                items.put("id", toRequestBody(SharedPref.getString(SharedKey.id_user)))
                mPresenter.updateProfilePicture(items, imageFile!!)
                progressBar.visibility = View.VISIBLE
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Logger.d("error" + error)
            }
        }

        if (requestCode == CAMERA && resultCode == Activity.RESULT_OK) {
            imageFile = File(filePath)
            if (imageFile?.exists()!!) {
                Logger.d("image exist")
                Logger.d("size before compress" + imageFile?.length()!! / 1024)
                try {
                    val bitmap = BitmapFactory.decodeFile(imageFile?.absolutePath)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, FileOutputStream(imageFile))
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    options.inSampleSize = 5
                    BitmapFactory.decodeFile(imageFile?.absolutePath!!, options)

                    setPic(imageFile!!)
                    Logger.d("size after compress" + imageFile?.length()!! / 1024)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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

                val bitmap = BitmapFactory.decodeFile(imageFile?.absolutePath!!)
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, FileOutputStream(imageFile))
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    options.inSampleSize = 5
                    BitmapFactory.decodeFile(imageFile?.absolutePath!!, options)
                    setPic(imageFile!!)
                    Logger.d("size after compress" + imageFile?.length()!! / 1024)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

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

                val bitmap = BitmapFactory.decodeFile(imageFile?.absolutePath!!)
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, FileOutputStream(imageFile))
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    options.inSampleSize = 5
                    BitmapFactory.decodeFile(imageFile?.absolutePath!!, options)
                    setPic(imageFile!!)
                    Logger.d("size after compress" + imageFile!!.length() / 1024)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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


    private fun toRequestBody(value: String): RequestBody {
        val body = RequestBody.create(MediaType.parse("text/plain"), value)
        return body
    }

    override fun onResume() {
        super.onResume()
        mPresenter.getProfile(items)
    }

    override fun onDetachScreen() {
        mPresenter.detachView()
    }

    //
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return super.onCreateOptionsMenu(menu)
    }


    private fun authEmail(email: String): Boolean {
        val p = Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}")
        val m = p.matcher(email)
        return m.matches()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_logout -> {
                SharedPref.clearAll()
                SharedPref.saveString(SharedKey.firebase_ID, FirebaseInstanceId.getInstance().token.toString())
                finishAffinity()
                val i = baseContext.packageManager
                        .getLaunchIntentForPackage(baseContext.packageName)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    //change password
    private fun checkOldPassword(): Observable<Boolean> {
        return RxHelperEditText.getTextWatcherObservable(tet_old_password)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .map({ oldPassword: String -> !TextUtils.isEmpty(oldPassword) && tet_old_password.length() > 5 })
    }

    private fun checkNewPassword(): Observable<Boolean> {
        return RxHelperEditText.getTextWatcherObservable(tet_new_password)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .map({ newPassword: String -> !TextUtils.isEmpty(newPassword) && tet_new_password.length() > 5 })
    }

    private fun checkRetypeNewPassword(): Observable<Boolean> {
        return RxHelperEditText.getTextWatcherObservable(tet_repeat_new_password)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .map({ retypeNewPassword: String -> !TextUtils.isEmpty(retypeNewPassword) && retypeNewPassword == tet_new_password.text.toString() })
    }

    private fun oldEmail(): Observable<Boolean> {
        return RxHelperEditText.getTextWatcherObservable(tet_old_email)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .map({ oldEmail: String -> !TextUtils.isEmpty(oldEmail) && authEmail(oldEmail) })
    }

    private fun newEmail(): Observable<Boolean> {
        return RxHelperEditText.getTextWatcherObservable(tet_new_email)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .map({ newEmail: String -> !TextUtils.isEmpty(newEmail) && authEmail(newEmail) })
    }

    private fun newRetypeEmail(): Observable<Boolean> {
        return RxHelperEditText.getTextWatcherObservable(tet_repeat_new_email)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .map({ retypeNewEmail: String -> !TextUtils.isEmpty(retypeNewEmail) && retypeNewEmail == tet_new_email.text.toString() })
    }

    private fun checkFormEmail(): Observable<Boolean> {
        return Observable.combineLatest(oldEmail(), newEmail(), newRetypeEmail(), (Function3 { t1, t2, t3 -> t1 && t2 && t3 }))
    }

    private fun checkFormPassword(): Observable<Boolean> {
        return Observable.combineLatest(checkOldPassword(), checkNewPassword(), checkRetypeNewPassword(), (Function3 { t1, t2, t3 -> t1 && t2 && t3 }))
    }

}
