<xsl:stylesheet version="1.0"
	xmlns:e="http://localhost/easy-in"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- This transformation should put the input data into a simple
	items list of addresses, resolving the name, address, city and country
	to simple elements carrying text.
	Here, only the behavior for very specific input is shown.
	The real desired transformation requires more logic, of course.
	 -->
	<xsl:template match="/e:data/e:items/e:item[@firstName = 'Jane']">
		<items>
			<item>
				<name>Jane Doe</name>
				<address>Sesame Street</address>
				<city>Lugano</city>
				<country>Switzerland</country>
			</item>
		</items>
	</xsl:template>
	
	<!-- recurse for everything else, but don't output unless matched above -->
	<xsl:template match="@*|node()|text()|comment()|processing-instruction()">
		<xsl:apply-templates select="node()" />
	</xsl:template>

</xsl:stylesheet>