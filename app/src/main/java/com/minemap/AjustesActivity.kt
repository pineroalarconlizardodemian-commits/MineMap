package com.minemap

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AjustesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        val radioJava = findViewById<RadioButton>(R.id.radioJava)
        val radioBedrock = findViewById<RadioButton>(R.id.radioBedrock)
        val spinnerClima = findViewById<Spinner>(R.id.spinnerClima)
        val btnGenerar = findViewById<Button>(R.id.btnGenerarMundo)

        // Llenar el Spinner del clima
        val climas = arrayOf("Automático", "Despejado", "Lluvia", "Tormenta")
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, climas)
        spinnerClima.adapter = adapter

        btnGenerar.setOnClickListener {
            // Ver qué edición eligió el usuario
            val edicion = if (radioJava.isChecked) "Java" else "Bedrock"
            val clima = spinnerClima.selectedItem.toString()

            // Aquí llamas a tu generador de Java (MundoGenerator)
            val lat = intent.getDoubleExtra("LATITUD", 0.0)
            val lon = intent.getDoubleExtra("LONGITUD", 0.0)
            
            val mensaje = MundoGenerator.generarMundo(lat, lon, edicion)
            
            Toast.makeText(this, "Edición: $edicion | Clima: $clima\n$mensaje", Toast.LENGTH_LONG).show()
            
            // Cerrar esta pantalla y volver al mapa
            finish()
        }
    }
}
