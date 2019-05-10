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
            if (call.method == "decodeMessage") {
                val message = decodeMessage(call.arguments as String)

                if (message.isNotEmpty()) {
                    Result.success(message)
                } else {
                    Result.error("NOPE", "No message returned", null)
                }
            } else if (call.method == "scan") {
                this.result = Result
                val intent = Intent(this, BarcodeScannerActivity::class.java)
                this.startActivityForResult(intent, 100)
            } else {
                Result.notImplemented()
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

    private val mLtDecoder = LTDecoder()
    private fun decodeMessage(rawResult: String): List<Any> {

//        if (!Python.isStarted()) {
//            Python.start(AndroidPlatform(this))
//        }

//        val pProgress = 0.0f
////        var messageToBeam: List<Any>
////
////        val progress = try {
////            mLtDecoder.decodeBytes(rawResult.toByteArray()) * 100
////        } catch (e: Throwable) {
////            pProgress
////        }
////
////        if (!mLtDecoder.done) {
////            messageToBeam = listOf("DECODE.NOT_DONE", progress)
////        } else {
////            var beamedMessage: ByteArray? = null
////            try {
////                val b64beamMessage = mLtDecoder.decodeDump()
////                beamedMessage = Base64.getDecoder().decode(b64beamMessage)
////            } catch (e: Throwable) {
////                //NOPE
////            }
////            messageToBeam = listOf(String(beamedMessage!!), progress)
////        }

//        if (Python.isStarted()) {
//            val mLtDecoder = try {
//                lterrorcorrection.LTDecoder()
//            } catch (E: Exception) {
//                return "Nope"
//            }
//
//            val progress = try {
//                mLtDecoder.decode_bytes(rawResult) * 100
//            } catch (e: Throwable) {
//                pProgress
//            }
//
//            if (!mLtDecoder.is_done) {
//                messageToBeam = "DECODE.NOT_DONE"
//            } else {
//                var beamedMessage: ByteArray? = null
//                try {
//                    val b64beamMessage = mLtDecoder.bytes_dump()
//                    beamedMessage = Base64.getDecoder().decode(b64beamMessage)
//                } catch (e: Throwable) {
//                    //NOPE
//                }
//                messageToBeam = String(beamedMessage!!)
//            }
//        } else {
//            messageToBeam = "We Could not Start Python the dev fucked up"
//        }

        return listOf()//messageToBeam
    }
}

//class logThePython {
//    fun logit(message: String) {
//        //lol it does nothing
//    }
//}
