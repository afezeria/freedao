package com.github.afezeria.freedao.processor.core

import javax.lang.model.element.Element

/**
 *
 */
class HandlerException(msg: String, val element: Element? = null) : RuntimeException(msg)
