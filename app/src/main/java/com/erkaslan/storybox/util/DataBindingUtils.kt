package com.erkaslan.storybox.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object DataBindingUtils {

    @JvmStatic
    @BindingAdapter(value = ["app:circularImageUrl", "app:placeHolder"], requireAll = false)
    fun loadCircularImageWithPlaceholder(imageView: ImageView, circularImageUrl: String?, placeholder: String?) {
        Glide.with(imageView.context).load(circularImageUrl).thumbnail(Glide.with(imageView.context).load(placeholder)).circleCrop().into(imageView)
    }

    @JvmStatic
    @BindingAdapter(value = ["app:imageUrl", "app:placeHolder"], requireAll = false)
    fun loadImageWithPlaceholder(imageView: ImageView, imageUrl: String?, placeholder: String?) {
        Glide.with(imageView.context).load(imageUrl).thumbnail(Glide.with(imageView.context).load(placeholder)).into(imageView)
    }
}