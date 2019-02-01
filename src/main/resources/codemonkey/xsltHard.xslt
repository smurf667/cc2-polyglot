<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:marker="http://localhost/marker"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="flags" />

	<!-- match all nodes that have a marker:anyOf attribute and handle them -->
	<xsl:template match="node()[@marker:anyOf]">
		<xsl:variable name="containsFlag">
			<xsl:choose>
				<xsl:when test="contains(@marker:anyOf, ',')">
					<!-- if the element holds a list, intersect that list with the flags passed in -->
					<xsl:call-template name="intersect">
						<xsl:with-param name="elementFlags" select="@marker:anyOf" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<!-- just one flag, directly check if it is present on the passed in flags -->
					<xsl:value-of select="contains($flags, @marker:anyOf)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="contains($containsFlag, 'true')">
			<!-- the element needs to stay in, because some flags are present on the element and in the
			passed in flags parameter, but the marker:anyOf attribute needs to go -->
			<xsl:call-template name="copy-element-no-marker"/>
		</xsl:if>
	</xsl:template>

	<!-- match all nodes that have a marker:anyOf attribute and handle them -->
	<xsl:template match="node()[@marker:allOf]">
		<xsl:variable name="containsFlag">
			<xsl:choose>
				<xsl:when test="contains(@marker:allOf, ',')">
					<!-- if the element holds a list, intersect that list with the flags passed in -->
					<xsl:call-template name="intersect">
						<xsl:with-param name="elementFlags" select="@marker:allOf" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<!-- just one flag, directly check if it is present on the passed in flags -->
					<xsl:value-of select="contains($flags, @marker:allOf)" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="not(contains($containsFlag, 'false'))">
			<!-- the element needs to stay in, because all flags are present on the element and in the
			passed in flags parameter, but the marker:allOf attribute needs to go -->
			<xsl:call-template name="copy-element-no-marker"/>
		</xsl:if>
	</xsl:template>
	
	<!-- handle all the rest by copying -->
	<xsl:template match="@*|text()|comment()|processing-instruction()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()|text()|comment()|processing-instruction()" />
		</xsl:copy>
	</xsl:template>

	<!-- copy nodes but drop unused namespaces -->
	<xsl:template match="*">
		<xsl:element name="{name()}">
			<xsl:variable name="currElem" select="." />
			<xsl:for-each select="namespace::*">
				<xsl:variable name="currPrefix" select="name()" />
				<!-- preserve the namespace if descendants use it -->
				<xsl:if test="not($currPrefix='marker') and ($currElem/descendant::*[namespace-uri()=current() and substring-before(name(),':') = $currPrefix or @*[substring-before(name(),':') = $currPrefix]])">
					<xsl:copy-of select="." />
				</xsl:if>
			</xsl:for-each>
			<xsl:apply-templates select="node()|@*|comment()" />
		</xsl:element>
	</xsl:template>

	<!-- the intersection recursively compares all element flags against the passed in
	flags and matches each element flag against the flags, replacing the occurrence with
	'true' or 'false'
	the result of e.g. flags passed in "a,b" and flags present on the element "x,b,y" would
	be "falsetruefalse". For "anyOf" one "true" is enough, for "allOf", no "false" must
	occur...
	-->
	<xsl:template name="intersect">
		<xsl:param name="elementFlags" />
		<xsl:choose>
			<xsl:when test="contains($elementFlags, ',')">
				<xsl:value-of select="contains($flags, substring-before($elementFlags, ','))" />
				<xsl:call-template name="intersect">
					<xsl:with-param name="elementFlags" select="substring-after($elementFlags, ',')" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="contains($flags, $elementFlags)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="copy-element-no-marker">
		<xsl:element name="{name()}">
			<xsl:variable name="currElem" select="." />
			<xsl:for-each select="namespace::*">
				<xsl:variable name="currPrefix" select="name()" />
				<xsl:if test="$currPrefix != 'marker'">
					<xsl:copy-of select="." />
				</xsl:if>
			</xsl:for-each>
			<xsl:apply-templates select="node()|text()|comment()|processing-instruction()" />
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>
