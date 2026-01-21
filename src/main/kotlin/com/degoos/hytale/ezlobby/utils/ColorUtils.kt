package com.degoos.hytale.ezlobby.utils

/**
 * Utility functions for color manipulation and conversion.
 */
object ColorUtils {

    /**
     * Converts RGB hex color to HSL color space.
     *
     * @param hexColor Hex color string (with or without # prefix, supports RGB and RGBA)
     * @return Triple of (Hue: 0-360, Saturation: 0-1, Lightness: 0-1)
     */
    fun hexToHsl(hexColor: String): Triple<Double, Double, Double> {
        val hex = hexColor.removePrefix("#")
        val r = hex.substring(0, 2).toInt(16) / 255.0
        val g = hex.substring(2, 4).toInt(16) / 255.0
        val b = hex.substring(4, 6).toInt(16) / 255.0

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min

        // Calculate Lightness
        val l = (max + min) / 2.0

        // Calculate Saturation
        val s = if (delta == 0.0) 0.0 else delta / (1.0 - Math.abs(2.0 * l - 1.0))

        // Calculate Hue
        val h = when {
            delta == 0.0 -> 0.0
            max == r -> 60.0 * (((g - b) / delta) % 6.0)
            max == g -> 60.0 * (((b - r) / delta) + 2.0)
            else -> 60.0 * (((r - g) / delta) + 4.0)
        }.let { if (it < 0) it + 360.0 else it }

        return Triple(h, s, l)
    }

    /**
     * Converts HSL color to RGB hex color.
     *
     * @param h Hue (0-360)
     * @param s Saturation (0-1)
     * @param l Lightness (0-1)
     * @param alpha Alpha channel (0-255), defaults to 255 (fully opaque)
     * @return Hex color string in format #RRGGBBAA
     */
    fun hslToHex(h: Double, s: Double, l: Double, alpha: Int = 255): String {
        val c = (1.0 - Math.abs(2.0 * l - 1.0)) * s
        val x = c * (1.0 - Math.abs((h / 60.0) % 2.0 - 1.0))
        val m = l - c / 2.0

        val (rPrime, gPrime, bPrime) = when {
            h < 60 -> Triple(c, x, 0.0)
            h < 120 -> Triple(x, c, 0.0)
            h < 180 -> Triple(0.0, c, x)
            h < 240 -> Triple(0.0, x, c)
            h < 300 -> Triple(x, 0.0, c)
            else -> Triple(c, 0.0, x)
        }

        val r = ((rPrime + m) * 255).toInt().coerceIn(0, 255)
        val g = ((gPrime + m) * 255).toInt().coerceIn(0, 255)
        val b = ((bPrime + m) * 255).toInt().coerceIn(0, 255)

        return String.format("#%02X%02X%02X%02X", r, g, b, alpha)
    }

    /**
     * Applies color theory to generate UI button state colors from a base hex RGB color.
     * Uses HSL color space for perceptually accurate transformations.
     *
     * @param baseColorHex Base color in hex format (with or without # prefix)
     * @return Map of state names to hex colors (Default, Hovered, Pressed, Disabled)
     */
    fun generateButtonStateColors(baseColorHex: String): Map<String, String> {
        val (h, s, l) = hexToHsl(baseColorHex)

        return mapOf(
            // Default: Base color with 60% opacity for subtle presence
            "Default" to hslToHex(h, s, l, 153), // ~60% alpha (0.6 * 255)

            // Hovered: Slightly lighter and more saturated with higher opacity for feedback
            "Hovered" to hslToHex(
                h,
                (s * 1.1).coerceIn(0.0, 1.0),
                (l * 1.15).coerceIn(0.0, 1.0),
                204 // ~80% alpha
            ),

            // Pressed: Darker and more saturated, fully opaque for strong visual feedback
            "Pressed" to hslToHex(
                h,
                (s * 1.15).coerceIn(0.0, 1.0),
                (l * 0.85).coerceIn(0.0, 1.0),
                255 // 100% alpha
            ),

            // Disabled: Desaturated and lighter with low opacity for inactive appearance
            "Disabled" to hslToHex(
                h,
                s * 0.3,
                (l * 1.2).coerceIn(0.0, 1.0),
                102 // ~40% alpha
            )
        )
    }
}
