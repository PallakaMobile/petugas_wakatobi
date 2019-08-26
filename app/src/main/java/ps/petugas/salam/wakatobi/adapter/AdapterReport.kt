package ps.petugas.salam.wakatobi.adapter

import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.report_nearby_item.view.*
import kotlinx.android.synthetic.main.report_newest_item.view.*
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.model.DataDetailReport
import ps.petugas.salam.wakatobi.ui.detaillaporan.VDetailReport
import ps.petugas.salam.wakatobi.utils.DateFormatUtils

@Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
/**
 **********************************************
 * Created by ukie on 3/21/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class AdapterReport(val listReport: ArrayList<DataDetailReport.Response>, val type: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (type == 0) {
            val newestHolder = holder as holderNewest
            newestHolder.bindReport(listReport[position])
        } else if (type == 1) {
            val nearbyHolder = holder as holderNearby
            nearbyHolder.bindReport(listReport[position])
        }
    }

    override fun getItemCount(): Int {
        return listReport.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        if (type == 0) {
            viewHolder = holderNewest(LayoutInflater.from(parent?.context).inflate(R.layout.report_newest_item, parent, false))
        } else if (type == 1) {
            viewHolder = holderNearby(LayoutInflater.from(parent?.context).inflate(R.layout.report_nearby_item, parent, false))
        }
        return viewHolder!!
    }

    class holderNearby(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bindReport(report: DataDetailReport.Response) {
            Glide.with(itemView.context)
                    .load(report.gambar)
                    .crossFade()
                    .thumbnail(0.5F)
                    .into(object : SimpleTarget<GlideDrawable>() {
                        override fun onResourceReady(resource: GlideDrawable?, glideAnimation: GlideAnimation<in GlideDrawable>) {
                            itemView.iv_image_report_nearby.setImageDrawable(resource)
                            itemView.progressBar_nearby.visibility = View.GONE
                        }

                        override fun onLoadStarted(placeholder: Drawable?) {
                            super.onLoadStarted(placeholder)
                            itemView.progressBar_nearby.visibility = View.VISIBLE
                        }

                        override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                            super.onLoadFailed(e, errorDrawable)
                            itemView.progressBar_nearby.visibility = View.GONE
                            Logger.d(e.toString())

                        }
                    })
            itemView.tv_title_report_nearby.text = report.judul
            itemView.tv_username_nearby.text = report.nama

            var distance: String? = null
            if (report.jarak == 0.0)
                distance = "< 1"
            else
                distance = report.jarak.toString()

            itemView.tv_distance.text = String.format(itemView.context.getString(R.string.format_km), distance)
            itemView.tv_date_report_nearby.text = DateFormatUtils.format(report.waktu_submit!!, 0)

            var duration = ""
            Logger.d("durasi", report.durasi)
            try {
                duration = report.durasi!!.replace("jam", "J")
                duration = duration.replace("menit", "M")
            } catch (e: Exception) {
//                duration = report.durasi!!.replace("menit", "M")
            }
            itemView.tv_time_to_location.text = duration

            itemView.ll_report_nearby.setOnClickListener {
                val detailReport = Intent(itemView.context, VDetailReport::class.java)
                detailReport.putExtra("id_report", report.id)
                detailReport.putExtra("type", "report")
                detailReport.putExtra("position", adapterPosition - 1)
                itemView.context.startActivity(detailReport)
            }

        }
    }

    class holderNewest(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        fun bindReport(report: DataDetailReport.Response) {
            Glide.with(itemView?.context)
                    .load(report.foto)
                    .crossFade()
                    .thumbnail(0.5F)
                    .error(R.drawable.ic_default_profile)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemView.iv_profile_newest)

            Glide.with(itemView?.context)
                    .load(report.gambar)
                    .crossFade()
                    .thumbnail(0.5F)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(object : SimpleTarget<GlideDrawable>() {
                        override fun onResourceReady(resource: GlideDrawable?, glideAnimation: GlideAnimation<in GlideDrawable>) {
                            itemView.iv_image_report_newest.setImageDrawable(resource)
                            itemView.progressBar_newest.visibility = View.GONE
                        }

                        override fun onLoadStarted(placeholder: Drawable?) {
                            super.onLoadStarted(placeholder)
                            itemView.progressBar_newest.visibility = View.VISIBLE
                        }

                        override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                            super.onLoadFailed(e, errorDrawable)
                            itemView.progressBar_newest.visibility = View.GONE
                            Logger.d(e.toString())

                        }
                    })
            itemView.tv_title_report_newest.text = report.judul
            itemView.tv_username_newest.text = report.nama
            itemView.tv_date_report_newest.text = DateFormatUtils.format(report.waktu_submit!!, 0)

            itemView.iv_image_report_newest.setOnClickListener {
                val detailReport = Intent(itemView.context, VDetailReport::class.java)
                detailReport.putExtra("id_report", report.id)
                detailReport.putExtra("type", "report")
                detailReport.putExtra("position", adapterPosition - 1)
                itemView.context.startActivity(detailReport)
            }
        }

    }
}