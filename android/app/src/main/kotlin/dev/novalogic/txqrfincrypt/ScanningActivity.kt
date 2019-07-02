package dev.novalogic.txqrfincrypt

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.util.*


class BarcodeScannerActivity : Activity(), ZXingScannerView.ResultHandler {

    lateinit var scannerView: ZXingScannerView
    lateinit var mLtDecoder: LTDecoder
    var progress: Double = 0.0

    companion object {
        const val REQUEST_TAKE_PHOTO_CAMERA_PERMISSION = 100
        const val TOGGLE_FLASH = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = ""
        scannerView = ZXingScannerView(this)
        scannerView.setAutoFocus(true)
        // this parameter will make your HUAWEI phone work great!
        scannerView.setAspectTolerance(0.5f)
        mLtDecoder = LTDecoder()
        setContentView(scannerView)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (scannerView.flash) {
            val item = menu.add(0,
                    TOGGLE_FLASH, 0, "Flash Off")
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        } else {
            val item = menu.add(0,
                    TOGGLE_FLASH, 0, "Flash On")
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == TOGGLE_FLASH) {
            scannerView.flash = !scannerView.flash
            this.invalidateOptionsMenu()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        // start camera immediately if permission is already given
        if (!requestCameraAccessIfNecessary()) {
            scannerView.startCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun handleResult(result: Result?) { //Handles the results of the scan and recursively calls the scanner to complete decoding
        val intent = Intent()
        val messageToBeam: String

        progress = try {
            Log.v("QR_SEEN", "QR Data: ${result!!.text}")
            mLtDecoder.decodeBytes(result.text.toByteArray(Charsets.UTF_8)) * 100
        } catch (e: Throwable) {
            Log.e("QR_ERROR", "Exception decoding QR code!", e)
            progress
        }

        if (!mLtDecoder.done) {
            Toast.makeText(this, "%.1f%% Done".format(progress), Toast.LENGTH_LONG).show()
            Log.v("QR_PROGRESS", "QR is %.1f%% done decoding".format(progress))
            scannerView.resumeCameraPreview(this)
        } else {
            var beamedMessage: ByteArray? = null
            try {
                beamedMessage = mLtDecoder.decodeDump()
            } catch (e: Throwable) {
                Log.e("QR_ERROR", "Exception decoding file!", e)
            }
            messageToBeam = String(beamedMessage!!)
            Log.v("MESSAGE", messageToBeam)

            intent.putExtra("SCAN_RESULT", messageToBeam)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun finishWithError(errorCode: String) {
        val intent = Intent()
        intent.putExtra("ERROR_CODE", errorCode)
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    private fun requestCameraAccessIfNecessary(): Boolean {
        val array = arrayOf(Manifest.permission.CAMERA)
        if (ContextCompat
                        .checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, array,
                    REQUEST_TAKE_PHOTO_CAMERA_PERMISSION)
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_TAKE_PHOTO_CAMERA_PERMISSION -> {
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    scannerView.startCamera()
                } else {
                    finishWithError("PERMISSION_NOT_GRANTED")
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}
