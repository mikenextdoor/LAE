import java.io.File
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.*
import java.lang.classfile.Interfaces
import java.lang.classfile.Label
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs.*
import java.lang.constant.MethodTypeDesc
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/*
public final class RetryPolicy {
  private final int maxRetries;

  public RetryPolicy(int);
    Code:
       0: aload_0
       1: invokespecial #9                  // Method java/lang/Object."<init>":()V
       4: aload_0
       5: iload_1
       6: putfield      #13                 // Field maxRetries:I
       9: return

  public final boolean canRetry(int);
    Code:
       0: iload_1
       1: aload_0
       2: getfield      #13                 // Field maxRetries:I
       5: if_icmpge     12
       8: iconst_1
       9: goto          13
      12: iconst_0
      13: ireturn
}
*/

fun retryPolicyBuilder(): KClass<*> {
    val className = "RetryPolicy"
    val bytes: ByteArray =
        ClassFile.of().build(ClassDesc.of(className)) { clb ->
            clb
                .withFlags(ACC_PUBLIC or ACC_FINAL)
                .withField("maxRetries", CD_int, ACC_PRIVATE or ACC_FINAL)
                .withMethod(INIT_NAME, MethodTypeDesc.of(CD_void, CD_int), ACC_PUBLIC) { mb ->
                    mb.withCode { cob ->
                        cob
                            .aload(0)
                            .invokespecial(CD_Object, INIT_NAME, MTD_void)
                            .aload(0)
                            .iload(1)
                            .putfield(ClassDesc.of(className), "maxRetries", CD_int)
                            .return_()
                    }
                }.withMethod("canRetry", MethodTypeDesc.of(CD_boolean, CD_int), ACC_PUBLIC or ACC_FINAL) { mb ->
                    mb.withCode { cob ->
                        val falseLabel = cob.newLabel()
                        val trueLabel = cob.newLabel()
                        cob
                            .iload(1)
                            .aload(0)
                            .getfield(ClassDesc.of(className), "maxRetries", CD_int)
                            .if_icmpge(falseLabel)
                            .iconst_1()
                            .goto_(trueLabel)
                            .labelBinding(falseLabel)
                            .iconst_0()
                            .labelBinding(trueLabel)
                            .ireturn()
                    }
                }
        }
    val resourcePath =
        Unit::class.java
            .getResource("/")
            ?.toURI()
            ?.path
    File(resourcePath, className.replace('.', '/') + ".class")
        .also { it.parentFile.mkdirs() } // Create directories if they do not exist
        .writeBytes(bytes)

    val barKlass =
        Unit::class.java.classLoader
            .loadClass("RetryPolicy")
            .kotlin

    return barKlass
}