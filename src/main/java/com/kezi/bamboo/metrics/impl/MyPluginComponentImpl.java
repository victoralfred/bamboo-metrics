package com.kezi.bamboo.metrics.impl;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.kezi.bamboo.metrics.api.MyPluginComponent;

import javax.inject.Named;

@ExportAsService ({MyPluginComponent.class})
@Named ("myPluginComponent")
public class MyPluginComponentImpl implements MyPluginComponent
{

    @Override
    public String getName() {
        return "";
    }
}