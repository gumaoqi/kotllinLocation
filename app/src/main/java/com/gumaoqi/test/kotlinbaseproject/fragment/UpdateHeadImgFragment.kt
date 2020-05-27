package com.gumaoqi.test.kotlinbaseproject.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import androidx.core.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.gumaoqi.test.kotlinbaseproject.R
import com.gumaoqi.test.kotlinbaseproject.base.BaseFragment
import com.gumaoqi.test.kotlinbaseproject.base.GuApplication
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.GET_BITMAP
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.OPEN_ALBUM
import com.gumaoqi.test.kotlinbaseproject.base.HandlerArg.Companion.TAKE_PHOTO
import com.gumaoqi.test.kotlinbaseproject.tool.C
import com.gumaoqi.test.kotlinbaseproject.tool.L
import com.gumaoqi.test.kotlinbaseproject.tool.S
import com.gumaoqi.test.kotlinbaseproject.tool.T
import kotlinx.android.synthetic.main.fragment_update_head_img.*
import java.io.File

class UpdateHeadImgFragment : BaseFragment() {

    private lateinit var gHandler: Handler
    private lateinit var imageUri: Uri
    private lateinit var outputImage: File

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_update_head_img, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        intData()
        setView()
    }


    override fun intData() {
        super.intData()
        gHandler = Handler(Handler.Callback { msg ->
            if (activity == null) {//已经与activity解绑了
                return@Callback false
            }
            when (msg.arg1) {
                HandlerArg.SUCCESS -> {
                }
                GET_BITMAP -> {
                    val bitmap = msg.obj as Bitmap
                    val bitmapSize = C.bitmapToBytes(bitmap).size
                    L.i(TAG, "大小：$bitmapSize")
                    L.i(TAG, "内容：" + C.bitmapToBytes(bitmap))
                    if (bitmapSize > GuApplication.maxPicSize) {
                        T.s("选择的照片不能超过6M")
                        return@Callback false
                    }
                    fragment_update_head_img_iv.setImageBitmap(bitmap)
                }
            }
            false
        })
    }

    override fun setView() {
        super.setView()
        Glide.with(GuApplication.context).load(S.getString("c10")).error(R.mipmap.ic_launcher).into(fragment_update_head_img_iv)
        fragment_update_head_img_one_bt.setOnClickListener {
            outputImage = File(GuApplication.context.externalCacheDir, "output_image.jpg")
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
            imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(activity!!, "com.gumaoqi.test.kotlinbaseproject.fileProvider", outputImage)
            } else {
                Uri.fromFile(outputImage)
            }
            startActivityForResult(
                    Intent("android.media.action.IMAGE_CAPTURE").putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    , TAKE_PHOTO)
        }
        fragment_update_head_img_two_bt.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, OPEN_ALBUM)
        }
        fragment_update_head_img_three_bt.setOnClickListener {
            T.s("暂时无法上传，需要备案域名")
        }
    }

    /**
     * onDestroyView中进行解绑操作
     */
    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            TAKE_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    L.i(TAG, imageUri.toString())
                    Glide.with(GuApplication.context).load(outputImage)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(MyTarget())
                }
            }
            OPEN_ALBUM -> {
                if (resultCode == RESULT_OK && data != null) {
                    Glide.with(GuApplication.context).load(data.data)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(MyTarget())
                }
            }
        }
    }

    /**
     * 打电话的方法
     */
    private fun call() {
        try {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:10086")
            startActivity(intent)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * 内部类，用于获取glide加载图片时的bitmap
     */
    inner class MyTarget : SimpleTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
            val massage = gHandler.obtainMessage()
            massage.arg1 = GET_BITMAP
            massage.obj = resource
            gHandler.sendMessageDelayed(massage, 100)
        }
    }

}