package candyenk.java.entity;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import candyenk.java.utils.UFile;

/**
 * 文件类型实体枚举
 */
public enum FileType {
    NULL,//没有后缀的文件
    UNKNOWN,//未知后缀的文件
    DIRECTORY,//目录
    TEXT("txt", "lua", "pdf", "xml", "htm", "html", "doc", "docx"),
    IMAGE("bmp", "jpg", "jpeg", "png", "gif", "tif", "tiff", "ico", "webp", "psd", "svg", "ai"),
    AUDIO("mp3", "wav", "wma", "ogg", "flac", "aac", "ape", "acc", "wv", "m4a", "m4r", "mp2", "mpa", "m1a", "m2a", "m3a", "mid", "midi", "rmi", "aif", "aiff", "aifc", "ra", "ram", "wma", "wav", "au", "snd"),
    VIDEO("avi", "mp4", "mkv", "wmv", "flv", "mov", "3gp", "mpg", "mpeg", "mpe", "mpv", "m4v", "mxf", "rm", "rmvb", "asf", "asx", "vob", "divx", "dv", "dvd", "f4v", "flv", "m2ts", "m2t", "m2v", "m4v", "mts", "mv", "mxf", "mxv", "qt", "ram", "smi", "smil", "swf", "vob", "wmv", "xvid", "webm"),
    COMPRESS("apk", "zip", "rar", "7z", "cab", "tar", "gz", "bz2", "jar", "war", "ear", "xz", "z", "iso", "dmg", "jar", "war", "ear", "xz", "z", "iso", "dmg"),
    BINARY("bin", "exe", "dll", "class", "so", "o", "a", "lib", "obj", "elf"),
    HEXADECIMAL("hex");

    /**
     * 判断文件类型
     */
    public static FileType type(File file) {
        if (file.isDirectory()) {
            return DIRECTORY;
        } else {
            return type(file.getName());
        }
    }


    public static FileType type(String filename) {
        String suffix = UFile.getSuffix(filename.toLowerCase());
        if (suffix.isEmpty()) {
            return FileType.NULL;
        } else if (TEXT.typeList.contains(suffix)) {
            return FileType.TEXT;
        } else if (IMAGE.typeList.contains(suffix)) {
            return FileType.IMAGE;
        } else if (AUDIO.typeList.contains(suffix)) {
            return FileType.AUDIO;
        } else if (VIDEO.typeList.contains(suffix)) {
            return FileType.VIDEO;
        } else if (COMPRESS.typeList.contains(suffix)) {
            return FileType.COMPRESS;
        } else if (BINARY.typeList.contains(suffix)) {
            return FileType.BINARY;
        } else if (HEXADECIMAL.typeList.contains(suffix)) {
            return FileType.HEXADECIMAL;
        } else {
            return FileType.UNKNOWN;
        }
    }

    private final List<String> typeList;

    FileType(String... value) {
        typeList = Arrays.asList(value);
    }


}