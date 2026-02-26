<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- Output format -->
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>

    <!-- Root template -->
    <xsl:template match="/">
        <TransformedEmployees>
            <xsl:for-each select="Employees/Employee">
                <StaffMember>
                    <StaffID>
                        <xsl:value-of select="ID"/>
                    </StaffID>
                    <FullName>
                        <xsl:value-of select="concat(FirstName, ' ', LastName)"/>
                    </FullName>
                    <Division>
                        <xsl:value-of select="Department"/>
                    </Division>
                    <AnnualCompensation>
                        <xsl:value-of select="format-number(Salary * 1.10, '0.00')"/>
                    </AnnualCompensation>
                </StaffMember>
            </xsl:for-each>
        </TransformedEmployees>
    </xsl:template>

</xsl:stylesheet>
