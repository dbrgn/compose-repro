package ch.dbrgn.compose_repro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.dbrgn.compose_repro.ui.theme.ComposeReproTheme
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    // Class to control how the bottom sheet behaves
    private val showBottomSheet = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeReproTheme {
                RootComposable(showBottomSheet)
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun RootComposable(showBottomSheet: MutableState<Boolean>) {
    // State: Bottom sheet scaffold
    val scaffoldState = rememberBottomSheetScaffoldState(
        DrawerState(DrawerValue.Closed),
        rememberBottomSheetState(BottomSheetValue.Collapsed)
    )

    // State: Height of the bottom sheet peek pane
    val baseBottomSheetPeekHeight by remember { mutableStateOf(60.dp) }
    val bottomSheetPeekHeight = if (showBottomSheet.value) baseBottomSheetPeekHeight else 0.dp

    // State: Scroll state of the bottom sheet
    val sheetContentScrollState = rememberScrollState()

    // State: Coroutine scope
    val scope = rememberCoroutineScope()

    // Handle back event when bottom sheet is expanded
    BackHandler(enabled = scaffoldState.bottomSheetState.isExpanded) {
        // Scroll sheet content all the way to the top
        scope.launch { sheetContentScrollState.scrollTo(0) }
        // Collapse bottom sheet with an animation
        scope.launch { scaffoldState.bottomSheetState.collapse() }
    }

    // Handle back event when bottom sheet is collapsed
    BackHandler(enabled = scaffoldState.bottomSheetState.isCollapsed) {
        // Hide bottom sheet
        showBottomSheet.value = false
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("App bar") },
                backgroundColor = MaterialTheme.colors.primary,
            )
        },
        content = { innerPadding ->
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.LightGray
            ) {
                Box(Modifier.padding(innerPadding)) {
                    Column {
                        Text("Hello Android")
                        Button({ showBottomSheet.value = true }) {
                            Text("Show bottom sheet")
                        }
                    }
                }
            }
        },
        sheetPeekHeight = bottomSheetPeekHeight,
        sheetContent = {
            Column(Modifier.verticalScroll(sheetContentScrollState).fillMaxWidth()) {
                // Peek area (same height as sheetPeekHeight)
                Box(Modifier.height(bottomSheetPeekHeight).padding(16.dp, 0.dp, 0.dp, 16.dp)) {
                    Column {
                        Text("Bottom sheet", style = MaterialTheme.typography.h6)
                        Text("Swipe up to open, press back btn to close", style = MaterialTheme.typography.body2)
                    }
                }

                // Divider
                Divider()

                // Expanded content
                Column(Modifier.padding(16.dp)) {
                    for (i in 0..15) {
                        Text(
                            "The bottom sheet content. The bottom sheet content. The bottom sheet content.",
                            style = MaterialTheme.typography.body2,
                        )
                    }
                }
            }
        },
    )
}