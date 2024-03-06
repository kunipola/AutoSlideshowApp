package jp.techacademy.yoshihiro.kunieda.autoslideshowapp

import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import jp.techacademy.yoshihiro.kunieda.autoslideshowapp.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var timer: Timer? = null
    private val PERMISSIONS_REQUEST_CODE = 100

    private var handler = Handler(Looper.getMainLooper())

    // APIレベルによって許可が必要なパーミッションを切り替える (API≧33：READ_MEDIA_IMAGE)
    private val readImagesPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_IMAGES
        else android.Manifest.permission.READ_EXTERNAL_STORAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // パーミッションの許可状態を確認する
        if (checkSelfPermission(readImagesPermission) == PackageManager.PERMISSION_GRANTED) {

            val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目（null = 全項目）
                null, // フィルタ条件（null = フィルタなし）
                null, // フィルタ用パラメータ
                null // ソート (nullソートなし）
            )

            // startStopButtonを押した場合
            //val moveToFirst = cursor!!.moveToFirst()
            //val moveToNext = cursor!!.moveToNext()
            binding.startStopButton.setOnClickListener {
                //再生押したとき
                if (binding.startStopButton.text == "再生") {
                    binding.startStopButton.text = "停止"
                    binding.nextButton.isEnabled = false //進むボタンを無効化する(.isClickableにするとボタン消える)
                    binding.backButton.isEnabled = false //進むボタンを無効化する(.isClickableにするとボタン消える)

                    Log.d("record", "再生ボタンを押下")

                    cursor!!.moveToFirst()

                    timer = Timer()
                    timer!!.schedule(object : TimerTask() {
                        override fun run() {

                            Log.d("record", "2秒ごと")
                            Log.d("record1",cursor.toString())


                            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)


                            val id = cursor.getLong(fieldIndex)



                            val imageUri =
                                ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                                )

                            handler.post{
                                binding.imageView.setImageURI(imageUri)
                            }

                            Log.d("record2",cursor.toString())

                            if(cursor.isLast) {
                                cursor.moveToFirst()
                            }else {
                                cursor.moveToNext()
                            }

                        }
                    }, 200, 2000) // 最初に始動させるまで200ミリ秒、ループの間隔を2000ミリ秒 に設定

                    //停止押したとき
                } else if (binding.startStopButton.text == "停止") {
                    binding.startStopButton.text = "再生"
                    binding.nextButton.isEnabled = true //進むボタンを有効化する(.isClickableにするとボタン消える)
                    binding.backButton.isEnabled = true //戻るボタンを有効化する(.isClickableにするとボタン消える)

                    Log.d("record", "停止ボタンを押下")

                    if(cursor!!.isFirst){
                        cursor.moveToLast()
                    }else{
                        cursor.moveToPrevious()
                    }

                        timer!!.cancel()
                        timer = null

                }

                //nextButtonを押した場合
                binding.nextButton.setOnClickListener {
                    Log.d("record", "進むボタンを押下")
                    Log.d("record11",cursor.toString())

                    if (cursor!!.isLast){
                        cursor.moveToFirst()
                    }else{
                        cursor.moveToNext()
                    }
                    Log.d("record12",cursor.toString())

                        val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                        val id = cursor.getLong(fieldIndex)
                        val imageUri =
                            ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                            )
                        handler.post {
                            binding.imageView.setImageURI(imageUri)
                        }
                }

                //backButtonを押した場合
                binding.backButton.setOnClickListener {
                    Log.d("record", "戻るボタンを押下")
                    Log.d("record21",cursor.toString())
                    if (cursor!!.isFirst){
                        cursor.moveToLast()
                    }else{
                        cursor.moveToPrevious()
                    }
                    Log.d("record22",cursor.toString())

                        val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                        val id = cursor.getLong(fieldIndex)
                        val imageUri =
                            ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                            )
                        handler.post {
                            binding.imageView.setImageURI(imageUri)
                        }
                }
            }
        }else {
            requestPermissions(
                arrayOf(readImagesPermission),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }
}



