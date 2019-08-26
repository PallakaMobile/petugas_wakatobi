package ps.petugas.salam.wakatobi.adapter

import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.action_item.view.*
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.model.DataAction
import ps.petugas.salam.wakatobi.ui.detaillaporan.VDetailReport
import ps.petugas.salam.wakatobi.utils.DateFormatUtils

/**
 **********************************************
 * Created by ukie on 3/26/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class AdapterAction(val responseList: ArrayList<DataAction.Response>, val type: String) : RecyclerView.Adapter<AdapterAction.MyAdapter>() {
    override fun onBindViewHolder(holder: MyAdapter?, position: Int) {
        holder?.onBindAction(responseList[position], type)
    }

    override fun getItemCount(): Int {
        return responseList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyAdapter {
        return MyAdapter(LayoutInflater.from(parent?.context).inflate(R.layout.action_item, parent, false))
    }

    class MyAdapter(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun onBindAction(response: DataAction.Response, type: String) {
            Glide.with(itemView?.context)
                    .load(response.gambar)
                    .thumbnail(0.5F)
                    .crossFade()
                    .override(100, 100)
                    .into(object : SimpleTarget<GlideDrawable>() {
                        override fun onResourceReady(resource: GlideDrawable?, glideAnimation: GlideAnimation<in GlideDrawable>) {
                            itemView.iv_image_report_action.setImageDrawable(resource)
                            itemView.progressBar.visibility = View.GONE
                        }

                        override fun onLoadStarted(placeholder: Drawable?) {
                            super.onLoadStarted(placeholder)
                            itemView.progressBar.visibility = View.VISIBLE
                        }

                        override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                            super.onLoadFailed(e, errorDrawable)
                            itemView.progressBar.visibility = View.GONE
                            Logger.d(e.toString())

                        }
                    })

            itemView.tv_title_report_action.text = response.judul
            itemView.tv_username_action.text = response.nama
            itemView.tv_date_report_action.text = DateFormatUtils.format(response.waktu_submit!!, 0)
            when (type) {
                "action" -> itemView.tv_date_confirm_report_action.text = String.format(itemView?.context!!.getString(R.string.date_confirm), DateFormatUtils.format(response.waktu_verifikasi!!, 0))
                "history" -> itemView.tv_date_confirm_report_action.text = String.format(itemView?.context!!.getString(R.string.date_confirm), DateFormatUtils.format(response.waktu_selesai!!, 0))
            }
            itemView.ll_root_list_action.setOnClickListener {
                val detailReport = Intent(itemView.context, VDetailReport::class.java)
                detailReport.putExtra("id_report", response.id)
                detailReport.putExtra("type", type)
                detailReport.putExtra("position", adapterPosition - 1)
                itemView.context.startActivity(detailReport)
            }
        }
    }
}