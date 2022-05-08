package io.github.afezeria.freedao.annotation;


import org.intellij.lang.annotations.Language;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 *
 */
@Target(ElementType.METHOD)
public @interface XmlTemplate {
    @Language(value = "Xml")
    String value();
}
