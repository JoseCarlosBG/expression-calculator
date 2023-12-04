import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;

@WebServlet(name = "calc", value = "/calc")
public class calc extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Enumeration<String> param = request.getParameterNames();
        ArrayList<String> val= new ArrayList<>();
        String equation=request.getParameter(param.nextElement()); //Equation
        while (param.hasMoreElements()){
            val.add(param.nextElement());
        }
        for (String s: val) equation = equation.replace(s, request.getParameter(s));

        response.setContentType("text/html");//setting the content type
        PrintWriter pw;//get the stream to write the data
        pw = response.getWriter();

        pw.println(eval(equation));

//writing html in the stream
        /*pw.println("<html><body>");
        pw.println("Welcome to servlet");
        pw.println("</body></html>");*/

        pw.close();//closing the stream
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    public int eval(String str){
        return new Object(){
            int pos =-1, ch;
            void nextChar(){
                ch=(++pos<str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat){
                while (ch == ' ') nextChar();
                if (ch == charToEat){
                    nextChar();
                    return true;
                }
                return false;
            }

            int parse(){
                nextChar();
                int x = parseExpression();
                if (pos<str.length()) throw new RuntimeException("Unexpected: " +(char)ch);
                return x;
            }

            int parseExpression(){
                int x = parseTerm();
                for(;;){
                    if (eat('+')) x+=parseTerm();
                    else if (eat('-')) x-=parseTerm();
                    else return x;
                }
            }

            int parseTerm(){
                int x = parseFactor();
                for(;;){
                    if (eat('*')) x*=parseFactor();
                    else if (eat('/')) x/=parseFactor();
                    else return x;
                }
            }

            int parseFactor(){
                if (eat('+')) return +parseFactor();
                if (eat('-')) return -parseFactor();

                int x;
                int startPos=this.pos;
                if(eat('(')){
                    x=parseExpression();
                    if(!eat(')')) throw new RuntimeException("Missing: ')'");
                } else if (ch>='0' && ch<='9'){
                    while (ch>='0' && ch<='9') nextChar();
                    x = Integer.parseInt(str.substring(startPos, this.pos));
                } else if (ch>='a' && ch<='z'){
                    while (ch>='a' && ch<='z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if(eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing: ')'");
                    } else x=parseFactor();
                } else throw new RuntimeException("Unexpected: " + (char)ch);
                if (eat('^')) x = (int)Math.pow(x, parseFactor());
                return x;
            }
        }.parse();
    }
}