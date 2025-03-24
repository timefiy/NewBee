package cn.zzuli.citypicker.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Description: 资产目录工具,可以在Application的时候就开始拷贝.把文件传到/data/data/包名/files/
 */

public class AssetsUtils {

    /**
     * 读取成String
     * @param fileName assets中文件的名称, 示例: china_city_data.json
     */
    public static synchronized String getString(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
