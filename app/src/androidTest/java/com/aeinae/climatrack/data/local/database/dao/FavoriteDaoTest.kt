package com.aeinae.climatrack.data.local.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.aeinae.climatrack.data.local.database.AppDataBase
import com.aeinae.climatrack.data.local.database.entity.FavoriteEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteDaoTest {

    private lateinit var database: AppDataBase
    private lateinit var dao: FavoriteDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDataBase::class.java
        ).allowMainThreadQueries().build()

        dao = database.favoriteDao()
    }

    @After
    fun teardown() {
        database.close()
    }


    private fun cairo() = FavoriteEntity(
        cityName = "Cairo",
        country = "EG",
        latitude = 30.0444,
        longitude = 31.2357
    )

    private fun london() = FavoriteEntity(
        cityName = "London",
        country = "GB",
        latitude = 51.5074,
        longitude = -0.1278
    )

    private fun tokyo() = FavoriteEntity(
        cityName = "Tokyo",
        country = "JP",
        latitude = 35.6762,
        longitude = 139.6503
    )



    @Test
    fun insertFavorite_thenGetAll_containsItem() = runTest {
        dao.insertFavorite(cairo())

        dao.getAllFavorites().test {
            val favorites = awaitItem()
            assertEquals(1, favorites.size)
            assertEquals("Cairo", favorites[0].cityName)
            assertEquals("EG", favorites[0].country)
            cancel()
        }
    }

    @Test
    fun deleteFavorite_itemRemoved() = runTest {
        dao.insertFavorite(cairo())

        var inserted: FavoriteEntity? = null
        dao.getAllFavorites().test {
            val favorites = awaitItem()
            assertEquals(1, favorites.size)
            inserted = favorites[0]
            cancel()
        }

        dao.delete(inserted!!)

        dao.getAllFavorites().test {
            val favorites = awaitItem()
            assertTrue(favorites.isEmpty())
            cancel()
        }
    }

    @Test
    fun insertMultiple_getAllReturnsAll() = runTest {
        dao.insertFavorite(cairo())
        dao.insertFavorite(london())
        dao.insertFavorite(tokyo())

        dao.getAllFavorites().test {
            val favorites = awaitItem()
            assertEquals(3, favorites.size)
            val cityNames = favorites.map { it.cityName }
            assertTrue(cityNames.contains("Cairo"))
            assertTrue(cityNames.contains("London"))
            assertTrue(cityNames.contains("Tokyo"))
            cancel()
        }
    }

    @Test
    fun getFavoriteById_existingId_returnsFavorite() = runTest {
        dao.insertFavorite(cairo())

        var inserted: FavoriteEntity? = null
        dao.getAllFavorites().test {
            inserted = awaitItem()[0]
            cancel()
        }

        val result = dao.getFavorite(inserted!!.id)
        assertEquals("Cairo", result?.cityName)
        assertEquals("EG", result?.country)
    }

    @Test
    fun getFavoriteById_nonExistingId_returnsNull() = runTest {
        val result = dao.getFavorite(999)
        assertNull(result)
    }

    @Test
    fun insertDuplicateCoordinates_bothExist() = runTest {
        val cairo1 = FavoriteEntity(
            cityName = "Cairo",
            country = "EG",
            latitude = 30.0444,
            longitude = 31.2357
        )
        val cairo2 = FavoriteEntity(
            cityName = "Cairo Duplicate",
            country = "EG",
            latitude = 30.0444,
            longitude = 31.2357
        )

        dao.insertFavorite(cairo1)
        dao.insertFavorite(cairo2)

        dao.getAllFavorites().test {
            val favorites = awaitItem()
            assertEquals(2, favorites.size)
            cancel()
        }
    }
}