package com.example.camera1

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    val TAG: String = "GILBOM"
    val TAKE_PICTURE: Int = 1

    // 경로 변수와 요청변수 생성
    var mCurrentPhotoPath: Int = 1
    val REQUEST_TAKE_PHOTO: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료")
            } else {
                Log.d(TAG, "권한 설정 요청")
                ActivityCompat.requestPermissions(this, /* 해석이 안되서 변환을 못하겠어... */ /* new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE } */, 1)
            }
        }
        btnCamera.setOnClickListener(View.OnClickListener {
            override fun onClick(v: View) {
                when (v.id) {
//                    R.id.btnCamera -> { var cameraIntent: Intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
//                        startActivityForResult(cameraIntent, TAKE_PICTURE)
                    R.id.btnCamera -> {
                        dispatchTakePictureIntent()
//                        break
                    }
                }
            }
        })
    }

    // 권한요청
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionResult")

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0])
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        when (requestCode) {
//            TAKE_PICTURE -> {
//                if (resultCode == RESULT_OK && intent.hasExtra("data")) {
//                    var bitmap: Bitmap = data?.extras?.get("data") as Bitmap
//                    if (bitmap != null) {
//                        ivImage.setImageBitmap(bitmap)
//                    }
//                }
//            }
//        }

        try {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    if (resultCode == RESULT_OK) {
                        var file = File(mCurrentPhotoPath)
                        var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver(), Uri.fromFile(file))
                        if (bitmap != null) {
                            ivImage.setImageBitmap(bitmap)
                        }
                    }
//                    break
                }
            }
        } catch (error: Exception) {
            error.printStackTrace()
        }

    }

    // 사진 촬영 후 썸네일만 띄워줌. 이미지를 파일로 저장해야 함.
    fun createImageFile(): File {
        var timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName: String = "JPEG_" + timeStamp + "_"
        var storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var image: File = File.createTempFile(imageFileName, ".jpg", storageDir)
        mCurrentPhotoPath = image.getAbsolutePath
        return image
    }

    // 카메라 인텐트 실행하는 부분
    fun dispatchTakePictureIntent() {
        val takePictureIntent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            var photoFile: File? = null

            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                if (photoFile != null) {
                    var photoURI: Uri = FileProvider.getUriForFile(this, "com.example.camera1.fileprovider", photoFile)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }
}