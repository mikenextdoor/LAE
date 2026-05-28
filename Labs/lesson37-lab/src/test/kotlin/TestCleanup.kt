package pt.isel

import TempImageCleanable
import java.io.File
import java.io.FileWriter
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class TestCleanup {
    @Test
    fun `write file`() {
        File("temp.txt").delete() // Ensure file does not exist before test
        val writer = FileWriter("temp.txt")
        writer.write("Hello JVM")
        assertTrue(File("temp.txt").exists())
    }

    @Test(expected = IllegalStateException::class)
    fun `write file using finally`() {
        File("temp.txt").delete() // Ensure file does not exist before test
        var writer: FileWriter? = null
        try {
            writer = FileWriter("temp.txt")
            writer.write("Hello JVM")
            // Force failure
            error("Simulated failure")
        } finally {
            println("Closing writer")
            writer?.close()
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `write file using use`() {
        File("temp2.txt").delete() // Ensure file does not exist before test
        FileWriter("temp2.txt").use { writer ->
            writer.write("Hello JVM 2")
            // Force failure
            error("Simulated failure")
        }
    }

    @Test
    fun `using TempImage with use`() {
        File("duke_star7.png").delete() // Ensure file does not exist before test
        TempImage("https://dev.java/assets/images/duke/duke_star7.png")
            .use {
                assertTrue(it.downloaded)
                TempImage("https://dev.java/assets/images/duke/duke_star7.png")
                    .use { second ->
                        assertFalse(second.downloaded)
                    }
            }
        assertFalse(File("duke_star7.png").exists())
    }

    @Test
    fun `using TempImage and forgetting to close`() {
        File("duke_star7.png").delete() // Ensure file does not exist before test
        TempImage("https://dev.java/assets/images/duke/duke_star7.png").also {
            assertTrue(it.downloaded)
        }
        TempImage("https://dev.java/assets/images/duke/duke_star7.png").also {
            assertFalse(it.downloaded)
        }
        assertTrue(File("duke_star7.png").exists())
    }

    @Test
    fun `using TempImage and forgetting to close but file is deleted through finalization`() {
        fun loadImageAndForgetClose() {
            TempImage("https://dev.java/assets/images/duke/duke_star7.png")
                .also {
                    // Not calling close !!!
                    assertTrue(it.downloaded)
                }
        }
        File("duke_star7.png").delete() // Ensure file does not exist before test
        loadImageAndForgetClose()
        assertTrue(File("duke_star7.png").exists())
        /*
        * Once an object is eligible for GC, finalization may occur on a different thread,
        * so you might need to pause briefly to observe the changes.
        */
        System.gc()
        Thread.sleep(100)
        assertFalse(File("duke_star7.png").exists())
    }

    @Test
    fun `using TempImage with use and still observe finalization`() {
        fun loadImageWithUse() {
            TempImage("https://dev.java/assets/images/duke/duke_star7.png")
                .use {
                    // Calling close !!!
                    assertTrue(it.downloaded)
                }
        }
        File("duke_star7.png").delete() // Ensure file does not exist before test
        loadImageWithUse()
        assertFalse(File("duke_star7.png").exists())
        /*
        * Once an object is eligible for GC, finalization may occur on a different thread,
        * so you might need to pause briefly to observe the changes.
        */
        System.gc()
        Thread.sleep(100)
        assertFalse(File("duke_star7.png").exists())
    }

    @Test
    fun `using TempImageCleanable and forgetting to close but file is deleted through finalization`() {
        fun loadImageAndForgetClose() {
            TempImageCleanable("https://dev.java/assets/images/duke/duke_star7.png")
                .also {
                    // Not calling close !!!
                    assertTrue(it.downloaded)
                }
        }
        File("duke_star7.png").delete() // Ensure file does not exist before test
        loadImageAndForgetClose()
        assertTrue(File("duke_star7.png").exists())
        /*
        * Once an object is eligible for GC, finalization may occur on a different thread,
        * so you might need to pause briefly to observe the changes.
        */
        System.gc()
        Thread.sleep(100)
        assertFalse(File("duke_star7.png").exists())
    }

    @Test
    fun `using TempImageCleanable with use and still observe finalization`() {
        fun loadImageWithUse() {
            TempImageCleanable("https://dev.java/assets/images/duke/duke_star7.png")
                .use {
                    // Calling close !!!
                    assertTrue(it.downloaded)
                }
        }
        File("duke_star7.png").delete() // Ensure file does not exist before test
        loadImageWithUse()
        assertFalse(File("duke_star7.png").exists())
        /*
        * Once an object is eligible for GC, finalization may occur on a different thread,
        * so you might need to pause briefly to observe the changes.
        */
        System.gc()
        Thread.sleep(100)
        assertFalse(File("duke_star7.png").exists())
    }
}