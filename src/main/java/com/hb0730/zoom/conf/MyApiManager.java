package com.hb0730.zoom.conf;

import com.hb0730.zoom.apis.manager.DefaultApiManager;
import com.hb0730.zoom.base.ext.services.proxy.SysProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:huangbing0730@gmail">hb0730</a>
 * @date 2024/10/24
 */
@Component
@Slf4j
public class MyApiManager extends DefaultApiManager {
    @Autowired
    private SysProxyService sysProxyService;

    @Override
    public boolean checkToken(String token, String apiName) {
        return sysProxyService.checkOpenApiAuth(token, apiName);
    }
}
