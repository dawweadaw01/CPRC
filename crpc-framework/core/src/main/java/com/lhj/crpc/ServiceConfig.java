package com.lhj.crpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @description：
 * @createTime：2023-09-0117:07
 * @author：banyanmei
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceConfig<T> {
    private Class<?> interfaceProvider;
    @Getter
    private Object ref;
    private String group = "default";

    public Class<?> getInterface() {
        return interfaceProvider;
    }

    public void setInterface(Class<?> interfaceProvider) {
        this.interfaceProvider = interfaceProvider;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
