package io.github.afezeria.freedao.processor.core.processor

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 *
 * key类型的字符串形式，字符串中不带空格
 *
 * 例：
 *
 *  java.util.collection.List
 *
 *  java.util.collection.List<String>
 *
 *  java.util.HashMap<K,V>
 * @author afezeria
 */
var typeCache: ConcurrentHashMap<String, LazyType> = ConcurrentHashMap()

/**
 * key格式：type:type
 */
var typeAssignableCache = ConcurrentHashMap<String, Boolean>()

val lock = ReentrantLock()

/**
 * 用于包裹注解处理器的api调用，确保对原生api的访问始终处于串行状态
 */
inline fun <T> sync(fn: () -> T): T {
    return lock.withLock(fn)
}

/**
 * 标记[fn]内的调用是线程安全的，所有对注解处理器的原生api调用都必须用该函数或[sync]包裹
 */
inline fun <T> safe(fn: () -> T): T {
    return fn()
}
