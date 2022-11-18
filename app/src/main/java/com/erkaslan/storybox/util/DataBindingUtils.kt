package com.erkaslan.storybox.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

object DataBindingUtils {

    @JvmStatic
    @BindingAdapter(value = ["app:circularImageUrl", "app:placeHolder"], requireAll = false)
    fun loadCircularImageWithPlaceholder(imageView: ImageView, circularImageUrl: String?, placeholder: String?) {
        Glide.with(imageView.context).load(circularImageUrl).thumbnail(Glide.with(imageView.context).load(placeholder)).circleCrop().into(imageView)
    }

    @JvmStatic
    @BindingAdapter(value = ["app:imageUrl", "app:placeHolder"], requireAll = false)
    fun loadImageWithPlaceholder(imageView: ImageView, imageUrl: String?, placeholder: String?) {
        Glide.with(imageView.context).load(imageUrl).thumbnail(Glide.with(imageView.context).load(placeholder)).apply(RequestOptions().transform(CenterCrop(), RoundedCorners(30))).into(imageView)
    }
}