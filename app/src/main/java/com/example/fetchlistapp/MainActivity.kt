package com.example.fetchlistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fetchlistapp.model.Item
import com.example.fetchlistapp.model.ItemGroup
import com.example.fetchlistapp.ui.theme.FetchListAppTheme
import com.example.fetchlistapp.ui.theme.GoldDark
import com.example.fetchlistapp.ui.theme.PurpleDark
import com.example.fetchlistapp.ui.theme.White
import com.example.fetchlistapp.viewmodel.ItemViewModel

/**
 * MainActivity is the entry point of the app.
 * It sets up the theme and content view and connects the ViewModel to the UI.
 */
class MainActivity : ComponentActivity() {
    // create a reference to the ViewModel
    private val viewModel: ItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // drawing behind system bars
        setContent {
            FetchListAppTheme {
                // create a base container with theme background
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FetchListApp(viewModel) // Call the main UI composable
                }
            }
        }
    }
}

/**
 * The main Composable function that builds the app's scaffold and handles UI state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FetchListApp(viewModel: ItemViewModel) {
    // observing LiveData from the ViewModel
    val itemGroups by viewModel.itemGroups.observeAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.observeAsState(initial = true)
    val error by viewModel.error.observeAsState(initial = "")

    // track which groups are expanded
    val expandedGroups = remember { mutableStateMapOf<Int, Boolean>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fetch Items List",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurpleDark,
                    titleContentColor = White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    // show loading spinner while data is being fetched
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = GoldDark
                    )
                }
                error.isNotEmpty() -> {
                    // display any errors
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                }
                else -> {
                    // display list of item groups
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(itemGroups) { group ->
                            val isExpanded = expandedGroups[group.listId] ?: false
                            ItemGroupCard(
                                itemGroup = group,
                                expanded = isExpanded,
                                onToggleExpand = {
                                    expandedGroups[group.listId] = !isExpanded
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card UI for each group of items, with expandable behavior.
 */
@Composable
fun ItemGroupCard(
    itemGroup: ItemGroup,
    expanded: Boolean,
    onToggleExpand: () -> Unit
) {
    // animate arrow icon rotation when expanded/collapsed
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "ArrowRotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // clickable header row with listId and arrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PurpleDark)
                    .clickable { onToggleExpand() }
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "List ID: ${itemGroup.listId}",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = White,
                    modifier = Modifier.rotate(rotation)
                )
            }

            // show or hide items list with smooth animation
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    itemGroup.items.forEach { item ->
                        ItemRow(item) // reusable row for each item
                        Divider(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Displays a single item row with name and ID.
 */
@Composable
fun ItemRow(item: Item) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.name ?: "",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "ID: ${item.id}",
            style = MaterialTheme.typography.bodyMedium,
            color = GoldDark
        )
    }
}
