<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes"/>
  <xsl:template match="/">
    <html>
      <head>
        <title>Checkstyle Results</title>
        <style type="text/css">
          body {
            font-family: Arial, sans-serif;
            margin: 20px;
          }
          .error { color: #FF0000; }
          .warning { color: #FFA500; }
          table { 
            border-collapse: collapse; 
            width: 100%; 
            margin-bottom: 20px;
          }
          th, td { 
            border: 1px solid #ddd; 
            padding: 8px; 
            text-align: left; 
          }
          th {
            background-color: #f2f2f2;
            font-weight: bold;
          }
          tr:nth-child(even) { background-color: #f9f9f9; }
          tr:hover { background-color: #f2f2f2; }
          h1 { color: #333; }
          .summary { 
            margin-bottom: 20px; 
            padding: 10px; 
            background-color: #f8f8f8;
            border: 1px solid #ddd;
          }
        </style>
      </head>
      <body>
        <h1>Checkstyle Results</h1>
        
        <div class="summary">
          <h2>Summary</h2>
          <p>
            <strong>Files checked:</strong> <xsl:value-of select="count(//file)"/>
            <br/>
            <strong>Total errors:</strong> <xsl:value-of select="count(//error[@severity='error'])"/>
            <br/>
            <strong>Total warnings:</strong> <xsl:value-of select="count(//error[@severity='warning'])"/>
          </p>
        </div>
        
        <h2>Details</h2>
        <xsl:for-each select="//file[error]">
          <h3>
            <xsl:value-of select="@name"/>
          </h3>
          <table>
            <tr>
              <th>Line</th>
              <th>Column</th>
              <th>Severity</th>
              <th>Message</th>
              <th>Rule</th>
            </tr>
            <xsl:for-each select="error">
              <tr>
                <td><xsl:value-of select="@line"/></td>
                <td><xsl:value-of select="@column"/></td>
                <td>
                  <xsl:attribute name="class">
                    <xsl:value-of select="@severity"/>
                  </xsl:attribute>
                  <xsl:value-of select="@severity"/>
                </td>
                <td><xsl:value-of select="@message"/></td>
                <td><xsl:value-of select="@source"/></td>
              </tr>
            </xsl:for-each>
          </table>
        </xsl:for-each>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
