package com.github.lizhian.nexus.upload;

/**
 * Constont
 * <p>
 * 2020/12/14 16:19
 *
 * @author lizhian
 */
class Constant {
    public static final String settings_ = "" +
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\"\n" +
            "          xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "          xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd\">\n" +
            "  <servers>\n" +
            "    <server>\n" +
            "      <id>upload-server</id>\n" +
            "      <username>{}</username>\n" +
            "      <password>{}</password>\n" +
            "    </server>\n" +
            "  </servers>\n" +
            "</settings>";

    public static final String uploadJar_ = "" +
            "{mvn} deploy:deploy-file"
            + " -Durl={url}"
            + " -DrepositoryId=upload-server -s {settings}"
            + " -Dpackaging=jar"
            + " -DgroupId={groupId}"
            + " -DartifactId={artifactId}"
            + " -Dversion={version}"
            + " -Dfile={jarFile}";

    public static final String uploadSourcesJar_ = "" +
            "{mvn} deploy:deploy-file"
            + " -Durl={url}"
            + " -DrepositoryId=upload-server -s {settings}"
            + " -Dpackaging=jar"
            + " -DgroupId={groupId}"
            + " -DartifactId={artifactId}"
            + " -Dversion={version}"
            + " -Dfile={sourcesFile}"
            + " -Dclassifier=sources";

    public static String uploadPom_ = "" +
            "{mvn} deploy:deploy-file"
            + " -Durl={url}"
            + " -DrepositoryId=upload-server -s {settings}"
            + " -Dpackaging=pom"
            + " -DgroupId={groupId}"
            + " -DartifactId={artifactId}"
            + " -Dversion={version}"
            + " -Dfile={pomFile}";
}
