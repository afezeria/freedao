package com.github.afezeria.freedao.processor.core.spi

import com.github.afezeria.freedao.processor.core.DaoModel
import com.squareup.javapoet.TypeSpec

/**
 *
 */
interface BuildDaoService {
    val order: Int
    fun build(daoModel: DaoModel, builder: TypeSpec.Builder)

//    /**
//     * 添加平台独有的dao层实现类的内容
//     */
//    fun buildClass(daoModel: DaoModel)
//
//    /**
//     * 添加平台独有的dao层方法实现
//     * @param methodModel MethodContext 当前方法
//     * @param sqlBuilderStatementClosure Function0<Unit> 调用后给[MethodModel.builder]添加sql构建语句
//     * 这部分逻辑是通用的只要在合适的位置调用就行了
//     */
//    fun buildMethod(methodModel: MethodModel): CodeBlock
}