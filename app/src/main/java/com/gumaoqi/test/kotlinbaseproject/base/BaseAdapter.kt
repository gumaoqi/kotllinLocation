package com.gumaoqi.test.kotlinbaseproject.base

import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.gumaoqi.test.kotlinbaseproject.R

open class BaseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = javaClass.simpleName
    lateinit var gHandler: Handler
    lateinit var adapterInfo: String


    fun setInfo(info: String) {
        this.adapterInfo = info
        notifyDataSetChanged()
    }

    fun init(handler: Handler) {
        gHandler = handler
//        adapterInfo = "请求数据中"
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            LoadMoreViewHolder(LayoutInflater.from(GuApplication.context).inflate(R.layout.item_load_more, viewGroup, false))


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = 0


    override fun getItemViewType(position: Int): Int = 0


    /**
     * 向使用该adapter的fragment或activity发送消息
     */
    fun setMessageToActivity(arg1: Int, arg2: Int, any: Any) {
        if (::gHandler.isInitialized) {
            gHandler.let {
                var message = it.obtainMessage()
                message.arg1 = arg1
                message.arg2 = arg2
                message.obj = any
                it.sendMessageDelayed(message, 100)
            }
        }
    }


    /**
     * 加载更多item的view
     */
    inner class LoadMoreViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val itemLoadMoreTv: TextView = view.findViewById(R.id.item_load_more_tv)
        val itemLoadMoreRootLl: LinearLayout = view.findViewById(R.id.item_load_more_root_ll)
    }
}