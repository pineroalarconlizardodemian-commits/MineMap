package com.minemap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.Spinner
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

            // Obtener las coordenadas que enviamos desde el mapa
            val lat = intent.getDoubleExtra("LATITUD", 0.0)
            val lon = intent.getDoubleExtra("LONGITUD", 0.0)

            // Ir a la pantalla de carga
            val intentCarga = Intent(this, CargandoActivity::class.java)
            intentCarga.putExtra("EDICION", edicion)
            intentCarga.putExtra("LATITUD", lat)
            intentCarga.putExtra("LONGITUD", lon)
            // (El clima lo guardamos para después)
            
            startActivity(intentCarga)
        }
    }
}
