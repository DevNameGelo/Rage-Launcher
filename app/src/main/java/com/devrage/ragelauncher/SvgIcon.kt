package com.devrage.ragelauncher

import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.caverock.androidsvg.SVG

@Composable
fun SvgIcon(asset: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
                setImageDrawable(SVG.getFromAsset(context.assets, "icons/$asset.svg").renderToPicture().let {
                    android.graphics.drawable.PictureDrawable(it)
                })
            }
        }
    )
}
