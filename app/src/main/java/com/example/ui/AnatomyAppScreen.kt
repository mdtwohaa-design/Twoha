package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.example.engine.Anatomy3DEngine
import com.example.engine.AnatomyLine
import com.example.engine.ProjectedPoint
import com.example.engine.Vector3D
import com.example.model.AnatomicalSystem
import com.example.model.AnatomyRepository
import com.example.model.Organ
import com.example.model.Pathology
import kotlinx.coroutines.delay
import kotlin.math.*

// Palette matches a high-tech obsidian/slate theme
val DarkSlateBg = Color(0xFF12141C)
val ObsidianCard = Color(0xFF1B1E2B)
val ObsidianBorder = Color(0xFF2C3146)
val HologramBlue = Color(0xFF00E5FF)
val GlowingOrange = Color(0xFFFF9100)
val NeutralText = Color(0xFFE2E8F0)
val SubduedText = Color(0xFF94A3B8)

@Composable
fun AnatomyAppScreen(viewModel: AnatomyViewModel) {
    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // Launch background loop for gentle 3D rotation
    LaunchedEffect(state.isAutoRotating) {
        while (state.isAutoRotating) {
            viewModel.animateAutoRotateStep()
            delay(16) // ~60 fps
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSlateBg)
            .pointerInput(Unit) {
                // Remove keyboard focus when tapping outside
                detectDragGestures(
                    onDragStart = { focusManager.clearFocus() },
                    onDrag = { _, _ -> }
                )
            }
    ) {
        val width = maxWidth
        val isWideLayout = width > 840.dp

        if (isWideLayout) {
            // Tablet Widescreen 3-pane Workstation Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                // Column 1: Systems & Organs Left Panel (300.dp)
                Surface(
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxHeight(),
                    color = DarkSlateBg,
                    border = BorderStroke(1.dp, ObsidianBorder)
                ) {
                    SystemsAndOrgansList(state = state, viewModel = viewModel)
                }

                // Column 2: 3D Visualization Table Center Panel (Flexible, centered)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                ) {
                    ViewportAnatomy3D(state = state, viewModel = viewModel)
                    
                    // Quick stats overlay on top edge
                    VisualHUDOverlay(state = state, isWide = true)
                }

                // Column 3: Diagnostic Sheet & Clinical Workspace (420.dp)
                Surface(
                    modifier = Modifier
                        .width(420.dp)
                        .fillMaxHeight(),
                    color = ObsidianCard,
                    tonalElevation = 2.dp,
                    border = BorderStroke(1.dp, ObsidianBorder)
                ) {
                    ControlAndStudyWorkspace(
                        state = state,
                        viewModel = viewModel,
                        isWide = true
                    )
                }
            }
        } else {
            // Compact Mobile Stacked Layout (Tabs: (1) 3D Model, (2) Clinical Study, (3) Gemini Tutor)
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar(
                        containerColor = ObsidianCard,
                        contentColor = SubduedText,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("compact_bottom_bar")
                    ) {
                        NavigationBarItem(
                            selected = state.activeTab == ActiveScreenTab.EXPLORE,
                            onClick = { viewModel.selectTab(ActiveScreenTab.EXPLORE) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "3D Explorer") },
                            label = { Text("3D Body", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = HologramBlue,
                                selectedTextColor = HologramBlue,
                                unselectedIconColor = SubduedText,
                                unselectedTextColor = SubduedText,
                                indicatorColor = ObsidianBorder
                            )
                        )
                        NavigationBarItem(
                            selected = state.activeTab == ActiveScreenTab.QUIZ,
                            onClick = { viewModel.selectTab(ActiveScreenTab.QUIZ) },
                            icon = { Icon(Icons.Default.List, contentDescription = "Quiz Hub") },
                            label = { Text("Clinical Quizzes", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = HologramBlue,
                                selectedTextColor = HologramBlue,
                                unselectedIconColor = SubduedText,
                                unselectedTextColor = SubduedText,
                                indicatorColor = ObsidianBorder
                            )
                        )
                        NavigationBarItem(
                            selected = state.activeTab == ActiveScreenTab.AI_TUTOR,
                            onClick = { viewModel.selectTab(ActiveScreenTab.AI_TUTOR) },
                            icon = { Icon(Icons.Default.Star, contentDescription = "Gemini Tutor") },
                            label = { Text("AI Medical Tutor", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = HologramBlue,
                                selectedTextColor = HologramBlue,
                                unselectedIconColor = SubduedText,
                                unselectedTextColor = SubduedText,
                                indicatorColor = ObsidianBorder
                            )
                        )
                    }
                },
                contentWindowInsets = WindowInsets.safeDrawing,
                containerColor = DarkSlateBg
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (state.activeTab) {
                        ActiveScreenTab.EXPLORE -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                // 3D viewport takes upper space
                                Box(
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .fillMaxWidth()
                                ) {
                                    ViewportAnatomy3D(state = state, viewModel = viewModel)
                                    VisualHUDOverlay(state = state, isWide = false)
                                }
                                
                                // Lower half is system toggles and simple quick select drawer
                                Box(
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                        .background(ObsidianCard)
                                        .border(BorderStroke(1.dp, ObsidianBorder), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                ) {
                                    MobileCompactDrawer(state = state, viewModel = viewModel)
                                }
                            }
                        }
                        ActiveScreenTab.QUIZ -> {
                            // Render Quizzes exclusively
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.setQuizType(QuizType.USMLE_MCQ) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (state.activeQuizType == QuizType.USMLE_MCQ) HologramBlue else ObsidianBorder,
                                                contentColor = if (state.activeQuizType == QuizType.USMLE_MCQ) DarkSlateBg else NeutralText
                                            )
                                        ) {
                                            Text("Clinical Boards MCQ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = { viewModel.setQuizType(QuizType.IDENTIFY_3D) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (state.activeQuizType == QuizType.IDENTIFY_3D) HologramBlue else ObsidianBorder,
                                                contentColor = if (state.activeQuizType == QuizType.IDENTIFY_3D) DarkSlateBg else NeutralText
                                            )
                                        ) {
                                            Text("3D Spot Identification", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    if (state.activeQuizType == QuizType.USMLE_MCQ) {
                                        QuizWorkspaceMCQ(state = state, viewModel = viewModel)
                                    } else {
                                        Column(modifier = Modifier.fillMaxSize()) {
                                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                                ViewportAnatomy3D(state = state, viewModel = viewModel)
                                            }
                                            Surface(
                                                modifier = Modifier.fillMaxWidth().height(160.dp),
                                                color = ObsidianCard,
                                                border = BorderStroke(1.dp, ObsidianBorder)
                                            ) {
                                                IdentifyQuizPill(state = state, viewModel = viewModel)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        ActiveScreenTab.AI_TUTOR -> {
                            // Render AI Tutor Interface
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                GeminiTutorWorkspace(state = state, viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SystemsAndOrgansList(state: AnatomyUiState, viewModel: AnatomyViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // App title with subtitle for Board exam revision
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                tint = HologramBlue,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Anatomy 3D",
                color = NeutralText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
        Text(
            text = "USMLE BOARD STUDY TOOL",
            color = SubduedText,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Divider(color = ObsidianBorder, thickness = 1.dp)

        Spacer(modifier = Modifier.height(8.dp))

        // Search Input
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search specific organ...", color = SubduedText, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SubduedText) },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = null, tint = SubduedText)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSlateBg,
                unfocusedContainerColor = DarkSlateBg,
                focusedTextColor = NeutralText,
                unfocusedTextColor = NeutralText,
                focusedBorderColor = HologramBlue,
                unfocusedBorderColor = ObsidianBorder
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("organ_search_input"),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Quick System filters header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Physiological Systems", color = SubduedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                if (state.activeSystems.size == AnatomicalSystem.values().size) "Desel All" else "Sel All",
                color = HologramBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        val selectAll = state.activeSystems.size < AnatomicalSystem.values().size
                        viewModel.setAllSystemsEnabled(selectAll)
                    }
                    .testTag("toggle_all_systems")
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // FlowRow Grid for system togglers
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AnatomicalSystem.values().forEach { sys ->
                val isActive = state.activeSystems.contains(sys)
                val color = Color(android.graphics.Color.parseColor(sys.colorHex))
                val border = if (isActive) color else ObsidianBorder
                val containerBc = if (isActive) color.copy(alpha = 0.15f) else ObsidianCard

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(containerBc)
                        .border(BorderStroke(1.dp, border), RoundedCornerShape(6.dp))
                        .clickable { viewModel.toggleSystem(sys) }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("system_toggle_${sys.name}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(sys.displayName, color = if (isActive) color else SubduedText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Selectable Structures", color = SubduedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(6.dp))

        // Organs list
        val filteredOrgans = AnatomyRepository.organs.filter { organ ->
            val systemMatch = state.activeSystems.contains(organ.system)
            val searchMatch = organ.name.contains(state.searchQuery, ignoreCase = true) ||
                    organ.latinName.contains(state.searchQuery, ignoreCase = true)
            systemMatch && searchMatch
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (filteredOrgans.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = SubduedText, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No structure matches search conditions.", color = SubduedText, fontSize = 12.sp, textAlign = TextAlign.Center)
                    }
                }
            } else {
                items(filteredOrgans) { organ ->
                    val isSelected = state.selectedOrgan?.id == organ.id
                    val sysColor = Color(android.graphics.Color.parseColor(organ.system.colorHex))
                    val cardBorder = if (isSelected) HologramBlue else ObsidianBorder

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ObsidianBorder.copy(alpha = 0.4f) else ObsidianCard)
                            .border(BorderStroke(1.dp, cardBorder), RoundedCornerShape(8.dp))
                            .clickable { viewModel.selectOrgan(organ) }
                            .padding(10.dp)
                            .testTag("organ_item_${organ.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Colored accent dot represent physiological system
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(sysColor)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(organ.name, color = NeutralText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(organ.latinName, color = SubduedText, fontSize = 10.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        }
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = "Details",
                            tint = SubduedText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ViewportAnatomy3D(state: AnatomyUiState, viewModel: AnatomyViewModel) {
    var widthSize by remember { mutableStateOf(300f) }
    var heightSize by remember { mutableStateOf(400f) }

    // State indicators for pulse animations
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(state.activeTab, state.activeQuizType) {
                // Drag gesture triggers custom rotation matrices update
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        viewModel.onRotate(dragAmount.x, dragAmount.y)
                    }
                )
            }
            .pointerInput(state.scale) {
                // Zoom controller on standard coordinates
                // We'll keep mouse/scroll or explicit HUD zoom buttons to make this extremely easy to use!
            }
            .drawBehind {
                // High-tech holographic background grid
                val gridBrush = Brush.radialGradient(
                    colors = listOf(Color(0xFF0F1E2E), DarkSlateBg),
                    center = Offset(size.width / 2, size.height / 2),
                    radius = size.width * 0.75f
                )
                drawRect(gridBrush)
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("anatomy_canvas")
        ) {
            val cx = size.width / 2
            val cy = size.height / 2
            val radiusScale = (min(size.width, size.height) / 4.4f) * state.scale

            widthSize = size.width
            heightSize = size.height

            // 1. Gather all active wireframe connections
            val activeLines = Anatomy3DEngine.generateLines()

            // 2. Map coordinates and project to 2D
            // Draw lines
            activeLines.forEach { line ->
                val startRot = line.start.rotate(state.pitch, state.yaw)
                val endRot = line.end.rotate(state.pitch, state.yaw)

                val projS = startRot.project(cx, cy, radiusScale)
                val projE = endRot.project(cx, cy, radiusScale)

                // Select color based on anatomical system connection
                val color = when (line.system) {
                    "skeletal" -> if (state.activeSystems.contains(AnatomicalSystem.SKELETAL)) {
                        Color(0xFF673AB7).copy(alpha = 0.5f)
                    } else Color.Transparent
                    "nervous" -> if (state.activeSystems.contains(AnatomicalSystem.NERVOUS)) {
                        Color(0xFF00E5FF).copy(alpha = 0.6f)
                    } else Color.Transparent
                    "cardio_artery" -> if (state.activeSystems.contains(AnatomicalSystem.CARDIOVASCULAR)) {
                        Color(0xFFE57373).copy(alpha = 0.7f)
                    } else Color.Transparent
                    "cardio_vein" -> if (state.activeSystems.contains(AnatomicalSystem.CARDIOVASCULAR)) {
                        Color(0xFFAB47BC).copy(alpha = 0.7f)
                    } else Color.Transparent
                    "respiratory" -> if (state.activeSystems.contains(AnatomicalSystem.RESPIRATORY)) {
                        Color(0xFF81C784).copy(alpha = 0.6f)
                    } else Color.Transparent
                    "digestive" -> if (state.activeSystems.contains(AnatomicalSystem.DIGESTIVE)) {
                        Color(0xFFFFB74D).copy(alpha = 0.5f)
                    } else Color.Transparent
                    "body_cage" -> Color(0xFF2C3E50).copy(alpha = 0.22f) // background cage always there
                    else -> Color.Transparent
                }

                if (color != Color.Transparent) {
                    drawLine(
                        color = color,
                        start = Offset(projS.x, projS.y),
                        end = Offset(projE.x, projE.y),
                        strokeWidth = if (line.isDotted) 1.dp.toPx() else 1.8f.todp(density = this).toPx(),
                        pathEffect = if (line.isDotted) PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f) else null,
                        cap = StrokeCap.Round
                    )
                }
            }

            // 3. Render Organs Nodes as glowing circles, with DEPTH SORTING (Back to front!)
            val activeOrgans = AnatomyRepository.organs.filter { state.activeSystems.contains(it.system) }
            val projectedOrgans = activeOrgans.map { organ ->
                val rot = organ.position3D.rotate(state.pitch, state.yaw)
                val proj = rot.project(cx, cy, radiusScale)
                OrganProjection(organ, proj)
            }.sortedBy { it.proj.depth } // Sort so back organs (lower depth) draw first, front organs draw on top

            projectedOrgans.forEach { item ->
                val organ = item.organ
                val px = item.proj.x
                val py = item.proj.y
                val depth = item.proj.depth // ranges from ~ -0.5 (back) to +0.5 (front)

                val sysColor = Color(android.graphics.Color.parseColor(organ.system.colorHex))
                
                // Opacity is governed by depth (back organs appear faded/translucent)
                val depthAlpha = ((depth + 0.5f) / 1.0f).coerceIn(0.2f, 1.0f)
                
                val isSelected = state.selectedOrgan?.id == organ.id
                val isTarget = state.identifyTargetOrgan?.id == organ.id && state.activeTab == ActiveScreenTab.QUIZ && state.activeQuizType == QuizType.IDENTIFY_3D

                if (isSelected) {
                    // Draw outer pulsing glowing aura
                    drawCircle(
                        color = HologramBlue.copy(alpha = 0.3f * depthAlpha),
                        radius = (14.dp.toPx()) * pulseAnim,
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = HologramBlue,
                        radius = 8.dp.toPx(),
                        center = Offset(px, py),
                        style = Stroke(width = 2.dp.toPx())
                    )
                } else if (isTarget) {
                    // Blinking orange target sphere
                    drawCircle(
                        color = GlowingOrange.copy(alpha = 0.4f * depthAlpha),
                        radius = (12.dp.toPx()) * pulseAnim,
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = GlowingOrange,
                        radius = 6.dp.toPx(),
                        center = Offset(px, py)
                    )
                } else {
                    // Default Organ node
                    drawCircle(
                        color = sysColor.copy(alpha = 0.65f * depthAlpha),
                        radius = 7.dp.toPx(),
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.9f * depthAlpha),
                        radius = 2.dp.toPx(),
                        center = Offset(px, py)
                    )
                }

                // If zoom scale is close or organ is selected, draw floating labels in 3D
                if (isSelected || state.scale > 1.4f) {
                    // Draw an anchor line out and render the text
                    val textOffset = Offset(px + 12.dp.toPx(), py - 12.dp.toPx())
                    drawLine(
                        color = if (isSelected) HologramBlue.copy(alpha = 0.7f) else sysColor.copy(alpha = 0.5f),
                        start = Offset(px, py),
                        end = textOffset,
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
        }

        // Tap gestures listener for 3D Organ Nodes selection
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(state.activeSystems, state.selectedOrgan) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            viewModel.onRotate(dragAmount.x, dragAmount.y)
                        }
                    )
                }
                .pointerInput(state.activeSystems, state.activeTab, state.activeQuizType) {
                    // Trigger tap selection check
                    detectTapGesturesForAnatomy(
                        cx = widthSize / 2,
                        cy = heightSize / 2,
                        radiusScale = (min(widthSize, heightSize) / 4.4f) * state.scale,
                        pitch = state.pitch,
                        yaw = state.yaw,
                        activeSystems = state.activeSystems,
                        onOrganTapped = { organ ->
                            if (state.activeTab == ActiveScreenTab.QUIZ && state.activeQuizType == QuizType.IDENTIFY_3D) {
                                viewModel.handle3DIdentifyTap(organ)
                            } else {
                                viewModel.selectOrgan(organ)
                            }
                        }
                    )
                }
        )

        // Reset camera HUD buttons at bottom right
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { viewModel.adjustScale(1.15f) },
                containerColor = ObsidianCard,
                contentColor = HologramBlue,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("zoom_in_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(20.dp))
            }
            FloatingActionButton(
                onClick = { viewModel.adjustScale(0.85f) },
                containerColor = ObsidianCard,
                contentColor = HologramBlue,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("zoom_out_button")
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Zoom Out", modifier = Modifier.size(20.dp))
            }
            FloatingActionButton(
                onClick = { viewModel.setAutoRotation(!state.isAutoRotating) },
                containerColor = if (state.isAutoRotating) HologramBlue else ObsidianCard,
                contentColor = if (state.isAutoRotating) DarkSlateBg else HologramBlue,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("auto_rotate_button")
            ) {
                Icon(
                    imageVector = if (state.isAutoRotating) Icons.Default.Close else Icons.Default.Refresh,
                    contentDescription = "Auto Rotate Toggle",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

data class OrganProjection(val organ: Organ, val proj: ProjectedPoint)

private suspend fun androidx.compose.ui.input.pointer.PointerInputScope.detectTapGesturesForAnatomy(
    cx: Float,
    cy: Float,
    radiusScale: Float,
    pitch: Float,
    yaw: Float,
    activeSystems: Set<AnatomicalSystem>,
    onOrganTapped: (Organ) -> Unit
) {
    detectTapGestures(onTap = { tapOffset ->
        val activeOrgans = AnatomyRepository.organs.filter { activeSystems.contains(it.system) }
        var closestOrgan: Organ? = null
        var closestDist = Float.MAX_VALUE

        activeOrgans.forEach { organ ->
            val rot = organ.position3D.rotate(pitch, yaw)
            val proj = rot.project(cx, cy, radiusScale)
            val dx = proj.x - tapOffset.x
            val dy = proj.y - tapOffset.y
            val dist = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            // Dynamic selection boundary box (~38 pixels range)
            if (dist < 42f && dist < closestDist) {
                closestOrgan = organ
                closestDist = dist
            }
        }

        closestOrgan?.let { onOrganTapped(it) }
    })
}

private fun Float.todp(density: androidx.compose.ui.unit.Density): androidx.compose.ui.unit.Dp {
    return with(density) { this@todp.toDp() }
}

@Composable
fun VisualHUDOverlay(state: AnatomyUiState, isWide: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "ROTATION MODULE V3D",
                color = HologramBlue,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                "YAW: ${String.format("%.2f", state.yaw)}r | PITCH: ${String.format("%.2f", state.pitch)}r | ZOOM: ${String.format("%.1fx", state.scale)}",
                color = SubduedText,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(ObsidianCard)
                .border(BorderStroke(1.dp, ObsidianBorder), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (state.isAutoRotating) Color.Green else Color.Yellow)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (state.isAutoRotating) "ORBIT ACTIVE" else "MANUAL CONTROL",
                    color = NeutralText,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MobileCompactDrawer(state: AnatomyUiState, viewModel: AnatomyViewModel) {
    var drawerTabSelected by remember { mutableStateOf(0) } // 0: Systems/Search, 1: Organ Details

    // Auto switch to details tab when an organ is selected
    LaunchedEffect(state.selectedOrgan) {
        if (state.selectedOrgan != null) {
            drawerTabSelected = 1
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = drawerTabSelected,
            containerColor = ObsidianCard,
            contentColor = HologramBlue,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[drawerTabSelected]),
                    color = HologramBlue
                )
            }
        ) {
            Tab(
                selected = drawerTabSelected == 0,
                onClick = { drawerTabSelected = 0 },
                text = { Text("Structures Directory", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = drawerTabSelected == 1,
                onClick = { drawerTabSelected = 1 },
                text = { Text("Anatomical Diagnostics", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (drawerTabSelected == 0) {
                SystemsAndOrgansList(state = state, viewModel = viewModel)
            } else {
                ControlAndStudyWorkspace(state = state, viewModel = viewModel, isWide = false)
            }
        }
    }
}

@Composable
fun ControlAndStudyWorkspace(
    state: AnatomyUiState,
    viewModel: AnatomyViewModel,
    isWide: Boolean
) {
    val organ = state.selectedOrgan

    if (organ == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = HologramBlue.copy(alpha = 0.4f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Interactive Anatomy Terminal",
                color = NeutralText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap on any glowing organ node inside the 3D rotating human body, or browse the directory list leftwards to populate full medical diagnostics, high-yield USMLE facts, and access your personal AI Tutor.",
                color = SubduedText,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    } else {
        var workspaceTab by remember { mutableStateOf(0) } // 0: Diagnostics/Overview, 1: Boards Pathology, 2: AI Tutor/Quiz

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Area of Selected structure
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSlateBg.copy(alpha = 0.4f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val sysColor = Color(android.graphics.Color.parseColor(organ.system.colorHex))
                
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(sysColor.copy(alpha = 0.2f))
                        .border(BorderStroke(1.5.dp, sysColor), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = sysColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = organ.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Latin: ${organ.latinName} | ${organ.system.displayName} System",
                        color = SubduedText,
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
                
                IconButton(onClick = { viewModel.selectOrgan(null) }) {
                    Icon(Icons.Default.Close, contentDescription = "Deselect", tint = SubduedText)
                }
            }

            // Tab bar for details
            TabRow(
                selectedTabIndex = workspaceTab,
                containerColor = ObsidianCard,
                contentColor = HologramBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[workspaceTab]),
                        color = HologramBlue
                    )
                }
            ) {
                Tab(
                    selected = workspaceTab == 0,
                    onClick = { workspaceTab = 0 },
                    text = { Text("Diagnostics", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = workspaceTab == 1,
                    onClick = { workspaceTab = 1 },
                    text = { Text("Board Pearls", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = workspaceTab == 2,
                    onClick = { workspaceTab = 2 },
                    text = { Text("Ask Tutor", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                when (workspaceTab) {
                    0 -> OrganDiagnosticsTab(organ = organ)
                    1 -> OrganBoardPearlsTab(organ = organ)
                    2 -> OrganTutorInterfaceTab(state = state, viewModel = viewModel, organ = organ)
                }
            }
        }
    }
}

@Composable
fun OrganDiagnosticsTab(organ: Organ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSlateBg),
                border = BorderStroke(1.dp, ObsidianBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("STRUCTURE DESCRIPTION", color = HologramBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(organ.description, color = NeutralText, fontSize = 13.sp, lineHeight = 18.sp)
                }
            }
        }

        item {
            Text("DETAILED ANATOMICAL SUB-ELEMENTS", color = SubduedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        items(organ.detailedStructure) { segment ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(HologramBlue)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(segment, color = NeutralText, fontSize = 12.7.sp, lineHeight = 17.sp)
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("PRIMARY PHYSIOLOGICAL ROLES", color = SubduedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        items(organ.functions) { func ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF81C784),
                    modifier = Modifier
                        .size(14.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(func, color = NeutralText, fontSize = 12.7.sp, lineHeight = 17.sp)
            }
        }
    }
}

@Composable
fun OrganBoardPearlsTab(organ: Organ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("CLINICAL SIGNIFICANCE & CORRELATIONS", color = SubduedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        items(organ.clinicalSignificance) { clinicalPoint ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianCard)
                    .border(BorderStroke(1.dp, ObsidianBorder), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = GlowingOrange,
                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(clinicalPoint, color = NeutralText, fontSize = 12.5.sp, lineHeight = 17.sp)
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("HIGH-YIELD PATHOLOGIES & BOARD PRESENTATIONS", color = SubduedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        items(organ.pathologies) { path ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSlateBg),
                border = BorderStroke(1.dp, ObsidianBorder.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = path.name,
                        color = Color(0xFFEF9A9A),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Clinical Presentation:",
                        color = SubduedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = path.presentation,
                        color = NeutralText,
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = "Pathophysiology:",
                        color = SubduedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = path.pathologyDescription,
                        color = NeutralText,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OrganTutorInterfaceTab(
    state: AnatomyUiState,
    viewModel: AnatomyViewModel,
    organ: Organ
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSlateBg),
            border = BorderStroke(1.dp, ObsidianBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    "SUGGESTED HIGH-YIELD QUERIES",
                    color = HologramBlue,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))
                
                val suggestions = listOf(
                    "Explain specific arterial blood supply & venous drainage details.",
                    "What are the diagnostic laboratory values related to chronic failures?",
                    "Give summary of high-yield USMLE facts."
                )

                suggestions.forEach { prompt ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ObsidianCard)
                            .border(BorderStroke(1.dp, ObsidianBorder), RoundedCornerShape(6.dp))
                            .clickable {
                                viewModel.setGeminiQuery(prompt)
                                viewModel.sendChatToTutor()
                            }
                            .padding(8.dp)
                    ) {
                        Text(prompt, color = NeutralText, fontSize = 11.5.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Open complete chatbot layout inside this tab or tell them to click the general AI Tutor page
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(HologramBlue.copy(alpha = 0.1f))
                .border(BorderStroke(1.dp, HologramBlue.copy(alpha = 0.4f)), RoundedCornerShape(8.dp))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = HologramBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Gemini AI integration active for ${organ.name}!", color = NeutralText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Head over to the 'AI Medical Tutor' tab to chat extensively in full view mode.", color = SubduedText, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun QuizWorkspaceMCQ(state: AnatomyUiState, viewModel: AnatomyViewModel) {
    val qList = state.mcqQuestionsList
    if (qList.isEmpty()) return

    val currentQ = qList[state.currentMcqIndex]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("quiz_mcq_container"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Score strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianCard)
                    .border(BorderStroke(1.dp, ObsidianBorder), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("USMLE CORE DIAGNOSTIC ASSESSMENT", color = HologramBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text("Reviewing 7 high-yield clinical scenarios", color = SubduedText, fontSize = 11.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(HologramBlue.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        "SCORE: ${state.quizCorrectCount}/${state.quizAttemptedCount}",
                        color = HologramBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Question Box
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "QUESTION ${state.currentMcqIndex + 1} OF ${qList.size}",
                        color = SubduedText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentQ.question,
                        color = NeutralText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Options
        items(currentQ.options.size) { idx ->
            val optionText = currentQ.options[idx]
            val isSelected = state.selectedOptionIndex == idx
            val isChecked = state.isAnswerChecked
            val isCorrectOption = idx == currentQ.correctIndex

            // Determine diagnostic background color representation
            val borderColors = when {
                isChecked && isCorrectOption -> Color.Green
                isChecked && isSelected && !isCorrectOption -> Color.Red
                isSelected -> HologramBlue
                else -> ObsidianBorder
            }

            val bgColors = when {
                isChecked && isCorrectOption -> Color.Green.copy(alpha = 0.1f)
                isChecked && isSelected && !isCorrectOption -> Color.Red.copy(alpha = 0.1f)
                isSelected -> HologramBlue.copy(alpha = 0.1f)
                else -> ObsidianCard
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColors)
                    .border(BorderStroke(1.dp, borderColors), RoundedCornerShape(8.dp))
                    .clickable { viewModel.selectOption(idx) }
                    .padding(12.dp)
                    .testTag("quiz_option_$idx"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Radio button representation
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (isSelected) HologramBlue else Color.Transparent)
                        .border(
                            BorderStroke(
                                1.5.dp,
                                if (isSelected) HologramBlue else SubduedText
                            ), RoundedCornerShape(9.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(DarkSlateBg)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(optionText, color = NeutralText, fontSize = 13.sp, modifier = Modifier.weight(1f))

                if (isChecked && isCorrectOption) {
                    Icon(Icons.Default.Check, contentDescription = "Correct", tint = Color.Green)
                } else if (isChecked && isSelected && !isCorrectOption) {
                    Icon(Icons.Default.Close, contentDescription = "Incorrect", tint = Color.Red)
                }
            }
        }

        // Submit & Details Actions
        item {
            AnimatedVisibility(visible = !state.isAnswerChecked && state.selectedOptionIndex != null) {
                Button(
                    onClick = { viewModel.submitMcqAnswer() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_mcq_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = HologramBlue, contentColor = DarkSlateBg),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Submit Diagnostic Answer", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        // Explanation and Feedback Box
        item {
            AnimatedVisibility(visible = state.isAnswerChecked) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = BorderStroke(1.dp, ObsidianBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        val isUserCorrect = state.selectedOptionIndex == currentQ.correctIndex
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isUserCorrect) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isUserCorrect) Color.Green else Color.Red
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isUserCorrect) "CORRECT EXPLANATION" else "INCORRECT CLINICAL CHOICE",
                                color = if (isUserCorrect) Color.Green else Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(currentQ.explanation, color = NeutralText, fontSize = 13.sp, lineHeight = 18.sp)

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val rOrgan = AnatomyRepository.getOrganById(currentQ.relatedOrganId)
                                    viewModel.selectOrgan(rOrgan)
                                    viewModel.selectTab(ActiveScreenTab.EXPLORE)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = ObsidianBorder, contentColor = NeutralText)
                            ) {
                                Text("Inspect 3D Organ", fontSize = 11.5.sp)
                            }
                            Button(
                                onClick = { viewModel.advanceMcqNext() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = HologramBlue, contentColor = DarkSlateBg)
                            ) {
                                Text("Next Scenario", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Reset Score buttons
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(
                    onClick = { viewModel.resetMcqScore() },
                    modifier = Modifier.testTag("reset_quiz_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset Score Tracker", color = SubduedText, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun IdentifyQuizPill(state: AnatomyUiState, viewModel: AnatomyViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val target = state.identifyTargetOrgan ?: return
        
        Text(
            "SPATIAL RECALL CHALLENGE",
            color = HologramBlue,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Identify: ${target.name} (${target.latinName})",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = state.identifyFeedbackText,
            color = if (state.identifyCorrect == true) Color.Green else if (state.identifyCorrect == false) Color.Red else SubduedText,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (state.identifyCorrect != null) {
                Button(
                    onClick = { viewModel.selectRandomIdentifyTarget() },
                    colors = ButtonDefaults.buttonColors(containerColor = HologramBlue, contentColor = DarkSlateBg)
                ) {
                    Text("Next Spot Challenge", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                text = "Session Score: ${state.quizCorrectCount}/${state.quizAttemptedCount}",
                color = SubduedText,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
fun GeminiTutorWorkspace(state: AnatomyUiState, viewModel: AnatomyViewModel) {
    val listState = rememberLazyListState()

    // Trigger scrolls to bottom when new messages join the chat history
    LaunchedEffect(state.chatHistory.size) {
        if (state.chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(state.chatHistory.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            colors = CardDefaults.cardColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, ObsidianBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Green)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "AI CLINICAL TUTOR COMPANION (GEMINI 3.5)",
                        color = HologramBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Selected Context: ${state.selectedOrgan?.name ?: "General Human Physiology"}",
                    color = SubduedText,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // History Log
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(BorderStroke(1.dp, ObsidianBorder), RoundedCornerShape(8.dp))
                .background(DarkSlateBg)
                .padding(8.dp)
        ) {
            if (state.chatHistory.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = HologramBlue.copy(alpha = 0.4f), modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Select a structure in the 3D explorer and ask your AI Medical Tutor anything! Histological divisions, USMLE pearls, or diagnostic parameters.", color = SubduedText, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 24.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.clearChatHistory() },
                        colors = ButtonDefaults.buttonColors(containerColor = ObsidianBorder, contentColor = NeutralText)
                    ) {
                        Text("Initialize Dialogue Simulator", fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.chatHistory) { message ->
                        val isUser = message.second
                        val alignments = if (isUser) Alignment.End else Alignment.Start
                        val bubbleBc = if (isUser) HologramBlue.copy(alpha = 0.15f) else ObsidianCard
                        val bubbleBorder = if (isUser) HologramBlue else ObsidianBorder

                        Column(
                            horizontalAlignment = alignments,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isUser) "STUDENT QUESTION" else "CLINICAL ANATOMY PROFESSOR",
                                fontSize = 8.5.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (isUser) HologramBlue else Color(0xFF81C784),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bubbleBc)
                                    .border(BorderStroke(1.dp, bubbleBorder), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                                    .widthIn(max = 300.dp)
                            ) {
                                Text(
                                    text = message.first,
                                    color = NeutralText,
                                    fontSize = 12.5.sp,
                                    lineHeight = 17.6.sp
                                )
                            }
                        }
                    }

                    if (state.tutorResponseState is TutorChatState.Loading) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                                    border = BorderStroke(1.dp, ObsidianBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = HologramBlue, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("AI Tutor formulating clinical explanation...", color = SubduedText, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.geminiQuery,
                onValueChange = { viewModel.setGeminiQuery(it) },
                placeholder = { Text("Ask medical tutor about structure...", fontSize = 13.sp, color = SubduedText) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = NeutralText,
                    unfocusedTextColor = NeutralText,
                    focusedContainerColor = ObsidianCard,
                    unfocusedContainerColor = ObsidianCard,
                    focusedBorderColor = HologramBlue,
                    unfocusedBorderColor = ObsidianBorder
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("gemini_chat_input"),
                maxLines = 3
            )

            FloatingActionButton(
                onClick = { viewModel.sendChatToTutor() },
                containerColor = HologramBlue,
                contentColor = DarkSlateBg,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("send_chat_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Query AI")
            }
        }
    }
}
