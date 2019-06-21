import java.io.*;

public class Parser {
    public static void main(String[] args) {
        File file = null;
        String line = null;
        BufferedReader br = null;

        try {
            file = new File("src/main/resources/messages.po");
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            while (true) {
                line = br.readLine();
                if (line != null) {
                    if (line.startsWith("msgid")) {
                        int startChar = line.indexOf("\"") + 1;
                        int endChar = line.lastIndexOf("\"");
                        System.out.println(line.substring(startChar, endChar));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
