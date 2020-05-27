package com.gumaoqi.test.kotlinbaseproject.base

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.entity.MineBean

class GuModelAdapter : BaseAdapter() {
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
                    val view = LayoutInflater.from(GuApplication.context).inflate(R.layout.item_mine_content, viewGroup, false)
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
                val mineBean = adapterList[position] as MineBean
                viewHolder.itemMineTv.text = mineBean.content
                Glide.with(GuApplication.context).load(mineBean.resId).into(viewHolder.itemMinePicIv)
                Glide.with(GuApplication.context).load(R.mipmap.ico_arrowright).into(viewHolder.itemMinePicTwoIv)
                viewHolder.itemMineContentRootLl.setOnClickListener {
                    setMessageToActivity(HandlerArg.MINE, 0, mineBean.content)
                }
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
        val itemMineTv: TextView = view.findViewById(R.id.item_mine_tv)
        val itemMinePicIv: ImageView = view.findViewById(R.id.item_mine_pic_iv)
        val itemMinePicTwoIv: ImageView = view.findViewById(R.id.item_mine_pic_two_iv)
        val itemMineContentRootLl: LinearLayout = view.findViewById(R.id.item_mine_content_root_ll)
    }
}