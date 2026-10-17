package com.example.data

import androidx.compose.ui.graphics.Color

enum class GroceryItemType(
    val label: String,
    val emoji: String,
    val primaryColor: Color,
    val lightColor: Color,
    val description: String
) {
    APPLE(
        label = "Quả táo",
        emoji = "🍎",
        primaryColor = Color(0xFFE53935),
        lightColor = Color(0xFFFFEBEE),
        description = "Táo đỏ ngọt lịm"
    ),
    CARROT(
        label = "Cà rốt",
        emoji = "🥕",
        primaryColor = Color(0xFFFF7043),
        lightColor = Color(0xFFFBE9E7),
        description = "Cà rốt giòn ngọt"
    ),
    MILK(
        label = "Hộp sữa",
        emoji = "🧃",
        primaryColor = Color(0xFF42A5F5),
        lightColor = Color(0xFFE3F2FD),
        description = "Sữa thơm mát"
    ),
    BREAD(
        label = "Bánh mì",
        emoji = "🥖",
        primaryColor = Color(0xFFFFA726),
        lightColor = Color(0xFFFFF3E0),
        description = "Bánh mì vàng giòn"
    ),
    EGG(
        label = "Quả trứng",
        emoji = "🥚",
        primaryColor = Color(0xFFFFB74D),
        lightColor = Color(0xFFFFF8E1),
        description = "Trứng gà tươi ngon"
    ),
    WATER(
        label = "Chai nước",
        emoji = "🧴",
        primaryColor = Color(0xFF26C6DA),
        lightColor = Color(0xFFE0F7FA),
        description = "Nước khoáng mát lành"
    ),
    COOKIE(
        label = "Bánh quy",
        emoji = "🍪",
        primaryColor = Color(0xFF8D6E63),
        lightColor = Color(0xFFEFEBE9),
        description = "Bánh quy sô-cô-la"
    ),
    BANANA(
        label = "Quả chuối",
        emoji = "🍌",
        primaryColor = Color(0xFFFDD835),
        lightColor = Color(0xFFFFFDE7),
        description = "Chuối vàng ngọt bùi"
    ),
    STRAWBERRY(
        label = "Dâu tây",
        emoji = "🍓",
        primaryColor = Color(0xFFE91E63),
        lightColor = Color(0xFFFCE4EC),
        description = "Dâu tây đỏ mọng"
    ),
    ORANGE(
        label = "Quả cam",
        emoji = "🍊",
        primaryColor = Color(0xFFFF9800),
        lightColor = Color(0xFFFFF3E0),
        description = "Cam mọng nước"
    ),
    DONUT(
        label = "Bánh Donut",
        emoji = "🍩",
        primaryColor = Color(0xFFEC407A),
        lightColor = Color(0xFFFCE4EC),
        description = "Bánh vòng ngọt ngào"
    ),
    CORN(
        label = "Bắp ngô",
        emoji = "🌽",
        primaryColor = Color(0xFFFFCA28),
        lightColor = Color(0xFFFFF8E1),
        description = "Bắp vàng thơm dẻo"
    )
}

data class GroceryItem(
    val id: String,
    val type: GroceryItemType,
    val label: String = type.label
)

sealed class CheckResult {
    object Idle : CheckResult()
    data class Correct(val total: Int = 6) : CheckResult()
    data class NeedMore(val current: Int, val needed: Int) : CheckResult()
    data class NeedLess(val current: Int, val excess: Int) : CheckResult()
}

data class GameLevel(
    val levelNumber: Int,
    val title: String,
    val description: String,
    val initialStallCount: Int,
    val initialReserveCount: Int,
    val itemTypes: List<GroceryItemType>,
    val targetCount: Int = 6
)

