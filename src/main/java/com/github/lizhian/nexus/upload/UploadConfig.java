package com.github.lizhian.nexus.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * UploadConfig
 * <p>
 * 2020/12/14 15:26
 *
 * @author lizhian
 */
@Getter
public class UploadConfig {
    @Setter
    private String username = "admin";
    @Setter
    private String password = "admin";
    @Setter
    public String mvn = SystemUtil.getOsInfo().isWindows() ? "mvn.cmd" : "mvn";
    @Setter
    private boolean uploadPomFile = true;
    @Setter
    private boolean uploadJarFile = true;
    @Setter
    private boolean uploadJarFileWithPom = true;
    @Setter
    private boolean uploadJarFileWithSources = true;

    public String localRepositoryPath = "C:/Users/someone/.m2/repository/";
    public String nexusRepositoryURL = "http://127.0.0.1:8080/nexus/content/repositories/thirdparty/";
    public final Set<String> includes = CollUtil.newHashSet();
    public final Set<String> excludes = CollUtil.newHashSet();

    public void setLocalRepositoryPath(String path) {
        path = StrUtil.replace(path, "\\", "/");
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        this.localRepositoryPath = path;
    }

    public void setNexusRepositoryURL(String url) {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        this.nexusRepositoryURL = url;
    }

    public void include(String... strings) {
        for (String string : strings) {
            if (StrUtil.isBlank(string)) {
                continue;
            }
            this.includes.add(string);
        }
    }

    public boolean hasIncludes() {
        return CollUtil.isNotEmpty(this.includes);
    }

    public void exclude(String... strings) {
        for (String string : strings) {
            if (StrUtil.isBlank(string)) {
                continue;
            }
            this.excludes.add(string);
        }
    }

    public boolean hasExcludes() {
        return CollUtil.isNotEmpty(this.excludes);
    }

    public void upload() {
        new UploadUtil(this).upload();
    }


}
