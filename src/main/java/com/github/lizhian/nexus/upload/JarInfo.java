package com.github.lizhian.nexus.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Builder;
import lombok.Getter;

import java.io.File;
import java.util.List;

/**
 * JarInfo
 * <p>
 * 2020/12/14 16:35
 *
 * @author lizhian
 */
@Getter
@Builder
class JarInfo {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String jarFile;
    private final String pomFile;
    private final String sourcesFile;


    public static JarInfo from(File file, String localRepositoryPath) {
        String jarFile = "";
        String pomFile = "";
        String sourcesFile = "";
        String path = FileUtil.getAbsolutePath(file).replaceAll("\\\\", "/");
        if (path.endsWith(".jar")) {
            jarFile = path;
            String pomFileTemp = StrUtil.removeSuffix(path, ".jar") + ".pom";
            if (FileUtil.exist(pomFileTemp)) {
                pomFile = pomFileTemp;
            }
            String sourcesFileTemp = StrUtil.removeSuffix(path, ".jar") + "-sources.jar";
            if (FileUtil.exist(sourcesFileTemp)) {
                sourcesFile = sourcesFileTemp;
            }

        }
        if (path.endsWith(".pom")) {
            pomFile = path;
        }
        List<String> strings = StrUtil.splitTrim(StrUtil.removePrefix(path, localRepositoryPath), "/");
        int size = strings.size();
        String groupId = String.join(".", strings.subList(0, size - 3));
        String artifactId = strings.get(size - 3);
        String version = strings.get(size - 2);
        return JarInfo.builder()
                .groupId(groupId)
                .artifactId(artifactId)
                .version(version)
                .jarFile(jarFile)
                .pomFile(pomFile)
                .sourcesFile(sourcesFile)
                .build();
    }
}
