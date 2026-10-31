package pe.bienesraices.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ZipSeguroTest {
    @TempDir
    Path temporal;

    @Test
    void comprimeYExtraeConSumasDeVerificacion() throws Exception {
        byte[] contenido = "reporte".getBytes(StandardCharsets.UTF_8);
        Map<String, byte[]> archivos = new LinkedHashMap<>();
        archivos.put("reporte.xlsx", contenido);

        byte[] zip = ZipSeguro.comprimir(archivos);
        List<Path> extraidos = ZipSeguro.extraer(new ByteArrayInputStream(zip), temporal);

        assertEquals(2, extraidos.size());
        String sumas = Files.readString(temporal.resolve("SHA256SUMS.txt"));
        assertTrue(sumas.contains(DigestUtils.sha256Hex(contenido)));
    }

    @Test
    void bloqueaZipSlip() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(bytes)) {
            zip.putNextEntry(new ZipEntry("../../Windows/malicioso.bat"));
            zip.write("echo atacado".getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }
        assertThrows(SecurityException.class,
                () -> ZipSeguro.extraer(new ByteArrayInputStream(bytes.toByteArray()), temporal.resolve("destino")));
    }
}
