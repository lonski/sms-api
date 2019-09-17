package pl.lonski.smsapi

import android.Manifest.permission.INTERNET
import android.Manifest.permission.SEND_SMS
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response.Status.*
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()

        Server().start()
    }

    private fun setupPermissions() {
        if (isDenied(SEND_SMS) || isDenied(INTERNET))
            ActivityCompat.requestPermissions(this, arrayOf(SEND_SMS, INTERNET), 101)
    }

    private fun isDenied(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PERMISSION_DENIED
    }
}

class Server : NanoHTTPD(8055) {

    override fun serve(session: IHTTPSession?): Response {
        session?.parseBody(HashMap())

        val recipient = session?.parameters?.get("recipient")?.first()
        val message = session?.parameters?.get("message")?.first()

        if (recipient != null && message != null) {
            return sendSms(recipient, message)
        }

        return newFixedLengthResponse(BAD_REQUEST, "text/html", "Missing one of parameters: [recipient, message]")
    }

    private fun sendSms(recipient: String, message: String): Response {
        try {
            SmsManager.getDefault().sendTextMessage(
                    "+$recipient", null, message, null, null)
        } catch (e: Exception) {
            return newFixedLengthResponse(INTERNAL_ERROR, "text/html", "ERR: ${e.message}")
        }
        return newFixedLengthResponse(OK, "text/html", "OK")
    }
}
