package com.minemap

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import java.io.File

class MundosActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var tabLayout: TabLayout

    // Lista de archivos simulada (después la conectaremos a la carpeta real)
    private val todosLosMundos = listOf(
        "Los_Angeles_Java.zip",
        "Baldwin_Street_Java.zip",
        "Nueva_York_Bedrock.mcworld"
    )
    
    private var mundosFiltrados = todosLosMundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mundos)

        listView = findViewById(R.id.recyclerViewMundos) // Usamos el ID del layout
        tabLayout = findViewById(R.id.tabLayout)

        actualizarLista(todosLosMundos)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> actualizarLista(todosLosMundos.sorted()) // Todos
                    1 -> actualizarLista(todosLosMundos.filter { it.endsWith(".zip") }.sorted()) // Java
                    2 -> actualizarLista(todosLosMundos.filter { it.endsWith(".mcworld") }.sorted()) // Bedrock
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Al tocar un mundo de la lista
        listView.setOnItemClickListener { _, _, position, _ ->
            val mundoSeleccionado = mundosFiltrados[position]
            if (mundoSeleccionado.endsWith(".mcworld")) {
                // Para Bedrock: Abrir con Minecraft
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse("file:///ruta/falsa/$mundoSeleccionado"), "application/octet-stream")
                try {
                    startActivity(Intent.createChooser(intent, "Abrir con..."))
                } catch (e: Exception) {
                    Toast.makeText(this, "No se encontró Minecraft instalado", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Para Java: Descargar a la carpeta de Descargas
                Toast.makeText(this, "Descargando $mundoSeleccionado a Descargas...", Toast.LENGTH_LONG).show()
                // Aquí iría el código para copiar el archivo a Descargas
            }
        }
    }

    private fun actualizarLista(lista: List<String>) {
        mundosFiltrados = lista
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lista)
        listView.adapter = adapter
    }
}
