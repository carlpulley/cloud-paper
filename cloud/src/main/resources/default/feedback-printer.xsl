<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:param name="module"/>
  <xsl:param name="student"/>
   
  <xsl:template match="/feedback">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">    
      <fo:layout-master-set>
        <fo:simple-page-master master-name="A4"
          page-width="297mm" 
          page-height="210mm"
          margin-top="1cm" 
          margin-bottom="1cm"
          margin-left="1cm" 
          margin-right="1cm"
        >
          <fo:region-body margin="3cm"/>
          <fo:region-before extent="2cm" region-name="xsl-region-header"/>
          <fo:region-after extent="2cm" region-name="xsl-region-footer"/>
          <fo:region-start extent="2cm"/>
          <fo:region-end extent="2cm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>
    
      <fo:page-sequence master-reference="A4" id="seq1">
        <fo:static-content flow-name="xsl-region-header">
          <fo:block font-size="9pt" text-align="center">
            <xsl:value-of select="$student"/>
          </fo:block>          
        </fo:static-content>

        <fo:static-content flow-name="xsl-region-footer">
          <fo:block font-size="9pt" text-align="center">
            Page <fo:page-number/> of <fo:page-number-citation-last ref-id="seq1"/>
          </fo:block>          
        </fo:static-content>

        <fo:flow flow-name="xsl-region-body">
          <fo:block font-family="arial">
            <fo:block font-size="16pt" font-weight="bold" margin-bottom="20mm">
              <xsl:value-of select='translate($module, "abcdefghijklmnopqrstuvwxyz", "ABCDEFGHIJKLMNOPQRSTUVWXYZ")'/> assessment feedback
            </fo:block>

            <fo:table font-size="12pt" table-layout="fixed" width="100%">
              <fo:table-column column-width="25mm"/>
              <fo:table-column column-width="proportional-column-width(1)"/>
            
              <fo:table-header>
                <fo:table-row>
                  <fo:table-cell border-style="solid" padding="1mm">
                    <fo:block font-weight="bold" text-align="center">
                      Question
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell border-style="solid" padding="1mm">
                    <fo:block font-weight="bold" text-align="center">
                      Comments
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-header>

              <fo:table-body>
              <xsl:for-each select="item">
                <xsl:sort select="@id"/>
    
                <fo:table-row>
                  <fo:table-cell border-style="solid" padding="1mm" display-align="center">
                    <fo:block text-align="center">
                      <xsl:value-of select="@id"/>
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell border-style="solid" padding="1mm">
                    <fo:block>
                      <xsl:value-of select="comment"/>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </xsl:for-each>
              </fo:table-body>
            </fo:table>
          </fo:block>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
</xsl:transform>
