package CodeGeneration;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

public class CodeGenerator {

    LinkedList<Instruction> instructions = new LinkedList<Instruction>();

    int label = 0;
    private String outputFileName;

    public CodeGenerator(String fn){
        this.outputFileName = fn;
    }

    public void emit(OpCode opCode){
        instructions.add(new Instruction(opCode));
    }

    public void emit (OpCode opCode, int operand){
        instructions.add(new Instruction(opCode, operand));
    }

    public void emitLabel(int operand){
        emit(OpCode.label, operand);
    }

    public int newLabel(){
        return label++;
    }

    public void toJasmin() throws IOException{
        PrintWriter out = new PrintWriter(new FileWriter(this.outputFileName));
        String temp = "";
        temp = temp + header;
        while(instructions.size() > 0){
            Instruction instr = instructions.remove();
            temp = temp + instr.toJasmin();
        }
        temp = temp + footer;
        out.println(temp);
        out.flush();
        out.close();

    }



    private static final String header = ".class public Output \n"
            + ".super java/lang/Object\n"
            + "\n"
            + ".method public <init>()V\n"
            + " aload_0\n"
            + " invokenonvirtual java/lang/Object/<init>()V\n"
            + " return\n"
            + ".end method\n"
            + "\n"
            + ".method public static print(I)V\n"
            + " .limit stack 2\n"
            + " getstatic java/lang/System/out Ljava/io/PrintStream;\n"
            + " iload_0 \n"
            + " invokestatic java/lang/Integer/toString(I)Ljava/lang/String;\n"
            + " invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n"
            + " return\n"
            + ".end method\n"
            + "\n"
            + ".method public static read()I\n"
            + " .limit stack 3\n"
            + " new java/util/Scanner\n"
            + " dup\n"
            + " getstatic java/lang/System/in Ljava/io/InputStream;\n"
            + " invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V\n"
            + " invokevirtual java/util/Scanner/next()Ljava/lang/String;\n"
            + " invokestatic java/lang/Integer.parseInt(Ljava/lang/String;)I\n"
            + " ireturn\n"
            + ".end method\n"
            + "\n"
            + ".method public static run()V\n"
            + " .limit stack 1024\n"
            + " .limit locals 256\n";

    private static final String footer = " return\n"
            + ".end method\n"
            + "\n"
            + ".method public static main([Ljava/lang/String;)V\n"
            + " invokestatic Output/run()V\n"
            + " return\n"
            + ".end method\n";
}