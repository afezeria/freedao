package com.github.afezeria.freedao.classic.processor

import kotlin.reflect.KClass

/**
 *
 */
interface CustomizeDaoService {
    fun annotations(): List<KClass<*>>
}