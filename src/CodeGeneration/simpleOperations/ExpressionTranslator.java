package CodeGeneration.simpleOperations;

import CodeGeneration.CodeGenerator;
import CodeGeneration.OpCode;
import LexAnalysis.Lexers.Lexer;
import LexAnalysis.NumberTok;
import LexAnalysis.Tag;
import LexAnalysis.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ExpressionTranslator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    CodeGenerator codeGen = new CodeGenerator("src/CodeGeneration/simpleOperations/Output.j");

    public ExpressionTranslator(Lexer l, BufferedReader br){
        lex = l;
        pbr = br;
        move();
    }

    void move(){
        look = lex.lexicalScan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s){
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t){
        if(look.tag == t){
            if(look.tag != Tag.EOF) move();
        }else error("syntax error");
    }

    public void prog(){
        // gui( PROG -> print ( EXPR ) EOF ) = { print }
        if(look.tag == Tag.PRINT) {
            move();
            match('(');
            expr();
            codeGen.emit(OpCode.invokestatic, 1);
            match(')');
            match(Tag.EOF);
            try {
                codeGen.toJasmin();
            } catch (IOException ioe) {
                System.out.println("IO ERROR\n");
            }
        }else{
            error("syntax error: '" + look + "' is not in gui(PROG -> print ( EXPR ) EOF )" );
        }

        //TODO

    }

    private void expr(){
        //gui(expr -> term exprp) = { (, NUM }
        if(look.tag == '(' || look.tag == Tag.NUM){
            term();
            exprp();
        }else{
            error("syntax error: '" + look + "' is not in gui(expr -> term exprp)" );
        }
    }



    //TODO
    private void exprp(){
        switch(look.tag){
            case '+': //gui(exprp -> + term exprp) = +
                move();
                term();
                codeGen.emit(OpCode.iadd);
                exprp();
                break;
            case '-': //gui(exprp -> - term exprp) = -
                move();
                term();
                codeGen.emit(OpCode.isub);
                exprp();
                break;
            case ')': //gui(exprp -> epsilon) = { ), EOF}
            case Tag.EOF:
                //do nothing
                break;
            default:
                error("syntax error: '" + look + "' is not in gui(exprp -> ...)");
        }

    }

    private void term(){
        if(look.tag == '(' || look.tag == Tag.NUM){ //gui(term -> fact termp) = { (, EOF}
            fact();
            termp();
        }else{
            error("syntax error: '" + look + "' is not in gui(term -> fact termp)" );
        }
    }

    private void termp(){
        switch(look.tag){
            case '*': //gui(termp -> * fact termp) = *
                move();
                fact();
                codeGen.emit(OpCode.imul);
                termp();
                break;
            case '/': //gui(termp -> / fact termp) = /
                move();
                fact();
                codeGen.emit(OpCode.idiv);
                termp();
                break;
            case '+': //gui(termp -> epsilon) = { +, -, ), EOF}
            case '-':
            case ')':
            case Tag.EOF:
                //do nothing
                break;
            default:
                error("syntax error: '" + look + "' is not in gui(termp -> ...)");
        }
    }

    private void fact(){
        if(look.tag == '('){ // gui(fact ->( expr ) } = (
            move();
            expr();
            match(Token.rpt.tag);
        }else if(look.tag == Tag.NUM){ //gui(fact -> NUM) = NUM
            codeGen.emit(OpCode.ldc, Integer.valueOf( ((NumberTok) look).lexeme ));
            move();
        }else{
            error("syntax error: '" + look + "' is not in gui(fact-> ...)");
        }

    }

    public static void main(String[] args){
        Lexer lex = new Lexer();
        String path = "src/CodeGeneration/simpleOperations/mathOperations.txt";
        try{
            BufferedReader br = new BufferedReader(new FileReader(path));
            ExpressionTranslator translator = new ExpressionTranslator(lex, br);
            translator.prog();
            System.out.println("input ok");
            br.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

}
