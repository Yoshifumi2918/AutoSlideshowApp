package jp.techacademy.yoshifumi.nishida.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.content.ContentUris
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Handler
import android.widget.Toast
import java.util.*


class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100

    private var mTimer: Timer? = null

    // タイマー用の時間のための変数
    private var mTimerSec = 0.0

    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()

                } else {

                    Toast.makeText(this,"パーミッションを許可してください", Toast.LENGTH_LONG).show()

                }
        }
    }

    private fun getContentsInfo() {
        //画像を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, //データの種類
            null, //項目（null = 全項目）
            null, //フィルタ条件(null = フィルタなし)
            null, //フィルタ用パラメータ
            null //ソート（nullソートなし）

        )



        if (cursor!!.moveToFirst()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する

            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

            image.setImageURI(imageUri)
        }



        next.setOnClickListener {
            //nextボタンを押すと画像が変化する

            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)


            if (cursor!!.moveToNext()) {

                image.setImageURI(imageUri)

            } else if (cursor!!.moveToFirst()) {

                image.setImageURI(imageUri)

            }

        }


        back.setOnClickListener {
            //backボタンを押すと画像が変化する


            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)


            if (cursor!!.moveToPrevious()) {

                image.setImageURI(imageUri)
            } else if (cursor!!.moveToLast()) {


                image.setImageURI(imageUri)

            }

        }



        PlayStop.setOnClickListener {
   //再生ボタンを押すと、2秒ごとに画像が移る処理

            if (mTimer == null) {


                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {


                    override fun run() {

                        if (cursor!!.moveToNext()) {

                            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                            val id = cursor.getLong(fieldIndex)
                            val imageUri =
                                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                            mTimerSec += 2.0
                            mHandler.post {


                                image.setImageURI(imageUri)

                                PlayStop.text = "Stop"

                                next.isClickable = false

                                back.isClickable = false

                            }
                        }
                    }
                }, 2000, 2000) // 最初に始動させるまで2000ミリ秒、ループの間隔を2000ミリ秒 に設定

            } else if(mTimer != null) {
                mTimer!!.cancel()
                mTimer = null

                PlayStop.text = "Play"

                next.isClickable = true

                back.isClickable = true

            }
        }
    }
        }
















