package com.cmk.app.ext

fun <T> Collection<T>.print(map: (T) -> String) =
    StringBuilder("\n[").also {
        //'遍历集合元素，通过 map 表达式将元素转换成感兴趣的字串，并独占一行'
        this.forEach { e ->
            it.append("\n\t${map(e)},")
            it.append("\n]")
        }.toString()
    }

fun <K, V> Map<K, V?>.print(map: (V?) -> String): String =
    StringBuilder("\n{").also { sb ->
        this.iterator().forEach { entry ->
            sb.append("\n\t[${entry.key}] = ${map(entry.value)}")
        }
        sb.append("\n}")
    }.toString()

/**
 * 打印 Map，生成结构化键值对子串
 * @param space 行缩进量
 */
fun <K, V> Map<K, V?>.print(space: Int = 0): String {
    //'生成当前层次的行缩进，用space个空格表示，当前层次每一行内容都需要带上缩进'
    val indent = StringBuilder().apply {
        repeat(space) { append(" ") }
    }.toString()
    return StringBuilder("\n${indent}{").also { sb ->
        this.iterator().forEach { entry ->
            //'如果值是 Map 类型，则递归调用print()生成其结构化键值对子串，否则返回值本身'
            val value = entry.value.let { v ->
                (v as? Map<*, *>)?.print("${indent}${entry.key} = ".length) ?: v.toString()
            }
            sb.append("\n\t${indent}[${entry.key}] = $value,")
        }
        sb.append("\n${indent}}")
    }.toString()
}

