


	package com.example.addition;

	import com.ibm.broker.plugin.*;

	public class AddNumbersCompute extends MbJavaComputeNode {

	    public void evaluate(MbMessageAssembly inAssembly) throws MbException {

	        MbMessage inMessage = inAssembly.getMessage();
	        MbMessage outMessage = inMessage.copy();
	        MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, outMessage);

	        // Read query parameters ?a=5&b=7
	        MbElement req = inAssembly.getGlobalEnvironment().getRootElement()
	                .getFirstElementByPath("HTTP/Input/QueryString");
	        String query = req != null ? req.getValueAsString() : "";

	        int a = 0, b = 0;

	        // Parse query params manually (simple logic)
	        if (query.contains("a=")) {
	            a = Integer.parseInt(query.split("a=")[1].split("&")[0]);
	        }
	        if (query.contains("b=")) {
	            b = Integer.parseInt(query.split("b=")[1].split("&")[0]);
	        }

	        int sum = a + b;

	        // Create XML output
	        MbElement root = outMessage.getRootElement();
	        root.deleteChildren();
	        MbElement xml = root.createElementAsLastChild(MbXMLNSC.PARSER_NAME);

	        MbElement result = xml.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "result", sum);

	        // Propagate to HTTP Reply
	        MbOutputTerminal out = getOutputTerminal("out");
	        out.propagate(outAssembly);
	    }
	}

