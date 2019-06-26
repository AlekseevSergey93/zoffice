import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    File file = null;
    String line, msgid, msgstr = null;
    String digitalChars = "";
    List<String> list = new ArrayList<>();
    BufferedReader br = null;
    BufferedWriter bw = null;
    GoogleCredentials credentials = null;
    Translate translate = null;
    Translation translation = null;

    public Parser() {
        try {
            credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/key.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (credentials != null) {
            translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();
        }
    }

    public void parse() {
        try {
            file = new File("/home/sergey/Projects/libreoffice/translations/source/ru/cui/messages.po");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), "UTF-8"));
            while ((line = br.readLine()) != null) {
                list.add(line);
                if (line.startsWith("msgid")) {
                    msgid = normalizationString(line);
                }else if (line.startsWith("msgstr")) {
                    msgstr = normalizationString(line);
                    if (needTranslate()) {
                        getGoogleTranslate();
                        toUpperCaseFirstChar();
                        list.remove((list.size() - 1));
                        list.add("msgstr \"" + msgstr + "\"\n\r #:Machine translate");
                        System.out.println(msgid + digitalChars + "    " + msgstr);
                    }
                }
            }
        } catch (
                UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        overwriteFile();
    }


    public void getGoogleTranslate() {
        if (!msgid.equals("") && msgid != null) {
            separateDigitalPart();
            translation = translate.translate(msgid,
                    Translate.TranslateOption.sourceLanguage("en"),
                    Translate.TranslateOption.targetLanguage("ru"),
                    Translate.TranslateOption.model("nmt"));
            msgstr = translation.getTranslatedText();
        }

        if (!digitalChars.equals("")) {
            msgstr += " " + digitalChars;
        }
    }

    public void toUpperCaseFirstChar() {
        if (msgstr != "" && msgstr != null) {
            msgstr = msgstr.substring(0, 1).toUpperCase() + msgstr.substring(1);
        }
    }

    public String normalizationString(String str){
        int startChar = str.indexOf("\"") + 1;
        int endChar = str.lastIndexOf("\"");
        return str.substring(startChar, endChar)
                   .trim()
                   .replace("_", "")
                   .replace("~", "");
    }

    public boolean needTranslate() {
        if (msgstr.equals("") && !msgid.equals("")) {
            return true;
        }else
            return false;
    }

    public void separateDigitalPart() {
        Pattern pattern = Pattern.compile("\\d{0,3}$");
        Matcher matcher = pattern.matcher(msgid);
        digitalChars = "";

        while (matcher.find()) {
            digitalChars += msgid.substring(matcher.start(), matcher.end());
        }

        if (!digitalChars.equals("")) {
            msgid = msgid.substring(0, msgid.indexOf(digitalChars));
        }
    }

    public void overwriteFile() {
        if (!list.isEmpty() && list != null) {
            try {
                bw = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(file)));

                for (String s: list) {
                    bw.write(s);
                    bw.newLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
