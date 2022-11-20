package com.erkaslan.storybox.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

object DataBindingUtils {
    @JvmStatic
    @BindingAdapter(value = ["app:circularImageUrl", "app:placeHolder"], requireAll = false)
    fun loadCircularImageWithPlaceholder(imageView: ImageView, circularImageUrl: String?, placeholder: String?) {
        Glide.with(imageView.context).load(circularImageUrl).thumbnail(Glide.with(imageView.context).load(placeholder)).circleCrop().into(imageView)
    }

    @JvmStatic
    @BindingAdapter(value = ["app:imageUrl", "app:placeHolder", "app:onSuccess", "app:onFailed"], requireAll = false)
    fun loadImageWithPlaceholder(imageView: ImageView, imageUrl: String?, placeholder: String?, onSuccess: (() -> Unit)?, onFailed: (() -> Unit)?) {
        Glide.with(imageView.context).load(imageUrl)
            .thumbnail(Glide.with(imageView.context).load(placeholder))
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(30)))
            .listener(object : RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onFailed?.invoke()
                    return false
                }
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onSuccess?.invoke()
                    return false
                }
            })
            .into(imageView)
    }
}