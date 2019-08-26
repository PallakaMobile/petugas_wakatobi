package ps.petugas.salam.wakatobi.ui.ranking

import android.app.AlertDialog
import android.app.ProgressDialog
import android.support.v4.content.ContextCompat
import android.text.Html
import android.view.Gravity
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.ranking_screen.*
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.base.BaseActivity
import ps.petugas.salam.wakatobi.model.DataRankList
import ps.petugas.salam.wakatobi.utils.SharedKey
import ps.petugas.salam.wakatobi.utils.SharedPref
import ps.petugas.salam.wakatobi.widget.CustomToastView

class VRanking : BaseActivity(), IRanking.View {
    private lateinit var dialog: ProgressDialog
    private val mPresenter = PRanking()

    override fun getLayoutResource(): Int {
        return R.layout.ranking_screen
    }

    override fun myCodeHere() {
        title = getString(R.string.ranking)
        setupView()
    }

    override fun setupView() {

        dialog = ProgressDialog(this)
        dialog.setMessage(getString(R.string.please_wait))
        dialog.setCancelable(false)

        mPresenter.attachView(this)
        mPresenter.getRanking(SharedPref.getString(SharedKey.id_user))
    }

    @Suppress("DEPRECATION")
    override fun onGetRanking(stat: DataRankList.Stat, response: List<DataRankList.Response>) {
        /**
         * Stat
         */
        Glide.with(this)
                .load(intent.extras.getString("profile_pic"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_default_profile)
                .into(iv_profile_image)
        tv_username.text = intent.extras.getString("username")
        tv_instance.text = intent.extras.getString("instance")
        tv_rank.text = Html.fromHtml(getString(R.string.rank) + " <b>" + stat.peringkat + "</b> " + getString(R.string.from) + " <b>" + stat.tot_petugas
                + "</b> " + getString(R.string.officer))
        tv_total_point.text = Html.fromHtml(getString(R.string.total_point_dot) + "<b> " + stat.poin + "</b>")
        tv_total_finish.text = stat.laporan_selesai

        /**
         * Table Rank
         */
        val tvNameTitle: TextView = TextView(this)
        val tvInstanceTitle: TextView = TextView(this)
        val tvPointTitle: TextView = TextView(this)
        val tvRankTitle: TextView = TextView(this)
        val tvActionTitle: TextView = TextView(this)
        val tableRowTitle: TableRow = TableRow(this)

        val rowLp = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT, 0.4f)
        rowLp.height = resources.getDimension(R.dimen.button_min_height).toInt()
        val cellLp = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT, 0.3f)

        //setup title

        tvNameTitle.text = getString(R.string.name)
        tvNameTitle.textSize = 12F
        tvNameTitle.gravity = Gravity.CENTER
        tvNameTitle.setPaddingRelative(10, 0, 10, 0)
        tvNameTitle.setBackgroundResource(R.drawable.cell_border)
        tableRowTitle.addView(tvNameTitle, cellLp)

        tvInstanceTitle.text = getString(R.string.instance)
        tvInstanceTitle.textSize = 12F
        tvInstanceTitle.gravity = Gravity.CENTER
        tvInstanceTitle.setPaddingRelative(10, 0, 10, 0)
        tvInstanceTitle.setBackgroundResource(R.drawable.cell_border)
        tableRowTitle.addView(tvInstanceTitle, cellLp)

        tvPointTitle.text = getString(R.string.point)
        tvPointTitle.textSize = 12F
        tvPointTitle.gravity = Gravity.CENTER
        tvPointTitle.setPaddingRelative(30, 0, 30, 0)
        tvPointTitle.setBackgroundResource(R.drawable.cell_border)
        tableRowTitle.addView(tvPointTitle, cellLp)

        tvRankTitle.text = getString(R.string.rank_en)
        tvRankTitle.gravity = Gravity.CENTER
        tvRankTitle.textSize = 12F
        tvRankTitle.setPaddingRelative(30, 0, 30, 0)
        tvRankTitle.setBackgroundResource(R.drawable.cell_border)
        tableRowTitle.addView(tvRankTitle, cellLp)

