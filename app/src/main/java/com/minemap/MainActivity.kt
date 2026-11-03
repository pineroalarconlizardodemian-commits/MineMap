package com.minemap

import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var searchBar: EditText
    private lateinit var btnOpenOptions: Button

    // Coordenadas iniciales (Los Ángeles)
    private val losAngeles = LatLng(34.0522, -118.2437)
    
    // Aquí guardaremos las coordenadas exactas del puntito
    private var latitudSeleccionada = 34.0522
    private var longitudSeleccionada = -118.2437

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchBar = findViewById(R.id.searchBar)
        btnOpenOptions = findViewById(R.id.btnOpenOptions)

        // Cargar el mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Botón para generar el mundo
        btnOpenOptions.setOnClickListener {
            // Aquí llamamos al código Java
            val mensaje = MundoGenerator.generarMundo(latitudSeleccionada, longitudSeleccionada, "Java")
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 1. Mover la cámara a Los Ángeles inicialmente
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(losAngeles, 15f))

        // 2. Detectar cuando el usuario mueve el mapa para actualizar las coordenadas del centro
        mMap.setOnCameraIdleListener {
            val centroPantalla = mMap.cameraPosition.target
            latitudSeleccionada = centroPantalla.latitude
            longitudSeleccionada = centroPantalla.longitude
        }

        // 3. Lógica del Buscador (Geocoder)
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
                val coordenadas = LatLng(direccion.latitude, direccion.longitude)
                
                // Mueve el mapa al lugar buscado
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 16f))
                searchBar.clearFocus()
            } else {
                Toast.makeText(this, "Lugar no encontrado", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
        }
    }
}
