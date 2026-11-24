package com.devrage.ragelauncher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.ImageView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Black = Color(0xFF08090D)
private val Panel = Color(0xFF12141B)
private val Panel2 = Color(0xFF191B24)
private val Red = Color(0xFFFF304F)
private val Muted = Color(0xFF9296A5)
private val White = Color(0xFFF5F6FA)

data class AppEntry(val label: String, val packageName: String, val icon: Drawable)

class MainActivity : ComponentActivity() {
    private val refreshTick = mutableIntStateOf(0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(8,9,13)
        window.navigationBarColor = android.graphics.Color.rgb(8,9,13)
        setContent { RageLauncherApp(refreshTick.intValue) }
    }
    override fun onResume() {
        super.onResume()
        refreshTick.intValue++
    }
}

private fun loadApps(context: Context): List<AppEntry> {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
    return pm.queryIntentActivities(intent, 0)
        .mapNotNull { ri ->
            val ai = ri.activityInfo ?: return@mapNotNull null
            try {
                AppEntry(ri.loadLabel(pm).toString(), ai.packageName, ri.loadIcon(pm))
            } catch (_: Exception) { null }
        }
        .distinctBy { it.packageName }
        .sortedBy { it.label.lowercase(Locale.getDefault()) }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RageLauncherApp(refreshKey: Int) {
    val context = LocalContext.current
    val apps = remember(refreshKey) { loadApps(context) }
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) { now = Date(); delay(60_000) }
    }
    var query by remember { mutableStateOf("") }
    var showAll by remember { mutableStateOf(true) }
    var favorites by remember {
        mutableStateOf(context.getSharedPreferences("rage_launcher", Context.MODE_PRIVATE)
            .getStringSet("favorites", emptySet())?.toSet() ?: emptySet())
    }
    var showSettings by remember { mutableStateOf(false) }
    val filtered = remember(apps, query) {
        if (query.isBlank()) apps else apps.filter { it.label.contains(query, true) }
    }
    val favApps = apps.filter { favorites.contains(it.packageName) }
    val visibleApps = if (showAll || query.isNotBlank()) filtered else favApps

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Red, background = Black, surface = Panel,
        onPrimary = White, onBackground = White, onSurface = White,
        secondary = Color(0xFFFF6B7F), surfaceVariant = Panel2,
        onSurfaceVariant = Muted
    )) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(
            listOf(Color(0xFF180B11), Black, Black)
        ))) {
            Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                .padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("RAGE", color = White, fontSize = 17.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
                        Text("LAUNCHER", color = Red, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                    }
                    Box(Modifier.size(42.dp).clip(CircleShape).background(Panel2)
                        .clickable { showSettings = true }, contentAlignment = Alignment.Center) {
                        SvgIcon("settings", Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.height(28.dp))
                Text(SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(now).uppercase(),
                    color = Muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
                Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    color = White, fontSize = 48.sp, fontWeight = FontWeight.Light, letterSpacing = (-2).sp)
                Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                    .background(Panel).padding(horizontal = 16.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    SvgIcon("search", Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    BasicTextField(value = query, onValueChange = { query = it },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(color = White, fontSize = 14.sp),
                        singleLine = true,
                        decorationBox = { inner ->
                            Box {
                                if (query.isEmpty()) Text("Search apps", color = Muted, fontSize = 14.sp)
                                inner()
                            }
                        })
                    if (query.isNotEmpty()) Text("×", color = Muted, modifier = Modifier.clickable { query = "" })
                }
                Spacer(Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (query.isNotBlank()) "SEARCH RESULTS" else if (showAll) "ALL APPS" else "FAVORITES",
                        color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp,
                        modifier = Modifier.weight(1f))
                    Text(if (showAll) "Favorites" else "All apps", color = Red, fontSize = 12.sp,
                        modifier = Modifier.clickable { showAll = !showAll; query = "" })
                }
                Spacer(Modifier.height(14.dp))
                if (visibleApps.isEmpty()) {
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text(if (query.isBlank()) "Long-press an app to pin it here" else "No apps found",
                            color = Muted, fontSize = 13.sp)
                    }
                } else {
                    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 76.dp),
                        modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        items(visibleApps, key = { it.packageName }) { app ->
                            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                                .combinedClickable(
                                    onClick = {
                                        val launch = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                        if (launch != null) context.startActivity(launch)
                                    },
                                    onLongClick = {
                                        val next = favorites.toMutableSet()
                                        if (next.contains(app.packageName)) next.remove(app.packageName)
                                        else next.add(app.packageName)
                                        favorites = next
                                        context.getSharedPreferences("rage_launcher", Context.MODE_PRIVATE)
                                            .edit().putStringSet("favorites", next).apply()
                                    }
                                ).padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                AndroidView(factory = { ImageView(it).apply {
                                    scaleType = ImageView.ScaleType.FIT_CENTER
                                    setImageDrawable(app.icon)
                                } }, modifier = Modifier.size(43.dp))
                                Spacer(Modifier.height(8.dp))
                                Text(app.label, color = White, fontSize = 10.sp,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                                if (favorites.contains(app.packageName)) {
                                    Box(Modifier.padding(top = 5.dp).size(3.dp).clip(CircleShape).background(Red))
                                }
                            }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("RAGE LAUNCHER", color = Muted, fontSize = 9.sp, letterSpacing = 1.5.sp)
                        Text("Made by DevNameGelo", color = Muted.copy(alpha = .7f), fontSize = 9.sp)
                    }
                    SvgIcon("bolt", Modifier.size(18.dp))
                }
            }
            if (showSettings) {
                AlertDialog(onDismissRequest = { showSettings = false },
                    containerColor = Panel,
                    title = { Text("Launcher settings", color = White) },
                    text = { Column {
                        Text("Set Rage Launcher as your default Home app in Android Settings.", color = Muted)
                        Spacer(Modifier.height(12.dp))
                        Text("Long-press any app to pin or unpin it from Favorites.", color = Muted)
                    } },
                    confirmButton = {
                        TextButton(onClick = {
                            showSettings = false
                            context.startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
                        }) { Text("Set default", color = Red) }
                    },
                    dismissButton = { TextButton(onClick = { showSettings = false }) { Text("Close", color = Muted) } })
            }
        }
    }
}
