package com.hb0730.zoom.conf;

import com.hb0730.zoom.sofa.rpc.core.factory.CustomRpcProxyConfig;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:huangbing0730@gmail">hb0730</a>
 * @date 2024/10/18
 */
public class MyCustomRpcProxyConfig implements CustomRpcProxyConfig {
    @Override
    public List<String> getIgnoreItems() {
        //忽略zoom-app应用
        return Arrays.asList("zoom-app");
    }
}
