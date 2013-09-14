<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:param name="title"/>
  <xsl:param name="student"/>

  <xsl:template match="/feedback">
    <html>
      <head>
        <title><xsl:value-of select="$title"/></title>
      </head>
      <body>
        <h1><xsl:value-of select="$student"/>: <xsl:value-of select="$title"/></h1>
    
        <ol>
          <xsl:for-each select="item">
            <xsl:sort select="@id"/>
            <li>
              <xsl:value-of select="comment"/>
            </li>
          </xsl:for-each>
        </ol>
      </body>
    </html>
  </xsl:template>
</xsl:transform>
