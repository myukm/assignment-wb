package assignment;


import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Utility {

    static Logger log = Logger.getLogger(Utility.class.getName());
    private static JSONParser jsonParser = new JSONParser();
    private static String dir = System.getProperty("user.dir");

    /**
     * Read the json file from the specified path
     *
     * @param path
     * @return
     */
    public static JSONObject readFile(String path) {
        try {
            //get the root directory path
            return (JSONObject) jsonParser.parse(new FileReader(dir + path));
        } catch (IOException e) {
            log.info("Cannot read testData.json");
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            log.info("Cannot read testData.json");
            e.printStackTrace();
            return null;
        }
    }

    public static String[] getUserCreds(String userType) {
        String[] userCreds = new String[2];
        try {
            JSONObject obj = (JSONObject) jsonParser.parse(new FileReader(dir + Constants.TEST_DATA_FILE_NAME));
            Set<String> fields = obj.keySet();
            for (String k : fields) {
                if (k.equals(userType.trim())) {
                    JSONObject input = (JSONObject) obj.get(k);
                    userCreds[0] = input.get("email").toString();
                    userCreds[1] = input.get("password").toString();
                    break;
                    }
                }
        } catch (IOException e) {
           log.error("Problem while reading baseTestUser from test input json");
           e.printStackTrace();
        } catch (ParseException e) {
            log.error("Problem while reading baseTestUser from test input json,check for json validity and availability");
            e.printStackTrace();
        }
        return userCreds;
    }

    public static void setWebDriverSystemProperty()
    {
        System.setProperty("webdriver.chrome.driver", Constants.WEB_DRIVER_PATH);
    }

    public static String getReviewMessage(){
        try {
            JSONObject obj = (JSONObject) jsonParser.parse(new FileReader(dir + Constants.TEST_DATA_FILE_NAME));
            Set<String> fields = obj.keySet();
            for (String k : fields) {
                if (k.equals("reviewMessage")) {
                    return (String)obj.get(k);
                }
            }
        } catch (IOException e) {
            log.error("Problem while reading reviewMessage from test input json");
            e.printStackTrace();
        } catch (ParseException e) {
            log.error("Problem while reading reviewMessage from test input json,check for json validity and availability");
            e.printStackTrace();
        }
       return "";
    }


}
