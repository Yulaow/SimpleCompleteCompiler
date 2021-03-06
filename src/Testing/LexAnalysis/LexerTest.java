package Testing.LexAnalysis;

import LexAnalysis.Lexers.Lexer;
import LexAnalysis.Tag;
import LexAnalysis.Token;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class LexerTest {

    private List<TestCase> tc = new ArrayList<TestCase>();


    private class TestCase {
        String inputString;
        String shouldBeResult;

        TestCase(String inputString, String shouldBeResult) {
            this.inputString = inputString;
            this.shouldBeResult = shouldBeResult;
        }
    }

    @Before
    public void setUp() throws Exception {
        tc.add(new TestCase("d=300;", "<257, d> <61> <256, 300> <59> <-1>" ));
        tc.add(new TestCase("print(d*t);", "<264, print> <40> <257, d> <42> <257, t> <41> <59> <-1>" ));
        tc.add(new TestCase("if x>y then x=0", "<259, if> <257, x> <258, >> <257, y> <260, then> <257, x> <61> <256, 0> <-1>" ));
        tc.add(new TestCase("for (ifx=1; ifx<=printread) do ifx=ifx+1", "<262, for> <40> <257, ifx> <61> <256, 1> <59> <257, ifx> <258, <=> <257, printread> <41> <263, do> <257, ifx> <61> <257, ifx> <43> <256, 1> <-1>" ));
        tc.add(new TestCase("&&&", "input is not part of the language"));
        tc.add(new TestCase("17&5", "input is not part of the language"));

    }

    @Test
    public void lexicalScan() {
        for (TestCase elem : tc) {
            Lexer lex = new Lexer();
            String msg = "testing string: " + elem.inputString + " expecting: " + elem.shouldBeResult;
            String resultString = "";

            BufferedReader br = new BufferedReader(new StringReader(elem.inputString));
            Token tok;
            do {
                tok = lex.lexicalScan(br);
                resultString += tok + " ";
                if(tok == null) // the lexer found an error
                    break;
            } while (tok.tag != Tag.EOF);

            if(tok != null)
                Assert.assertEquals(msg, elem.shouldBeResult, resultString.substring(0,resultString.length() - 1) );
            else
                Assert.assertEquals(msg, elem.shouldBeResult, "input is not part of the language");

        }
    }
}