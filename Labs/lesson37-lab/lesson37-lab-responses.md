# lesson37-lab Answers

# 3. Implementing a Custom Closeable Type
2. **Why does the file no longer exist outside the use blocks?**
   The file no longer exists the both the use blocks execute the close() function, it is implicit in the use method. So, the close() method checks if the file exists, if so deletes it.
   In the first use, it deletes the file. In the second use, the file doesn't exists, so close() executes but doesn't operate.

# 4. Finalization
4. **Why does the file still exist immediately after loadImageAndForgetClose()?**
   The file still existes because the method close() is never called until. After the call, the method System.gc() is called, but it takes time to execute the finalize().

5. **Why is Thread.sleep(100) necessary to observe the effect of finalization?**
   The Thread.sleep(100) is necessary so the System.gc() executes the finalize() function before the assertFalse function.

7. **How many times does "Try deleting file..." get printed in the last test? Why?**
   It is printed twice. One in the call of the method loadImageWithUse(), because of the use. And the second in the System.gc() call, because it runs the finalize() function.

8. **Why do finalizable objects require additional GC work?**
   Because the Thread needs to wake-up, then needs to call finalize and finally needs to remove from the heap. It needs to execute multiple thread work.

# 5. Cleaner API

3. **How many times is "Try deleting file..." printed for each test? Why?**
   In the first test, it only prints once: when System.gc() is called and executes the finalize().
   In the second test, it also only prints once. Even tho both .use and System.gc() call the close method, when the .use ends, it calls close(). The close() method calls cleanable.clean() who executes the Runnable, and marks that code as "cleaned".
4. **What happens internally when clean() is invoked?**
   Executes the Runnable and marks the code as already executed. If it's called after the first call, does not execute.
5. **Why is this behavior better than finalization?**
   It's better because, it verifies if a certain block of code as been cleaned already, if so doesn't clean again. Stoping repetitive calls.

# 6. Captured References and Cleaner Pitfalls
1. **Explain why the file is not deleted in the test that does not call close(), even after invoking System.gc() and waiting.'**
``` Kotlin
Compiled from "TempImageCleanable.kt"
public final class TempImageCleanable$cleanable$1 implements java.lang.Runnable {
  final TempImageCleanable this$0;
  TempImageCleanable$cleanable$1(TempImageCleanable);
  public void run();
}
```
In the first test the file is not deleted because inside the class there is a field. So, while Runnable is active, by Cleaner, that field is referenced. This means that the System.gc() can't reach it.