import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.*;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Dfdl_JavaCompute extends MbJavaComputeNode {

    public void evaluate(MbMessageAssembly inAssembly) throws MbException {

        MbOutputTerminal out = getOutputTerminal("out");

        try {
            MbMessage inMessage = inAssembly.getMessage();

            // === READ CSV FROM BLOB ===
            MbElement blobElement = inMessage.getRootElement().getFirstElementByPath("BLOB/BLOB");
            if (blobElement == null || blobElement.getValue() == null)
                throw new MbUserException(this, "evaluate", "", "Empty BLOB input", null, null);

            byte[] csvBytes = (byte[]) blobElement.getValue();
            String csvText = new String(csvBytes, StandardCharsets.UTF_8);

            // === PARSE CSV ===
            String[] lines = csvText.split("\\r?\\n");
            if (lines.length == 0)
                throw new MbUserException(this, "evaluate", "", "CSV has no data", null, null);

            String[] headers = lines[0].split(",");

            // === XML DOCUMENT CREATION ===
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("CSV");
            doc.appendChild(root);

            Element dataRows = doc.createElement("DataRows");
            root.appendChild(dataRows);

            // === BUILD XML ===
            for (int i = 1; i < lines.length; i++) {
                if (lines[i].trim().isEmpty()) continue;

                Element rowElem = doc.createElement("Row");
                rowElem.setAttribute("Index", String.valueOf(i));
                dataRows.appendChild(rowElem);

                String[] cells = lines[i].split(",", -1);

                for (int c = 0; c < headers.length; c++) {
                    String header = headers[c].trim();
                    if (header.isEmpty()) header = "Column" + (c + 1);

                    String cellVal = (c < cells.length) ? cells[c].trim() : "";

                    Element cellElem = doc.createElement(sanitizeXML(header));
                    cellElem.setTextContent(cellVal);
                    rowElem.appendChild(cellElem);
                }
            }

            // === TRANSFORM TO STRING ===
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String xmlOutput = writer.toString();

            // === PREPARE OUTPUT MESSAGE ===
            MbMessage outMessage = new MbMessage(inMessage);
            MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, outMessage);

            MbElement oldBody = outMessage.getRootElement().getLastChild();
            if (oldBody != null) oldBody.detach();

            MbElement outRoot = outMessage.getRootElement().createElementAsLastChild("BLOB");
            outRoot.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
                    "BLOB", xmlOutput.getBytes(StandardCharsets.UTF_8));

            out.propagate(outAssembly);

        } catch (Exception e) {
            throw new MbUserException(this, "evaluate",
                    "CSVToXML_Error",
                    e.getMessage(), null, null);
        }
    }

    private static String sanitizeXML(String name) {
        if (name == null || name.isEmpty()) return "Column";
        String clean = name.replaceAll("[^A-Za-z0-9_\\-]", "_");
        if (clean.matches("^[0-9].*")) clean = "H_" + clean;
        return clean;
    }
}
