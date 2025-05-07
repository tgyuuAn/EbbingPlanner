package com.tgyuu.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
fun EbbingDialog(
    dialogTop: @Composable () -> Unit,
    dialogBottom: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            colors = cardColors().copy(containerColor = EbbingTheme.colors.background),
            shape = RoundedCornerShape(12.dp),
            modifier = modifier
                .padding(horizontal = 32.dp)
                .widthIn(max = 400.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                dialogTop()
                dialogBottom()
            }
        }
    }
}

@Composable
fun EbbingDialogDefaultTop(
    title: AnnotatedString,
    subText: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 40.dp, bottom = 12.dp),
    ) {
        Text(
            text = title,
            color = EbbingTheme.colors.black,
            textAlign = TextAlign.Center,
            style = EbbingTheme.typography.headingMSB,
        )

        Text(
            text = subText,
            color = EbbingTheme.colors.dark2,
            textAlign = TextAlign.Center,
            style = EbbingTheme.typography.bodySM,
        )
    }
}

@Composable
fun EbbingDialogDefaultTop(
    title: String,
    subText: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 40.dp, bottom = 12.dp),
    ) {
        Text(
            text = title,
            color = EbbingTheme.colors.black,
            style = EbbingTheme.typography.headingMSB,
            textAlign = TextAlign.Center,
        )

        Text(
            text = subText,
            color = EbbingTheme.colors.dark2,
            style = EbbingTheme.typography.bodySM,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun EbbingDialogIconTop(
    @DrawableRes iconId: Int,
    title: String,
    contentDescription: String? = null,
    subText: String? = null,
    descriptionComposable: @Composable (() -> Unit)? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 40.dp, bottom = 8.dp),
    ) {
        Image(
            painter = painterResource(iconId),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(EbbingTheme.colors.black),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .size(40.dp),
        )

        Text(
            text = title,
            color = EbbingTheme.colors.black,
            style = EbbingTheme.typography.headingMSB,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        subText?.let {
            Text(
                text = subText,
                color = EbbingTheme.colors.dark2,
                style = EbbingTheme.typography.bodySM,
                textAlign = TextAlign.Center,
            )
        }

        descriptionComposable?.let {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EbbingTheme.colors.light3)
                    .padding(vertical = 12.dp),
            ) {
                descriptionComposable()
            }
        }
    }
}

@Composable
fun EbbingDialogBottom(
    leftButtonText: String,
    rightButtonText: String,
    onLeftButtonClick: () -> Unit,
    onRightButtonClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 20.dp),
    ) {
        EbbingOutlinedButton(
            label = leftButtonText,
            onClick = onLeftButtonClick,
            modifier = Modifier.weight(1f),
        )

        EbbingSolidButton(
            label = rightButtonText,
            onClick = onRightButtonClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@EbbingPreview
@Composable
fun PreviewDialog1() {
    BasePreview {
        EbbingDialog(
            dialogTop = {
                EbbingDialogDefaultTop(
                    title = AnnotatedString("Default Title"),
                    subText = "This is a default subtitle"
                )
            },
            dialogBottom = {
                EbbingDialogBottom(
                    leftButtonText = "Label",
                    rightButtonText = "Label",
                    onLeftButtonClick = {},
                    onRightButtonClick = {}
                )
            },
            onDismissRequest = {}
        )
    }
}

@EbbingPreview
@Composable
fun PreviewDialog2() {
    BasePreview {
        EbbingDialog(
            dialogTop = {
                EbbingDialogIconTop(
                    iconId = R.drawable.ic_close,
                    title = "Icon Title",
                    descriptionComposable = {
                        Text(
                            text = AnnotatedString("This is an icon subtitle"),
                            color = EbbingTheme.colors.dark3,
                            style = EbbingTheme.typography.bodySM,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(EbbingTheme.colors.light3)
                        )
                    },
                    contentDescription = "Icon Description"
                )
            },
            dialogBottom = {
                EbbingDialogBottom(
                    leftButtonText = "Label",
                    rightButtonText = "Label",
                    onLeftButtonClick = {},
                    onRightButtonClick = {}
                )
            },
            onDismissRequest = {}
        )
    }
}
