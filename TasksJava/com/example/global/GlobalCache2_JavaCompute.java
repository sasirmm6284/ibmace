package com.example.global;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.*;

public class GlobalCache2_JavaCompute extends MbJavaComputeNode {

    public void evaluate(MbMessageAssembly inAssembly) throws MbException {

        MbMessage inMessage = inAssembly.getMessage();
        MbMessage outMessage = new MbMessage(inMessage);
        MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, outMessage);

        // Get global cache map
        MbGlobalMap globalCache = MbGlobalMap.getGlobalMap("CacheDetails");

        String dataSource = "";

        try {
            // Check if data is in cache
            String keyList = (String) globalCache.get("Cachekey");  // 🛠 Fixed to match where you're storing

            if (keyList == null || keyList.isEmpty()) {
                System.out.println("Cache empty: fetching from database...");
                dataSource = "Database";

                // Load JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                try (
                    Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/sys", "root", "Sasikumar6@");
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT id, name, Address FROM details")
                ) {

                    StringBuilder keysBuilder = new StringBuilder();

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String address = rs.getString("Address");

                        String studentData = name + "|" + address;
                        globalCache.put(String.valueOf(id), studentData);

                        keysBuilder.append(id).append(",");
                    }

                    // Store key list in cache
                    globalCache.put("Cachekey", keysBuilder.toString());

                    System.out.println("Data loaded into cache.");
                }

            } else {
                System.out.println("Fetching data from cache...");
                dataSource = "Global Cache";
            }

            // Build XML output
            MbElement outRoot = outMessage.getRootElement();
            MbElement outBody = outRoot.createElementAsLastChild(MbXMLNSC.PARSER_NAME);
            MbElement collegeList = outBody.createElementAsLastChild(MbElement.TYPE_NAME, "CollegeList", null);

            String keysStr = (String) globalCache.get("Cachekey");

            if (keysStr != null && !keysStr.isEmpty()) {
                String[] keys = keysStr.split(",");

                for (String key : keys) {
                    if (key == null || key.isEmpty()) continue;

                    String cachedData = (String) globalCache.get(key);
                    if (cachedData == null) {
                        System.out.println("No cache entry for key: " + key);
                        continue;
                    }

                    String[] values = cachedData.split("\\|");

                    if (values.length < 2) {
                        System.out.println("Invalid cache format for key: " + key);
                        continue;
                    }

                    MbElement record = collegeList.createElementAsLastChild(MbElement.TYPE_NAME, "Student", null);
                    record.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Id", key);
                    record.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Name", values[0]);
                    record.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Address", values[1]);
                }
            }

            System.out.println("Data for CollegeList fetched from: " + dataSource);

            getOutputTerminal("out").propagate(outAssembly);

        } catch (Exception e) {
            System.err.println("Error in GlobalCache2_JavaCompute: " + e.toString());
            throw new MbUserException(this, "evaluate", "", "", e.toString(), null);
        }
    }
}
