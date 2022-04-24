package cbass.desafioservicio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import cbass.desafioservicio.databinding.ActivityMainBinding
import cbass.desafioservicio.service.ContadorService

class MainActivity : AppCompatActivity(), Handler.Callback {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding)
        {
            btnIO.setOnClickListener{
                if(ContadorService.running)
                {
                    ContadorService.stopCount(this@MainActivity)
                    btnIO.text = "START"
                }
                else
                {
                    ContadorService.startCount(this@MainActivity, "Iniciado", Handler(mainLooper,this@MainActivity))
                    btnIO.text = "STOP"
                }
            }

        }
    }

    override fun handleMessage(msg: Message): Boolean {
        val contador = msg.data.getString("Contador")
        binding.tvCont.text=contador.toString()
        return true
    }


}