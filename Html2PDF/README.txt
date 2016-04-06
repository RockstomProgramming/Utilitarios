Esta classe fornece a funcionalidade de converter arquivos no formato HTML em PDF.

Adicione as seguintes dependências:
<dependency>
	<groupId>com.itextpdf</groupId>
	<artifactId>itextpdf</artifactId>
	<version>5.4.2</version>
</dependency>

<dependency>
	<groupId>com.itextpdf.tool</groupId>
	<artifactId>xmlworker</artifactId>
	<version>5.4.1</version>
</dependency>

<dependency>
	<groupId>org.xhtmlrenderer</groupId>
	<artifactId>core-renderer</artifactId>
	<version>R8</version>
</dependency>

<dependency>
  	<groupId>com.sun.media</groupId>
  	<artifactId>jai-codec</artifactId>
 	 <version>1.1.3</version>
</dependency>

<dependency>
	<groupId>net.sf.jtidy</groupId>
	<artifactId>jtidy</artifactId>
	<version>r938</version>
</dependency>

Exemplo de uso:

String html = HtmlToPdf.montarEstruturaHtml("<h1>Hello World!</h1>");
HtmlToPdf.converter(html, "hello.pdf", FacesContext.getCurrentInstance());
