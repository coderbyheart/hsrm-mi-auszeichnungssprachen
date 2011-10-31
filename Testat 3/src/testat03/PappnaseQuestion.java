package testat03;

import java.util.LinkedList;

/**
 * Testat 03
 * 
 * Quizfrage einer Frage
 * 
 * @author Valery Becker <valery.becker@googlemail.com>
 * @author Markus Tacker <m@tacker.org>
 */
public class PappnaseQuestion {
	private String text;
	private LinkedList<PappnaseAnswer> answers;
	
	public PappnaseQuestion(String text)
	{
		this.text = text;
		answers = new LinkedList<PappnaseAnswer>();
	}


	public String getText() {
		return text;
	}
	


	public LinkedList<PappnaseAnswer> getAnswers() {
		return answers;
	}
	
	public void addAnswer(PappnaseAnswer answer)
	{
		answers.push(answer);
	}
	
	
}
