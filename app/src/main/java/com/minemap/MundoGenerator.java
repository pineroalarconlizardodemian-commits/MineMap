package com.minemap;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MundoGenerator {

    public static String generarMundo(Context context, double latitud, double longitud, String edicion) {
        try {
            // 1. Obtener la altura real del terreno (Relieve)
            int alturaReal = obtenerAlturaDelTerreno(latitud, longitud);
            
            // 2. Definir el nombre del archivo
            String nombreArchivo = "MineMap_" + edicion + "_" + (int)latitud + ".zip";
            if (edicion.equals("Bedrock")) {
                nombreArchivo = "MineMap_" + edicion + "_" + (int)latitud + ".mcworld";
            }

            // 3. Crear un texto que incluya la altura (simulando el relieve)
            String contenidoPrueba = "Este es un archivo de prueba de MineMap con RELIEVE.\n" +
                    "Edicion: " + edicion + "\n" +
                    "Latitud: " + latitud + "\n" +
                    "Longitud: " + longitud + "\n" +
                    "Altura del terreno real: " + alturaReal + " metros (aprox " + (alturaReal / 2) + " bloques de Minecraft)";

            // 4. Guardar el archivo en Descargas
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, nombreArchivo);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/octet-stream");
                values.put(MediaStore.Downloads.IS_PENDING, 1);

                ContentResolver resolver = context.getContentResolver();
                Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                Uri item = resolver.insert(collection, values);

                if (item != null) {
                    OutputStream stream = resolver.openOutputStream(item);
                    if (stream != null) {
                        stream.write(contenidoPrueba.getBytes(StandardCharsets.UTF_8));
                        stream.close();
                    }
                    values.clear();
                    values.put(MediaStore.Downloads.IS_PENDING, 0);
                    resolver.update(item, values, null, null);
                }
            } else {
                java.io.File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                java.io.File file = new java.io.File(downloadDir, nombreArchivo);
                java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
                fos.write(contenidoPrueba.getBytes(StandardCharsets.UTF_8));
                fos.close();
            }
            
            return "¡Éxito! Relieve obtenido (" + alturaReal + "m). Archivo creado en Descargas: " + nombreArchivo;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al generar: " + e.getMessage();
        }
    }

    // Función para pedirle la altura a una API gratuita
    private static int obtenerAlturaDelTerreno(double lat, double lon) {
        try {
            // Usamos Open-Elevation (gratis y sin API Key)
            String urlStr = "https://api.open-elevation.com/api/v1/lookup?locations=" + lat + "," + lon;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // La respuesta es algo como: {"results":[{"latitude":34.05,"longitude":-118.24,"elevation":86.0}]}
            String res = response.toString();
            int index = res.indexOf("\"elevation\":");
            if (index != -1) {
                String sub = res.substring(index + 12);
                sub = sub.substring(0, sub.indexOf(","));
                return (int) Double.parseDouble(sub.trim());
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo elevación: " + e.getMessage());
        }
        return 0; // Si falla, devuelve 0
    }
                }
