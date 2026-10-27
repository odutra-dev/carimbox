package com.dutradev.carimbosocian.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dutradev.carimbosocian.R
import com.dutradev.carimbosocian.ui.theme.StatusDelivered
import com.dutradev.carimbosocian.ui.theme.StatusPending

@Composable
fun StatusBadge(
    isDelivered: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isDelivered) StatusDelivered.copy(alpha = 0.15f) else StatusPending.copy(alpha = 0.15f)
    val textColor = if (isDelivered) StatusDelivered else StatusPending
    val label = if (isDelivered) stringResource(R.string.concluidos) else stringResource(R.string.pendentes)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
