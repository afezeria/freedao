package io.github.afezeria.freedao.processor.core.processor

import java.util.concurrent.ConcurrentHashMap

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
