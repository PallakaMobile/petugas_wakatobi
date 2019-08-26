package ps.petugas.salam.wakatobi.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatSpinner
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import ps.petugas.salam.wakatobi.R

/**
 * ----------------------------------------------
 * Created by ukieTux on 3/2/17 with ♥ .
 * @email : ukie.tux@gmail.com
 * @github : https://www.github.com/tuxkids
 * * ----------------------------------------------
 * * © 2017 | All Rights Reserved
 */

class CustomSpinner(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {
    private var tvTitle: TextView
    private var spItem: AppCompatSpinner

    init {
        val values = context.obtainStyledAttributes(attrs, R.styleable.CustomSpinner, 0, 0)
        val textSize = values.getDimension((R.styleable.CustomSpinner_cs_title_size), 12F)
        LayoutInflater.from(context).inflate(R.layout.custom_spinner, this, true)
        tvTitle = findViewById(R.id.tv_title_spinner) as TextView
        spItem = findViewById(R.id.sp_content) as AppCompatSpinner
        tvTitle.text = values.getString(R.styleable.CustomSpinner_cs_title)
        tvTitle.setTextColor(values.getColor((R.styleable.CustomSpinner_cs_title_color), ContextCompat.getColor(context, R.color.colorPrimary)))
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)

        values.recycle()
    }


}
