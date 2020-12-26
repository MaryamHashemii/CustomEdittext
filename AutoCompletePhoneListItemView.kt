package ir.app7030.android.widget

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import ir.app7030.android.R
import kotlinx.android.synthetic.main.row_auto_complete_phone.view.*

class AutoCompletePhoneListItemView(context: Context) : LinearLayout(context, null, 0) {



    init {
        LayoutInflater.from(context).inflate(R.layout.row_auto_complete_phone,this,true)
    }
    fun setTitleTextWatcher(textWatcher: TextWatcher){
        tvSubtitle?.addTextChangedListener(textWatcher)
    }

    fun removeTitleTextWatcher(textWatcher: TextWatcher){
        tvSubtitle?.removeTextChangedListener(textWatcher)
    }



    fun setTitle(title: String?){
        title?.let {
            tvTitle.visibility = View.VISIBLE
            tvTitle?.setText(it)
        } ?: kotlin.run { tvTitle.visibility = View.GONE }
    }


    fun setSubTitle( subTitle:String?){
        subTitle?.let {
            tvSubtitle.visibility= View.VISIBLE
            tvSubtitle?.setText(it)
        } ?: kotlin.run { tvSubtitle.visibility= View.GONE }
    }

    fun setIconColor(color: Int?){
        color?.let {
            ivLogo.setColorFilter(color)
        }
    }


    fun setIcon(icon :Drawable?){
        icon?.let {
            ivLogo.visibility =View.VISIBLE
            ivLogo.setImageDrawable(it)
        } ?:kotlin.run { ivLogo.visibility =View.GONE}

    }
    fun setTitleColor(color: Int){
        tvTitle.setTextColor(color)
    }

    fun setTitleTypeFace(typeface: Typeface?){
        typeface?.let {tvTitle.typeface = it}
    }
}
