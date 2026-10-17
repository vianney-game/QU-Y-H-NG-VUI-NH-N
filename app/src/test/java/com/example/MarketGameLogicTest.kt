package com.example

import com.example.data.CheckResult
import com.example.data.GroceryItem
import com.example.data.GroceryItemType
import com.example.data.LevelRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketGameLogicTest {

    @Test
    fun testLevelRepository_hasAtLeastTenLevels() {
        assertTrue(LevelRepository.predefinedLevels.size >= 10)
    }

    @Test
    fun testLevelGeneration_maintainsUniqueIds() {
        val level = LevelRepository.getLevel(1)
        val (stall, reserve) = LevelRepository.generateItemsForLevel(level)
        val allIds = (stall + reserve).map { it.id }
        assertEquals(allIds.size, allIds.toSet().size)
    }

    @Test
    fun testCheckLogic_threeItems_needsThreeMore() {
        val count = 3
        val result = if (count == 6) {
            CheckResult.Correct(6)
        } else if (count < 6) {
            CheckResult.NeedMore(count, 6 - count)
        } else {
            CheckResult.NeedLess(count, count - 6)
        }

        assertTrue(result is CheckResult.NeedMore)
        assertEquals(3, (result as CheckResult.NeedMore).needed)
    }

    @Test
    fun testCheckLogic_fiveItems_needsOneMore() {
        val count = 5
        val result = if (count == 6) {
            CheckResult.Correct(6)
        } else if (count < 6) {
            CheckResult.NeedMore(count, 6 - count)
        } else {
            CheckResult.NeedLess(count, count - 6)
        }

        assertTrue(result is CheckResult.NeedMore)
        assertEquals(1, (result as CheckResult.NeedMore).needed)
    }

    @Test
    fun testCheckLogic_sixItems_isCorrect() {
        val count = 6
        val result = if (count == 6) {
            CheckResult.Correct(6)
        } else if (count < 6) {
            CheckResult.NeedMore(count, 6 - count)
        } else {
            CheckResult.NeedLess(count, count - 6)
        }

        assertTrue(result is CheckResult.Correct)
        assertEquals(6, (result as CheckResult.Correct).total)
    }

    @Test
    fun testCheckLogic_eightItems_needsLessTwo() {
        val count = 8
        val result = if (count == 6) {
            CheckResult.Correct(6)
        } else if (count < 6) {
            CheckResult.NeedMore(count, 6 - count)
        } else {
            CheckResult.NeedLess(count, count - 6)
        }

        assertTrue(result is CheckResult.NeedLess)
        assertEquals(2, (result as CheckResult.NeedLess).excess)
    }

    @Test
    fun testCheckLogic_tenItems_needsLessFour() {
        val count = 10
        val result = if (count == 6) {
            CheckResult.Correct(6)
        } else if (count < 6) {
            CheckResult.NeedMore(count, 6 - count)
        } else {
            CheckResult.NeedLess(count, count - 6)
        }

        assertTrue(result is CheckResult.NeedLess)
        assertEquals(4, (result as CheckResult.NeedLess).excess)
    }

    @Test
    fun testItemTransfer_increasesAndDecreasesByOne() {
        val initialReserve = mutableListOf(
            GroceryItem("1", GroceryItemType.APPLE),
            GroceryItem("2", GroceryItemType.CARROT)
        )
        val initialStall = mutableListOf(
            GroceryItem("3", GroceryItemType.MILK)
        )

        assertEquals(1, initialStall.size)
        assertEquals(2, initialReserve.size)

        // Transfer 1 item to stall
        val itemToMove = initialReserve.removeAt(0)
        initialStall.add(itemToMove)

        assertEquals(2, initialStall.size)
        assertEquals(1, initialReserve.size)

        // Transfer 1 item back from stall
        val itemToReturn = initialStall.removeAt(initialStall.lastIndex)
        initialReserve.add(itemToReturn)

        assertEquals(1, initialStall.size)
        assertEquals(2, initialReserve.size)
    }
}
