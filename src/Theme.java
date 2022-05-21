import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class Theme {
    private static final Image sunImage = new Image("/icons/sun.png");
    private static final Image moonImage = new Image("/icons/moon.png");
    private static final String darkFill = "-fx-fill: #ffffff";
    private static final String lightFill = "-fx-fill: #000000";
    private static final String FOLDERNAME = "\\dream-calendar\\";
    private static final String DEFAULT_FILENAME = "settings.xml";
    private static final String DEFAULT_FILEPATH = System.getenv("APPDATA") + "\\dream-calendar\\settings.xml";
    private static Document doc;

    private ImageView themeMod;
    private AnchorPane mainAnchorPane;
    private AnchorPane anchorPane;
    private GridPane gridPane;
    private Label currentTime;
    private Text choseTimeText;
    private Text chosenDateText;
    private Text currentMonthText;
    private Text colon;

    public Theme(ImageView themeMod, AnchorPane mainAnchorPane, AnchorPane anchorPane, GridPane gridPane,
            Label currentTime,
            Text choseTimeText, Text chosenDateText, Text currentMonthText, Text colon) {
        this.themeMod = themeMod;
        this.mainAnchorPane = mainAnchorPane;
        this.anchorPane = anchorPane;
        this.gridPane = gridPane;
        this.currentTime = currentTime;
        this.choseTimeText = choseTimeText;
        this.chosenDateText = chosenDateText;
        this.currentMonthText = currentMonthText;
        this.colon = colon;
    }

    /**
     * Установление темного стиля для различных элементов
     */
    public void setDarkTheme() {
        themeMod.setImage(sunImage);
        mainAnchorPane.setStyle("-fx-background-color: #181818");
        anchorPane.setStyle("-fx-background-color: #181818; -fx-border-color: #ffffff");
        setNumbersColor();
        choseTimeText.setStyle(darkFill);
        chosenDateText.setStyle(darkFill);
        currentMonthText.setStyle(darkFill);
        colon.setStyle(darkFill);
    }

    /**
     * Установление светлой темы для разлчиных элементов
     */
    public void setLightTheme() {
        themeMod.setImage(moonImage);
        mainAnchorPane.setStyle("-fx-background-color: #ffffff");
        anchorPane.setStyle("-fx-background-color: #dff5f8; -fx-border-color: #76787a");
        setNumbersColor();
        choseTimeText.setStyle(lightFill);
        chosenDateText.setStyle(lightFill);
        currentMonthText.setStyle(lightFill);
        colon.setStyle(lightFill);
    }

    /**
     * Установление стиля номеров дней, исходя из темы
     */
    private void setNumbersColor() {
        if (Controller.isDarkTheme) {
            setDarkNumbers();
        } else {
            setLightNumbers();
        }
    }

    /**
     * Установление стиля номеров дней для темной темы
     */
    private void setDarkNumbers() {
        int count = 0;
        for (Node element : anchorPane.getChildren()) {
            if (element instanceof Text) {
                if (element.getStyle().equals("-fx-fill: #296cff")) {
                    element.setStyle("-fx-fill: #296cff");
                } else if (GridPane.getColumnIndex(gridPane.getChildren().get(count)) == null
                        && (element.getStyle().equals("-fx-opacity: 0.25")
                                || element.getStyle().equals("-fx-fill: #000000; -fx-opacity: 0.25"))
                        || (element.getStyle().equals("-fx-opacity: 0.25")
                                || element.getStyle().equals("-fx-fill: #000000; -fx-opacity: 0.25"))
                                && (GridPane.getColumnIndex(gridPane.getChildren().get(count)) != 5
                                        && GridPane.getColumnIndex(gridPane.getChildren().get(count)) != 6)) {
                    element.setStyle("-fx-fill: #ffffff; -fx-opacity: 0.25");
                } else if (GridPane.getColumnIndex(gridPane.getChildren().get(count)) == null
                        || GridPane.getColumnIndex(gridPane.getChildren().get(count)) != 5
                                && GridPane.getColumnIndex(gridPane.getChildren().get(count)) != 6) {
                    element.setStyle(darkFill);
                }
                count++;
            }
        }
    }

    /**
     * Установление стиля номеров дней для светлой темы
     */
    private void setLightNumbers() {
        int count = 0;
        for (Node element : anchorPane.getChildren()) {
            if (element instanceof Text) {
                if (element.getStyle().equals("-fx-fill: #296cff")) {
                    element.setStyle("-fx-fill: #296cff");
                } else if (GridPane.getColumnIndex(gridPane.getChildren().get(count)) == null
                        && (element.getStyle().equals("-fx-opacity: 0.25")
                                || element.getStyle().equals("-fx-fill: #ffffff; -fx-opacity: 0.25"))
                        || (element.getStyle().equals("-fx-opacity: 0.25")
                                || element.getStyle().equals("-fx-fill: #ffffff; -fx-opacity: 0.25"))
                                && (GridPane.getColumnIndex(gridPane.getChildren().get(count)) != 5
                                        && GridPane.getColumnIndex(gridPane.getChildren().get(count)) != 6)) {
                    element.setStyle("-fx-fill: #000000; -fx-opacity: 0.25");
                } else if (GridPane.getColumnIndex(gridPane.getChildren().get(count)) == null
                        || GridPane.getColumnIndex(gridPane.getChildren().get(count)) != 5
                                && GridPane.getColumnIndex(gridPane.getChildren().get(count)) != 6) {
                    element.setStyle(lightFill);
                }
                count++;
            }
        }
    }

    /**
     * Обработка создания, заполнения и сохранения xml-файла настроек
     */
    public static void saveThemeMod() {
        String appDataPath = System.getenv("APPDATA") + FOLDERNAME;
        File folder = new File(appDataPath);
        folder.mkdir();
        File file = new File(appDataPath + DEFAULT_FILENAME);
        try {
            if (file.createNewFile()) {
                Logger.getLogger(Theme.class.getName())
                        .info("Успешно создан файл для сохранения темы.");
            }
        } catch (IOException e) {
            Logger.getLogger(Theme.class.getName())
                    .severe("При создании файла произошла ошибка. " + e.getMessage());
            return;
        }
        doc = createDoc();
        fillXML();
        saveXML();
    }

    /**
     * Создание элемента Documents
     *
     * @return Document
     */
    private static Document createDoc() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        assert dBuilder != null;
        return dBuilder.newDocument();
    }

    /**
     * Заполнение xml-файла
     */
    private static void fillXML() {
        Element rootElement = doc.createElement("settings");
        doc.appendChild(rootElement);
        Element element = doc.createElement("theme");
        rootElement.appendChild(element);

        Attr attr = doc.createAttribute("mod");
        if (Controller.isDarkTheme) {
            attr.setValue("dark");
        } else {
            attr.setValue("light");
        }
        element.setAttributeNode(attr);
    }

    /**
     * Сохранение xml-файла
     */
    private static void saveXML() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            System.out.println("Error: " + e.getMessage());
        }
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(DEFAULT_FILEPATH);
        try {
            assert transformer != null;
            transformer.transform(source, result);
        } catch (TransformerException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
