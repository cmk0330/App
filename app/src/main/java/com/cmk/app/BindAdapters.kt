package com.cmk.app

import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * @Author: romens
 * @Date: 2019-11-8 10:59
 * @Desc:
 */
@BindingAdapter("imageSrc")
fun bindImage(imageView: ImageView, id: Int): Unit {
    imageView.setImageResource(id)
}

@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView?, imageUrl: String?) {
    val options =
        RequestOptions() //                .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .centerCrop()
    Glide.with(imageView!!).load(imageUrl).apply(options).into(imageView)
}

//@BindingAdapter("android:layout_width")
//fun setWidth(view: View, width: Int) {
//    view.layoutParams.width = width
//}
//
//@BindingAdapter("android:layout_height")
//fun setHeight(view: View, height: Int) {
//    view.layoutParams.height = height
//}

@BindingAdapter(value = ["selected"])
fun bindSelected(view: View, selected: Boolean) {
    view.isSelected = selected
    Log.e("bnidningAdapter--->", "select:$selected")
}

@BindingAdapter(value = ["enabled"])
fun bindEnabled(view: View, enable: Boolean) {
    view.isEnabled = enable
}

@BindingAdapter("isGone")
fun bindGone(view: View, isGone: Boolean) {
    if (isGone) view.visibility = View.GONE else view.visibility = View.VISIBLE
}

@BindingAdapter("visibility")
fun bindVisibility(view:View, visibility:Int) {
    view.visibility = visibility
}

@BindingAdapter("inputEmpty")
fun bindInputEmpty(editText: EditText, isInputEmpty: Boolean) {
    if (isInputEmpty) editText.setText("")
}