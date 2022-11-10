package com.lc.workmanagerdemo

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

fun ImageView.loadFromUrl(uri: Uri?) =
    if (uri != null) {
        Glide.with(this.context.applicationContext)
            .load(uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    } else {
        Glide.with(this.context.applicationContext)
            .load(R.drawable.ic_launcher_background)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }