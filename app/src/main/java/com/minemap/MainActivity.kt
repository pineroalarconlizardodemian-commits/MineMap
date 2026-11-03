package com.minemap

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var searchBar: EditText
    private lateinit var btnOpenOptions: Button

    // Coordenadas iniciales de Los Ángeles
    private var latitudSeleccionada = 34.0522
    private var longitudSeleccionada = -118.2437

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar MapLibre
        MapLibre.getInstance(this)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        
        searchBar = findViewById(R.id.searchBar)
        btnOpenOptions = findViewById(R.id.btnOpenOptions)

        // Configurar el mapa
        mapView.getMapAsync { map ->
            map.setStyle("https://tiles.openfreemap.org/styles/liberty") { style ->
                
                // Mover la cámara a Los Ángeles
                map.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(latitudSeleccionada, longitudSeleccionada))
                    .zoom(15.0)
                    .build()

                // Detectar cuando el usuario mueve el mapa
                map.addOnCameraIdleListener {
                    val centro = map.cameraPosition.target
                    if (centro != null) {
                        latitudSeleccionada = centro.latitude
                        longitudSeleccionada = centro.longitude
                    }
                }
            }
        }

        // BOTÓN ACTUALIZADO: Abre la pantalla de Ajustes
        btnOpenOptions.setOnClickListener {
            val intent = Intent(this, AjustesActivity::class.java)
            // Enviamos las coordenadas donde está el puntito a la nueva pantalla
            intent.putExtra("LATITUD", latitudSeleccionada)
            intent.putExtra("LONGITUD", longitudSeleccionada)
            startActivity(intent)
        }

        // Lógica del Buscador (Geocoder)
        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val lugar = searchBar.text.toString()
                if (lugar.isNotEmpty()) {
                    buscarLugar(lugar)
                }
                true
            } else {
                false
            }
        }
    }

    private fun buscarLugar(nombreLugar: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val direcciones = geocoder.getFromLocationName(nombreLugar, 1)
            if (!direcciones.isNullOrEmpty()) {
                val direccion = direcciones[0]
                val nuevasCoordenadas = LatLng(direccion.latitude, direccion.longitude)
                
                mapView.getMapAsync { map ->
                    map.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder().target(nuevasCoordenadas).zoom(16.0).build()
                        ), 
                        1000 // 1 segundo de animación
                    )
                }
                searchBar.clearFocus()
            } else {
                Toast.makeText(this, "Lugar no encontrado", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Ciclo de vida obligatorio para MapLibre ---
    override fun onStart() { super.onStart(); mapView.onStart() }
    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { mapView.onPause(); super.onPause() }
    override fun onStop() { mapView.onStop(); super.onStop() }
    override fun onDestroy() { mapView.onDestroy(); super.onDestroy() }
    override fun onLowMemory() { super.onLowMemory(); mapView.onLowMemory() }
}
