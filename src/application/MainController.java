package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.nio.file.Files;
import java.util.Iterator;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.io.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;

public class MainController {

    @FXML
    private TextArea inputArea;

    @FXML
    private TextArea outputArea;

    @FXML
    private RadioButton apiButton;

    @FXML
    private RadioButton manualButton;

    private ToggleGroup methodGroup;

    @FXML
    public void initialize() {
        methodGroup = new ToggleGroup();
        apiButton.setToggleGroup(methodGroup);
        manualButton.setToggleGroup(methodGroup);
        apiButton.setSelected(true);
    }

    @FXML
    private void convertXmlToJson() {
        String input = inputArea.getText();
        if (apiButton.isSelected()) {
            try {
                JSONObject json = XML.toJSONObject(input);
                outputArea.setText(json.toString(4));
            } catch (Exception e) {
                outputArea.setText("Erreur : XML invalide");
            }
        } else {
            try {
                outputArea.setText(xmlToJsonManual(input));
            } catch (Exception e) {
                outputArea.setText("Erreur : XML invalide (manuel)");
            }
        }
    }
    
    private String formatXml(String xml) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            Source source = new StreamSource(new StringReader(xml));
            StringWriter writer = new StringWriter();

            transformer.transform(source, new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            return xml;
        }
    }


    @FXML
    private void convertJsonToXml() {
        String input = inputArea.getText();
        if (input == null || input.trim().isEmpty()) return;

        if (apiButton.isSelected()) {
            try {
                JSONObject json = new JSONObject(input);
                String xmlOneLine = XML.toString(json);
                outputArea.setText(formatXml(xmlOneLine));

            } catch (Exception e) {
                outputArea.setText("Erreur : JSON invalide");
            }
        } else {
            try {
                JSONObject jsonObject = new JSONObject(input);
                outputArea.setText(jsonToXmlManual(jsonObject, 0, "root"));
            } catch (Exception e) {
                outputArea.setText("Erreur : JSON invalide (manuel)");
            }
        }
    }


    @FXML
    private void loadXml() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers XML", "*.xml"));
        File file = fileChooser.showOpenDialog(inputArea.getScene().getWindow());
        if (file != null) {
            try {
                inputArea.setText(Files.readString(file.toPath()));
            } catch (Exception e) {
                outputArea.setText("Erreur lecture XML");
            }
        }
    }

    @FXML
    private void loadJson() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers JSON", "*.json"));
        File file = fileChooser.showOpenDialog(inputArea.getScene().getWindow());
        if (file != null) {
            try {
                inputArea.setText(Files.readString(file.toPath()));
            } catch (Exception e) {
                outputArea.setText("Erreur lecture JSON");
            }
        }
    }

   

    private String xmlToJsonManual(String xml) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));

        Element root = doc.getDocumentElement();
        JSONObject json = new JSONObject();

        json.put(root.getTagName(), elementToJson(root));

        return json.toString(4);
    }
    
    private Object elementToJson(Element element) {

        NodeList children = element.getChildNodes();
        JSONObject obj = new JSONObject();
        boolean hasElementChild = false;

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);

            if (node instanceof Element) {
                hasElementChild = true;
                Element child = (Element) node;
                String name = child.getTagName();
                Object value = elementToJson(child);

                if (obj.has(name)) {
                    if (!(obj.get(name) instanceof JSONArray)) {
                        JSONArray arr = new JSONArray();
                        arr.put(obj.get(name));
                        obj.put(name, arr);
                    }
                    ((JSONArray) obj.get(name)).put(value);
                } else {
                    obj.put(name, value);
                }
            }
        }

        if (!hasElementChild) {
            return element.getTextContent().trim();
        }

        return obj;
    }



    private String jsonToXmlManual(Object json, int indent, String tagName) {
        StringBuilder sb = new StringBuilder();
        String indentStr = "    ".repeat(indent);

        if (json instanceof JSONObject) {
            JSONObject obj = (JSONObject) json;
            Iterator<String> keys = obj.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                Object value = obj.get(key);

                sb.append(indentStr).append("<").append(key).append(">\n");
                sb.append(jsonToXmlManual(value, indent + 1, key));
                sb.append(indentStr).append("</").append(key).append(">\n");
            }
        }
        else if (json instanceof JSONArray) {
            JSONArray arr = (JSONArray) json;

            for (int i = 0; i < arr.length(); i++) {
                sb.append(indentStr).append("<").append(tagName).append(">\n");
                sb.append(jsonToXmlManual(arr.get(i), indent + 1, tagName));
                sb.append(indentStr).append("</").append(tagName).append(">\n");
            }
        }
        else {
            sb.append(indentStr)
              .append(json.toString())
              .append("\n");
        }
        return sb.toString();
    }
}