        tvActionTitle.text = getString(R.string.action)
        tvActionTitle.textSize = 12F
        tvActionTitle.gravity = Gravity.CENTER
        tvActionTitle.setPaddingRelative(30, 0, 30, 0)
        tvActionTitle.setBackgroundResource(R.drawable.cell_border)
        tableRowTitle.addView(tvActionTitle, cellLp)

        tableRowTitle.minimumHeight = resources.getDimension(R.dimen.button_min_height).toInt()
        tl_rank.addView(tableRowTitle, rowLp)


        var tvName: TextView
        var tvInstance: TextView
        var tvPoint: TextView
        var tvRank: TextView
        var tvAction: TextView

        var tableRow: TableRow

        //setup value
        for (items in response.indices) {

            tableRow = TableRow(this)
            tvName = TextView(this)
            tvInstance = TextView(this)
            tvPoint = TextView(this)
            tvRank = TextView(this)
            tvAction = TextView(this)

            if (response[items].nama!!.length < 16)
                tvName.text = response[items].nama
            else
                tvName.text = response[items].nama!!.substring(0, 15)

            tvName.textSize = 12F

            tvName.setPaddingRelative(10, 30, 10, 30)
            tvName.setBackgroundResource(R.drawable.cell_border)
            tvName.gravity = Gravity.CENTER
            tvName.setTextColor(ContextCompat.getColor(this, R.color.blue_soft))

            tvName.setOnClickListener {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setMessage(response[items].nama)
                alertDialog.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialog.show()
            }


            tableRow.addView(tvName, cellLp)

            val sb = StringBuffer(response[items].instansi!!.substring(0, 9))
            sb.append("...")
            tvInstance.text = sb
            tvInstance.textSize = 12F
            tvInstance.setPaddingRelative(10, 30, 30, 10)
            tvInstance.setBackgroundResource(R.drawable.cell_border)
            tvInstance.gravity = Gravity.CENTER
            tvInstance.setTextColor(ContextCompat.getColor(this, R.color.blue_soft))

            tvInstance.setOnClickListener {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setMessage(response[items].instansi)
                alertDialog.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialog.show()
            }
            tableRow.addView(tvInstance, cellLp)

            tvPoint.text = response[items].poin
            tvPoint.textSize = 12F
            tvPoint.setPaddingRelative(10, 30, 10, 30)
            tvPoint.setBackgroundResource(R.drawable.cell_border)
            tvPoint.gravity = Gravity.CENTER
            tableRow.addView(tvPoint, cellLp)

            tvRank.text = response[items].rank
            tvRank.textSize = 12F
            tvRank.setPaddingRelative(30, 30, 30, 30)
            tvRank.setBackgroundResource(R.drawable.cell_border)
            tvRank.gravity = Gravity.CENTER
            tableRow.addView(tvRank, cellLp)

            tvAction.text = response[items].aksi
            tvAction.textSize = 12F
            tvAction.setPaddingRelative(30, 30, 30, 30)
            tvAction.setBackgroundResource(R.drawable.cell_border)
            tvAction.gravity = Gravity.CENTER
            tableRow.addView(tvAction, cellLp)

            if (response[items].flag == 1) {
                tableRow.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                tvName.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                tvInstance.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                tvPoint.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                tvRank.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                tvAction.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
            tableRow.minimumHeight = resources.getDimension(R.dimen.button_min_height_small).toInt()
            tl_rank.addView(tableRow, rowLp)
        }

    }

    override fun onShowDialog(showDialog: Boolean) {
        if (showDialog)
            dialog.show()
        else dialog.dismiss()
    }

    override fun onMessage(message: String) {
        CustomToastView.makeText(this, message, Toast.LENGTH_SHORT)
    }

    override fun onDetachScreen() {
        mPresenter.detachView()
    }
}
