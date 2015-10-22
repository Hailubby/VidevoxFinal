package vidivox;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class DocumentLimit extends DocumentFilter {

	int maximumCharacters;
	
	public DocumentLimit(int maximum){
		maximumCharacters = maximum;
	}
	
	public void insertString(FilterBypass fb, int offs,String str, AttributeSet a) throws BadLocationException{
		//Below the character limit, insert strings or text as usual
		if ((fb.getDocument().getLength() + str.length()) <= maximumCharacters)
	            super.insertString(fb, offs, str, a);
	        else
	        	//If past the character limit, beep instead of inserting the characters
	            Toolkit.getDefaultToolkit().beep();
	}
	
	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException{
		if ((fb.getDocument().getLength() + str.length() - length) <= maximumCharacters){
			super.replace(fb, offs, length, str, a);
		}else{
			Toolkit.getDefaultToolkit().beep();
		
		}	
	}
}
