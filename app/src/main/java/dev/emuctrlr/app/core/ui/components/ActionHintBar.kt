package dev.emuctrlr.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/*
le rendu UI

taille icône
taille texte
spacing
layout
apparence visuelle
 */

@Composable
fun ActionHintBar(
    hints: List<ActionHint>,
    controllerHintStyle: ControllerHintStyle = ControllerHintStyle.GENERIC,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        hints.forEachIndexed { index, hint ->
            ActionHintItem(
                hint = hint,
                controllerHintStyle = controllerHintStyle
            )

            if (index < hints.lastIndex) {
                Spacer(Modifier.width(24.dp)) // espace entre chaque hint
            }
        }
    }
}

@Composable
private fun ActionHintItem(
    hint: ActionHint,
    controllerHintStyle: ControllerHintStyle
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(
                id = ActionIconResolver.iconRes(hint.action, controllerHintStyle)
            ),
            contentDescription = hint.label,
            tint = MaterialTheme.colorScheme.onSurface.copy(0.8f),
            modifier = Modifier.size(18.dp)
        )

        Spacer(Modifier.width(5.dp)) // espace entre l'icone et le label

        Text(
            text = hint.label,
            color = MaterialTheme.colorScheme.onSurface.copy(0.8f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}