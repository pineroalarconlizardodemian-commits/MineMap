package com.minemap

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CargandoActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var txtPorcentaje: TextView
    private lateinit var txtEstado: TextView
    private lateinit var btnExportar: Button
    
    private var progreso = 0
    private var edicion = "Java"
    private var lat = 0.0
    private var lon = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cargando)

        progressBar = findViewById(R.id.progressBar)
        txtPorcentaje = findViewById(R.id.txtPorcentaje)
        txtEstado = findViewById(R.id.txtEstado)
        btnExportar = findViewById(R.id.btnExportar)

        // Recibir datos de la pantalla de ajustes
        edicion = intent.getStringExtra("EDICION") ?: "Java"
        lat = intent.getDoubleExtra("LATITUD", 0.0)
        lon = intent.getDoubleExtra("LONGITUD", 0.0)

        iniciarCarga()
    }

    private fun iniciarCarga() {
        val handler = Handler(Looper.getMainLooper())
        
        val runnable = object : Runnable {
            override fun run() {
                progreso += 2
                progressBar.progress = progreso
                txtPorcentaje.text = "$progreso%"

                if (progreso >= 100) {
                    txtEstado.text = "¡Mundo generado con éxito!"
                    btnExportar.visibility = View.VISIBLE
                    
                    configurarBotonExportar()
                } else {
                    if (progreso == 30) txtEstado.text = "Creando edificios..."
                    if (progreso == 60) txtEstado.text = "Añadiendo vegetación..."
                    if (progreso == 85) txtEstado.text = "Empaquetando archivo..."
                    
                    handler.postDelayed(this, 50)
                }
            }
        }
        handler.post(runnable)
    }

    private fun configurarBotonExportar() {
        btnExportar.setOnClickListener {
            // Llamamos a Java con TODOS los parámetros: Context, Latitud, Longitud y Edición
            val mensaje = MundoGenerator.generarMundo(this, lat, lon, edicion)
            
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

            // Si es Bedrock, intentamos abrir el menú de "Abrir con..."
            if (edicion == "Bedrock") {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse("content://com.minemap.provider/mundo.mcworld"), "application/octet-stream")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                try {
                    startActivity(Intent.createChooser(intent, "Abrir mundo con..."))
                } catch (e: Exception) {
                    Toast.makeText(this, "El archivo se guardó en Descargas, ábrelo desde tu gestor de archivos", Toast.LENGTH_LONG).show()
                }
            } else {
                // Si es Java, abrimos el explorador de archivos
                Toast.makeText(this, "Ve a tu carpeta de Descargas para extraer el .zip", Toast.LENGTH_LONG).show()
                val intent = Intent(Intent.ACTION_VIEW)
                intent.type = "resource/folder"
                try {
                    startActivity(intent)
                } catch (e: Exception) { }
            }
        }
    }
}
