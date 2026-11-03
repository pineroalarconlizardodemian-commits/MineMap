package com.minemap

import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
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
        
        // Inicializar MapLibre (Mapa gratuito)
        MapLibre.getInstance(this)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        
        searchBar = findViewById(R.id.searchBar)
        btnOpenOptions = findViewById(R.id.btnOpenOptions)

        // Configurar el mapa
        mapView.getMapAsync { map ->
            // Usamos un estilo de mapa gratuito de OpenStreetMap
            map.setStyle("https://tiles.openfreemap.org/styles/liberty") { style ->
                
                // Mover la cámara a Los Ángeles
                map.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(latitudSeleccionada, longitudSeleccionada))
                    .zoom(15.0)
                    .build()

                // Detectar cuando el usuario mueve el mapa
                map.addOnCameraIdleListener {
                    val centro = map.cameraPosition.target
                    latitudSeleccionada = centro.latitude
                    longitudSeleccionada = centro.longitude
                }
            }
        }

        // Botón para generar el mundo
        btnOpenOptions.setOnClickListener {
            val mensaje = MundoGenerator.generarMundo(latitudSeleccionada, longitudSeleccionada, "Java")
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
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
                
                // Mueve el mapa suavemente al lugar buscado
                mapView.getMapAsync { map ->
                    map.animateCamera(
                        org.maplibre.android.camera.CameraUpdateFactory.newCameraPosition(
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
