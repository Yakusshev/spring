package com.yakushev.spring.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.presentation.game.GameViewModel


@Composable
private fun BoxWithConstraintsScope.Buttons(viewModel: GameViewModel) {
    ConstraintLayout(
        modifier = Modifier.align(Alignment.BottomCenter)
    ) {
        val (buttonTop, buttonDown, buttonRight, buttonLeft) = createRefs()

        val margin = 32.dp

        Box(
            modifier = Modifier
                .constrainAs(buttonDown) {
                    bottom.linkTo(parent.bottom, margin)
                    centerHorizontallyTo(parent)
                }
                .directionButtonModifier(viewModel, DirectionEnum.DOWN)
        )
        Box(
            modifier = Modifier
                .constrainAs(buttonTop) {
                    bottom.linkTo(buttonDown.top, margin)
                    centerHorizontallyTo(parent)
                }
                .directionButtonModifier(viewModel, DirectionEnum.UP)
        )
        Box(
            modifier = Modifier
                .constrainAs(buttonRight) {
                    centerVerticallyTo(parent)
                    start.linkTo(buttonDown.end, margin / 2)
                }
                .directionButtonModifier(viewModel, DirectionEnum.RIGHT)
        )
        Box(
            modifier = Modifier
                .constrainAs(buttonLeft) {
                    centerVerticallyTo(parent)
                    end.linkTo(buttonDown.start, margin / 2)
                }
                .directionButtonModifier(viewModel, DirectionEnum.LEFT)
        )
    }
}

fun Modifier.directionButtonModifier(
    viewModel: GameViewModel,
    direction: DirectionEnum
): Modifier = this
    .rotate(degrees = 45f)
    .size(64.dp)
    .alpha(0.25f)
    .background(color = Color.Red)
    .clickable { viewModel.onDirectionChanged(direction) }