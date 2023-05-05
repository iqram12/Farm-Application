package com.dev.farmbuyer.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dev.farmbuyer.databinding.ImagesProductBinding
import com.smarteist.autoimageslider.SliderViewAdapter

// To make images autoscroll
// Adapted java code from: https://github.com/smarteist/Android-Image-Slider
class ProductSliderAdapter(private val myList: ArrayList<String>) : SliderViewAdapter<ProductSliderAdapter.SliderViewHolder>() {

    class SliderViewHolder(private var binding : ImagesProductBinding) : ViewHolder(binding.root) {
        var itemImage = binding.productImage
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderViewHolder {
        return SliderViewHolder(ImagesProductBinding.inflate(LayoutInflater.from(parent?.context), parent, false))
    }

    override fun onBindViewHolder(viewHolder: SliderViewHolder, position: Int) {
        Glide.with(viewHolder.itemImage)
            .load(myList[position])
            .into(viewHolder.itemImage)
    }

    override fun getCount(): Int {
        return myList.size
    }
}