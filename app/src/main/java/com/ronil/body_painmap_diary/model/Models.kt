package com.ronil.body_painmap_diary.model
import androidx.compose.ui.graphics.Color

/**
 * The 5 pain levels (0 to 4) from the requirements document.
 * Each level carries:
 *   - value : its numeric value (used for saving and averaging)
 *   - label : the human-readable name (used on screen and by the speaker)
 *   - fill  : the color of the number circle
 *   - onFill: the color of the digit inside the circle (for contrast)
 */
enum class PainLevel(
    val value: Int,
    val label: String,
    val fill: Color,
    val onFill: Color
) {
    None    (0, "No Pain",   Color(0xFF2E7D4F), Color.White),
    Little  (1, "A Little",  Color(0xFF7CB342), Color(0xFF1B2A2A)),
    Some    (2, "Some Pain", Color(0xFFF9A825), Color(0xFF1B2A2A)),
    ALot    (3, "A Lot",     Color(0xFFE65100), Color.White),
    VeryBad (4, "Very Bad",  Color(0xFFC62828), Color.White)
}