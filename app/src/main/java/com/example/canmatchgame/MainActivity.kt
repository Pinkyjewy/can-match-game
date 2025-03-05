package com.example.canmatchgame

import android.annotation.SuppressLint
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canmatchgame.ui.theme.CanMatchGameTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CanMatchGameTheme {
                StartScreen()
            }
        }
    }
}

@Composable
fun StartScreen() {
    var startGame by remember { mutableStateOf(false) }

    if (startGame) {
        GameScreen()
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.snacknbev),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Can You Match?",
                    fontSize = 70.sp,
                    color = Color.White,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Black,   // Shadow color
                            offset = Offset(10f, 10f),  // X and Y offset
                            blurRadius = 16f // Blur effect
                        )
                    ),
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = { startGame = true }) {
                    Text(text = "Start Game")
                }
            }
        }
    }
}

fun generateCans(count: Int): List<Int> {
    val allowedCans = listOf(
        R.drawable.coke,
        R.drawable.fanta_orange,
        R.drawable.fanta_purple,
        R.drawable.mountaindew,
        R.drawable.pepsi,
        R.drawable.redbull,
        R.drawable.sevenup,
        R.drawable.sprite
    )
    return shuffleUntilNoMatch(allowedCans.shuffled().take(count))
}

fun generateBottles(): List<Int> {
    val allowedBottles = listOf(
        R.drawable.aquafinabottle,
        R.drawable.beerbottle,
        R.drawable.cokebottle,
        R.drawable.fantabottle_orange,
        R.drawable.greenbottle,
        R.drawable.orangebottle,
        R.drawable.redbottle
    )
    return shuffleUntilNoMatch(allowedBottles.shuffled().take(5))
}

fun generateCerealBoxes(): List<Int> {
    val allowedCerealBoxes = listOf(
        R.drawable.cheeriosbox,
        R.drawable.chipsahoybox,
        R.drawable.frootloopsbox,
        R.drawable.frostedflakesbox,
        R.drawable.milobox,
        R.drawable.orangebox,
        R.drawable.redbox,
        R.drawable.ricebox
    )
    return shuffleUntilNoMatch(allowedCerealBoxes.shuffled().take(6))
}

fun generateCupNoodles(): List<Int> {
    val allowedCupNoodles = listOf(
        R.drawable.cupchick,
        R.drawable.cupcurry,
        R.drawable.cupkyushu,
        R.drawable.cupramen,
        R.drawable.cupsea,
        R.drawable.cuptomyam,
        R.drawable.cupveg,
        R.drawable.cupwhite
    )
    return shuffleUntilNoMatch(allowedCupNoodles.shuffled().take(6))
}

fun <T> shuffleUntilNoMatch(list: List<T>): List<T> {
    var shuffledList: List<T>
    do {
        shuffledList = list.shuffled()
    } while (shuffledList.indices.any { index -> shuffledList[index] == list[index] })
    return shuffledList
}

