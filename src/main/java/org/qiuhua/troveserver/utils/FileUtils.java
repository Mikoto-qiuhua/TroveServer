package org.qiuhua.troveserver.utils;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qiuhua.troveserver.Main;
import org.qiuhua.troveserver.utils.yaml.file.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileUtils {


    @Getter
    private static final File dataFolder = new File(System.getProperty("user.dir"));



    /**
     * 创建一个指定路径的文件
     * @param path
     */
    public static void createFolder(String path){
        try {
            File folder = new File(FileUtils.getDataFolder(), path);
            if (!folder.exists()) {
                boolean success = folder.mkdirs();
                if (success) {
                    Main.getLogger().debug("成功创建文件夹: " + folder.getAbsolutePath());
                } else {
                    throw new IOException("无法创建文件夹: " + folder.getAbsolutePath());
                }
            } else {
                // 检查是否是文件夹而不是文件
                if (!folder.isDirectory()) {
                    throw new IOException(folder.getAbsolutePath() + " 已存在但不是文件夹！");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("初始化 " + path + " 文件夹失败", e);
        }

    }


    /**
     * 从资源文件夹生成文件
     * @param resourcePath 资源路径
     * @param replace 是否覆盖原有的
     * @return
     */
    public static void saveResource(@NotNull String resourcePath, boolean replace) {
        if (!resourcePath.isEmpty()) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = getResource(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("嵌入资源 '" + resourcePath + "' 无法找到 " + dataFolder);
            } else {
                File outFile = new File(dataFolder, resourcePath);
                int lastIndex = resourcePath.lastIndexOf(47);
                File outDir = new File(dataFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        if(Main.getLogger() != null){
                            Main.getLogger().debug("无法保存 {} 到 {} 因为 {} 已经存在", outFile.getName(), outFile, outFile.getName());
                        }
                    } else {
                        OutputStream out = new FileOutputStream(outFile);
                        byte[] buf = new byte[1024];

                        int len;
                        while((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        in.close();
                    }
                } catch (IOException ex) {
                    if(Main.getLogger() != null){
                        Main.getLogger().warn("无法保存 {} 到 {}", outFile.getName(), outFile);
                    }
                }

            }
        } else {
            throw new IllegalArgumentException("ResourcePath 不能为 null 或为空");
        }
    }


    public static YamlConfiguration loadFile(String filePath){
        return YamlConfiguration.loadConfiguration(new File(dataFolder, filePath));
    }

    /**
     * 加载文件夹中的yml
     * @param folder
     */
    public static Map<String, YamlConfiguration> loadFiles(File folder){
        Map<String, YamlConfiguration> map = new HashMap<>();
        // 检查文件夹是否存在且是一个文件夹
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            // 遍历文件夹中的所有文件和文件夹
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 如果是文件夹，则递归调用该方法，传入文件夹作为参数
                        map.putAll(loadFiles(file));
                    } else if (file.isFile() && file.getName().endsWith(".yml")) {
                        // 检查文件是否是一个文件且以 .yml 结尾
                        // 加载文件的配置
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                        String fileName = file.getName().replace(".yml", "");
                        if(map.containsKey(fileName)){
                            Main.getLogger().warn("出现重名配置 {} 请注意修改,本次将越过该配置读取", fileName);
                            continue;
                        }
                        map.put(fileName, config);
                    }
                }
            }
        }
        return map;
    }

    /**
     * 读取文件夹中的全部文件文件名称
     * @param folder
     * @return
     */
    public static List<String> loadFilesName(File folder){
        List<String> list = new ArrayList<>();
        // 检查文件夹是否存在且是一个文件夹
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            // 遍历文件夹中的所有文件和文件夹
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 如果是文件夹，则递归调用该方法，传入文件夹作为参数
                        list.addAll(loadFilesName(file));
                    } else if (file.isFile()) {
                        String fileName = file.getName();
                        if(list.contains(fileName)){
                            Main.getLogger().warn("出现重名配置 {} 请注意修改,本次将越过该配置读取", fileName);
                            continue;
                        }
                        list.add(fileName);
                    }
                }
            }
        }
        return list;
    }


    /**
     * 将字符串内容写入到指定文件
     * @param file 目标文件
     * @param content 要写入的内容
     * @param charset 字符编码
     */
    public static void writeStringToFile(@NotNull File file, @NotNull String content, @NotNull Charset charset) {
        try {
            // 确保父目录存在
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean success = parentDir.mkdirs();
                if (!success) {
                    throw new IOException("无法创建父目录: " + parentDir.getAbsolutePath());
                }
            }

            // 写入文件内容
            try (OutputStream out = new FileOutputStream(file);
                 OutputStreamWriter writer = new OutputStreamWriter(out, charset)) {
                writer.write(content);
                writer.flush();
            }
            //Main.getLogger().debug("成功写入文件: " + file.getAbsolutePath());
        } catch (IOException ex) {
            Main.getLogger().warn("无法写入文件 {}: {}", file.getAbsolutePath(), ex.getMessage());
            throw new RuntimeException("写入文件失败: " + file.getAbsolutePath(), ex);
        }
    }

    /**
     * 将字符串内容写入到指定文件（使用默认UTF-8编码）
     * @param file 目标文件
     * @param content 要写入的内容
     */
    public static void writeStringToFile(@NotNull File file, @NotNull String content) {
        writeStringToFile(file, content, StandardCharsets.UTF_8);
    }



    @Nullable
    private static InputStream getResource(@NotNull String filename) {
        try {
            URL url = Main.getInstance().getClass().getClassLoader().getResource(filename);
            if (url == null) {
                return null;
            } else {
                URLConnection connection = url.openConnection();
                connection.setUseCaches(false);
                return connection.getInputStream();
            }
        } catch (IOException var4) {
            return null;
        }
    }

}
