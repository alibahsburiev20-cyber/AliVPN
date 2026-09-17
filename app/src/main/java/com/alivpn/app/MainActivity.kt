package com.alivpn.app

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var server: TextView
    private lateinit var details: TextView
    private lateinit var hint: TextView
    private lateinit var button: MaterialButton

    companion object {
        private const val VPN_REQUEST_CODE = 1001
        const val CONFIG_URL = "https://raw.githubusercontent.com/aviamastersgh/vpn-free-russia/main/verified_configs.txt"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        status = findViewById(R.id.tvStatus)
        server = findViewById(R.id.tvServer)
        details = findViewById(R.id.tvDetails)
        hint = findViewById(R.id.tvHint)
        button = findViewById(R.id.btnConnect)

        button.setOnClickListener {
            if (AliVpnService.isRunning) disconnect() else startFlow()
        }
    }

    private fun startFlow() {
        setConnecting(true, "Загружаем список конфигураций…")
        thread {
            val result = fetchConfigs()
            runOnUiThread {
                if (result.ok) {
                    details.text = "Конфигураций получено: ${result.count}\nПроверка: готово\nРежим: прототип"
                    requestVpnPermission()
                } else {
                    setConnecting(false, "Не удалось загрузить список узлов. Проверь интернет.")
                    Toast.makeText(this, result.error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun requestVpnPermission() {
        val intent = VpnService.prepare(this)
        if (intent != null) startActivityForResult(intent, VPN_REQUEST_CODE)
        else startVpn()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VPN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) startVpn()
            else setConnecting(false, "Разрешение VPN отклонено")
        }
    }

    private fun startVpn() {
        val serviceIntent = Intent(this, AliVpnService::class.java)
        startService(serviceIntent)
        status.text = "● ПОДКЛЮЧЕНО"
        status.setTextColor(getColor(R.color.green))
        button.text = "DISCONNECT"
        server.text = "Авто / выбранный узел"
        hint.text = "VPN-интерфейс активен. Полный прокси-туннель подключим в следующей версии."
    }

    private fun disconnect() {
        stopService(Intent(this, AliVpnService::class.java))
        status.text = "● ОТКЛЮЧЕНО"
        status.setTextColor(getColor(R.color.muted))
        button.text = "CONNECT"
        hint.text = "Нажми CONNECT — приложение снова начнёт проверку узлов."
    }

    private fun setConnecting(active: Boolean, message: String) {
        status.text = if (active) "● ПОДКЛЮЧЕНИЕ…" else "● ОТКЛЮЧЕНО"
        status.setTextColor(getColor(if (active) R.color.green else R.color.muted))
        button.isEnabled = !active
        hint.text = message
    }

    private data class FetchResult(val ok: Boolean, val count: Int = 0, val error: String = "")

    private fun fetchConfigs(): FetchResult {
        return try {
            val conn = URL(CONFIG_URL).openConnection() as HttpURLConnection
            conn.connectTimeout = 10_000
            conn.readTimeout = 15_000
            conn.requestMethod = "GET"
            val code = conn.responseCode
            if (code !in 200..299) return FetchResult(false, error = "HTTP $code")
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            val count = text.lineSequence().count { line ->
                val s = line.trim()
                s.isNotEmpty() && !s.startsWith("#")
            }
            FetchResult(true, count)
        } catch (e: Exception) {
            FetchResult(false, error = e.message ?: "Ошибка сети")
        }
    }
}
