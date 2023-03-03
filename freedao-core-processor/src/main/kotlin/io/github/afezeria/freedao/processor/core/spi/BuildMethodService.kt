package io.github.afezeria.freedao.processor.core.spi

import com.squareup.javapoet.CodeBlock
import io.github.afezeria.freedao.processor.core.method.AbstractMethodDefinition

/**
 *
 * @author afezeria
 */
interface BuildMethodService {
    /**
     * 添加平台独有的dao层方法实现
     * @param methodHandler MethodContext 当前方法
     * 这部分逻辑是通用的只要在合适的位置调用就行了
     */
    fun build(methodHandler: AbstractMethodDefinition): CodeBlock
}