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

inline fun <T> sync(fn: () -> T): T {
    return lock.withLock(fn)
}



