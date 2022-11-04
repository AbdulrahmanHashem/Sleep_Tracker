package com.example.android.sleeptracker

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.sleeptracker.database.SleepDatabase
import com.example.android.sleeptracker.database.SleepDatabaseDao
import com.example.android.sleeptracker.database.SleepNight
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var sleepDatabaseDao: SleepDatabaseDao
    private lateinit var db: SleepDatabase

    @Before
    fun createDb(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, SleepDatabase::class.java).allowMainThreadQueries().build()
        sleepDatabaseDao = db.sleepDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun CloseDb() {
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun insertAndGetNight() {
        val night = SleepNight()
        sleepDatabaseDao.insert(night)
        val tonight = sleepDatabaseDao.getTonight()
        assertEquals(tonight?.sleepQuality, -1)
    }
}