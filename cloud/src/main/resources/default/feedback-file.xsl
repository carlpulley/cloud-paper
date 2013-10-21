<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:param name="module"/>
  <xsl:param name="student"/>

  <xsl:template match="/feedback">
    <html>
      <head>
        <title>Assessment feedback for <xsl:value-of select='translate($module, "abcdefghijklmnopqrstuvwxyz", "ABCDEFGHIJKLMNOPQRSTUVWXYZ")'/></title>
      </head>
      <body>
        <h1><xsl:value-of select="$student"/>: <xsl:value-of select='translate($module, "abcdefghijklmnopqrstuvwxyz", "ABCDEFGHIJKLMNOPQRSTUVWXYZ")'/> assessment feedback</h1>
    
        <ol>
          <xsl:for-each select="question">
            <xsl:sort select="@value"/>
            <xsl:for-each select="suite/test[@passed='false']/outcome">
              <li>
                <xsl:value-of select="comment"/>
              </li>
            </xsl:for-each>
          </xsl:for-each>
        </ol>
      </body>
    </html>
  </xsl:template>
</xsl:transform>
