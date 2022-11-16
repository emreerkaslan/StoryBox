package com.erkaslan.storybox.Utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object DataBindingUtils {

    @JvmStatic
    @BindingAdapter(value = ["app:circularImageUrl", "app:placeHolder"], requireAll = false)
    fun loadCircularImageWithPlaceholder(imageView: ImageView, imageUrl: String?, placeholder: String?) {
        Glide.with(imageView.context).load(imageUrl).thumbnail(Glide.with(imageView.context).load(placeholder)).circleCrop().into(imageView)
    }
}