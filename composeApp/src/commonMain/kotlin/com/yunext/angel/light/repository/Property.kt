package com.yunext.angel.light.repository

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

// 定义 SerializersModule 来处理多态类型
val module = SerializersModule {

    polymorphic(Property::class) {
        subclass(IntProperty::class)
        subclass(FloatProperty::class)
        subclass(TextProperty::class)
        subclass(BooleanProperty::class)
        subclass(EnumProperty::class)
        subclass(ArrayProperty::class)
        subclass(StructProperty::class)
    }
}

// 使用自定义的 Json 配置
val json = Json {
    prettyPrint = true
    serializersModule = module
}

// 密封类表示不同的属性类型
sealed class Property {
    abstract val identifier: String
    abstract val desc: String
    abstract val name: String
    abstract val dataType: String
    abstract val accessMode: String
    abstract val required: Boolean
    abstract val specs: Specs
}

// 密封类表示不同的规格
sealed class Specs

// Int 类型属性
@Serializable
data class IntProperty(
    override val identifier: String,
    override val desc: String,
    override val name: String,
    override val dataType: String,
    override val accessMode: String,
    override val required: Boolean,
    override val specs: IntSpecs
) : Property()

// Int 类型的规格
@Serializable
data class IntSpecs(
    val max: Int,
    val min: Int,
    val unit: String,
    val unitName: String,
    val step: Int
) : Specs()

// Float 类型属性
@Serializable
data class FloatProperty(
    override val identifier: String,
    override val desc: String,
    override val name: String,
    override val dataType: String,
    override val accessMode: String,
    override val required: Boolean,
    override val specs: FloatSpecs
) : Property()

// Float 类型的规格
@Serializable
data class FloatSpecs(
    val max: Float,
    val min: Float,
    val unit: String,
    val unitName: String,
    val step: Float
) : Specs()

// Text 类型属性
@Serializable
data class TextProperty(
    override val identifier: String,
    override val desc: String,
    override val name: String,
    override val dataType: String,
    override val accessMode: String,
    override val required: Boolean,
    override val specs: TextSpecs
) : Property()

// Text 类型的规格
@Serializable
data class TextSpecs(
    val length: Int
) : Specs()

// Boolean 类型属性
@Serializable
data class BooleanProperty(
    override val identifier: String,
    override val desc: String,
    override val name: String,
    override val dataType: String,
    override val accessMode: String,
    override val required: Boolean,
    override val specs: BooleanSpecs
) : Property()

// Boolean 类型的规格
@Serializable
data class BooleanSpecs(
    val enumDesc: Map<String, String>
) : Specs()

// Enum 类型属性
@Serializable
data class EnumProperty(
    override val identifier: String,
    override val desc: String,
    override val name: String,
    override val dataType: String,
    override val accessMode: String,
    override val required: Boolean,
    override val specs: EnumSpecs
) : Property()

// Enum 类型的规格
@Serializable
data class EnumSpecs(
    val enumDesc: Map<String, String>
) : Specs()

// Array 类型属性
@Serializable
data class ArrayProperty(
    override val identifier: String,
    override val desc: String,
    override val name: String,
    override val dataType: String,
    override val accessMode: String,
    override val required: Boolean,
    override val specs: ArraySpecs
) : Property()

// Array 类型的规格
@Serializable
data class ArraySpecs(
    val length: Int,
    @Contextual
    val type: Property
) : Specs()

// Struct 类型属性
@Serializable
data class StructProperty(
    override val identifier: String,
    override val desc: String,
    override val name: String,
    override val dataType: String,
    override val accessMode: String,
    override val required: Boolean,
    override val specs: StructSpecs
) : Property()

// Struct 类型的规格
@Serializable
data class StructSpecs(

    val items: List<  @Contextual Property>
) : Specs()

// Tsl 类
@Serializable
data class Tsl(
    val id: String,
    val version: String,
    val productKey: String,
    val current: Boolean,
    val properties: List<  @Contextual Property>,
    val events: List<String>, // 假设 events 是字符串列表
    val services: List<String> // 假设 services 是字符串列表
)

fun main() {
    val jsonString = """
        {
          "id": "tsl.id",
          "version": "productKey.version",
          "productKey": "tsl.productKey",
          "current": false,
          "properties": [
            {
              "identifier": "int.identifier",
              "desc": "int.desc",
              "name": "int.name",
              "dataType": "int",
              "accessMode": "r",
              "required": false,
              "specs": {
                "max": 100,
                "min": 0,
                "unit": "int.unit",
                "unitName": "int.unitName",
                "step": 1
              }
            },
            {
              "identifier": "float.identifier",
              "desc": "float.desc",
              "name": "float.name",
              "dataType": "float",
              "accessMode": "r",
              "required": false,
              "specs": {
                "max": 100.0,
                "min": 0.0,
                "unit": "int.unit",
                "unitName": "int.unitName",
                "step": 1.0
              }
            },
            {
              "identifier": "text.identifier",
              "desc": "text.desc",
              "name": "text.name",
              "dataType": "text",
              "accessMode": "r",
              "required": false,
              "specs": {
                "length": 10240
              }
            },
            {
              "identifier": "boolean.identifier",
              "desc": "boolean.desc",
              "name": "boolean.name",
              "dataType": "bool",
              "accessMode": "r",
              "required": false,
              "specs": {
                "enumDesc": {
                  "1": "boolean.true",
                  "0": "boolean.false"
                }
              }
            },
            {
              "identifier": "textEnum.identifier",
              "desc": "textEnum.desc",
              "name": "textEnum.name",
              "dataType": "enum",
              "accessMode": "r",
              "required": false,
              "specs": {
                "enumDesc": {
                  "dong": "*东*",
                  "xi": "*西*",
                  "nan": "*南*",
                  "bei": "*北*"
                }
              }
            },
            {
              "identifier": "array.identifier",
              "desc": "array.desc",
              "name": "array.name",
              "dataType": "array",
              "accessMode": "r",
              "required": false,
              "specs": {
                "length": 10,
                "type": {
                  "identifier": "array.int.identifier",
                  "desc": "array.int.desc",
                  "name": "array.int.name",
                  "dataType": "int",
                  "accessMode": "r",
                  "required": false,
                  "specs": {
                    "max": 100,
                    "min": 0,
                    "unit": "int.unit",
                    "unitName": "int.unitName",
                    "step": 1
                  }
                }
              }
            },
            {
              "identifier": "struct.identifier",
              "desc": "struct.desc",
              "name": "struct.name",
              "dataType": "struct",
              "accessMode": "r",
              "required": false,
              "specs": {
                "items": [
                  {
                    "identifier": "struct.int.identifier",
                    "desc": "struct.int.desc",
                    "name": "struct.int.name",
                    "dataType": "int",
                    "accessMode": "r",
                    "required": false,
                    "specs": {
                      "max": 100,
                      "min": 0,
                      "unit": "int.unit",
                      "unitName": "int.unitName",
                      "step": 1
                    }
                  },
                  {
                    "identifier": "struct.textEnum.identifier",
                    "desc": "struct.textEnum.desc",
                    "name": "struct.textEnum.name",
                    "dataType": "enum",
                    "accessMode": "r",
                    "required": false,
                    "specs": {
                      "enumDesc": {
                        "dong": "*东*",
                        "xi": "*西*",
                        "nan": "*南*",
                        "bei": "*北*"
                      }
                    }
                  }
                ]
              }
            }
          ],
          "events": [],
          "services": []
        }
    """.trimIndent()
    val tsl = Json.decodeFromString<Tsl>(jsonString)
    println(tsl)
}