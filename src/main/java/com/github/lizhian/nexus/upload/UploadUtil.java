package com.github.lizhian.nexus.upload;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * UploadUtil
 * <p>
 * 2020/12/14 16:19
 *
 * @author lizhian
 */
@RequiredArgsConstructor
class UploadUtil {
    private final UploadConfig uploadConfig;
    private String tempSettings;
    public AtomicInteger jar_count = new AtomicInteger(0);
    public AtomicInteger sources_count = new AtomicInteger(0);
    public AtomicInteger pom_count = new AtomicInteger(0);

    public void upload() {
        createTempSettings();
        File path = FileUtil.file();
        parsingPath(path);
        deleteTempSettings();
        StaticLog.info("jar_count:{}", jar_count);
        StaticLog.info("sources_count:{}", sources_count);
        StaticLog.info("pom_count:{}", pom_count);
    }


    //解析目录
    private void parsingPath(File path) {
        if (path == null) {
            return;
        }
        File[] files = path.listFiles();
        if (files == null) {
            return;
        }
        for (File item : files) {
            if (item.isDirectory()) {
                parsingPath(item);
                continue;
            }
            if (uploadConfig.isUploadJarFile()
                    && item.getName().endsWith(".jar")
                    && !item.getName().endsWith("-sources.jar")) {
                uploadJar(item);
                break;
            }
            if (uploadConfig.isUploadPomFile()
                    && item.getName().endsWith(".pom")) {
                uploadPom(item, uploadConfig);
            }
        }
    }


    //上传jar包文件
    private void uploadJar(File file) {
        JarInfo jarInfo = JarInfo.from(file, uploadConfig.getLocalRepositoryPath());
        if (intercept(jarInfo)) {
            StaticLog.info("忽略jar:[{}][{}][{}]"
                    , jarInfo.getGroupId()
                    , jarInfo.getArtifactId()
                    , jarInfo.getVersion()
            );
            return;
        }
        Map<String, Object> map = BeanUtil.beanToMap(jarInfo);
        map.put("mvn", uploadConfig.getMvn());
        map.put("url", uploadConfig.getNexusRepositoryURL());
        map.put("settings", tempSettings);
        String uploadJar = StrUtil.format(Constant.uploadJar_, map);

        if (uploadConfig.isUploadJarFileWithPom() && StrUtil.isNotBlank(jarInfo.getPomFile())) {
            uploadJar = uploadJar + " -DpomFile=" + jarInfo.getPomFile();
        } else {
            uploadJar = " -DgeneratePom=true";
        }
        RuntimeUtil.execForLines(uploadJar);
        StaticLog.info("上传jar:[{}][{}][{}] 上传指令:{}"
                , jarInfo.getGroupId()
                , jarInfo.getArtifactId()
                , jarInfo.getVersion()
                , uploadJar
        );
        jar_count.addAndGet(1);
        if (uploadConfig.isUploadJarFileWithSources() && StrUtil.isNotBlank(jarInfo.getSourcesFile())) {
            String uploadSourcesJar = StrUtil.format(Constant.uploadSourcesJar_, map);
            RuntimeUtil.execForLines(uploadSourcesJar);
            StaticLog.info("上传sources:[{}][{}][{}] 上传指令:{}"
                    , jarInfo.getGroupId()
                    , jarInfo.getArtifactId()
                    , jarInfo.getVersion()
                    , uploadSourcesJar
            );
            sources_count.addAndGet(1);
        }
    }


    //上传.pom文件(纯pom没有jar)
    private void uploadPom(File file, UploadConfig uploadConfig) {
        JarInfo jarInfo = JarInfo.from(file, uploadConfig.getLocalRepositoryPath());
        if (intercept(jarInfo)) {
            StaticLog.info("忽略pom:[{}][{}][{}]"
                    , jarInfo.getGroupId()
                    , jarInfo.getArtifactId()
                    , jarInfo.getVersion()
            );
            return;
        }
        Map<String, Object> map = BeanUtil.beanToMap(jarInfo);
        map.put("mvn", uploadConfig.getMvn());
        map.put("url", uploadConfig.getNexusRepositoryURL());
        map.put("settings", tempSettings);
        String uploadPom = StrUtil.format(Constant.uploadPom_, map);
        RuntimeUtil.execForLines(uploadPom);
        StaticLog.info("上传pom:[{}][{}][{}] 上传指令:{}"
                , jarInfo.getGroupId()
                , jarInfo.getArtifactId()
                , jarInfo.getVersion()
                , uploadPom
        );
        pom_count.addAndGet(1);
    }

    //是否拦截
    private boolean intercept(JarInfo jarInfo) {
        String groupId = jarInfo.getGroupId();
        String artifactId = jarInfo.getArtifactId();
        for (String exclude : uploadConfig.getExcludes()) {
            if (groupId.contains(exclude) || artifactId.contains(exclude)) {
                return true;
            }
        }
        if (CollUtil.isEmpty(uploadConfig.getIncludes())) {
            return false;
        }
        for (String include : uploadConfig.getIncludes()) {
            if (groupId.contains(include) || artifactId.contains(include)) {
                return false;
            }
        }
        return true;
    }

    //创建临时settings.xml文件
    private void createTempSettings() {
        String content = StrUtil.format(Constant.settings_, uploadConfig.getUsername(), uploadConfig.getPassword());
        String tempSettings = uploadConfig.getLocalRepositoryPath() + "temp-settings.xml";
        FileUtil.writeUtf8String(content, tempSettings);
        this.tempSettings = tempSettings;
    }

    //删除临时settings.xml文件
    private void deleteTempSettings() {
        FileUtil.del(this.tempSettings);
    }

}
