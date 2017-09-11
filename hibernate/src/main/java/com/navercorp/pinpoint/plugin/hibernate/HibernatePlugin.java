/*
 * Copyright 2015 NAVER Corp.
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
 */

package com.navercorp.pinpoint.plugin.hibernate;

import java.security.ProtectionDomain;
import java.util.List;

import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentException;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentMethod;
import com.navercorp.pinpoint.bootstrap.instrument.MethodFilter;
import com.navercorp.pinpoint.bootstrap.instrument.MethodFilters;
import com.navercorp.pinpoint.bootstrap.instrument.Instrumentor;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformCallback;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplate;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplateAware;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.ExecutionPolicy;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeFactory;

/**
 * 2017年9月8日09:15:14
 * @author caiwenqing
 *
 */
public class HibernatePlugin implements ProfilerPlugin, TransformTemplateAware {
	//自动以int code 为5600
    public static final ServiceType HIBERNATE = ServiceTypeFactory.of(5600, "HIBERNATE");

    private static final String HIBERNATE_SCOPE = "HIBERNATE_SCOPE";

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());

    private TransformTemplate transformTemplate;

    @Override
    public void setup(ProfilerPluginSetupContext context) {

        HibernatePluginConfig hibernatePluginConfig = new HibernatePluginConfig(context.getConfig());
        if (logger.isInfoEnabled()) {
            logger.info("HibernatePlugin config:{}", hibernatePluginConfig);
        }

        if (hibernatePluginConfig .isHibernateEnabled()) {
            addInterceptorsForSqlSession();
        }
    }

    // SqlSession implementations
    private void addInterceptorsForSqlSession() {
        //final MethodFilter methodFilter = MethodFilters.name("selectOne", "selectList", "selectMap", "select", "insert", "update", "delete");
        //final String[] sqlSessionImpls = { "org.apache.ibatis.session.defaults.DefaultSqlSession", "org.mybatis.spring.SqlSessionTemplate" };
        final MethodFilter methodFilter = MethodFilters.name("select", "insert", "update", "delete", "save", "saveorupdate", "get", "load", 
        		"createQuery", "setParameter", 
        		"createSqlQuery", 
        		"createCriteria", "add");
        final String[] sqlSessionImpls = { "org.hibernate.Session", "org.hibernate.Query" , "org.hibernate.SQLQuery" , "org.hibernate.Criteria"};
        //org.hibernate.Session
        //save,delete,update,saveorupdate,get,load,
        //org.hibernate.Query
        //createQuery,setParameter
        //org.hibernate.SQLQuery
        //createSqlQuery,
        //org.hibernate.Criteria
        //createCriteria,add
        
        
        for (final String sqlSession : sqlSessionImpls) {
            transformTemplate.transform(sqlSession, new TransformCallback() {

                @Override
                public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader,
                                            String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                                            byte[] classfileBuffer) throws InstrumentException {
                    
                    final InstrumentClass target = instrumentor.getInstrumentClass(loader, sqlSession, classfileBuffer);

                    final List<InstrumentMethod> methodsToTrace = target.getDeclaredMethods(methodFilter);
                    for (InstrumentMethod methodToTrace : methodsToTrace) {
                        String sqlSessionInterceptor = "com.navercorp.pinpoint.plugin.mybatis.interceptor.SqlSessionInterceptor";
                        methodToTrace.addScopedInterceptor(sqlSessionInterceptor, HIBERNATE_SCOPE, ExecutionPolicy.BOUNDARY);
                    }
                    
                    return target.toBytecode();
                }
            });

        }
    }

    @Override
    public void setTransformTemplate(TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }
}
