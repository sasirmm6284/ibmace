<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ext="http://example.com/hospital/extensions"
    exclude-result-prefixes="ext">
      <!-- Prevent XSLT from adding <?xml ...?> line -->
    <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>

    <!-- Read timestamp from input XML -->
    <xsl:variable name="timestamp" select="string(/HospitalData/Timestamp)"/>

    <!-- Root template -->
    <xsl:template match="/HospitalData">
        <HospitalData 
            xmlns:ext="http://example.com/hospital/extensions"
            ext:processedOn="{$timestamp}"
            ext:source="ACE-System"
            ext:version="1.0">
            <xsl:apply-templates select="Patient"/>
        </HospitalData>
    </xsl:template>

    <!-- Patient template -->
    <xsl:template match="Patient">
        <Patient ext:lastModified="{$timestamp}" ext:id="{PatientID}">
            <xsl:copy-of select="PatientID | FirstName | LastName | Age | Gender | Phone | Address"/>
            <xsl:apply-templates select="Visit"/>
        </Patient>
    </xsl:template>

    <!-- Visit template -->
    <xsl:template match="Visit">
        <Visit ext:visitTag="{concat(VisitID,'-TAG')}" ext:visitTime="{$timestamp}">
            <xsl:copy-of select="VisitID | VisitDate | DoctorName | Department | Diagnosis | Medication | Dose | BillAmount"/>
        </Visit>
    </xsl:template>

</xsl:stylesheet>
