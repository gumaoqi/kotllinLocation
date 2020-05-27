package  com.gumaoqi.test.kotlinbaseproject.tool

import  android.content.ContentValues
import  android.content.Context
import  android.graphics.Bitmap
import  android.graphics.BitmapFactory
import  android.graphics.Canvas
import  android.graphics.Color
import  android.net.Uri
import  android.os.Build
import  android.provider.MediaStore
import  android.util.Base64
import  android.view.View
import  com.gumaoqi.test.kotlinbaseproject.R
import  java.io.*
import  java.security.MessageDigest
import  java.security.NoSuchAlgorithmException
import  java.util.*

/**
 *  单例类，类型转换工具
 */
object C {
    /**
     *  将Bitmap转换成Base64字符串
     *
     *  @param  bitmap
     *  @return
     */
    fun bitmapToBase64(bitmap: Bitmap): String {
        val bos = ByteArrayOutputStream()
        //使用Bitmap.CompressFormat.PNG
        //Bitmap.CompressFormat.JPEG  不支持透明，图片透明部分会变黑（手写输入会遇到问题）
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, bos)//参数100表示不压缩
        val bytes = bos.toByteArray()
        //转换来的base64码需要加前缀，必须是NO_WRAP参数，表示没有空格。
        return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
        //转换来的base64码不需要需要加前缀，必须是NO_WRAP参数，表示没有空格。
        //return  "data:image/jpeg;base64,"  +  Base64.encodeToString(bytes,  Base64.NO_WRAP);
    }

    /**
     *  base64转为bitmap
     *
     *  @param  base64Data
     *  @return
     */
    fun base64ToBitmap(base64Data: String?): Bitmap? {
        if (base64Data == null || base64Data == "") return null
        val bytes = Base64.decode(base64Data.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1], Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     *  base64转为byte[]
     *
     *  @param  base64Data
     *  @return
     */
    fun base64ToBytes(base64Data: String): ByteArray {
        val bitmap = base64ToBitmap(base64Data)
        val baos = ByteArrayOutputStream()
        if (bitmap == null) {
            return "".toByteArray()
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    /**
     *  将base64的头去掉
     *
     *  @param  base64
     *  @return
     */
    fun base64ToNoHeaderBase64(base64: String): String {
        return base64.replace("data:image/jpeg;base64,", "")
    }

    /**
     *  uri转换成file
     *
     *  @param  uri
     *  @param  context
     *  @return
     */

    fun uriToFile(uri: Uri, context: Context): File? {
        var path: String? = null
        if ("file" == uri.scheme) {
            path = uri.encodedPath
            if (path != null) {
                path = Uri.decode(path)
                val cr = context.contentResolver
                val buff = StringBuffer()
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'$path'").append(")")
                val cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA), buff.toString(), null, null)
                var index = 0
                var dataIdx = 0
                cur!!.moveToFirst()
                while (!cur.isAfterLast) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID)
                    index = cur.getInt(index)
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    path = cur.getString(dataIdx)
                    cur.moveToNext()
                }
                cur.close()
                if (index == 0) {
                } else {
                    val u = Uri.parse("content://media/external/images/media/$index")
                    println("temp  uri  is  :$u")
                }
            }
            if (path != null) {
                return File(path)
            }
        } else if ("content" == uri.scheme) {
            //  4.2.2以后
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(uri, proj, null, null, null)
            if (cursor!!.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = cursor.getString(columnIndex)
            }
            cursor.close()
            if (path != null) {
                return File(path)
            }
        } else {
            //Log.i(TAG,  "Uri  Scheme:"  +  uri.getScheme());
        }
        return null
    }

    /**
     *  file转化成uri
     *
     *  @param  context
     *  @param  imageFile
     *  @return
     */

    fun getImageContentUri(context: Context, imageFile: File): Uri? {
        val filePath = imageFile.absolutePath
        val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID), MediaStore.Images.Media.DATA + "=?  ",
                arrayOf(filePath), null)
        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri = Uri.parse("content://media/external/images/media")
            val returnUri = Uri.withAppendedPath(baseUri, "" + id)
            cursor.close()
            return returnUri
        } else {
            if (imageFile.exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                return null
            }
        }
    }

    /**
     *  获取指定文字的md5码
     *
     *  @param  plainText  指定的文字
     *  @return
     */
    fun getMd5(plainText: String): String? {
        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(plainText.toByteArray())
            val b = md.digest()
            var i: Int
            val buf = StringBuffer("")
            for (offset in b.indices) {
                i = b[offset].toInt()
                if (i < 0)
                    i += 256
                if (i < 16)
                    buf.append("0")
                buf.append(Integer.toHexString(i))
            }
            //  32位加密
            return buf.toString()
            //  16位的加密
            //  return  buf.toString().substring(8,  24);
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        }
    }


    /**
     *  文件转base64字符串
     *
     *  @param  file  文件
     *  @return
     */
    fun fileToBase64(file: File): String? {
        var base64: String? = null
        var `in`: InputStream? = null
        try {
            `in` = FileInputStream(file)
            val bytes = ByteArray(`in`.available())
            val length = `in`.read(bytes)
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT)
        } catch (e: FileNotFoundException) {
            //  TODO  Auto-generated  catch  block
            e.printStackTrace()
        } catch (e: IOException) {
            //  TODO  Auto-generated  catch  block
            e.printStackTrace()
        } finally {
            try {
                `in`?.close()
            } catch (e: IOException) {
                //  TODO  Auto-generated  catch  block
                e.printStackTrace()
            }

        }
        return base64
    }

    /**
     *  将毫秒数转化为HH:mm:ss的时间格式
     *
     *  @param  time  需要转换的毫秒数
     *  @return  转换后的时间格式
     */
    fun longTimeChange(time: Long): String {
        val hour = (time / (1000 * 60 * 60)).toString() + ""
        var minute = (time % (1000 * 60 * 60) / (1000 * 60)).toString() + ""
        if (minute.length == 1) {
            minute = "0$minute"
        }
        var second = (time % (1000 * 60) / 1000).toString() + ""
        if (second.length == 1) {
            second = "0$second"
        }
        return "$hour:$minute:$second"
    }


    /**
     *  可逆的加密算法，string转base64
     *
     *  @param  inStr
     *  @return
     */
    fun stringToBase64(inStr: String): String {
        return Base64.encodeToString(inStr.toByteArray(), 0)
    }

    /**
     *  可逆的解密算法，base64转string
     *
     *  @param  inStr
     *  @return
     */
    fun base64ToString(inStr: String): String {
        return String(Base64.decode(inStr, 0))
    }

    /**
     *  从view上获取bitmap图片
     *
     *  @param  view
     *  @return
     */
    private fun loadBitmapFromView(view: View): Bitmap {
        val width = view.width
        val height = view.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        /**  如果不设置canvas画布为白色，则生成透明    */
        view.layout(0, 0, width, height)
        view.draw(canvas)
        return bitmap
    }

    /**
     *  根据毫秒数获取时间yyyy-MM-dd  hh:mm:ss
     *
     *  @param  time
     *  @return
     */
    fun longTimeChangeYear(time: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        val month = calendar.get(Calendar.MONTH) + 1
        return "" + calendar.get(Calendar.YEAR) + "-" +
                (if (month >= 10) "" + month else "0$month") + "-" +
                (if (calendar.get(Calendar.DAY_OF_MONTH) >= 10) "" + calendar.get(Calendar.DAY_OF_MONTH) else "0" + calendar.get(Calendar.DAY_OF_MONTH)) + "    " +
                (if (calendar.get(Calendar.HOUR_OF_DAY) >= 10) "" + calendar.get(Calendar.HOUR_OF_DAY) else "0" + calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                (if (calendar.get(Calendar.MINUTE) >= 10) "" + calendar.get(Calendar.MINUTE) else "0" + calendar.get(Calendar.MINUTE)) + ":" +
                if (calendar.get(Calendar.SECOND) >= 10) "" + calendar.get(Calendar.SECOND) else "0" + calendar.get(Calendar.SECOND)
    }

    /**
     *  得到bitmap的大小
     */
    public fun getBitmapSize(bitmap: Bitmap): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {        //API  19
            return bitmap.getAllocationByteCount()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API  12
            return bitmap.getByteCount()
        }
        //  在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                              //earlier  version
    }


    public fun bitmapToBytes(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    /**
     *  补充：计算两点之间真实距离
     *  @return  米
     */
    public fun getDistance(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Double {
        //  维度
        val lat1 = (Math.PI / 180) * latitude1
        val lat2 = (Math.PI / 180) * latitude2

        //  经度
        val lon1 = (Math.PI / 180) * longitude1
        val lon2 = (Math.PI / 180) * longitude2

        //  地球半径
        val r = 6371

        //  两点间距离km，如果想要米的话，结果*1000就可以了
        val d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * r

        return String.format("%.2f", (d * 1000)).toDouble()
    }
}