@Composable
fun GameScreen() {
    var level by remember { mutableStateOf(1) }
    var swipeLimit by remember { mutableStateOf(15) }
    var cans by remember { mutableStateOf(generateCans(4)) }
    var bottles by remember { mutableStateOf(generateBottles()) }
    var cerealBoxes by remember { mutableStateOf(generateCerealBoxes()) }
    var cupNoodles by remember { mutableStateOf(generateCupNoodles()) }
    var hasLost by remember { mutableStateOf(false) }
    var timer by remember { mutableStateOf(45) }
    var showLevelComplete by remember { mutableStateOf(false) }
    var correctAnswer by remember { mutableStateOf<List<Int>>(listOf()) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var points by remember { mutableStateOf(6) }
    var correctCount by remember { mutableStateOf(0) }

    LaunchedEffect(level, isTimerRunning) {
        if (level == 3 && isTimerRunning) {
            while (timer > 0) {
                delay(1000L)
                timer--
            }
            if (timer == 0 && !hasLost) {
                hasLost = true
                correctAnswer = when (level) {
                    1 -> generateCans(4)
                    2 -> generateBottles()
                    3 -> generateCerealBoxes()
                    else -> listOf()
                }
            }
        }
    }

    fun resetGame() {
        swipeLimit = 15
        cans = generateCans(4)
        bottles = generateBottles()
        cerealBoxes = generateCerealBoxes()
        cupNoodles = generateCupNoodles()
        hasLost = false
        timer = 45
        showLevelComplete = false
        correctAnswer = listOf()
        isTimerRunning = false
        points = 6
        correctCount = 0
    }

    fun goToNextLevel() {
        level++
        resetGame()
    }

    fun restartGame() {
        level = 1
        resetGame()
    }

    if (showLevelComplete) {
        LevelCompleteScreen(onNextLevel = { goToNextLevel() }, correctAnswer = correctAnswer)
    } else if (hasLost) {
        ResultScreen(onRestart = { restartGame() }, message = "Oh no, you lost :'(", correctAnswer = correctAnswer)
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Level $level",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            when (level) {
                1 -> {
                    Text(
                        text = "Match these cans to the correct position!!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CanMatchGame(cans, level, swipeLimit, timer, points, onWin = {
                        showLevelComplete = true
                        correctAnswer = cans
                    }, onLose = {
                        hasLost = true
                        correctAnswer = cans
                    }, onSwipe = {
                        // No swipe limit on level 1
                    })
                }
                2 -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Match these bottles to the correct position within 15 times!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CanMatchGame(bottles, level, swipeLimit, timer, points, onWin = {
                        showLevelComplete = true
                        correctAnswer = bottles
                    }, onLose = {
                        hasLost = true
                        correctAnswer = bottles
                    }, onSwipe = {
                        if (swipeLimit > 0) swipeLimit--
                    })
                }
                3 -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!isTimerRunning) {
                        Text(
                            text = "Next Level: Match 6 cereal boxes to the correct position within 45 seconds!!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { isTimerRunning = true }) {
                            Text(text = "Start")
                        }
                    } else {
                        Text(
                            text = "Match these cereal boxes to the correct position within 45 seconds!!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CanMatchGame(cerealBoxes, level, swipeLimit, timer, points, onWin = {
                            showLevelComplete = true
                            correctAnswer = cerealBoxes
                        }, onLose = {
                            hasLost = true
                            correctAnswer = cerealBoxes
                        }, onSwipe = {
                        })
                    }
                }
                4 -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Match these cup noodles to their correct positions with: ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "+2 for each correct match",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Green
                        )
                        Text(
                            text = " -1 for each incorrect match.",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Red
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    CanMatchGame(cupNoodles, level, swipeLimit, timer, points, onWin = {
                        showLevelComplete = true
                        correctAnswer = cupNoodles
                    }, onLose = {
                        hasLost = true
                        correctAnswer = cupNoodles
                    }, onSwipe = { correct ->
                        if (correct) {
                            points += 2
                            correctCount++
                        } else {
                            points--
                        }
                        if (points <= 0 && correctCount < 6) {
                            hasLost = true
                            correctAnswer = cupNoodles
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun LevelCompleteScreen(onNextLevel: () -> Unit, correctAnswer: List<Int>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Here is the answer",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            correctAnswer.forEach { item ->
                Image(
                    painter = painterResource(id = item),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp).padding(4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Yay! You win!!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Green
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNextLevel) {
            Text(text = "Next Level ->")
        }
    }
}

@Composable
fun ResultScreen(onRestart: () -> Unit, message: String, correctAnswer: List<Int>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Here is the answer",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            correctAnswer.forEach { item ->
                Image(
                    painter = painterResource(id = item),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp).padding(4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRestart) {
            Text(text = "Back to Level 1 <-")
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CanMatchGame(
    correctAnswer: List<Int>,
    level: Int,
    swipeLimit: Int,
    timer: Int,
    points: Int,
    onWin: () -> Unit,
    onLose: () -> Unit,
    onSwipe: (Boolean) -> Unit
) {
    val shuffledItems = remember { mutableStateListOf(*shuffleUntilNoMatch(correctAnswer).toTypedArray()) }
    var selectedItemIndex by remember { mutableStateOf<Int?>(null) }
    val animationSpeeds = remember { shuffledItems.map { Animatable(0f) } }
    val scope = rememberCoroutineScope()
    val correctCount by derivedStateOf { shuffledItems.countIndexed { i, c -> c == correctAnswer[i] } }
    var showPopupText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            shuffledItems.forEachIndexed { index, item ->
                val offsetX = animationSpeeds[index].value.dp
                Image(
                    painter = painterResource(id = item),
                    contentDescription = null,
                    modifier = Modifier
                        .offset(x = offsetX)
                        .size(if (level == 3 || level == 4) 150.dp else 150.dp)
                        .padding(4.dp)
                        .clickable(enabled = correctCount != correctAnswer.size) {
                            if (selectedItemIndex == null) {
                                selectedItemIndex = index
                            } else {
                                val previouslySelectedIndex = selectedItemIndex!!
                                scope.launch {
                                    // Animate the position changes
                                    launch {
                                        animationSpeeds[previouslySelectedIndex].animateTo(
                                            targetValue = (index - previouslySelectedIndex) * 150f,
                                            animationSpec = tween(durationMillis = 300)
                                        )
                                    }
                                    launch {
                                        animationSpeeds[index].animateTo(
                                            targetValue = (previouslySelectedIndex - index) * 150f,
                                            animationSpec = tween(durationMillis = 300)
                                        )
                                    }
                                    // Wait for animations to finish
                                    delay(300)
                                    // Swap items
                                    val temp = shuffledItems[previouslySelectedIndex]
                                    shuffledItems[previouslySelectedIndex] = shuffledItems[index]
                                    shuffledItems[index] = temp
                                    // Reset animations
                                    animationSpeeds.forEach { it.snapTo(0f) }
                                    onSwipe(shuffledItems[index] == correctAnswer[index])
                                    selectedItemIndex = null
                                    if (correctCount == correctAnswer.size) {
                                        onWin()
                                    } else if ((level == 2 && swipeLimit <= 0) || (level == 3 && timer <= 0)) {
                                        onLose()
                                    }
                                }
                            }
                        }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        BasicText(text = "You have $correctCount correct")

        // Display Swipe Limit for Level 2 and Time Limit for Level 3
        if (level == 2) {
            Text(
                text = "Swipe Limit: $swipeLimit",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        if (level == 3) {
            Text(
                text = "Time Left: ${timer}s",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        if (level == 4) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Points remaining: $points",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                if (showPopupText.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = showPopupText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (showPopupText == "+2") Color.Green else Color.Red,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
        if (correctCount == correctAnswer.size) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Yay! You win!!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green
            )
            Button(onClick = onWin) {
                Text(text = "Next Level ->")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CanMatchGameTheme {
        StartScreen()
    }
}

fun <T> List<T>.countIndexed(predicate: (Int, T) -> Boolean): Int {
    var count = 0
    for (index in indices) {
        if (predicate(index, this[index])) count++
    }
    return count
}
