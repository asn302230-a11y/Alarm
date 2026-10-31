package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AlarmViewModel
import com.example.ui.components.AboutIraqAlarmDialog
import com.example.ui.components.AddEditAlarmSheet
import com.example.ui.components.AlarmCard
import com.example.ui.components.AlarmRingingOverlay
import com.example.ui.components.BaghdadClockView
import com.example.ui.components.IraqFlagBadge
import com.example.ui.components.IraqFlagHeader
import com.example.ui.components.IraqTimerStopwatchView
import com.example.ui.components.SleepCycleCalculatorView
import com.example.ui.theme.IraqBlack
import com.example.ui.theme.IraqBlackDark
import com.example.ui.theme.IraqGold
import com.example.ui.theme.IraqGreen
import com.example.ui.theme.IraqRed
import com.example.ui.theme.IraqSurfaceDark
import com.example.ui.theme.IraqWhite
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAlarmApp()
            }
        }
    }
}

@Composable
fun MainAlarmApp(
    viewModel: AlarmViewModel = viewModel()
) {
    val context = LocalContext.current
    val alarms by viewModel.alarms.collectAsStateWithLifecycle()
    val ringingAlarm by viewModel.ringingAlarm.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val editingAlarm by viewModel.editingAlarm.collectAsStateWithLifecycle()
    val isCreatingNew by viewModel.isCreatingNew.collectAsStateWithLifecycle()
    val currentTimeMillis by viewModel.currentTimeMillis.collectAsStateWithLifecycle()
    val previewingTone by viewModel.previewingTone.collectAsStateWithLifecycle()

    var showAboutDialog by remember { mutableStateOf(false) }

    // Request notification permission on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(IraqBlackDark)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = IraqBlackDark,
            topBar = {
                IraqFlagHeader(
                    currentTimeMillis = currentTimeMillis,
                    nextAlarmText = viewModel.getNextAlarmTimeRemaining(),
                    onInfoClick = { showAboutDialog = true }
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.navigationBarsPadding(),
                    containerColor = IraqSurfaceDark,
                    tonalElevation = 8.dp
                ) {
                    val tabs = listOf(
                        Triple(0, "Alarms", Icons.Default.Alarm),
                        Triple(1, "Baghdad", Icons.Default.Public),
                        Triple(2, "Timer", Icons.Default.Timer),
                        Triple(3, "Sleep", Icons.Default.Bedtime)
                    )

                    tabs.forEach { (index, title, icon) ->
                        val isSelected = selectedTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedTab(index) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = IraqWhite,
                                unselectedIconColor = IraqWhite.copy(alpha = 0.5f),
                                selectedTextColor = IraqGold,
                                unselectedTextColor = IraqWhite.copy(alpha = 0.5f),
                                indicatorColor = IraqRed
                            ),
                            modifier = Modifier.testTag("nav_tab_$title")
                        )
                    }
                }
            },
            floatingActionButton = {
                if (selectedTab == 0) {
                    FloatingActionButton(
                        onClick = { viewModel.startCreateAlarm() },
                        containerColor = IraqRed,
                        contentColor = IraqWhite,
                        shape = CircleShape,
                        modifier = Modifier.testTag("add_alarm_fab")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Alarm",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedTab) {
                    0 -> {
                        // ALARMS TAB
                        if (alarms.isEmpty()) {
                            // Empty state
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                IraqFlagBadge(width = 64.dp, height = 40.dp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No Alarms Set",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IraqWhite
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tap the + button to schedule your morning wake-up or prayer times with Iraqi flag colors.",
                                    fontSize = 13.sp,
                                    color = IraqWhite.copy(alpha = 0.65f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(alarms, key = { it.id }) { alarm ->
                                    AlarmCard(
                                        alarm = alarm,
                                        isPlayingPreview = previewingTone == alarm.soundTone,
                                        onToggle = { viewModel.toggleAlarm(alarm) },
                                        onClick = { viewModel.startEditAlarm(alarm) },
                                        onDelete = { viewModel.deleteAlarm(alarm) },
                                        onPreviewSound = { viewModel.previewTone(alarm.soundTone) },
                                        onTestTrigger = { viewModel.triggerTestAlarm(alarm) }
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        // BAGHDAD WORLD CLOCK TAB
                        BaghdadClockView(
                            currentTimeMillis = currentTimeMillis,
                            onAddPrayerAlarm = { prayerAlarm ->
                                viewModel.saveAlarm(prayerAlarm)
                                viewModel.setSelectedTab(0)
                            }
                        )
                    }
                    2 -> {
                        // TIMER & STOPWATCH TAB
                        IraqTimerStopwatchView()
                    }
                    3 -> {
                        // SLEEP CALCULATOR TAB
                        SleepCycleCalculatorView(
                            currentTimeMillis = currentTimeMillis,
                            onSetAlarm = { cycleAlarm ->
                                viewModel.saveAlarm(cycleAlarm)
                                viewModel.setSelectedTab(0)
                            }
                        )
                    }
                }
            }
        }

        // Full Screen Ringing Overlay
        AnimatedVisibility(
            visible = ringingAlarm != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ringingAlarm?.let { activeAlarm ->
                AlarmRingingOverlay(
                    alarm = activeAlarm,
                    onDismiss = { viewModel.dismissRinging() },
                    onSnooze = { minutes -> viewModel.snoozeRinging(minutes) }
                )
            }
        }

        // Add / Edit Modal Sheet
        editingAlarm?.let { alarmToEdit ->
            AddEditAlarmSheet(
                initialAlarm = alarmToEdit,
                isCreatingNew = isCreatingNew,
                previewingTone = previewingTone,
                onSave = { updated -> viewModel.saveAlarm(updated) },
                onDismiss = { viewModel.cancelEditAlarm() },
                onPreviewTone = { tone -> viewModel.previewTone(tone) }
            )
        }

        // About Dialog
        if (showAboutDialog) {
            AboutIraqAlarmDialog(onDismiss = { showAboutDialog = false })
        }
    }
}
