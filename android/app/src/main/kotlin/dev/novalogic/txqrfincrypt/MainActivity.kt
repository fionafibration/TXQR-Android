package dev.novalogic.txqrfincrypt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log

import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

import java.util.*

import io.flutter.plugin.common.MethodChannel.Result

class MainActivity : FlutterActivity() {
    private val CHANNEL = "tx.novalogic.dev/fincrypt"
    var result : Result? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GeneratedPluginRegistrant.registerWith(this)

        MethodChannel(flutterView, CHANNEL).setMethodCallHandler { call, Result ->
            when {
                call.method == "altScan" -> {
                    this.result = Result
                    val intent = Intent(this, AltBarcodeScannerActivity::class.java)
                    this.startActivityForResult(intent, 100)
                }
                call.method == "scan" -> {
                    this.result = Result
                    val intent = Intent(this, BarcodeScannerActivity::class.java)
                    this.startActivityForResult(intent, 100)
                }
                else -> Result.notImplemented()
            }
        }
    }

    override fun onActivityResult(code: Int, resultCode: Int, data: Intent?) {
        if (code == 100) {
            if (resultCode == Activity.RESULT_OK) {
                val barcode = data?.getStringExtra("SCAN_RESULT")
                Log.v("Scan_Result", barcode)
                barcode?.let { this.result?.success(barcode) }
            } else {
                val errorCode = data?.getStringExtra("ERROR_CODE")
                this.result?.error(errorCode, null, null)
            }
        }
    }
}

//class logThePython {
//    fun logit(message: String) {
//        //lol it does nothing
//    }
//}
