package com.minemap;

public class MundoGenerator {

    /**
     * Este método recibe las coordenadas del mapa y el tipo de edición (Java o Bedrock).
     * Por ahora, solo devuelve un mensaje de confirmación.
     */
    public static String generarMundo(double latitud, double longitud, String edicion) {
        // Aquí irá la lógica para:
        // 1. Convertir Lat/Lon a bloques de Minecraft.
        // 2. Crear los archivos de la región (.mca).
        // 3. Comprimir en .zip o .mcworld.

        String info = String.format("Generando mundo para %s en:\nLat: %.5f\nLon: %.5f", edicion, latitud, longitud);
        
        // Imprimimos en consola para ver que el puente funciona
        System.out.println("MineMap Java Generator: " + info);
        
        return "¡Éxito! " + info;
    }
}
