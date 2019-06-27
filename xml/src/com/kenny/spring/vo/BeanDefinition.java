package com.kenny.spring.vo;

import java.io.Serializable;

/**
 * ClassName: vo
 * Function:  基于此对象储存Bean的配置对象
 * Date:      2019/6/27 7:44
 * author     Kenny
 * version    V1.0
 */
public class BeanDefinition implements Serializable {

    private String id;
    private String pkgClass;
    private Boolean lazy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPkgClass() {
        return pkgClass;
    }

    public void setPkgClass(String pkgClass) {
        this.pkgClass = pkgClass;
    }

    public Boolean getLazy() {
        return lazy;
    }

    public void setLazy(Boolean lazy) {
        this.lazy = lazy;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "id='" + id + '\'' +
                ", pkgClass='" + pkgClass + '\'' +
                ", lazy=" + lazy +
                '}';
    }
}
