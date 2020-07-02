package com.gumaoqi.test.kotlinbaseproject.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.base.BaseFragment
import com.gumaoqi.test.kotlinbaseproject.base.GuBottomNavigationBean
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_FIVE
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_FOUR
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_FRAGMENT
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_OME
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_THREE
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.CHANGE_TWO
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.SUCCESS
import com.gumaoqi.test.kotlinbaseproject.tool.S
import com.gumaoqi.test.kotlinbaseproject.tool.T
import kotlinx.android.synthetic.main.fragment_gu_bottom_navigation.*
import java.util.ArrayList

internal class GuBottomNavigationFragment : BaseFragment() {


    private lateinit var gHandler: Handler
    private lateinit var guBottomNavigationBeanList: MutableList<GuBottomNavigationBean>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gu_bottom_navigation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        intData()
        setView()
    }


    /**
     * 初始化数据，需要修改的地方
     */
    override fun intData() {
        super.intData()
        gHandler = Handler(Handler.Callback { msg ->
            if (activity == null) {
                return@Callback false
            }
            when (msg.arg1) {
                SUCCESS -> {
                }
            }
            false
        })
        guBottomNavigationBeanList = ArrayList()
        guBottomNavigationBeanList.add(GuBottomNavigationBean(
                R.id.fragment_gu_bottom_navigation_item_one_ll, "定位", R.drawable.checkbox_pressed, R.drawable.checkbox_normal))
        guBottomNavigationBeanList.add(GuBottomNavigationBean(
                R.id.fragment_gu_bottom_navigation_item_two_ll, "列表", R.drawable.checkbox_pressed, R.drawable.checkbox_normal))
        guBottomNavigationBeanList.add(GuBottomNavigationBean(
                R.id.fragment_gu_bottom_navigation_item_three_ll, "查询", R.drawable.checkbox_pressed, R.drawable.checkbox_normal))
        guBottomNavigationBeanList.add(GuBottomNavigationBean(
                R.id.fragment_gu_bottom_navigation_item_four_ll, "其他", R.drawable.checkbox_pressed, R.drawable.checkbox_normal))
        guBottomNavigationBeanList.add(GuBottomNavigationBean(
                R.id.fragment_gu_bottom_navigation_item_five_ll, "我的", R.drawable.checkbox_pressed, R.drawable.checkbox_normal))
    }

    override fun setView() {
        super.setView()
//        clearView()
        //设置当前为第一个选中
        setCheckedItem(R.id.fragment_gu_bottom_navigation_item_one_ll)
        //添加点击事件
        fragment_gu_bottom_navigation_item_one_ll.setOnClickListener {
            setCheckedItem(it.id)
        }
        fragment_gu_bottom_navigation_item_two_ll.setOnClickListener {
            setCheckedItem(it.id)
        }
        fragment_gu_bottom_navigation_item_three_ll.setOnClickListener {
            setCheckedItem(it.id)
        }
        fragment_gu_bottom_navigation_item_four_ll.setOnClickListener {
            setCheckedItem(it.id)
        }
        fragment_gu_bottom_navigation_item_five_ll.setOnClickListener {
            setCheckedItem(it.id)
        }
    }

    /**
     * onDestroyView中进行解绑操作
     */
    override fun onDestroyView() {
        super.onDestroyView()
    }


    /**
     * 通过id设置当前被check的item
     *
     * @param id
     */
    private fun setCheckedItem(id: Int) {
        if (id == R.id.fragment_gu_bottom_navigation_item_three_ll ||
                id == R.id.fragment_gu_bottom_navigation_item_four_ll) {
//            if (S.getString("c10") != "1") {
//                T.s("普通用户不能够执行该操作")
//                return
//            }
        }
        clearView()
        for (guBottomNavigationBean in guBottomNavigationBeanList) {
            if (guBottomNavigationBean.llId == id) {
                setMessageToActivity(CHANGE_FRAGMENT, id)
                when (id) {
                    R.id.fragment_gu_bottom_navigation_item_one_ll -> {
                        fragment_gu_bottom_navigation_icon_one_iv.setBackgroundResource(guBottomNavigationBean.checkedId)
                        fragment_gu_bottom_navigation_name_one_tv.setTextColor(Color.BLACK)
                        setMessageToActivity(CHANGE_OME, 0)
                    }
                    R.id.fragment_gu_bottom_navigation_item_two_ll -> {
                        fragment_gu_bottom_navigation_icon_two_iv.setBackgroundResource(guBottomNavigationBean.checkedId)
                        fragment_gu_bottom_navigation_name_two_tv.setTextColor(Color.BLACK)
                        setMessageToActivity(CHANGE_TWO, 0)
                    }
                    R.id.fragment_gu_bottom_navigation_item_three_ll -> {
                        fragment_gu_bottom_navigation_icon_three_iv.setBackgroundResource(guBottomNavigationBean.checkedId)
                        fragment_gu_bottom_navigation_name_three_tv.setTextColor(Color.BLACK)
                        setMessageToActivity(CHANGE_THREE, 0)
                    }
                    R.id.fragment_gu_bottom_navigation_item_four_ll -> {
                        fragment_gu_bottom_navigation_icon_four_iv.setBackgroundResource(guBottomNavigationBean.checkedId)
                        fragment_gu_bottom_navigation_name_four_tv.setTextColor(Color.BLACK)
                        setMessageToActivity(CHANGE_FOUR, 0)
                    }
                    R.id.fragment_gu_bottom_navigation_item_five_ll -> {
                        fragment_gu_bottom_navigation_icon_five_iv.setBackgroundResource(guBottomNavigationBean.checkedId)
                        fragment_gu_bottom_navigation_name_five_tv.setTextColor(Color.BLACK)
                        setMessageToActivity(CHANGE_FIVE, 0)
                    }
                }
            }
        }
    }

    /**
     * 将所有的item都设置为没有check的状态
     */
    private fun clearView() {
        for (guBottomNavigationBean in guBottomNavigationBeanList) {
            when (guBottomNavigationBean.llId) {
                R.id.fragment_gu_bottom_navigation_item_one_ll -> {
                    fragment_gu_bottom_navigation_icon_one_iv.setBackgroundResource(guBottomNavigationBean.unCheckedId)
                    fragment_gu_bottom_navigation_name_one_tv.text = guBottomNavigationBean.name
                    fragment_gu_bottom_navigation_name_one_tv.setTextColor(Color.GRAY)
                }
                R.id.fragment_gu_bottom_navigation_item_two_ll -> {
                    fragment_gu_bottom_navigation_icon_two_iv.setBackgroundResource(guBottomNavigationBean.unCheckedId)
                    fragment_gu_bottom_navigation_name_two_tv.text = guBottomNavigationBean.name
                    fragment_gu_bottom_navigation_name_two_tv.setTextColor(Color.GRAY)
                }
                R.id.fragment_gu_bottom_navigation_item_three_ll -> {
                    fragment_gu_bottom_navigation_icon_three_iv.setBackgroundResource(guBottomNavigationBean.unCheckedId)
                    fragment_gu_bottom_navigation_name_three_tv.text = guBottomNavigationBean.name
                    fragment_gu_bottom_navigation_name_three_tv.setTextColor(Color.GRAY)
                }
                R.id.fragment_gu_bottom_navigation_item_four_ll -> {
                    fragment_gu_bottom_navigation_icon_four_iv.setBackgroundResource(guBottomNavigationBean.unCheckedId)
                    fragment_gu_bottom_navigation_name_four_tv.text = guBottomNavigationBean.name
                    fragment_gu_bottom_navigation_name_four_tv.setTextColor(Color.GRAY)
                }
                R.id.fragment_gu_bottom_navigation_item_five_ll -> {
                    fragment_gu_bottom_navigation_icon_five_iv.setBackgroundResource(guBottomNavigationBean.unCheckedId)
                    fragment_gu_bottom_navigation_name_five_tv.text = guBottomNavigationBean.name
                    fragment_gu_bottom_navigation_name_five_tv.setTextColor(Color.GRAY)
                }
            }
        }
    }
}