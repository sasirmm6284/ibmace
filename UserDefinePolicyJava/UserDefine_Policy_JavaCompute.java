import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbPolicy;
import com.ibm.broker.plugin.MbUserException;

public class UserDefine_Policy_JavaCompute extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		  MbMessage outMessage = new MbMessage(inAssembly.getMessage());
	        MbMessageAssembly outAssembly =
	                new MbMessageAssembly(inAssembly, outMessage);
		try {
			 MbPolicy myPol = getPolicy("UserDefined", "{UDP}:UserDefinedProperty");

      	 if (myPol == null) {
              throw new MbUserException(this, "evaluate()", "",
                      "", "Policy NOT FOUND or NOT DEPLOYED", null);
		} 
      	 
      	 
      	// ⭐ Read property
           String userValue =
               myPol.getPropertyValueAsString("ID");
         //  String userValue1 =
                 //  myPol.getPropertyValueAsString("prop2");


           System.out.println("Policy value = " + userValue);
        //   System.out.println("Policy value = " + userValue1);
            MbElement root = outMessage.getRootElement();

           MbElement xmlRoot =
             root.createElementAsLastChild(MbElement.TYPE_NAME, "XMLNSC", null);

          MbElement result =
              xmlRoot.createElementAsLastChild(MbElement.TYPE_NAME, "Result", null);

           result.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"Value", userValue); 
         //  result.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"Value", userValue1);
           
		}
      	 
		   catch (Exception e) {
	            throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
	        }
      	 
        
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		getOutputTerminal("out").propagate(outAssembly);

	}
}
