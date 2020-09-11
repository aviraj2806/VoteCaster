package com.shweta.votecaster.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.shweta.votecaster.R
import java.util.*

class SplashActivity : AppCompatActivity() {

    val MULTIPLE_PERMISSIONS = 123
    var permissions = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)

        Handler().postDelayed(Runnable {
            if (checkPermissions()) {
                getCallDetails()
            }
        },500)

    }
    private fun checkPermissions(): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> =
            ArrayList()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(applicationContext, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MULTIPLE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // permissions granted.
                    getCallDetails() // Now you call here what ever you want :)
                } else {
                    Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show()
                    finishAffinity()
                }
                return
            }
        }
    }

    private fun getCallDetails() {

        if(sharedPreferences.getBoolean("isLoggedIn",false)){
            val intent = Intent(this@SplashActivity, HomeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            finish()
        }
        else {
            val intent = Intent(this@SplashActivity, AuthActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            finish()
        }
    }
}
