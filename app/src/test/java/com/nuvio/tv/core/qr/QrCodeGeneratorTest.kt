package com.nuvio.tv.core.qr

import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrCodeGeneratorTest {

    private val url = "https://nuvio.tv/device?code=ABC123&nonce=0123456789abcdef"

    @Test
    fun `pixel rendering preserves every encoded module with login margin`() {
        val size = 420
        val matrix = QRCodeWriter().encode(
            url, BarcodeFormat.QR_CODE, size, size,
            mapOf(EncodeHintType.MARGIN to 1)
        )

        val pixels = QrCodeGenerator.toPixels(matrix, size)

        assertEquals(size * size, pixels.size)
        for (y in 0 until size) {
            for (x in 0 until size) {
                val expected = if (matrix[x, y]) Color.BLACK else Color.WHITE
                assertEquals("pixel $x,$y", expected, pixels[y * size + x])
            }
        }
        assertTrue(pixels.any { it == Color.BLACK })
        assertTrue(pixels.any { it == Color.WHITE })
    }

    @Test
    fun `pixel rendering preserves zero margin matrix`() {
        val size = 512
        val matrix = QRCodeWriter().encode(
            url, BarcodeFormat.QR_CODE, size, size,
            mapOf(EncodeHintType.MARGIN to 0)
        )

        val pixels = QrCodeGenerator.toPixels(matrix, size)

        for (y in 0 until size) {
            for (x in 0 until size) {
                val expected = if (matrix[x, y]) Color.BLACK else Color.WHITE
                assertEquals("pixel $x,$y", expected, pixels[y * size + x])
            }
        }
    }
}
