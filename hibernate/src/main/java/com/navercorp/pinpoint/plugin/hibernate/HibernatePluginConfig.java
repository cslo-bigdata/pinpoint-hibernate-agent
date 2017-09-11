/*
 * Copyright 2016 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.navercorp.pinpoint.plugin.hibernate;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;

/**
 * 2017年9月8日09:15:48
 * @author caiwenqing
 *
 */
public class HibernatePluginConfig {

    private final boolean hibernateEnabled;

    public HibernatePluginConfig(ProfilerConfig profilerConfig) {
        this.hibernateEnabled = profilerConfig.readBoolean("profiler.orm.mybatis", true);
    }

    public boolean isHibernateEnabled() {
        return hibernateEnabled;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HibernatePluginConfig{");
        sb.append("hibernateEnabled=").append(hibernateEnabled);
        sb.append('}');
        return sb.toString();
    }
}
