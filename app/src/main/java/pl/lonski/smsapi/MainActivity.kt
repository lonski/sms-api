package pl.lonski.smsapi

import android.Manifest.permission.*
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val permissions = arrayOf(SEND_SMS, INTERNET, ACCESS_WIFI_STATE)
    private val logSize = 100
    private val messages = LinkedList<String>()
    private val timestampFormatter = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.getDefault())
    private var server = Server(8080, ::log)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
        findViewById<TextView>(R.id.log).movementMethod = ScrollingMovementMethod()
        findViewById<Button>(R.id.start_stop).setOnClickListener(::changeServerState)
    }

    override fun onDestroy() {
        if (server.isAlive)
            server.stop()
        super.onDestroy()
    }

    private fun setupPermissions() {
        if (permissions.any { isDenied(it) })
            ActivityCompat.requestPermissions(this, permissions, 101)
    }

    private fun isDenied(permission: String) = ContextCompat.checkSelfPermission(this, permission) == PERMISSION_DENIED

    private fun changeServerState(button: View) {
        try {
            button.isEnabled = false
            if (server.isAlive)
                server.stop()
            else
                startServer()
        } catch (e: Exception) {
            log("Error: ${e.message}")
        } finally {
            button.isEnabled = true
        }
    }

    private fun startServer() {
        val portNumber = findViewById<EditText>(R.id.port_number).text.toString().toInt()
        log("Starting server on ${getIp()}:${portNumber}")
        if (server.listeningPort != portNumber)
            server = Server(portNumber, ::log)
        server.start()
    }

    private fun log(message: String) {
        while (messages.size >= logSize)
            messages.removeLast()
        messages.add("[${timestampFormatter.format(Date())}]: $message")
        findViewById<TextView>(R.id.log).text = messages.joinToString("\n")
    }

    private fun getIp(): String {
        val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return ipToString(wm.connectionInfo.ipAddress)
    }

    private fun ipToString(i: Int): String {
        return (i and 0xFF).toString() + "." +
                (i shr 8 and 0xFF) + "." +
                (i shr 16 and 0xFF) + "." +
                (i shr 24 and 0xFF)
    }
}

