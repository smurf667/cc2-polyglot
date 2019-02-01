<xsl:stylesheet version="1.0"
	xmlns:e="http://localhost/easy-in"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- copy 'items' and apply templates to all element children -->
	<xsl:template match="//e:items">
		<!-- this is a bit 'exotic' as normally xsl:copy would be used, but 
			the emitted namespace does not work for the xpath matcher used
			in the verifier -->
		<items>
			<xsl:apply-templates select="node()" />
		</items>
	</xsl:template>

	<!-- transform the item into the desired format -->
	<xsl:template match="//e:item">
		<item>
			<name><xsl:value-of select="@firstName"/><xsl:text> </xsl:text><xsl:value-of select="@lastName"/></name>
			<address><xsl:value-of select="@street"/></address>
			<city><xsl:value-of select="/e:data/e:cities/e:city[@code = current()/@cityCode]/text()"/></city>
			<country><xsl:value-of select="/e:data/e:countries/e:country[@code = current()/@countryCode]/@name"/></country>
		</item>
	</xsl:template>

	<!-- recurse for everything else, but don't output unless matched above -->
	<xsl:template match="@*|node()|text()|comment()|processing-instruction()">
		<xsl:apply-templates select="node()" />
	</xsl:template>

</xsl:stylesheet>