object LevelRepository {
    val predefinedLevels = listOf(
        // Level 1: 3 on stall, need +3
        GameLevel(
            levelNumber = 1,
            title = "Màn 1: Quả táo & Quả cam",
            description = "Quầy đang có 3 món. Bé thêm giúp cô cho đủ 6 món nhé!",
            initialStallCount = 3,
            initialReserveCount = 5,
            itemTypes = listOf(GroceryItemType.APPLE, GroceryItemType.ORANGE)
        ),
        // Level 2: 5 on stall, need +1
        GameLevel(
            levelNumber = 2,
            title = "Màn 2: Bánh mì & Hộp sữa",
            description = "Quầy đang có 5 món. Chỉ thiếu 1 chút nữa là đủ 6 món rồi!",
            initialStallCount = 5,
            initialReserveCount = 4,
            itemTypes = listOf(GroceryItemType.BREAD, GroceryItemType.MILK)
        ),
        // Level 3: 6 on stall, exactly 6! Test child's ability to count and confirm
        GameLevel(
            levelNumber = 3,
            title = "Màn 3: Nước khoáng & Trứng gà",
            description = "Bé hãy đếm xem trên quầy đã có bao nhiêu món nhé!",
            initialStallCount = 6,
            initialReserveCount = 3,
            itemTypes = listOf(GroceryItemType.WATER, GroceryItemType.EGG)
        ),
        // Level 4: 8 on stall, need -2
        GameLevel(
            levelNumber = 4,
            title = "Màn 4: Cà rốt & Bắp ngô",
            description = "Quầy đang có 8 món, hơi nhiều rồi! Bé bớt đi 2 món nhé!",
            initialStallCount = 8,
            initialReserveCount = 2,
            itemTypes = listOf(GroceryItemType.CARROT, GroceryItemType.CORN)
        ),
        // Level 5: 2 on stall, need +4
        GameLevel(
            levelNumber = 5,
            title = "Màn 5: Chuối vàng & Dâu tây",
            description = "Quầy mới có 2 món thôi. Bé kéo thêm trái cây lên quầy nhé!",
            initialStallCount = 2,
            initialReserveCount = 6,
            itemTypes = listOf(GroceryItemType.BANANA, GroceryItemType.STRAWBERRY)
        ),
        // Level 6: 9 on stall, need -3
        GameLevel(
            levelNumber = 6,
            title = "Màn 6: Bánh quy & Bánh Donut",
            description = "Quầy có tới 9 món bánh! Bé bớt ra ngoài để còn đúng 6 món nhé!",
            initialStallCount = 9,
            initialReserveCount = 1,
            itemTypes = listOf(GroceryItemType.COOKIE, GroceryItemType.DONUT)
        ),
        // Level 7: 4 on stall, need +2
        GameLevel(
            levelNumber = 7,
            title = "Màn 7: Gian hàng tươi ngon",
            description = "Quầy đang có 4 món. Bé hãy thêm 2 món nữa vào quầy nhé!",
            initialStallCount = 4,
            initialReserveCount = 4,
            itemTypes = listOf(GroceryItemType.APPLE, GroceryItemType.CARROT, GroceryItemType.MILK)
        ),
        // Level 8: 10 on stall, need -4
        GameLevel(
            levelNumber = 8,
            title = "Màn 8: Siêu thị đầy ắp",
            description = "Quầy có tận 10 món! Bé hãy cất bớt 4 món về kho nhé!",
            initialStallCount = 10,
            initialReserveCount = 2,
            itemTypes = listOf(GroceryItemType.ORANGE, GroceryItemType.BANANA, GroceryItemType.BREAD)
        ),
        // Level 9: 1 on stall, need +5
        GameLevel(
            levelNumber = 9,
            title = "Màn 9: Mở hàng sớm",
            description = "Mới có 1 món hàng thôi! Bé bày thêm thật nhiều cho đủ 6 món nào!",
            initialStallCount = 1,
            initialReserveCount = 7,
            itemTypes = listOf(GroceryItemType.STRAWBERRY, GroceryItemType.DONUT, GroceryItemType.WATER)
        ),
        // Level 10: 7 on stall, need -1
        GameLevel(
            levelNumber = 10,
            title = "Màn 10: Hội chợ rực rỡ",
            description = "Quầy đang có 7 món, chỉ dư 1 món thôi! Bé bớt 1 món nhé!",
            initialStallCount = 7,
            initialReserveCount = 3,
            itemTypes = listOf(GroceryItemType.APPLE, GroceryItemType.CORN, GroceryItemType.COOKIE, GroceryItemType.EGG)
        )
    )

    fun getLevel(index: Int): GameLevel {
        if (index in 1..predefinedLevels.size) {
            return predefinedLevels[index - 1]
        }
        // Procedurally generate subsequent levels for infinite fun!
        val allTypes = GroceryItemType.values().toList()
        val randomTypes = allTypes.shuffled().take(3)
        val initialStall = (0..10).random()
        val reserveCount = (4..8).random()
        return GameLevel(
            levelNumber = index,
            title = "Màn $index: Thử thách siêu vui",
            description = "Bé hãy làm cho quầy có đúng 6 món hàng nhé!",
            initialStallCount = initialStall,
            initialReserveCount = reserveCount,
            itemTypes = randomTypes
        )
    }

    fun generateItemsForLevel(level: GameLevel): Pair<List<GroceryItem>, List<GroceryItem>> {
        val stallItems = mutableListOf<GroceryItem>()
        val reserveItems = mutableListOf<GroceryItem>()
        var counter = 1

        for (i in 0 until level.initialStallCount) {
            val type = level.itemTypes[i % level.itemTypes.size]
            stallItems.add(
                GroceryItem(
                    id = "item_stall_${level.levelNumber}_${counter++}_${type.name}",
                    type = type
                )
            )
        }

        for (i in 0 until level.initialReserveCount) {
            val type = level.itemTypes[(i + stallItems.size) % level.itemTypes.size]
            reserveItems.add(
                GroceryItem(
                    id = "item_res_${level.levelNumber}_${counter++}_${type.name}",
                    type = type
                )
            )
        }

        return Pair(stallItems, reserveItems)
    }
}
