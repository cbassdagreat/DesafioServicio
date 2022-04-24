package cbass.desafioservicio.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentCallbacks
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import cbass.desafioservicio.MainActivity
import cbass.desafioservicio.R

class ContadorService : Service() {

    var contador = 0
    private var mHandler: Handler = Handler(Looper.myLooper()!!)
    private lateinit var mRunnable: Runnable

    companion object {
        var running = false
        private lateinit var handleCallback: Handler
        fun startCount(context: Context, mensaje: String, handler: Handler) {
            val startIntent = Intent(context, ContadorService::class.java)
            startIntent.putExtra("inputExtra", mensaje)
            ContextCompat.startForegroundService(context, startIntent)
            running = true
            handleCallback = handler
        }

        fun stopCount(context: Context) {
            val stopIntent = Intent(context, ContadorService::class.java)
            context.stopService(stopIntent)
            running = false
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

     override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("FOREGROUND SERVICIO", "INICIADO")
        val input = intent?.getStringExtra("inputExtra") ?: ""
        crearNotificacion(input)
        runTask()
        return START_NOT_STICKY
    }

    override fun onCreate() {
        Log.i("FOREGROUND SERVICIO", "CREADO")
        super.onCreate()
    }

    override fun onDestroy() {
        Log.i("FOREGROUND SERVICIO", "DESTRUIDO")
        mHandler.removeCallbacks(mRunnable)
        super.onDestroy()
    }


    private fun crearNotificacion(input: String) {
        val canal = "CANAl_CONTADOR"
        val nc: NotificationChannel
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nc = NotificationChannel(canal, "Canal servicio", NotificationManager.IMPORTANCE_NONE)
            nc.description = "CONTANDO"
            nm.createNotificationChannel(nc)
        }
        val intento = Intent(this, MainActivity::class.java)
        val pendiente = PendingIntent.getActivity(this, 0, intento, 0)
        val notificacion = NotificationCompat.Builder(this, canal)
            .setContentTitle("CONTADOR!!!!")
            .setContentText("CONTENIDO")
            .setSmallIcon(R.drawable.ic_baseline_timer_24)
            .setContentIntent(pendiente)
            .build()
        startForeground(1, notificacion)

    }


    private fun runTask() {
        val delayTime = 1000 * 7L
        mRunnable = Runnable {
            contador++
            //envío el valor
            notifiyNextEvent()
            mHandler.postDelayed(mRunnable, delayTime)
        }
        mHandler.postDelayed(mRunnable, delayTime)
    }

    private fun notifiyNextEvent() {
        Log.i("CONTADOR SERVICE", "Run Task: $contador")
        val mensaje = handleCallback.obtainMessage(1, "msg")
        mensaje.data.putString("Contador", contador.toString())
        mensaje.sendToTarget()
    }

}