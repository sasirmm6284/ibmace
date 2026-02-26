import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.*;

public class UnzipAndParseXml extends MbJavaComputeNode {
    public void evaluate(MbMessageAssembly inAssembly) throws MbException {
        MbOutputTerminal out = getOutputTerminal("out");
        MbMessage inMessage = inAssembly.getMessage();
        MbMessageAssembly outAssembly = null;
        try {
            MbElement blobElem = inMessage.getRootElement().getFirstElementByPath("BLOB/BLOB");
            if (blobElem == null) throw new MbUserException(this, "evaluate()", "", "", "No InputRoot.BLOB.BLOB", null);
            byte[] blob = (byte[]) blobElem.getValue();

            ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(blob));
            ZipEntry entry;
            byte[] xmlBytes = null;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".xml")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[4096];
                    int r;
                    while ((r = zis.read(buf)) > 0) baos.write(buf, 0, r);
                    xmlBytes = baos.toByteArray();
                    zis.closeEntry();
                    break;
                }
                zis.closeEntry();
            }
            zis.close();

            if (xmlBytes == null) throw new MbUserException(this, "evaluate()", "", "", "No .xml in ZIP", null);

            MbMessage outMessage = new MbMessage();
            MbElement outRoot = outMessage.getRootElement();

            // Try common signature: CCSID as int (1208 = UTF-8)
            

            outAssembly = new MbMessageAssembly(inAssembly, outMessage);
        } catch (MbException e) { throw e;
        } catch (Exception e) { throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
        }
        out.propagate(outAssembly);
    }
}
