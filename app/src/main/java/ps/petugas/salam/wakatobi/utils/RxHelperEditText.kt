package ps.petugas.salam.wakatobi.utils

import android.support.design.widget.TextInputEditText
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

import com.orhanobut.logger.Logger

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by ukieTux on 10/13/16 with ♥ .

 * @email : ukie.tux@gmail.com
 * *
 * @github : https://www.github.com/tuxkids
 * *
 *
 *
 * * © 2016 | All Rights Reserved
 */

object RxHelperEditText {
    fun getTextWatcherObservable(editText: TextInputEditText): Observable<String> {

        val subject = PublishSubject.create<String>()

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                subject.onNext(s.toString())
                Logger.d(s.toString())
            }
        })

        return subject
    }
}
