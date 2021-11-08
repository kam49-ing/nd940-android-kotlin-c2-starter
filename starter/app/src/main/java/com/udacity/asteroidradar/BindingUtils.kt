package com.udacity.asteroidradar

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter


@BindingAdapter("asteroidId")
fun TextView.setAsteroidId(item: Asteroid?){
    item?.let {
        text = item.id.toString()
    }
}
@BindingAdapter("asteroidStatusImage")
fun ImageView.setAsteroidImage(item: Asteroid?){
    item?.let {
        setImageResource(when(item.isPotentiallyHazardous){
            true-> R.drawable.ic_status_potentially_hazardous
            else-> R.drawable.ic_status_normal
        })
    }
}
@BindingAdapter("asteroidCodename")
fun TextView.setAsteroidCodeName(item: Asteroid?){
    item?.let {
        text = item.codename.toString()
    }
}

