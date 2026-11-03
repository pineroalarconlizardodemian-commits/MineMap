package com.minemap;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MundoGenerator {

    public static String generarMundo(Context context, double latitud, double longitud, String edicion) {
        // Nombre del archivo según la edición
        String nombreArchivo = "MineMap_" + edicion + "_" + (int)latitud + ".zip";
        if (edicion.equals("Bedrock")) {
            nombreArchivo = "MineMap_" + edicion + "_" + (int)latitud + ".mcworld";
        }

        String contenidoPrueba = "Este es un archivo de prueba de MineMap.\nEdicion: " + edicion + "\nLatitud: " + latitud + "\nLongitud: " + longitud;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 o superior (Usa MediaStore para guardar en Descargas)
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
                // Android 9 o inferior
                java.io.File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                java.io.File file = new java.io.File(downloadDir, nombreArchivo);
                java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
                fos.write(contenidoPrueba.getBytes(StandardCharsets.UTF_8));
                fos.close();
            }
            return "¡Éxito! Archivo real creado en Descargas: " + nombreArchivo;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al guardar: " + e.getMessage();
        }
    }
}
