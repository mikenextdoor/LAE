

package reflection




import java.io.File
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.*
import java.lang.classfile.Interfaces
import java.lang.classfile.Label
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDesc
import java.lang.constant.ConstantDescs.*
import java.lang.constant.MethodTypeDesc
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import java.sql.Date
import javax.management.Query.match
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter


fun <T: Any> buildClassFormatter(entityClass: KClass<T>) : AbstractClassFormatter<T> {
    val className = "EstatisticaAbusoBaseLine"

    val bytes: ByteArray =
        ClassFile.of().build(ClassDesc.of("EstatisticaAbusoBaseLine")) { clb ->
            clb
                .withSuperclass(ClassDesc.of("reflection.AbstractClassFormatter"))
//                public EstatisticaAbusoBaseLine();
//                    Code:
//                        0: aload_0
//                        1: ldc           #1                  // class org/example/classes/EstatisticaAbuso
//                        3: invokestatic  #3                  // Method kotlin/jvm/JvmClassMappingKt.getKotlinClass:(Ljava/lang/Class;)Lkotlin/reflect/KClass;
//                        6: invokespecial #9                  // Method reflection/AbstractClassFormatter."<init>":(Lkotlin/reflect/KClass;)V
//                        9: return
                .withMethod(INIT_NAME, MTD_void, ACC_PUBLIC) { mb ->
                    mb.withCode { cob ->
                        cob
                            .aload(0)
                            .ldc(ClassDesc.of(entityClass.qualifiedName))
                            .invokestatic(ClassDesc.of("kotlin.jvm.JvmClassMappingKt"), "getKotlinClass",
                                MethodTypeDesc.of(ClassDesc.of("kotlin.reflect.KClass"), ClassDesc.of("java.lang.Class"))
                            )
                            .invokespecial(ClassDesc.of("reflection.AbstractClassFormatter"), INIT_NAME,
                                MethodTypeDesc.of(CD_void, ClassDesc.of("kotlin.reflect.KClass"))
                            )
                            .return_()
                    }
                }
//                public java.util.List<org.example.classes.EstatisticaAbuso> toClassFormatter(java.sql.ResultSet) throws java.sql.SQLException;
//                    Code:
//                        0: new           #15                 // class java/util/ArrayList
//                        3: dup
//                        4: invokespecial #17                 // Method java/util/ArrayList."<init>":()V
//                        7: astore_2
//                        8: aload_1
//                        9: invokeinterface #20,  1           // InterfaceMethod java/sql/ResultSet.next:()Z
//                        14: ifeq          50
//                        17: aload_2
//                        18: new           #1                  // class org/example/classes/EstatisticaAbuso
//                        21: dup
//                        22: aload_1
//                        23: ldc           #26                 // String EAbusiva
//                        25: invokeinterface #28,  2           // InterfaceMethod java/sql/ResultSet.getBoolean:(Ljava/lang/String;)Z
//                        30: aload_1
//                        31: ldc           #32                 // String Total
//                        33: invokeinterface #34,  2           // InterfaceMethod java/sql/ResultSet.getInt:(Ljava/lang/String;)I
//                        38: invokespecial #38                 // Method org/example/classes/EstatisticaAbuso."<init>":(ZI)V
//                        41: invokeinterface #41,  2           // InterfaceMethod java/util/List.add:(Ljava/lang/Object;)Z
//                        46: pop
//                        47: goto          8
//                        50: aload_2
//                        51: areturn
                .withMethod("toClassFormatter", MethodTypeDesc.of(ClassDesc.of("java.util.List"), ClassDesc.of("java.sql.ResultSet")), ACC_PUBLIC) { mb ->
                    mb.withCode { cob ->
                        val loopEnd = cob.newLabel()
                        val loopStart = cob.newLabel()

                        cob
                            .new_(ClassDesc.of("java.util.ArrayList"))
                            .dup()
                            .invokespecial(ClassDesc.of("java.util.ArrayList"), INIT_NAME, MTD_void)
                            .astore(2)
                            .labelBinding(loopStart)
                            .aload(1)
                            .invokeinterface(ClassDesc.of("java.sql.ResultSet"), "next", MethodTypeDesc.of(CD_boolean))
                            .ifeq(loopEnd)
                            .aload(2)
                            .new_(ClassDesc.of("org.example.classes.EstatisticaAbuso"))
                            .dup()
                            .aload(1)
                            .ldc("EAbusiva" as ConstantDesc)
                            .invokeinterface(ClassDesc.of("java.sql.ResultSet"), "getBoolean", MethodTypeDesc.of(CD_boolean, ClassDesc.of("java.lang.String")))
                            .aload(1)
                            .ldc("Total" as ConstantDesc)
                            .invokeinterface(ClassDesc.of("java.sql.ResultSet"), "getInt", MethodTypeDesc.of(CD_int, ClassDesc.of("java.lang.String")))
                            .invokespecial(ClassDesc.of("org.example.classes.EstatisticaAbuso"), INIT_NAME, MethodTypeDesc.of(CD_void, CD_boolean, CD_int))
                            .invokeinterface(ClassDesc.of("java.util.List"), "add", MethodTypeDesc.of(CD_boolean, CD_Object))
                            .pop()
                            .goto_(loopStart)
                            .labelBinding(loopEnd)
                            .aload(2)
                            .areturn()
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
    var clazzKlass = Unit::class.java.classLoader
        .loadClass(className)
        .kotlin

    return  clazzKlass.createInstance() as AbstractClassFormatter<T>
}

/*

fun KClass<*>.descriptor(): ClassDesc =
    if (this.java.isPrimitive) {
        val kClass = Char::class
        val desc =
            when (this) {
                kClass -> {
                    CD_char
                }

                Short::class -> {
                    CD_short
                }

                Int::class -> {
                    CD_int
                }

                Long::class -> {
                    CD_long
                }

                Float::class -> {
                    CD_float
                }

                Double::class -> {
                    CD_double
                }

                Boolean::class -> {
                    CD_boolean
                }

                else -> {
                    throw IllegalStateException("No primitive type for ${this.qualifiedName}!")
                }
            }
        desc
    } else {
        ClassDesc.of(this.java.name)
    }

fun KType.descriptor(): ClassDesc {
    val klass = this.classifier as KClass<*>
    return klass.descriptor()
}


*/
