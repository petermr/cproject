<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html"/>
    
	<xsl:template match="/">
	  <html>
		<xsl:apply-templates select="argList"/>
	  </html>
	</xsl:template>

	<xsl:template match="argList">
	  <div>
	    <h1><tt><xsl:value-of select="@name"/> (<xsl:value-of select="@version"/>)</tt></h1>
		<xsl:apply-templates select="arg"/>
	  </div>
	</xsl:template>

	<xsl:template match="arg">
	<!-- 
		if (BRIEF.equals(namex)) {
			this.setBrief(value);
		} else if (LONG.equals(namex)) {
			this.setLong(value);
		} else if (NAME.equals(namex)) {
			this.setName(value);
		} else if (HELP.equals(namex)) {
			this.setHelp(value);
		} else if (ARGS.equals(namex)) {
			this.setArgs(value);
		} else if (CLASS_TYPE.equals(namex)) {
			this.setClassType(value);
		} else if (DEFAULT.equals(namex)) {
			this.setDefault(value);
		} else if (COUNT_RANGE.equals(namex)) {
			this.setCountRange(value);
		} else if (FORBIDDEN.equals(namex)) {
			this.setForbiddenString(value);
		} else if (REQUIRED.equals(namex)) {
			this.setRequiredString(value);
		} else if (FINAL_METHOD.equals(namex)) {
			this.setFinalMethod(value);
		} else if (INIT_METHOD.equals(namex)) {
			this.setInitMethod(value);
		} else if (OUTPUT_METHOD.equals(namex)) {
			this.setOutputMethod(value);
		} else if (PARSE_METHOD.equals(namex)) {
			this.setParseMethod(value);
		} else if (PATTERN.equals(namex)) {
			this.setPatternString(value);
		} else if (RUN_METHOD.equals(namex)) {
			this.setRunMethod(value);
		} else if (VALUE_RANGE.equals(namex)) {
			this.setValueRange(value);
		} else {
			throw new RuntimeException("Unknown attribute on <arg name='"+name+"'>: "+namex+"='"+value+"'");
		}
	 -->
		<h2>argument <tt><xsl:value-of select="@name"/></tt></h2>
		<code><xsl:value-of select="@long"/><xsl:text> </xsl:text><xsl:value-of select="@args"/><xsl:value-of select="@countRange"/></code><br/>
		<xsl:if test="@pattern or @valueRange">
		  <code>Constraints: <xsl:text> </xsl:text><xsl:value-of select="@pattern or @valueRange"/</code>
		</xsl:if>
		<xsl:if test="@default">
		  <code>Default: <xsl:text> </xsl:text><xsl:value-of select="@default"/</code>
		</xsl:if>
		<em>b: <xsl:value-of select="@brief"/> h: <xsl:value-of select="@help"/>  c: <xsl:value-of select="@help"/></em>
		<xsl:apply-templates select="help"/>
	</xsl:template>

	<xsl:template match="help">
	  <h2>Description</h2>
	  <xsl:copy-of select="."/>
	</xsl:template>
	
</xsl:stylesheet>
