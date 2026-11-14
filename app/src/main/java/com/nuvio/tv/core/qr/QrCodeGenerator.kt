package com.nuvio.tv.core.qr

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

object QrCodeGenerator {

    fun generate(content: String, size: Int = 512, margin: Int = 4): Bitmap {
        require(content.isNotBlank()) { "QR content must not be blank" }
        require(size >= 128) { "QR size must be at least 128 pixels" }

        val bitMatrix = QRCodeWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            size,
            size,
            mapOf(EncodeHintType.MARGIN to margin.coerceAtLeast(0))
        )
        val pixels = toPixels(bitMatrix, size)

        return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, size, 0, 0, size, size)
        }
    }

    internal fun toPixels(bitMatrix: BitMatrix, size: Int): IntArray {
        require(size > 0) { "QR size must be positive" }
        val pixels = IntArray(size * size) { Color.WHITE }
        for (y in 0 until size) {
            val rowOffset = y * size
            for (x in 0 until size) {
                if (bitMatrix[x, y]) pixels[rowOffset + x] = Color.BLACK
            }
        }
        return pixels
    }
}
