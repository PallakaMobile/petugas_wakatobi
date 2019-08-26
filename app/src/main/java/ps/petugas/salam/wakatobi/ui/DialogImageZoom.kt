package ps.salam.wakatobi.ui

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.dialog_image_zoom.*
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.base.BaseDialogFragment

/**
 * ----------------------------------------------
 * Created by ukieTux on 1/14/17 with ♥ .
 * @email : ukie.tux@gmail.com
 * @github : https://www.github.com/tuxkids
 * * ----------------------------------------------
 * * © 2017 | All Rights Reserved
 */

class DialogImageZoom : BaseDialogFragment() {
    override fun getStyleResource(): Int {
        return R.style.DialogImage
    }

    override fun getLayoutResource(): Int {
        return R.layout.dialog_image_zoom
    }

    override fun putYourMethodHere() {
        iv_close.setOnClickListener { dismiss() }
        tv_status.text = arguments.getString("image_title")
        Logger.d(arguments.getString("image_url"))
        Glide.with(activity)
                .load(arguments.getString("image_url"))
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .crossFade()
                .sizeMultiplier(0.5f)
                .override(2048, 2048)
                .fitCenter()
                .priority(Priority.HIGH)
                .into(object : SimpleTarget<GlideDrawable>() {
                    override fun onResourceReady(resource: GlideDrawable?, glideAnimation: GlideAnimation<in GlideDrawable>) {
                        tiv_product_image.setImageDrawable(resource)
                        tiv_product_image.fitImageToView()
                        tiv_product_image.scaleType = ImageView.ScaleType.FIT_CENTER
                        tiv_product_image.setZoom(1F)
                        progressBar.visibility = View.GONE
                    }

                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        progressBar.visibility = View.VISIBLE
                    }

                    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                        super.onLoadFailed(e, errorDrawable)
                        progressBar.visibility = View.GONE
                        Logger.d(e.toString())
                    }
                })
        tiv_product_image.setOnTouchImageViewListener {
            if (tiv_product_image.isZoomed) {
                iv_close.visibility = View.GONE
                tv_status.visibility = View.GONE
            } else {
                iv_close.visibility = View.VISIBLE
                tv_status.visibility = View.VISIBLE
            }
        }
    }
}
