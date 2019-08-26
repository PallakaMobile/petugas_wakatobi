package ps.petugas.salam.wakatobi.base

import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout


/**
 * Created by ukietux on 5/25/16.
 */

abstract class BaseDialogFragment : DialogFragment() {
    private var onDismissListener: DialogInterface.OnDismissListener? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity, getStyleResource())
        dialog.window!!.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(getLayoutResource(), container)
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            putYourMethodHere()
        } catch (e: Exception) {
        }
    }

    protected abstract fun getLayoutResource(): Int

    protected abstract fun getStyleResource(): Int


    protected abstract fun putYourMethodHere()

    //create interface untuk mendapatkan status Dialog dismis
    fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener) {
        this.onDismissListener = onDismissListener
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        if (onDismissListener != null) {
            onDismissListener!!.onDismiss(dialog)
        }
    }

    //end interface untuk mendapatkan status Dialog dismis
}
