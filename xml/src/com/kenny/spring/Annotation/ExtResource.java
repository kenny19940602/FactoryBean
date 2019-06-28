package com.kenny.spring.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName: ExtResource
 * Function:  TODO
 * Date:      2019/6/28 14:05
 * author     Kenny
 * version    V1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtResource {

}
