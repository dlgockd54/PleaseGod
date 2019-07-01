package com.example.pleasegod.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pleasegod.R

class MainActivity : AppCompatActivity() {
    companion object {
        val REQUEST_CODE: Int = 111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for(result in grantResults) {
            if(result == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this, RestroomListActivity::class.java))
                finish()
            }
        }
    }
}
