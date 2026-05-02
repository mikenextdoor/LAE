package reflection

import java.io.File
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.classfile.Interfaces
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs.*
import java.lang.constant.MethodTypeDesc
import java.sql.Date
import javax.management.Query.match
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter

private fun <T : Any> buildFormatter(clazz: KClass<T>): KClass<out Any> {

    // 1. Select a constructor with a matching parameter
    // for each property from the source type
    val ctor = clazz.constructors.first()

    val params = ctor.parameters.map { param ->
        val property = clazz.memberProperties.first { it.name == param.name }
        val columnNamee = property.findAnnotation<Column>()?.columnName ?: param.name
        val kClass = param.type.classifier as KClass<*>
        columnNamee to kClass
    }

    val className = "reflection.${clazz.simpleName}Formatter"
    val mapper = ClassDesc.of(ClassFormatter::class.qualifiedName)
    val resultSetDesc = ClassDesc.of("java.sql.ResultSet")

    val bytes: ByteArray =
        ClassFile.of().build(ClassDesc.of(className)) { clb ->
            clb
                .withInterfaces(Interfaces.ofSymbols(mapper).interfaces())
                //   public pt.isel.PersonDto2Person();
                //    Code:
                //         0: aload_0
                //         1: invokespecial #1    // Method java/lang/Object."<init>":()V
                //         4: return
                .withMethod(INIT_NAME, MTD_void, ACC_PUBLIC) { mb ->
                    mb.withCode { cob ->
                        cob
                            .aload(0)
                            .invokespecial(CD_Object, INIT_NAME, MTD_void)
                            .return_()
                    }
                }
                //   public pt.isel.Person mapFrom(pt.isel.PersonDto);
                //    Code:
                //         0: new           #7                  // class pt/isel/Person
                //         3: dup
                //         4: aload_1
                //         5: invokevirtual #9                  // Method pt/isel/PersonDto.getName:()Ljava/lang/String;
                //         8: aload_1
                //         9: invokevirtual #15                 // Method pt/isel/PersonDto.getCountry:()Ljava/lang/String;
                //        12: aload_1
                //        13: invokevirtual #18                 // Method pt/isel/PersonDto.getBorn:()I
                //        16: invokespecial #22                 // Method pt/isel/Person."<init>":(Ljava/lang/String;Ljava/lang/String;I)V
                //        19: areturn
                //
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
    return Unit::class.java.classLoader
        .loadClass(className)
        .kotlin
}

fun KClass<*>.descriptor(): ClassDesc =
    if (this.java.isPrimitive) {
        val desc =
            when (this) {
                Char::class -> {
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