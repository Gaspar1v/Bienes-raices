package pe.bienesraices.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.digest.DigestUtils;

public final class ZipSeguro {
    public static final int MAX_ENTRADAS = 200;
    public static final long MAX_BYTES_TOTALES = 50L * 1024 * 1024;

    private ZipSeguro() {
    }

    public static byte[] comprimir(Map<String, byte[]> archivos) throws IOException {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        StringBuilder sumas = new StringBuilder();
        try (ZipOutputStream zip = new ZipOutputStream(salida)) {
            for (Map.Entry<String, byte[]> archivo : archivos.entrySet()) {
                zip.putNextEntry(new ZipEntry(archivo.getKey()));
                zip.write(archivo.getValue());
                zip.closeEntry();
                sumas.append(DigestUtils.sha256Hex(archivo.getValue())).append("  ").append(archivo.getKey()).append('\n');
            }
            zip.putNextEntry(new ZipEntry("SHA256SUMS.txt"));
            zip.write(sumas.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            zip.closeEntry();
        }
        return salida.toByteArray();
    }

    public static List<Path> extraer(InputStream entrada, Path destino) throws IOException {
        Path base = destino.toAbsolutePath().normalize();
        Files.createDirectories(base);
        List<Path> extraidos = new ArrayList<>();
        long totalBytes = 0;
        try (ZipInputStream zip = new ZipInputStream(entrada)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (extraidos.size() >= MAX_ENTRADAS) {
                    throw new SecurityException("El zip supera el máximo de " + MAX_ENTRADAS + " entradas");
                }
                Path objetivo = base.resolve(entry.getName()).normalize();
                if (!objetivo.startsWith(base)) {
                    throw new SecurityException("Entrada zip fuera del directorio destino: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(objetivo);
                    continue;
                }
                Files.createDirectories(objetivo.getParent());
                try (OutputStream out = Files.newOutputStream(objetivo)) {
                    byte[] buffer = new byte[8192];
                    int leidos;
                    while ((leidos = zip.read(buffer)) > 0) {
                        totalBytes += leidos;
                        if (totalBytes > MAX_BYTES_TOTALES) {
                            throw new SecurityException("El contenido descomprimido supera el límite permitido");
                        }
                        out.write(buffer, 0, leidos);
                    }
                }
                extraidos.add(objetivo);
            }
        }
        return extraidos;
    }
}
