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
                outputArea.setText(jsonToXmlManual(jsonObject, 0));
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

    private String xmlToJsonManual(String xml) {
        String cleanXml = xml.replaceAll("<\\?.*?\\?>", "")
                             .replaceAll("<!DOCTYPE.*?>", "")
                             .replaceAll("(?s)", "")
                             .trim();

        return "{\n" + formatXmlNode(cleanXml, 1) + "\n}";
    }

    private String formatXmlNode(String xml, int indent) {
        StringBuilder sb = new StringBuilder();
        String indentStr = "    ".repeat(indent);
        String remaining = xml.trim();

        while (!remaining.isEmpty() && remaining.startsWith("<")) {
            int tagStart = remaining.indexOf("<");
            int tagEnd = remaining.indexOf(">");
            if (tagStart == -1 || tagEnd == -1) break;

            String tagName = remaining.substring(tagStart + 1, tagEnd).split("\\s+")[0].replace("/", "");
            String closeTag = "</" + tagName + ">";
            int closeIndex = remaining.indexOf(closeTag);

            if (closeIndex != -1) {
                String content = remaining.substring(tagEnd + 1, closeIndex).trim();
                sb.append(indentStr).append("\"").append(tagName).append("\": ");

                if (content.startsWith("<")) {
                    sb.append("{\n").append(formatXmlNode(content, indent + 1))
                      .append("\n").append(indentStr).append("}");
                } else {
                    sb.append("\"").append(content.replace("\"", "\\\"")).append("\"");
                }

                sb.append(",\n");
                remaining = remaining.substring(closeIndex + closeTag.length()).trim();
            } else {
                remaining = remaining.substring(tagEnd + 1).trim();
            }
        }

        if (sb.length() > 2) sb.setLength(sb.length() - 2); 
        return sb.toString();
    }

    private String jsonToXmlManual(Object json, int indent) {
        StringBuilder sb = new StringBuilder();

        String indentStr = "";
        for (int i = 0; i < indent; i++) indentStr += "    ";

        if (json instanceof JSONObject) {
            JSONObject obj = (JSONObject) json;
            Iterator<?> keys = obj.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                Object value = obj.get(key);

                sb.append(indentStr).append("<").append(key).append(">\n");
                sb.append(jsonToXmlManual(value, indent + 1));
                sb.append(indentStr).append("</").append(key).append(">\n");
            }
        }
        else if (json instanceof JSONArray) {
            JSONArray arr = (JSONArray) json;
            for (int i = 0; i < arr.length(); i++) {
                sb.append(jsonToXmlManual(arr.get(i), indent));
            }
        }
        else {
            sb.append(indentStr)
              .append(json != null ? json.toString() : "")
              .append("\n");
        }

        return sb.toString();
    }


}