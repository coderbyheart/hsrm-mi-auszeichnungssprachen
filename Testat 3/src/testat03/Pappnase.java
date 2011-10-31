package testat03;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;

import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Testat 03
 * 
 * Wiesbaden sucht die Super-Pappnase Für das beliebte Quiz „Wiesbaden sucht die
 * Super-Pappnase“ sollen neue Kandidaten gefunden werden. Dazu stehen
 * XML-Dateien mit Fragen aus verschiedenen Wissensgebieten zur Verfügung.
 * 
 * Das Quiz stell 5 Fragen und gibt am Ende das Ergebnis aus.
 * 
 * Die gestellten Fragen werden in einer HTML-Datei gespeichert.
 * 
 * @author Valery Becker <valery.becker@googlemail.com>
 * @author Markus Tacker <m@tacker.org>
 */
public class Pappnase {

	private String questionXmlFile;
	private String topic;
	private Document questionDom;
	private LinkedList<PappnaseQuestion> questions;
	private LinkedList<PappnaseQuestion> questionsAsked;
	private int questionsCorrect = 0;

	private static final int NUM_QUESTIONS = 5;

	/**
	 * Startet das Quiz
	 * 
	 * @param args
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException, XPathExpressionException {

		if (args.length == 0) {
			new Pappnase("data/Fragen1.xml", "nasen");
		} else {
			new Pappnase(args[0], args[1]);
		}
	}

	/**
	 * Ereugt ein Quiz zum Thema topic, die Fragem kommen aus questionXmlFile
	 * 
	 * @param questionXmlFile
	 * @param topic
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public Pappnase(String questionXmlFile, String topic)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException {
		this.questionXmlFile = questionXmlFile;
		this.topic = topic;
		questions = new LinkedList<PappnaseQuestion>();

		parseDocument();
		loadQuestions();
		doQuiz();
		saveQuiz();
	}

	/**
	 * Parst das XML-File und lädt die Fragen zum angeforderten Gebiet
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void parseDocument() throws ParserConfigurationException,
			SAXException, IOException {
		DocumentBuilderFactory domBuilderFactory = DocumentBuilderFactory
				.newInstance();
		domBuilderFactory.setIgnoringElementContentWhitespace(true);
		domBuilderFactory.setIgnoringComments(true);
		DocumentBuilder domBuilder = domBuilderFactory.newDocumentBuilder();
		questionDom = domBuilder.parse(questionXmlFile);
		removeWhitespaceNodes(questionDom.getDocumentElement());
	}

	/**
	 * Liest die Fragen ein
	 * 
	 * @throws XPathExpressionException
	 */
	private void loadQuestions() throws XPathExpressionException {
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		XPathExpression xPression = xpath
				.compile("/fragekatalog/frage[@gebiet=\"" + topic + "\"]");
		NodeList result = (NodeList) xPression.evaluate(questionDom,
				XPathConstants.NODESET);
		for (int i = 0; i < result.getLength(); i++) {
			Node node = result.item(i);
			PappnaseQuestion question = new PappnaseQuestion(node
					.getFirstChild().getTextContent());
			addAnswers(node, question);
			addQuestion(question);
		}
	}

	/**
	 * Liest die Antwortmöglichkeiten ein
	 * 
	 * @param frageNode
	 * @param question
	 */
	private void addAnswers(Node frageNode, PappnaseQuestion question) {
		NodeList childs = frageNode.getChildNodes();
		for (int i = 0, n = childs.getLength(); i < n; i++) {
			Node node = childs.item(i);
			if (!node.getNodeName().equals("antwortmoeglichkeiten"))
				continue;
			NodeList answerNodes = node.getChildNodes();
			for (int j = 0; j < answerNodes.getLength(); j++) {
				Node answerNode = answerNodes.item(j);
				boolean answerCorrect = answerNode.getAttributes()
						.getNamedItem("korrekt").getNodeValue().equals("ja");
				PappnaseAnswer answer = new PappnaseAnswer(answerNode
						.getTextContent(), answerCorrect);
				question.addAnswer(answer);
			}
		}

	}

