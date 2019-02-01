<xsl:stylesheet version="1.0"
	xmlns:marker="http://localhost/marker"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	exclude-result-prefixes="marker">

	<!-- this transformation takes a parameter 'flags'
	which holds a comma-separated list of arbitrary strings
	(no whitespaces) which need to be processed -->
	<xsl:param name="flags"/>
	
	<!-- This transformation should handle all elements using the
	marker namespace; if there is a flag overlap between the 'flags'
	parameter value and the 'anyOf' value, the element stays, if not
	it is removed. For 'allOf', all listed flags must be present in
	'flags'. Elements that do not use the marker namespace are kept,
	however if other namespaces are declared but not used, they should
	be removed. The marker namespace must not be present in the output.
	Elements such as comments need to be preserved.
	Here, only the behavior for very specific input and flags="x,y,z"
	is shown.
	The real desired transformation requires more logic, of course.
	 -->
	<xsl:template match="a/b[@marker:anyOf = $flags]">
		<a>
			<b>this is a simple structure present when any of 'x', 'y' or 'z' are passed in the flags string</b>
			<b>
				<c>hello - the parent should no longer use the 'unused' namespace, as no element is using it</c>
			</b>
			<b xmlns:used="http://localhost/used">
				<!-- the used namespace is present on the second 'c' element and thus should not be removed -->
				<c>hello</c>
				<c used:used="true">world</c>
			</b>
		</a>
	</xsl:template>

</xsl:stylesheet>