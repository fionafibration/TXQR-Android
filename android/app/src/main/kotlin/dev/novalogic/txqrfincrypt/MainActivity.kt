package dev.novalogic.txqrfincrypt

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.nbadal.AnimatedGifEncoder
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

import java.util.*

import io.flutter.plugin.common.MethodChannel.Result
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.IllegalArgumentException

class MainActivity : FlutterActivity() {
    private val methodChannel = "tx.novalogic.dev/fincrypt"
    var result : Result? = null
    var messageToEncode: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GeneratedPluginRegistrant.registerWith(this)

        MethodChannel(flutterView, methodChannel).setMethodCallHandler { call, Result ->
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
                call.method == "makeQR" -> {
                    this.result = Result
                    this.messageToEncode = call.arguments as String
                    if (!requestStorageAccessIfNecessary(this)) {
                        makeQR(this.messageToEncode!!)
                    }
                }
                else -> Result.notImplemented()
            }
        }
    }

    private fun makeQR(arguments: String) {
        Log.v("progress", "started making codes")
        val blocks = encoder(arguments.toByteArray(), 256, 5)

        Log.v("progress", "data encoded")
        val bitmaps = mutableListOf<Bitmap>()
        blocks.forEach { block ->
            Log.v("progress", "encoding images")
            bitmaps.add(QRGenerator(
                    block.toString(), this
            ).textToImageEncode(block.toString())!!
            )
        }

        Log.v("progress", "saving")
        this.result!!.success(saveImage(generateGif(bitmaps), this))
    }

    private fun generateGif(bitmaps: List<Bitmap>): ByteArray {
        val byteOutStream = ByteArrayOutputStream()
        val gifEncoder = AnimatedGifEncoder()
        gifEncoder.start(byteOutStream)
        gifEncoder.setRepeat(0)
        gifEncoder.setFrameRate(3f)
        bitmaps.forEach { bitmap ->
            gifEncoder.addFrame(bitmap)
        }
        gifEncoder.finish()
        return byteOutStream.toByteArray()
    }

    private fun saveImage(gifBytes: ByteArray, context: Context): String {
        val wallpaperDirectory = File(
                Environment
                        .getExternalStorageDirectory()
                        .toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            Log.d("imagesDirectory", "" + wallpaperDirectory.mkdirs())
            wallpaperDirectory.mkdirs()
        }

        try {
            val file = File(wallpaperDirectory, Calendar.getInstance()
                    .timeInMillis.toString() + ".gif")
            file.createNewFile() //give read write permission
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(gifBytes)
            MediaScannerConnection.scanFile(context,
                    arrayOf(file.path),
                    arrayOf("image/gif"), null)
            fileOutputStream.close()
            Log.d("file done", "File Saved :: ->>>>" + file.absolutePath)

            return file.absolutePath
        } catch (e1: IOException) {
            Log.e("there was an ioException", "ioException while saving")
        }
        return ""
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

    inner class QRGenerator(messageData: String, context: Context) {
        private lateinit var bitmap: Bitmap
        private lateinit var path: String
        init {
            bitmap = textToImageEncode(messageData)!!
            path = saveImage(bitmap, context)
        }

        fun getPath() : String {
            return path
        }

        private fun saveImage(bitmap: Bitmap?, context: Context): String {
            val bytes = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
            val wallpaperDirectory = File(
                    Environment
                            .getExternalStorageDirectory()
                            .toString() + IMAGE_DIRECTORY)
            // have the object build the directory structure, if needed.

            if (!wallpaperDirectory.exists()) {
                Log.d("imagesDirectory", "" + wallpaperDirectory.mkdirs())
                wallpaperDirectory.mkdirs()
            }

            try {
                val file = File(wallpaperDirectory, Calendar.getInstance()
                        .timeInMillis.toString() + ".jpg")
                file.createNewFile() //give read write permission
                val fileOutputStream = FileOutputStream(file)
                fileOutputStream.write(bytes.toByteArray())
                MediaScannerConnection.scanFile(context,
                        arrayOf(file.path),
                        arrayOf("image/jpeg"), null)
                fileOutputStream.close()
                Log.d("file done", "File Saved :: ->>>>" + file.absolutePath)

                return file.absolutePath
            } catch (e1: IOException) {
                Log.d("hmmm", "ioexception while saving")
            }
            return ""
        }

        fun textToImageEncode(messageData: String): Bitmap? {
            val bitMatrix: BitMatrix
            try {
                bitMatrix = MultiFormatWriter().encode(
                        messageData,
                        BarcodeFormat.QR_CODE,
                        QRCodeWidth, QRCodeWidth
                )
            } catch (e: IllegalArgumentException) {
                return null
            }

            val bitMatrixWidth = bitMatrix.width
            val bitMatrixHeight = bitMatrix.height
            val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

            for (y in 0 until bitMatrixHeight) {
                val offset = y * bitMatrixWidth
                for (x in 0 until bitMatrixWidth) {
                    pixels[offset + x] = if (bitMatrix.get(x, y))
                        BLACK
                    else
                        WHITE
                }
            }
            val bitmap = Bitmap.createBitmap(
                    bitMatrixWidth,
                    bitMatrixHeight,
                    Bitmap.Config.RGB_565)
            bitmap.setPixels(pixels, 0, QRCodeWidth, 0, 0,
                    bitMatrixWidth, bitMatrixHeight)
            return bitmap
        }
    }

    private fun requestStorageAccessIfNecessary(context: Context): Boolean {
        val array = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, array,
                    REQUEST_STORAGE_PERMISSION)
            return true
        }
        return false
    }

    private fun finishWithError(errorCode: String) {
        //val intent = Intent()
        //intent.putExtra("ERROR_CODE", errorCode)
        //setResult(Activity.RESULT_CANCELED, intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    makeQR(this.messageToEncode!!)
                } else {
                    finishWithError("PERMISSION_NOT_GRANTED")
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    companion object {
        const val BLACK: Int = 2130968606
        const val WHITE: Int = 2147483647
        const val REQUEST_STORAGE_PERMISSION = 101

        //Code Resolution
        const val QRCodeWidth = 968
        private const val IMAGE_DIRECTORY = "/QRCodeDocuments"
    }
}

//class logThePython {
//    fun logit(message: String) {
//        //lol it does nothing
//    }
//}
