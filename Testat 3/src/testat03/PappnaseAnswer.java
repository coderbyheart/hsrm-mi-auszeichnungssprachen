package testat03;

/**
 * Testat 03
 * 
 * Antwortmöglichkeit einer Frage
 * 
 * @author Valery Becker <valery.becker@googlemail.com>
 * @author Markus Tacker <m@tacker.org>
 */
public class PappnaseAnswer {
	private String text;
	private boolean isCorrectAnswer = false;
	
	public PappnaseAnswer(String text, boolean isCorrectAnswer)
	{
		this.text = text;
		this.isCorrectAnswer = isCorrectAnswer;
	}

	public String getText() {
		return text;
	}

	public boolean isCorrect() {
		return isCorrectAnswer;
	}

}
