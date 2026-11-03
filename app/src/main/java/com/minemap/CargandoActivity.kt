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
import java.io.File

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
        
        // Simulamos la carga (aquí irá la lógica real de generación de bloques luego)
        val runnable = object : Runnable {
            override fun run() {
                progreso += 2
                progressBar.progress = progreso
                txtPorcentaje.text = "$progreso%"

                if (progreso >= 100) {
                    txtEstado.text = "¡Mundo generado con éxito!"
                    btnExportar.visibility = View.VISIBLE
                    
                    // Configurar el botón de exportar
                    configurarBotonExportar()
                } else {
                    // Cambiar textos según el progreso
                    if (progreso == 30) txtEstado.text = "Creando edificios..."
                    if (progreso == 60) txtEstado.text = "Añadiendo vegetación..."
                    if (progreso == 85) txtEstado.text = "Empaquetando archivo..."
                    
                    handler.postDelayed(this, 50) // Velocidad de la barra
                }
            }
        }
        handler.post(runnable)
    }

    private fun configurarBotonExportar() {
        btnExportar.setOnClickListener {
            // Llamamos a Java para que nos devuelva el archivo generado
            val archivoGenerado = MundoGenerator.generarMundo(lat, lon, edicion)

            if (edicion == "Java") {
                // Abrir el explorador de archivos
                Toast.makeText(this, "Mundo de Java guardado en Descargas (.zip)", Toast.LENGTH_LONG).show()
                
                // Intentar abrir el administrador de archivos
                val intent = Intent(Intent.ACTION_VIEW)
                intent.type = "resource/folder"
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Busca el archivo en tu carpeta de Descargas", Toast.LENGTH_LONG).show()
                }

            } else {
                // Para Bedrock, intentamos abrir el menú de "Abrir con..." directamente
                Toast.makeText(this, "Abriendo Minecraft Bedrock...", Toast.LENGTH_SHORT).show()
                
                /* 
                 * AQUÍ IRÁ LA LÓGICA REAL CUANDO TENGAMOS EL ARCHIVO .MCWORLD
                 * Ejemplo:
                 * val file = File(getExternalFilesDir(null), "mundo.mcworld")
                 * val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
                 * val intent = Intent(Intent.ACTION_VIEW).apply {
                 *     setDataAndType(uri, "application/octet-stream")
                 *     addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                 * }
                 * startActivity(Intent.createChooser(intent, "Abrir con..."))
                 */
            }
        }
    }
}
