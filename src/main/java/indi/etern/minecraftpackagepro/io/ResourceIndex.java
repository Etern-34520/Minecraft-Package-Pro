package indi.etern.minecraftpackagepro.io;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Map.Entry;

public class ResourceIndex {
    /**
     * from Minecraft Code Pack,
     * Thanks to Mojang!
     */
    //private static final Logger logger = LogManager.getLogger();
    private final Map<String, String> resourceMap = Maps.newHashMap();//Changed

    public ResourceIndex(File p_i1047_1_, String p_i1047_2_)
    {
        if (p_i1047_2_ != null)
        {
            String localFilePath = p_i1047_1_.getPath();
            try {
                localFilePath = URLDecoder.decode(localFilePath, "UTF-8");
                System.out.println(localFilePath);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            File parentFile = new File(localFilePath);
            File file1 = new File(parentFile, "objects");
            File file2 = new File(parentFile, "indexes/" + p_i1047_2_ + ".json");
            BufferedReader bufferedreader = null;

            try
            {
                bufferedreader = Files.newReader(file2, Charsets.UTF_8);
                JsonObject jsonobject = JsonParser.parseReader(bufferedreader).getAsJsonObject();//Changed
                JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "objects", null);//Changed

                if (jsonobject1 != null)
                {
                    for (Entry<String, JsonElement> entry : jsonobject1.entrySet())
                    {
                        JsonObject jsonobject2 = (JsonObject)entry.getValue();
                        String s = entry.getKey();//Changed
                        String[] astring = s.split("/", 2);
                        String s1 = astring.length == 1 ? astring[0] : astring[0] + ":" + astring[1];
                        String s2 = JsonUtils.getString(jsonobject2, "hash");
                        File file3 = new File(file1, s2.substring(0, 2) + "/" + s2);
                        this.resourceMap.put(file3.getName(),s1);//Changed
                    }
                }
            }
            catch (JsonParseException var20)
            {
                var20.printStackTrace();
//                logger.error("Unable to parse resource index file: " + file2);
            }
            catch (FileNotFoundException var21)
            {
                var21.printStackTrace();
//                logger.error("Can't find the resource index file: " + file2);
            }
            finally
            {
                IOUtils.closeQuietly(bufferedreader);//Changed
            }
        }
    }

    public Map<String, String> getResourceMap()
    {
        return this.resourceMap;
    }//Changed
}