	/**
	 * Fügt dem aktuellen Quiz eine Frage hinzu
	 * 
	 * @param question
	 */
	private void addQuestion(PappnaseQuestion question) {
		questions.add(question);
	}

	/**
	 * Entfernt whitespace aus dem Document
	 * 
	 * @see http://forums.java.net/jive/thread.jspa?messageID=345459
	 * @param e
	 */
	private static void removeWhitespaceNodes(Element e) {
		NodeList children = e.getChildNodes();
		for (int i = children.getLength() - 1; i >= 0; i--) {
			Node child = children.item(i);
			if (child instanceof Text
					&& ((Text) child).getData().trim().length() == 0) {
				e.removeChild(child);
			} else if (child instanceof Element) {
				removeWhitespaceNodes((Element) child);
			}
		}
	}

	/**
	 * Startet das Quiz
	 */
	private void doQuiz() {
		int numQuestions = Math.min(NUM_QUESTIONS, questions.size());
		questionsAsked = new LinkedList<PappnaseQuestion>();
		System.out
				.println("*** Willkommen zu Wiesbaden sucht die Super-Pappnase (WSDSP) ***");
		System.out.println();
		System.out.println("Du musst jetzt " + numQuestions
				+ " Fragen zum Thema \"" + topic + "\" beantworten.");

		for (int i = 0; i < numQuestions; i++) {
			// Frage ausgeben
			System.out.println();
			PappnaseQuestion currQuestion = getNextQuestions();
			LinkedList<PappnaseAnswer> currAnswers = currQuestion.getAnswers();
			System.out.println(" " + (i + 1) + ". Frage: ");

			// Antwortmöglichkeiten ausgeben
			System.out.println("    " + currQuestion.getText());
			for (int j = 0; j < currAnswers.size(); j++) {
				System.out.println(" " + nToChar(j) + ") "
						+ currAnswers.get(j).getText());
			}

			// Antwort einlesen
			int selectedAnswerIndex = -1;
			boolean answerOk = false;
			System.out.print("    Deine Antwort: ");
			do {
				try {
					selectedAnswerIndex = charToN((char) System.in.read());
					if (selectedAnswerIndex >= 0
							&& selectedAnswerIndex < currAnswers.size())
						answerOk = true;
				} catch (IOException e) {
				}
			} while (!answerOk);

			// Ergebnis der Antwort anzeigen
			PappnaseAnswer selectedAnswer = currAnswers
					.get(selectedAnswerIndex);

			if (selectedAnswer.isCorrect()) {
				System.out.println("    " + selectedAnswer.getText()
						+ " ist richtig!");
				questionsCorrect++;
			} else {
				System.out.println("    " + selectedAnswer.getText()
						+ " ist leider falsch.");
			}
		}

		// Zusammenfassung anzeigen
		System.out.println();
		if (questionsCorrect == NUM_QUESTIONS)
			System.out.print("Toll! ");
		System.out.println("Du hast " + questionsCorrect + " von "
				+ NUM_QUESTIONS + " Fragen richtig beantwortet.");
	}

	/**
	 * Wandelt eine Zahl in einen Buchstaben um (0... -> a...)
	 * 
	 * @param n
	 * @return Buchstaben zur Zahl
	 */
	private char nToChar(int n) {
		return (char) (97 + n);
	}

	/**
	 * Wandelt einen Buchstaben in eine Zahl um (a... -> 0...)
	 * 
	 * @param c
	 * @return Zahl des Buchstabens
	 */
	private int charToN(char c) {
		return (int) c - 97;
	}

	/**
	 * Ermittelt eine zufällige nächste Zahl
	 * 
	 * @return Nächste Frage
	 */
	private PappnaseQuestion getNextQuestions() {
		int randIndex;
		do {
			randIndex = (int) (Math.random() * questions.size());
		} while (questionsAsked.contains(questions.get(randIndex)));
		questionsAsked.add(questions.get(randIndex));
		return questions.get(randIndex);
	}

