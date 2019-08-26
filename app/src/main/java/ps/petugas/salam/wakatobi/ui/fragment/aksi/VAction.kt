package ps.petugas.salam.wakatobi.ui.fragment.aksi

import android.app.ProgressDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.action_screen.*
import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.adapter.AdapterAction
import ps.petugas.salam.wakatobi.base.BaseFragment
import ps.petugas.salam.wakatobi.model.DataAction
import ps.petugas.salam.wakatobi.support.xrecyclerview.XRecyclerView
import ps.petugas.salam.wakatobi.utils.SharedKey
import ps.petugas.salam.wakatobi.utils.SharedPref
import ps.petugas.salam.wakatobi.widget.CustomToastView

/**
 **********************************************
 * Created by ukie on 3/24/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class VAction : BaseFragment(), IAction.View {


    private val mPresenter = PAction()
    private lateinit var dialog: ProgressDialog
    private lateinit var adapter: AdapterAction
    private var listReponse = ArrayList<DataAction.Response>()

    private var page = 1
    private var totalPage = 0

    private var isRefresh = false
    private var isLoadMore = false

    override fun getLayoutResource(): Int {
        return R.layout.action_screen
    }

    override fun myCodeHere() {
//        setupView()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser)
            setupView()
    }

    override fun setupView() {
        mPresenter.attachView(this)

        dialog = ProgressDialog(activity)
        dialog.setMessage(getString(R.string.please_wait))
        dialog.setCancelable(false)

        mPresenter.getActionReport(page, SharedPref.getString(SharedKey.id_user))

        //setup recylcerview
        val layoutManager = LinearLayoutManager(activity)
        adapter = AdapterAction(listReponse, "action")
        rv_action_report.adapter = adapter
        rv_action_report.setHasFixedSize(true)
        rv_action_report.layoutManager = layoutManager
        rv_action_report.setLoadingListener(object : XRecyclerView.LoadingListener {
            override fun onRefresh() {
                page = 1
                isRefresh = true
                mPresenter.getActionReport(page, SharedPref.getString(SharedKey.id_user))
            }

            override fun onLoadMore() {
                page++
                if (page <= totalPage) {
                    isLoadMore = true
                    mPresenter.getActionReport(page, SharedPref.getString(SharedKey.id_user))
                } else
                    rv_action_report.loadMoreComplete()
            }
        })

    }

    override fun onShowDialog(isShow: Boolean) {
        if (isShow)
            dialog.show()
        else
            dialog.dismiss()
    }

    override fun onGetActionReport(dataAction: DataAction) {
        if (page == 1 && dataAction.response.isEmpty()) {
            tv_no_data_action.visibility = View.VISIBLE
            rv_action_report.visibility = View.GONE
        } else {
            tv_no_data_action.visibility = View.GONE
            rv_action_report.visibility = View.VISIBLE
        }

        if (isRefresh || page == 1) {
            if (listReponse.isNotEmpty())
                listReponse.clear()
            isRefresh = false
            rv_action_report.refreshComplete()
        }
        if (isLoadMore) {
            isLoadMore = false
            rv_action_report.loadMoreComplete()
        }

        page = dataAction.pagination.active_page
        totalPage = dataAction.pagination.total_page
        listReponse.addAll(dataAction.response)
        adapter.notifyDataSetChanged()
    }

    override fun onShowError(errorMessage: String) {
        CustomToastView.makeText(activity, errorMessage, Toast.LENGTH_SHORT)
    }

    override fun detachScreen() {
        mPresenter.detachView()
    }

}