package com.example.datebasetestdemo

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.Exception
import java.lang.NullPointerException

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    // 摄像头相关
    private val takePhoto = 1
    private val fromAlbum = 2
    lateinit var imageUri: Uri
    lateinit var  outputImage: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 从相册选择
        albumButton.setOnClickListener {
            // 打开文件选择器
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            // 指定只显示图片
            intent.type = "image/*"
            startActivityForResult(intent, fromAlbum)
        }

        // 调起摄像头
        cameraButton.setOnClickListener {
            // 创建File对象，暂存拍摄之后的图片
            outputImage = File(externalCacheDir, "output_image.jpg")
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
            // 将File转化为Uri对象，注意版本
            imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this, "com.example.camera.test", outputImage)
            } else {
                Uri.fromFile(outputImage)
            }
            // 启动相机程序
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, takePhoto)
        }

        // 创建一个通知
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("important", "Important", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }
        notificationButton.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            val pi = PendingIntent.getActivity(this, 0, intent, 0)
            val notification = NotificationCompat.Builder(this, "important")
                    .setContentTitle("This is content title")
                    .setStyle(NotificationCompat.BigTextStyle().bigText("Learn how to build notification, send and sync data, and use voice action, Get the official Android IDF and developer tools to build apps"))
                    .setSmallIcon(R.mipmap.small_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.large_icon))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build()
            manager.notify(1, notification)
        }

        val dbHelper = MyDataBaseHelper(this, "BookStore.db", 1)
        createDataBase.setOnClickListener {
            dbHelper.writableDatabase
        }

        addData.setOnClickListener {
            val db = dbHelper.writableDatabase
            // 添加数据的第一种方式
            val value1 = ContentValues().apply {
                put("name", "jyf")
                put("author", "jyf")
                put("pages", 234)
                put("price", 16.96)
            }
            // 比较好的方式
            db.insert("Book", null, value1)
            db.execSQL("insert into Book(name, author, pages, price) values(?, ?, ?, ?)",
            arrayOf("Fage", "Jyf", "520", "56.47"))
        }

        queryData.setOnClickListener {
            val db = dbHelper.writableDatabase
            // 查询Book表中的所有数据
//            val cursor = db.query("Book", null, null, null, null, null, null)
//            if (cursor.moveToFirst()) {
//                do {
//                    // 遍历Cursor对象，取出数据并打印出来
//                    val name = cursor.getString(cursor.getColumnIndex("name"))
//                    val author = cursor.getString(cursor.getColumnIndex("author"))
//                    val pages = cursor.getInt(cursor.getColumnIndex("pages"))
//                    val price = cursor.getDouble(cursor.getColumnIndex("price"))
//                    Log.d(TAG, "name is $name author is $author pages is $pages price is $price")
//                } while (cursor.moveToNext())
//            }
//            cursor.close()

            // 第二种查询方式
            val cursor1 = db.rawQuery("select * from Book", null)
            if (cursor1.moveToFirst()) {
                do {
                    // 遍历Cursor对象，取出数据并打印出来
                    val name = cursor1.getString(cursor1.getColumnIndex("name"))
                    val author = cursor1.getString(cursor1.getColumnIndex("author"))
                    val pages = cursor1.getInt(cursor1.getColumnIndex("pages"))
                    val price = cursor1.getDouble(cursor1.getColumnIndex("price"))
                    Log.d(TAG, "name is $name author is $author pages is $pages price is $price")
                } while (cursor1.moveToNext())
            }
            cursor1.close()
        }

        updateData.setOnClickListener {
            val db = dbHelper.writableDatabase
            val value = ContentValues()
            value.put("price", 25.67)
            db.update("Book", value, "name = ?", arrayOf("jyf"))

            // 第二种方式
            db.execSQL("update Book set price = ? where name = ?",
            arrayOf("45.67", "jyf"))
        }

        deleteData.setOnClickListener {
            val db = dbHelper.writableDatabase
            db.delete("Book", "pages = ?", arrayOf("234"))

            // 第二种方式
            db.execSQL("delete from Book where pages > ?", arrayOf("500"))
        }

        // 事务
        replaceData.setOnClickListener {
            val db = dbHelper.writableDatabase
            db.beginTransaction()
            try {
                db.delete("Book", null, null)
                val values = ContentValues().apply {
                    put("name", "First Code")
                    put("author", "jyf")
                    put("pages", 234)
                    put("price", 16.96)
                }
                db.insert("Book", null, values)
                db.setTransactionSuccessful()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                db.endTransaction()
            }
        }
    }

    /**
     * 监听相机回调
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK) {
                    // 将拍照的结果取出来
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    mImageView.setImageBitmap(rotateIfRequired(bitmap))
                }
            }

            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        val bitmap = getBitmapFromUri(uri)
                        mImageView.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver
            .openFileDescriptor(uri, "r")?.use {
                BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
            }

    /**
     * 如果需要旋转图片，先旋转图片
     */
    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotateBitmap
    }
}