import java.io.File
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.*
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs.CD_int
import java.lang.constant.ConstantDescs.CD_float
import java.lang.constant.MethodTypeDesc
import kotlin.reflect.KFunction
import kotlin.reflect.full.staticFunctions

/*
public final class FooKt {
public static final float Foo(int, float, int, float);
    Code:
        0: iload_0
        1: i2f
        2: iload_0
        3: i2f
        4: fload_1
        5: fmul
        6: fsub
        7: iload_2
        8: i2f
        9: fadd
        10: fload_3
        11: fsub
        12: freturn
}
*/


fun buildFoo(): KFunction<*> {
    val className = "FooKt"
    val methodName = "Foo"
    val bytes: ByteArray =
        ClassFile.of().build(ClassDesc.of(className)) { clb ->
            clb
                .withFlags(ACC_PUBLIC or ACC_FINAL)
                .withMethod(methodName, MethodTypeDesc.of(CD_float, CD_int, CD_float, CD_int, CD_float), ACC_PUBLIC or ACC_FINAL or ACC_STATIC) { mb ->
                    mb.withCode { cob ->
                        cob
                            .iload(0)
                            .i2f()
                            .iload(0)
                            .i2f()
                            .fload(1)
                            .fmul()
                            .fsub()
                            .iload(2)
                            .i2f()
                            .fadd()
                            .fload(3)
                            .fsub()
                            .freturn()
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
            .loadClass(className)
            .kotlin

    return barKlass.staticFunctions.first { it.name == methodName }
}