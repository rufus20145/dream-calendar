import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class SettingsLoader {

    private static final String DEFAULT_FILEPATH = System.getenv("APPDATA") + "\\dream-calendar\\settings.xml";

    public static boolean loadSettingsFromFile() {
        return loadSettingsFromFile(DEFAULT_FILEPATH);
    }

    /**
     *
     * @param filePath путь к файлу с настройками
     * @return true - темная тема, false - светлая
     */
    public static boolean loadSettingsFromFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            Document doc = null;
            try {
                assert dBuilder != null;
                doc = dBuilder.parse(file);
            } catch (SAXException | IOException e) {
                e.printStackTrace();
            }
            assert doc != null;
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("theme");
            Node nNode = nList.item(0);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                return eElement.getAttribute("mod").equals("dark");
            }
        }
        return false;
    }
}
