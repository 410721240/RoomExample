package com.example.roomexample.bindingadapter

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

//for resolve app:setImage in the item_layout.xml
//imageView has two customized attributes: setImage and asIcon
@BindingAdapter("setImage", "asIcon")
fun ImageView.setSceneImage(uri: String?, asIcon: Boolean) {
    uri?.let {
        val option = if (asIcon)   //resize into a small icon
            RequestOptions().centerCrop().override(60, 60)
        else
            RequestOptions().centerCrop()

        Glide.with(this.context)
            .load(Uri.parse(uri))
            .apply(option)
            .into(this)
    }
}

@BindingAdapter("showDate")
fun TextView.showDate(timeStamp: Timestamp) {
    val date = Date(timeStamp.seconds*1000)
    val sfd = SimpleDateFormat("yyyy-MM-dd")

    text = sfd.format(date).toString()
}

//for stopping the progressbar on the screen
@BindingAdapter("Data")
fun ProgressBar.setViewVisibility(dataChecked: Any?) {
    visibility = if (dataChecked != null) View.GONE else View.VISIBLE
}
