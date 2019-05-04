package dev.novalogic.txqrfincrypt

import android.os.Bundle

import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainActivity: FlutterActivity() {
  private val CHANNEL = "tx.novalogic.dev/fincrypt"
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if(! Python.isStarted()) {
      Python.start(AndroidPlatform(this))
    }

    GeneratedPluginRegistrant.registerWith(this)

    MethodChannel(flutterView, CHANNEL).setMethodCallHandler { call, result ->
      if (call.method == "decodeMessage") {
        val message = decodeMessage(call.arguments as String)

        if (message != "") {
          result.success(message)
        } else {
          result.error("NOPE", "No message returned", null)
        }
      } else {
        result.notImplemented()
      }
    }
  }

  private fun decodeMessage(rawResult:String): String {

    return rawResult
  }
}
