package com.nmit;

public class ReverseString {
    
    // Static method to reverse the string
    public static String getReverseString(String str) {
        StringBuffer buffer = new StringBuffer();
        buffer = buffer.append(str);
        return buffer.reverse().toString();
    }

    // Main method to test the getReverseString function
    public static void main(String[] args) {
        // Test input
        String testString = "Hello, World!";
        
        // Call the getReverseString method and print the result
        String reversed = getReverseString(testString);
        
        // Output the result
        System.out.println("Original String: " + testString);
        System.out.println("Reversed String: " + reversed);
    }
}
