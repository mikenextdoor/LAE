import java.awt.image.BufferedImage
import java.io.Closeable
import java.io.File
import java.lang.ref.Cleaner
import java.net.URI
import javax.imageio.ImageIO

class TempImageCleanable(url: String) : Closeable {
    companion object {
        val cleaner: Cleaner = Cleaner.create()
    }

    private val cleanable =
        cleaner.register(
            this,
            object : Runnable {
                /*
                 * Duplicate properties to NOT capture a reference to the enclosing
                 * object, which will prevent GC to collect that object.
                 * The only variable is the url parameter that will be copied.
                 */
                //private val file = File(url.substringAfterLast('/'))

                override fun run() {
                    println("Try deleting file...")
                    if (file.exists()) {
                        file.delete()
                    }
                }
            },
        )
    /*
     * Replace the former `close()` implementation with a call to `cleanable.clean()`,
     * which will execute the registered cleaning action immediately and prevent it
     * from being executed again during finalization.
     */
    override fun close() {
        cleanable.clean()
    }
    /*
     * Copy and paste the rest of the implementation from TempImage,
     * including the properties and the init block, but without the finalize() method.
     */
    val img: BufferedImage
    val downloaded: Boolean
    private val file = File(url.substringAfterLast('/'))

    init {
        if (file.exists()) {
            img = ImageIO.read(file)
            downloaded = false
        } else {
            val conn = URI(url).toURL().openConnection()
            val kind = conn.getHeaderField("Content-Type").substringAfterLast('/')
            conn.getInputStream().use { stream ->
                img = ImageIO.read(stream)
                ImageIO.write(img, kind, file)
                downloaded = true
            }
        }
    }
}

/*
public final class TempImageCleanable$cleanable$1 implements java.lang.Runnable {
    final TempImageCleanable this$0;
    TempImageCleanable$cleanable$1(TempImageCleanable);
    public void run();
}*/
