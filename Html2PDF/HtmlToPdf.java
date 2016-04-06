package br.gov.prf.olever.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import com.lowagie.text.Image;

/**
 * Classe utilitária responsável por converter HTML em PDF.
 * @author Wesley Luiz
 * @version 1.0.0
 */
public final class HtmlToPdf {

	public static final String ENCODE_UTF_8 = "UTF-8";

	private HtmlToPdf() {
		super();
	}
	
	/**
	 * Método que recebe uma <code>String</code> contendo o código html 
	 * o nome do arquivo e gera uma saída em stream do PDF.
	 * @author Wesley Luiz
	 * @param html
	 * @param nomeArquivo
	 * @param context
	 */
	public static void converter(String html, String nomeArquivo, FacesContext context) {
		try {
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");
			response.setContentType("application/pdf");

			OutputStream out = response.getOutputStream();
			
			gerarSaidaEmPDF(html, out, ENCODE_UTF_8);
			
			out.flush();
			out.close();
			
			context.responseComplete();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Método responsável por montar a estrutura básica de um arquivo HTML (adiciona as tags: html, head e body).
	 * @author Wesley Luiz
	 * @param html
	 * @return
	 */
	public static String montarEstruturaHtml(String html) {
		return new StringBuilder()
		.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
		.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\">")
		.append("<head>")
		.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">")
		.append("</head>")
		.append("<body style=\"font-family:Times New Roman\">")
		.append(html)
		.append("</body>")
		.append("</html>")
		.toString();
	}
	
	private static void gerarSaidaEmPDF(String html, OutputStream out, String encode) {
		try {
			ITextRenderer renderer = new ITextRenderer();
			InputStream in = new ByteArrayInputStream(html.getBytes(Charset.forName(encode)));

			Tidy tidy = new Tidy();
			tidy.setInputEncoding(encode);
			tidy.setOutputEncoding(encode);
			
			Document doc = tidy.parseDOM(in, null);
			
			renderer.getSharedContext().setReplacedElementFactory(new ImageReplaced());
			renderer.setDocument(doc, null);
			renderer.layout();
			renderer.createPDF(out);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static class ImageReplaced implements ReplacedElementFactory {

		@Override
		@SuppressWarnings("restriction")
		public ReplacedElement createReplacedElement(final LayoutContext c, final BlockBox box, final UserAgentCallback uac, final int cssWidth, final int cssHeight) {
			if ("img".equals(box.getElement().getNodeName())) {
				String attr = box.getElement().getAttribute("src");

				Image image = null;

				try {
					byte[] dados = null;
					
					if (attr.contains("http")) {
						String[] ext = attr.split("\\.");
						File img = new File("temp.".concat(ext[ext.length -1]));
						FileUtils.copyURLToFile(new URL(attr), img);
						dados = IOUtils.toByteArray(new FileInputStream(img));
					} else {
						String base64 = attr.split(",", 2)[1];
						final sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
						dados = dec.decodeBuffer(base64);
					}
					
					image = Image.getInstance(dados);
				} catch (final Exception e) {
					e.printStackTrace();
				}
				
				final FSImage fsImage = new ITextFSImage(image);
				
				if (cssWidth != -1 && cssHeight != -1) {
					fsImage.scale(cssWidth, cssHeight);
				}
				
				return new ITextImageElement(fsImage);
			}
			return null;
		}

		@Override
		public void reset() {

		}

		@Override
		public void remove(final Element e) {

		}

		@Override
		public void setFormSubmissionListener(final FormSubmissionListener listener) {

		}
	}
}
