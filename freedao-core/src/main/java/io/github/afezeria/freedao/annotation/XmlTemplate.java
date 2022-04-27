package io.github.afezeria.freedao.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 *
 */
@Target(ElementType.METHOD)
public @interface XmlTemplate {
    //language=Xml
    String value();
}
