package fr.isep.permdemo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import fr.isep.permdemo.ui.theme.Border
import fr.isep.permdemo.ui.theme.CardShape
import fr.isep.permdemo.ui.theme.Ink
import fr.isep.permdemo.ui.theme.SurfaceWhite

@Composable
fun ResultCard(text: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(SurfaceWhite)
            .border(1.dp, Border, CardShape)
            .padding(20.dp),
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge, color = Ink)
    }
}

@Composable
fun PhotoResult(image: ImageBitmap, description: String, fillWidth: Boolean = true, modifier: Modifier = Modifier) {
    val size = if (fillWidth) Modifier.fillMaxWidth().heightIn(max = 320.dp) else Modifier.height(200.dp)
    Image(
        bitmap = image,
        contentDescription = description,
        contentScale = if (fillWidth) ContentScale.Crop else ContentScale.Fit,
        modifier = modifier
            .then(size)
            .clip(CardShape)
            .border(1.dp, Border, CardShape),
    )
}
