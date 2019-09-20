package pl.lonski.smsapi

import android.telephony.SmsManager
import fi.iki.elonen.NanoHTTPD
import java.util.*

class Server(port: Int, val log: (msg: String) -> Unit) : NanoHTTPD(port) {

    override fun start() {
        super.start()
        log("Server started.")
    }

    override fun stop() {
        super.stop()
        log("Server stopped.")
    }

    override fun serve(session: IHTTPSession?): Response {
        session?.parseBody(HashMap())

        val recipient = session?.parameters?.get("recipient")?.first()
        val message = session?.parameters?.get("message")?.first()
        log("Request from ${session?.remoteIpAddress}: \n\trecipient=[$recipient]\n\tmessage=[$message]")

        if (recipient != null && message != null) {
            return sendSms(recipient, message)
        }

        log("Bad request. Missing one of parameters: [recipient, message]")
        return newFixedLengthResponse(
            Response.Status.BAD_REQUEST,
            "text/html",
            "Missing one of parameters: [recipient, message]"
        )
    }

    private fun sendSms(recipient: String, message: String): Response {
        try {
            SmsManager.getDefault().sendTextMessage("+$recipient", null, message, null, null)
        } catch (e: Exception) {
            log("Error sending sms: ${e.message}")
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/html", "ERR: ${e.message}")
        }
        log("Sms sent successfully.")
        return newFixedLengthResponse(Response.Status.OK, "text/html", "OK")
    }
}
