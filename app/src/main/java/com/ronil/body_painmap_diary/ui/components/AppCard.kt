package com.ronil.body_painmap_diary.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    // The exact ambient blue shadow from the design system
    val ambientBlue = Color(0xFF0D47A1).copy(alpha = 0.08f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp, // High elevation for a very soft, spread-out shadow
                shape = RoundedCornerShape(16.dp),
                ambientColor = ambientBlue,
                spotColor = ambientBlue
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        // Set Card's internal elevation to 0 so we don't get double shadows
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(content = content)
    }
}