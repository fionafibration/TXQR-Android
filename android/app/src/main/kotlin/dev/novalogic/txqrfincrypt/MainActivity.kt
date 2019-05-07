package dev.novalogic.txqrfincrypt

import android.os.Bundle

import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.util.*

class MainActivity : FlutterActivity() {
    private val CHANNEL = "tx.novalogic.dev/fincrypt"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    private fun decodeMessage(rawResult: String): String {

//        if (!Python.isStarted()) {
//            Python.start(AndroidPlatform(this))
//        }

        val pProgress = 0.0f
        var messageToBeam: String = "nope"
        val mLtDecoder = LTDecoder()

        val progress = try {
            mLtDecoder.decodeBytes(rawResult.toByteArray()) * 100
        } catch (e: Throwable) {
            pProgress
        }

        if (!mLtDecoder.is_done) {//TODO this needs implementation
            messageToBeam = "DECODE.NOT_DONE"
        } else {
            var beamedMessage: ByteArray? = null
            try {
                val b64beamMessage = mLtDecoder.decodeDump()
                beamedMessage = Base64.getDecoder().decode(b64beamMessage)
            } catch (e: Throwable) {
                //NOPE
            }
            messageToBeam = String(beamedMessage!!)
        }

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

        return messageToBeam
    }
}

class logThePython {
    fun logit(message: String) {
        //lol it does nothing
    }
}
