package online.morn.convert.mysql;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 工具
 */
public class PublicUtil {

    public static void println(String x){
        System.out.println(x);
    }

    public static void printException(Exception e){
        e.printStackTrace();
    }

    /**
     * 首字母大写
     * @param str
     * @return
     */
    public static String upperCaseFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 首字母小写
     * @param str
     * @return
     */
    public static String lowerCaseFirst(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 写文件
     * @param filePath
     * @param content
     */
    public static void writeFile(String filePath, String content) {
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            PublicUtil.printException(e);
        }
    }
}
