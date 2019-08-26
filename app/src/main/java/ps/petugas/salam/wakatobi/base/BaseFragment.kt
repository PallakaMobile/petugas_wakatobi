package ps.petugas.salam.wakatobi.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 *----------------------------------------------
 * Created by ukieTux on 2/25/17 with ♥ .
 * @email  : ukie.tux@gmail.com
 * @github : https://www.github.com/tuxkids
 * ----------------------------------------------
 *          © 2017 | All Rights Reserved
 */
abstract class BaseFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(getLayoutResource(), container, false) as View
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            myCodeHere()
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }



    protected abstract fun getLayoutResource(): Int
    protected abstract fun myCodeHere()
    protected abstract fun detachScreen()

    override fun onDestroyView() {
        super.onDestroyView()
        detachScreen()
    }
}