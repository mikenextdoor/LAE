
package reflection



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
    val abstractFormatter = "AbstractClassFormatter"
    val jvmMappingDesc = ClassDesc.of("kotlin.jvm.JvmClassMappingKt")
    val kClassDesc = ClassDesc.of("kotlin.reflect.KClass")

    val bytes: ByteArray =
        ClassFile.of().build(ClassDesc.of(className)) { clb ->
            clb
                .withSuperclass(ClassDesc.of(abstractFormatter))
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
                            .invokestatic(
                                jvmMappingDesc,
                                "getKotlinClass",
                                MethodTypeDesc.of(kClassDesc, ClassDesc.of("java.lang.Class"))
                            )
                            .invokespecial(ClassDesc.of(abstractFormatter), INIT_NAME, MTD_void)
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
                .withMethod("mapFrom", mapFromDesc, ACC_PUBLIC) { mb ->
                    mb.withCode { cob ->
                        cob
                            .new_(ClassDesc.of(dest.qualifiedName))
                            .dup()
                        params.forEach { (srcProp, _) ->
                            cob
                                .aload(1)
                                .invokevirtual(
                                    ClassDesc.of(src.qualifiedName),
                                    srcProp.javaGetter?.name,
                                    MethodTypeDesc.of(srcProp.returnType.descriptor()),
                                )
                        }
                        cob.invokespecial(
                            dest.descriptor(),
                            INIT_NAME,
                            MethodTypeDesc.of(CD_void, params.map { (_, destParam) -> destParam.type.descriptor() }),
                        )
                        cob.areturn()
                    }
                }
                //   public java.lang.Object mapFrom(java.lang.Object);
                //    Code:
                //         0: aload_0
                //         1: aload_1
                //         2: checkcast     #10                 // class pt/isel/PersonDto
                //         5: invokevirtual #25                 // Method mapFrom:(Lpt/isel/PersonDto;)Lpt/isel/Person;
                //         8: areturn
                .withMethod("mapFrom", MethodTypeDesc.of(CD_Object, CD_Object), ACC_PUBLIC) { mb ->
                    mb.withCode { cob ->
                        cob
                            .aload(0)
                            .aload(1)
                            .checkcast(src.descriptor())
                            .invokevirtual(ClassDesc.of(className), "mapFrom", mapFromDesc)
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

    return  clazzKlass
}

