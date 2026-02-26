package com.demo.add;
import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;

public class AddNumbersJava extends MbJavaComputeNode {

    public void evaluate(MbMessageAssembly inAssembly) throws MbException {

        // Input message
        MbMessage inMessage = inAssembly.getMessage();

        // Get JSON/Data
        MbElement jsonRoot = inMessage.getRootElement()
                .getFirstElementByPath("JSON/Data");

        if (jsonRoot == null) {
            throw new MbException(
                    this,
                    "evaluate",
                    "",
                    "",
                    "Input JSON/Data not found",
                    null
            );
        }

        int a = jsonRoot.getFirstElementByPath("a").getValueAsInteger();
        int b = jsonRoot.getFirstElementByPath("b").getValueAsInteger();

        int sum = a + b;

        // Create output message
        MbMessage outMessage = new MbMessage();
        MbMessageAssembly outAssembly =
                new MbMessageAssembly(inAssembly, outMessage);

        MbElement outRoot = outMessage.getRootElement();

        // Correct JSON domain creation
        MbElement jsonOut = outRoot.createElementAsLastChild(
                MbElement.TYPE_NAME,
                "JSON",
                null
        );

        MbElement data = jsonOut.createElementAsLastChild(
                MbElement.TYPE_NAME,
                "Data",
                null
        );

        data.createElementAsLastChild(
                MbElement.TYPE_NAME_VALUE,
                "sum",
                sum
        );

        // Propagate message
        propagate(outAssembly);
    }
}
