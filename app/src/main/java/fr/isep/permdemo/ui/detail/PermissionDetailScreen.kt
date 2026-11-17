package fr.isep.permdemo.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.isep.permdemo.R
import fr.isep.permdemo.permissions.DemoCase
import fr.isep.permdemo.permissions.PermissionStatus
import fr.isep.permdemo.permissions.lifecycle
import fr.isep.permdemo.permissions.rememberPermissionFlow
import fr.isep.permdemo.ui.components.CodeCard
import fr.isep.permdemo.ui.components.CodeLanguage
import fr.isep.permdemo.ui.components.EventLog
import fr.isep.permdemo.ui.components.ExpressiveShape
import fr.isep.permdemo.ui.components.LifecycleStepper
import fr.isep.permdemo.ui.components.StatusChip
import fr.isep.permdemo.ui.theme.Background
import fr.isep.permdemo.ui.theme.Border
import fr.isep.permdemo.ui.theme.CardShape
import fr.isep.permdemo.ui.theme.ConstantTextStyle
import fr.isep.permdemo.ui.theme.Ink
import fr.isep.permdemo.ui.theme.SurfaceWhite
import fr.isep.permdemo.ui.theme.TextSecondary
import fr.isep.permdemo.ui.theme.colors

@Composable
fun PermissionDetailScreen(case: DemoCase, onBack: () -> Unit) {
    val flow = rememberPermissionFlow(case)
    val accent = case.type.colors.strong
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
    ) {
        DetailHeader(case, flow.status, onBack)
        Column(
            Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            Section(R.string.section_action) {
                DemoActions(case, flow)
            }
            Section(R.string.section_lifecycle) {
                LifecycleStepper(case.type.lifecycle, flow.log, accent)
                SubTitle(R.string.section_log)
                EventLog(flow.log)
            }
            Section(R.string.section_files) {
                case.files.forEach { file ->
                    CodeCard(
                        path = file.path,
                        role = stringResource(file.role),
                        code = file.code,
                        language = if (file.path.endsWith(".xml")) CodeLanguage.XML else CodeLanguage.KOTLIN,
                    )
                }
                SystemSideCard(case)
            }
        }
    }
}

@Composable
private fun Section(title: Int, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(title), style = MaterialTheme.typography.headlineSmall, color = Ink)
        content()
    }
}

@Composable
private fun SubTitle(title: Int) {
    Text(
        stringResource(title),
        style = MaterialTheme.typography.titleMedium,
        color = Ink,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
private fun SystemSideCard(case: DemoCase) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(SurfaceWhite)
            .border(1.dp, Border, CardShape)
            .padding(20.dp),
    ) {
        Text(stringResource(R.string.system_side), style = MaterialTheme.typography.titleMedium, color = Ink)
        Spacer(Modifier.height(4.dp))
        Text(stringResource(R.string.system_side_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(case.systemPath), style = MaterialTheme.typography.bodyLarge, color = Ink)
    }
}

@Composable
private fun DetailHeader(case: DemoCase, status: PermissionStatus, onBack: () -> Unit) {
    val colors = case.type.colors
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(colors.container),
    ) {
        ExpressiveShape(
            shape = case.shape,
            color = colors.strong,
            size = 220.dp,
            modifier = Modifier.align(Alignment.TopEnd).offset(x = 80.dp, y = (-40).dp),
        )
        Column(Modifier.statusBarsPadding().padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)) {
            Text(
                text = stringResource(R.string.back),
                style = MaterialTheme.typography.labelLarge,
                color = Ink,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onBack)
                    .padding(vertical = 8.dp, horizontal = 4.dp),
            )
            Spacer(Modifier.height(32.dp))
            Text(
                stringResource(case.type.label).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.strong,
            )
            Text(stringResource(case.title), style = MaterialTheme.typography.displaySmall, color = Ink)
            if (case.constants.isNotEmpty()) {
                Text(case.constants.joinToString(" · "), style = ConstantTextStyle, color = TextSecondary)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.who_decides_label),
                style = MaterialTheme.typography.labelMedium,
                color = colors.strong,
            )
            Text(stringResource(case.type.whoDecides), style = MaterialTheme.typography.bodyLarge, color = Ink)
            Spacer(Modifier.height(16.dp))
            Row { StatusChip(status) }
        }
    }
}
