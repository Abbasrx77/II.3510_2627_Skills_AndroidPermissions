package fr.isep.permdemo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import fr.isep.permdemo.R
import fr.isep.permdemo.permissions.EventLogger
import fr.isep.permdemo.ui.theme.AndroidGreen
import fr.isep.permdemo.ui.theme.CodeBackground
import fr.isep.permdemo.ui.theme.CodeCardShape
import fr.isep.permdemo.ui.theme.CodeComment
import fr.isep.permdemo.ui.theme.CodeText
import fr.isep.permdemo.ui.theme.CodeTextStyle

@Composable
fun EventLog(log: EventLogger, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(CodeCardShape)
                .background(CodeBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (log.events.isEmpty()) {
                Text(stringResource(R.string.log_empty), style = MaterialTheme.typography.bodyMedium, color = CodeComment)
            }
            log.events.forEach { event ->
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = CodeComment)) { append(event.time) }
                        append("  ")
                        withStyle(SpanStyle(color = AndroidGreen)) { append("${event.step} ·") }
                        append(" ")
                        append(event.message)
                    },
                    style = CodeTextStyle,
                    color = CodeText,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedPillButton(stringResource(R.string.clear_log), onClick = log::clear)
    }
}
