package io.github.afezeria.freedao.processor.core

import io.github.afezeria.freedao.processor.core.processor.LazyType

/**
 * @author afezeria
 */
/**
 * 存储[io.github.afezeria.freedao.annotation.Mapping]内容
 * @property source resultSet列名
 * @property target bean类字段名
 * @property typeHandler 结果处理器类型
 * @property targetType 字段类型
 * @property constructorParameterIndex 在构造器参数中的位置，-1表示该字段使用setter方法设置
 */
data class MappingData(
    var source: String,
    val target: String,
    var typeHandlerLA: LazyType?,
    val targetTypeLA: LazyType? = null,
    val constructorParameterIndex: Int = -1,
)
