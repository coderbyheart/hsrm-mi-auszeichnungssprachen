<xsl:stylesheet version="1.0"
               xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns="http://www.w3.org/TR/xhtml1/strict">
    <xsl:key name="countryById" match="country" use="@id" />
    <xsl:template match="/">
        <html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" lang="de">
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                <meta http-equiv="Content-Language" content="de-de" />
                <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/3.1.1/build/cssbase/reset-base.css" />
		<style type="text/css">
		.right { text-align: right; }
		</style>
                <title>Deutschland</title>
            </head>
            <body>
                <h1>Deutschland</h1>
                <h2>Deutsche Bundesländer</h2>
                <p>Tabelle aller deutschen Bundesländer.</p>
                <table>
                	<thead>
                		<tr>
                			<th>Name</th>
                			<th>Einwohnerzahl</th>
                			<th>Fläche</th>
                			<th>Anzahl der Städte</th>
                			<th>Stadt mit der geringsten Einwohnerzahl</th>
                		</tr>
                	</thead>
                	<tbody>
	                	<!-- ID von Deutschland: f0_220 -->
	                	<xsl:for-each select='//country[@id="f0_220"]/province'>
		                	<xsl:sort select="@area" data-type="number" order="descending" />
		                	<tr>
			                	<td><xsl:value-of select="@name" /></td>
			                	<td class="right"><xsl:value-of select="@population" /></td>
			                	<td class="right"><xsl:value-of select="@area" /></td>
			                	<td class="right"><xsl:value-of select="count(city)" /></td>
			                	<td>
				                	<xsl:for-each select="city">
				                	<xsl:sort select="population" data-type="number" order="descending" />
			                		<xsl:if test="position()=last()">
						<xsl:value-of select="name" />
						</xsl:if>
						</xsl:for-each>
			                	</td>
		                	</tr>	                
	                	</xsl:for-each>
                	</tbody>
                </table>
                
                <h2>Organisationen, in denen Deutschland Mitglied ist</h2>
                <ul>
                	<xsl:for-each select='//organization/members[@country="f0_220"]'>
                		<li><xsl:value-of select="../@name" /></li>
		</xsl:for-each>
                </ul>
                
                <h2>Angrenzende Länder</h2>
                <table>
                	<thead>
                		<tr>
                			<th>Name</th>
                			<th>Bevölkerungsdichte</th>
                		</tr>
                	</thead>
                	<tbody>
                		<xsl:for-each select='key("countryById", //country[@id="f0_220"]/border/@country)'>
                			<xsl:sort select="round(@population div @total_area)" data-type="number" order="descending" />
		                	<tr>
			                	<td><xsl:value-of select="@name" /></td>
			                	<td><xsl:value-of select="round(@population div @total_area)" /> Einwohner / km²</td>
		                	</tr>	                
	                	</xsl:for-each>
             	</tbody>
                </table>

            </body>
        </html>   	
    </xsl:template>
</xsl:stylesheet>