	/**
	 * Speichert das Quiz in einer XHTML-Datei
	 * 
	 * @throws ParserConfigurationException
	 */
	private void saveQuiz() throws ParserConfigurationException {
		// Document erzeugen
		Document doc = buildDoc();

		// Speichern
		System.out.println();
		String htmlOut = topic + ".html";
		try {
			writeToFile(docToString(doc), htmlOut);
			System.out.println("Zusammenfassung in " + htmlOut
					+ " geschrieben.");
		} catch (Exception e) {
			System.out.println("Problem!");
			System.out.println("Konnte Zusammenfassung nicht in " + htmlOut
					+ " speichern.");
		}
	}

	/**
	 * Erzeugt ein DOM-Document des Quizzes
	 * 
	 * @return Document
	 * @throws ParserConfigurationException
	 */
	private Document buildDoc() throws ParserConfigurationException {
		// Document erzeugen
		DocumentBuilderFactory domBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder domBuilder = domBuilderFactory.newDocumentBuilder();

		DocumentType xhtmlStrict = domBuilder.getDOMImplementation()
				.createDocumentType("html", "-//W3C//DTD XHTML 1.0 Strict//EN",
						"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
		Document doc = domBuilder.getDOMImplementation().createDocument(
				"http://www.w3.org/1999/xhtml", "html", xhtmlStrict);
		doc.setXmlStandalone(true);
		Node html = doc.getDocumentElement();

		// Head
		Node head = doc.createElement("head");
		html.appendChild(head);

		Element meta = doc.createElement("meta");
		meta.setAttribute("http-equiv", "content-type");
		meta.setAttribute("content", "text/html; charset=utf-8");
		head.appendChild(meta);

		Element style = doc.createElement("style");
		head.appendChild(style);
		style.setAttribute("type", "text/css");
		style
				.appendChild(doc
						.createTextNode("\nbody { font-family: \"Lucida Grande\", Arial, Helvetica, sans-serif; }\nbody ol li { margin: 0 0 1em 0; }\nol ol li { font-size: 0.875em; margin: 0.5em 0 0 0; }\n"));

		// Title
		Node title = doc.createElement("title");
		title.appendChild(doc.createTextNode("Gestellte Fragen aus dem Thema: "
				+ topic));
		head.appendChild(title);

		// Body
		Element body = doc.createElement("body");
		html.appendChild(body);
		Element h1 = doc.createElement("h1");
		h1.appendChild(doc
				.createTextNode(NUM_QUESTIONS + " Fragen zu " + topic));
		body.appendChild(h1);

		// Fragen
		Element frageOl = doc.createElement("ol");
		body.appendChild(frageOl);

		for (PappnaseQuestion currQuestion : questionsAsked) {
			// Frage-Text
			Element frageLi = doc.createElement("li");
			frageLi.appendChild(doc.createTextNode(currQuestion.getText()));
			frageOl.appendChild(frageLi);
			// Antwort-Optionen
			Element frageUl = doc.createElement("ol");
			frageLi.appendChild(frageUl);
			frageUl.setAttribute("style", "list-style-type: lower-alpha;");
			for (PappnaseAnswer currAnswer : currQuestion.getAnswers()) {
				Element li = doc.createElement("li");
				if (currAnswer.isCorrect()) {
					Element s = doc.createElement("strong");
					s.appendChild(doc.createTextNode(currAnswer.getText()));
					li.appendChild(s);
				} else {
					li.appendChild(doc.createTextNode(currAnswer.getText()));
				}
				frageUl.appendChild(li);
			}
		}

		return doc;
	}

	/**
	 * Schreibt einen String in eine Datei
	 * 
	 * @param content
	 * @param filename
	 * @throws Exception
	 */
	private void writeToFile(String content, String filename) throws Exception {

		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(filename));
			bw.write(content);
			bw.close();
		} catch (IOException e1) {
			throw new Exception("Konnte Datei " + filename + " nicht öffnen.");
		}
	}

	/**
	 * Erzeugt einen String aus einem XHTML-Dom-Document
	 * 
	 * @param doc
	 * @return String
	 */
	public String docToString(Document doc) {
		try {
			Source source = new DOMSource(doc);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			// damit der HTML Doctype ausgegeben wird
			DocumentType doctype = doc.getDoctype();
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype
					.getPublicId());
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype
					.getSystemId());
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}
}
