package ps.petugas.salam.wakatobi.widget

import android.app.Activity
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import ps.petugas.salam.wakatobi.R

/**
 *----------------------------------------------
 * Created by ukieTux on 3/1/17 with ♥ .
 * @email  : ukie.tux@gmail.com
 * @github : https://www.github.com/tuxkids
 * ----------------------------------------------
 *          © 2017 | All Rights Reserved
 */
object CustomToastView {

    fun makeText(activity: Activity, message: String?, duration: Int): Toast {
        val toast = Toast(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.item_toast, null)
        val ivLogoToast = view.findViewById(R.id.iv_logo_toast) as ImageView
        val tvToastMessage = view.findViewById(R.id.tv_toast_message) as TextView

        ivLogoToast.setImageResource(R.mipmap.logo_icon)
        tvToastMessage.text = message

        toast.setGravity(Gravity.TOP, 0, activity.resources.getDimension(R.dimen.default_margin).toInt())
        toast.duration = duration
        toast.view = view
        toast.show()

        return toast
    }
}