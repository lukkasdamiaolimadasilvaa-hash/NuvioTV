package com.nuvio.tv.ui.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nuvio.tv.domain.model.ContinueWatchingItem

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDetail: (String, String, String) -> Unit = { _, _, _ -> },
    onContinueWatchingClick: (ContinueWatchingItem) -> Unit = {},
    onContinueWatchingStartFromBeginning: (ContinueWatchingItem) -> Unit = onContinueWatchingClick,
    onContinueWatchingPlayManually: (ContinueWatchingItem) -> Unit = onContinueWatchingClick,
    onNavigateToCatalogSeeAll: (String, String, String) -> Unit = { _, _, _ -> },
    onNavigateToFolderDetail: (String, String) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Imagem e dados padrão estilo Stranger Things (Foto 2)
    val backdropUrl = "https://images.ctfassets.net/4cd45et68cgf/7LrDuWeCiyygOqAaSYcWSj/c2b489a24d26ccb271d5334adbc5f307/Stranger_Things_S4_Backdrop.jpg"
    val title = "STRANGER THINGS"
    val rankingText = "#1 in TV Shows Today"
    val synopsis = "When a young boy vanishes, a small town uncovers a mystery involving secret experiments, terrifying supernatural forces and one strange little girl."
    val rating = "TV-14"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141414))
    ) {
        // 1. BACKDROP AO FUNDO (Imagem de destaque)
        AsyncImage(
            model = backdropUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.90f)
                .align(Alignment.TopEnd)
        )

        // 2. GRADIENTES OFICIAIS NETFLIX
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF141414),
                            Color(0xFF141414).copy(alpha = 0.98f),
                            Color(0xFF141414).copy(alpha = 0.70f),
                            Color.Transparent
                        ),
                        startX = 0f,
                        endX = 1300f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF141414).copy(alpha = 0.6f),
                            Color(0xFF141414)
                        ),
                        startY = 350f
                    )
                )
        )

        // 3. TAG TV-14 NO CANTO DIREITO
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 40.dp)
                .offset(y = 40.dp)
                .background(Color(0x80000000))
                .border(1.dp, Color(0x66FFFFFF))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = rating,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 4. CORPO DA TELA
        Row(modifier = Modifier.fillMaxSize()) {
            
            // MENU LATERAL
            NetflixSidebar()

            // INFORMAÇÕES DO FILME EM DESTAQUE
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(start = 28.dp, top = 44.dp)
            ) {
                // Selo N SERIES
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "N",
                        color = Color(0xFFE50914),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "S E R I E S",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Título
                Text(
                    text = title,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-1).sp,
                    lineHeight = 54.sp,
                    modifier = Modifier.width(550.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Selo Top 10
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE50914), RoundedCornerShape(2.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "TOP\n10",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 9.sp
                        )
                    }
                    Text(
                        text = rankingText,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sinopse
                Text(
                    text = synopsis,
                    color = Color(0xFFE5E5E5),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(480.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Botões Play e More Info
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NetflixPrimaryPlayButton(onClick = { })
                    NetflixSecondaryInfoButton(onClick = { })
                }

                Spacer(modifier = Modifier.weight(1f))

                // Fileira Trending Now
                Text(
                    text = "Trending Now",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                NetflixThumbnailRow()

                Spacer(modifier = Modifier.height(26.dp))
            }
        }
    }
}

@Composable
fun NetflixPrimaryPlayButton(onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1.08f else 1.0f, label = "scale")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .scale(scale)
            .height(42.dp)
            .width(115.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (isFocused) Color.White else Color(0xFFE5E5E5))
            .border(
                width = if (isFocused) 3.dp else 0.dp,
                color = if (isFocused) Color.LightGray else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Play",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}

@Composable
fun NetflixSecondaryInfoButton(onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1.08f else 1.0f, label = "scale")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .scale(scale)
            .height(42.dp)
            .width(140.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF6D6D6E).copy(alpha = if (isFocused) 0.9f else 0.65f))
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) Color.White else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "More info",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
    }
}

@Composable
fun NetflixSidebar() {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(52.dp)
            .background(Color.Transparent)
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Icon(Icons.Default.Search, null, tint = Color(0xFFB3B3B3), modifier = Modifier.size(20.dp))
        Icon(Icons.Default.Home, null, tint = Color.White, modifier = Modifier.size(22.dp))
        Icon(Icons.Default.Tv, null, tint = Color(0xFFB3B3B3), modifier = Modifier.size(20.dp))
        Icon(Icons.Default.Movie, null, tint = Color(0xFFB3B3B3), modifier = Modifier.size(20.dp))
        Icon(Icons.Default.Whatshot, null, tint = Color(0xFFB3B3B3), modifier = Modifier.size(20.dp))
        Icon(Icons.Default.Add, null, tint = Color(0xFFB3B3B3), modifier = Modifier.size(22.dp))
    }
}

@Composable
fun NetflixThumbnailRow() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(List(8) { it }) { _, _ ->
            var isFocused by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(if (isFocused) 1.08f else 1.0f, label = "thumbScale")

            Box(
                modifier = Modifier
                    .scale(scale)
                    .width(185.dp)
                    .height(105.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF262626))
                    .border(
                        width = if (isFocused) 3.dp else 0.dp,
                        color = if (isFocused) Color.White else Color.Transparent,
                        shape = RoundedCornerShape(3.dp)
                    )
                    .onFocusChanged { isFocused = it.isFocused }
                    .focusable()
            ) {
                Text(
                    text = "N",
                    color = Color(0xFFE50914),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    }
}
