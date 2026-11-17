package fr.isep.permdemo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.isep.permdemo.R
import fr.isep.permdemo.permissions.EventLogger
import fr.isep.permdemo.permissions.LifecycleStep
import fr.isep.permdemo.ui.theme.AndroidGreen
import fr.isep.permdemo.ui.theme.Border
import fr.isep.permdemo.ui.theme.CardShape
import fr.isep.permdemo.ui.theme.ConstantTextStyle
import fr.isep.permdemo.ui.theme.Ink
import fr.isep.permdemo.ui.theme.SurfaceWhite
import fr.isep.permdemo.ui.theme.TextSecondary

private enum class StepState { NOT_REACHED, IN_PROGRESS, PASSED }

@Composable
fun LifecycleStepper(
    steps: List<LifecycleStep>,
    log: EventLogger,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(SurfaceWhite)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        steps.forEachIndexed { index, step ->
            val passedAt = log.passedAt[step.number]
            val state = when {
                log.inProgress == step.number -> StepState.IN_PROGRESS
                passedAt != null -> StepState.PASSED
                else -> StepState.NOT_REACHED
            }
            val nextPassed = steps.getOrNull(index + 1)?.let { log.passedAt[it.number] != null } == true
            StepRow(
                step = step,
                state = state,
                time = passedAt,
                accent = accent,
                isLast = index == steps.lastIndex,
                lineGreen = state == StepState.PASSED && nextPassed,
            )
        }
    }
}

@Composable
private fun StepRow(
    step: LifecycleStep,
    state: StepState,
    time: String?,
    accent: Color,
    isLast: Boolean,
    lineGreen: Boolean,
) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            StepBullet(step.number, state, accent)
            if (!isLast) {
                Box(
                    Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(if (lineGreen) AndroidGreen else Border),
                )
            }
        }
        Column(
            Modifier
                .weight(1f)
                .padding(start = 14.dp, top = 2.dp, bottom = if (isLast) 0.dp else 18.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = stringResource(step.title),
                    style = MaterialTheme.typography.titleSmall,
                    color = if (state == StepState.NOT_REACHED) TextSecondary else Ink,
                    modifier = Modifier.weight(1f),
                )
                val trailing = when (state) {
                    StepState.IN_PROGRESS -> stringResource(R.string.step_in_progress)
                    else -> time
                }
                if (trailing != null) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        trailing,
                        style = ConstantTextStyle,
                        color = if (state == StepState.IN_PROGRESS) accent else TextSecondary,
                    )
                }
            }
            if (step.api != null) {
                Text(step.api, style = ConstantTextStyle, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun StepBullet(number: Int, state: StepState, accent: Color) {
    val base = Modifier.size(28.dp).clip(CircleShape)
    val styled = when (state) {
        StepState.PASSED -> base.background(AndroidGreen)
        StepState.IN_PROGRESS -> base.background(SurfaceWhite).border(2.dp, accent, CircleShape)
        StepState.NOT_REACHED -> base.background(SurfaceWhite).border(1.dp, Border, CircleShape)
    }
    Box(styled, contentAlignment = Alignment.Center) {
        Text(
            number.toString(),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = when (state) {
                StepState.PASSED -> Ink
                StepState.IN_PROGRESS -> accent
                StepState.NOT_REACHED -> TextSecondary
            },
        )
    }
}
