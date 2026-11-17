package fr.isep.permdemo.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.isep.permdemo.R
import fr.isep.permdemo.permissions.DemoCase
import fr.isep.permdemo.permissions.DemoCatalog
import fr.isep.permdemo.permissions.PermissionPrefs
import fr.isep.permdemo.permissions.PermissionStatus
import fr.isep.permdemo.permissions.readStatus
import fr.isep.permdemo.ui.components.ExpressiveShape
import fr.isep.permdemo.ui.components.PermissionCard
import fr.isep.permdemo.ui.theme.AndroidGreen
import fr.isep.permdemo.ui.theme.Background
import fr.isep.permdemo.ui.theme.ExpressiveShapes
import fr.isep.permdemo.ui.theme.Ink
import fr.isep.permdemo.ui.theme.TextSecondary

@Composable
fun HomeScreen(onOpenCase: (DemoCase) -> Unit) {
    val activity = LocalActivity.current!!
    val prefs = remember { PermissionPrefs(activity) }
    fun readAll() = DemoCatalog.cases.associate { it.id to it.readStatus(activity, prefs) }
    var statuses by remember { mutableStateOf(readAll()) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { statuses = readAll() }
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Background),
        contentPadding = PaddingValues(bottom = 24.dp + bottomInset),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { HomeHeader() }
        items(DemoCatalog.cases, key = { it.id }) { case ->
            PermissionCard(
                case = case,
                status = statuses[case.id] ?: PermissionStatus.NEVER_ASKED,
                onClick = { onOpenCase(case) },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun HomeHeader() {
    Box(Modifier.fillMaxWidth()) {
        ExpressiveShape(
            shape = ExpressiveShapes.Cookie,
            color = AndroidGreen,
            size = 280.dp,
            modifier = Modifier.align(Alignment.TopEnd).offset(x = 120.dp, y = (-100).dp),
        )
        Column(
            Modifier
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 56.dp, bottom = 12.dp),
        ) {
            Text(
                text = stringResource(R.string.home_credits),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.displaySmall,
                color = Ink,
                modifier = Modifier.padding(end = 48.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
            )
        }
    }
}
