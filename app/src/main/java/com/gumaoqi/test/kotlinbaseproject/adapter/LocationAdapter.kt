package com.gumaoqi.test.kotlinbaseproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.base.BaseAdapter
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg
import com.gumaoqi.test.kotlinbaseproject.entity.Result

class LocationAdapter : BaseAdapter() {
    lateinit var adapterList: List<Any>

    fun getList(): List<Any>? {
        return adapterList
    }

    fun setList(list: List<Any>) {
        this.adapterList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                0 -> {
                    val view = LayoutInflater.from(GuApplication.context).inflate(R.layout.item_location, viewGroup, false)
                    val viewHolder = ContentViewHolder(view)
                    viewHolder
                }
                else -> {
                    val view = LayoutInflater.from(GuApplication.context).inflate(R.layout.item_load_more, viewGroup, false)
                    val viewHolder = LoadMoreViewHolder(view)
                    viewHolder
                }
            }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is ContentViewHolder -> {
                val result = adapterList[position] as Result
                viewHolder.itemLocationOneTv.text = "序号：${position + 1}"
                viewHolder.itemLocationTwoTv.text = "纬度：${result.c1}"
                viewHolder.itemLocationThreeTv.text = "经度：${result.c2}"
                viewHolder.itemLocationFourTv.text = "位置：${result.c3}"
                viewHolder.itemLocationFiveTv.text = "时间：${result.createdAt}"
                viewHolder.itemLocationSixTv.text = "距离：" + result.c6 + "米"
                viewHolder.itemLocationSevenTv.text = "手机：${result.c7}"
            }
            is LoadMoreViewHolder -> {
                viewHolder.itemLoadMoreTv.text = adapterInfo
                viewHolder.itemLoadMoreRootLl.setOnClickListener {
                    setMessageToActivity(HandlerArg.MINE, 0, adapterInfo)
                }
            }
        }
    }

    override fun getItemCount(): Int =
            if (::adapterList.isInitialized) {
                adapterList.size + 1
            } else {
                1
            }

    override fun getItemViewType(position: Int): Int =
            if (::adapterList.isInitialized) {
                if (position > adapterList.size - 1) {
                    1
                } else {
                    0
                }
            } else {
                1
            }

    /**
     * 普通item的view
     */
    inner class ContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemLocationOneTv: TextView = view.findViewById(R.id.item_location_one_tv)
        val itemLocationTwoTv: TextView = view.findViewById(R.id.item_location_two_tv)
        val itemLocationThreeTv: TextView = view.findViewById(R.id.item_location_three_tv)
        val itemLocationFourTv: TextView = view.findViewById(R.id.item_location_four_tv)
        val itemLocationFiveTv: TextView = view.findViewById(R.id.item_location_five_tv)
        val itemLocationSixTv: TextView = view.findViewById(R.id.item_location_six_tv)
        val itemLocationSevenTv: TextView = view.findViewById(R.id.item_location_seven_tv)
    }
}