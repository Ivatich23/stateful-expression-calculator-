
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.tokenizer.UnknownFunctionOrVariableException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet(urlPatterns = "/calc/*", name = "calcServlet")
public class CalcServlet extends HttpServlet {
    private Map<String, Double> variableMap = new ConcurrentHashMap<>();
    String expression = null;
    String doubleValue = null;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        double result = 0;
        Expression myExpression = new ExpressionBuilder(expression)
                .variables(variableMap.keySet().toArray(new String[0]))
                .build();
        myExpression.setVariables(variableMap);
        result = myExpression.evaluate();
        try (PrintWriter writer = resp.getWriter()) {
            writer.append(Integer.toString((int) result));
            resp.setStatus(HttpServletResponse.SC_OK);
        }catch (UnknownFunctionOrVariableException e){
            PrintWriter writer = resp.getWriter();
            writer.append("bad format");
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] split = req.getPathInfo().split("/");
        String uriPass = split[1];
        if (uriPass.equals("expression")) {
            if (expression != null) {
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_CREATED);
            }
            expression = req.getReader().readLine();
            if (expression.equals("bad format")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            try {
                if (variableMap.containsKey(uriPass)) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
                if (!variableMap.containsKey(uriPass)) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                }
                doubleValue = req.getReader().readLine();
                if (Double.parseDouble(doubleValue) < -10000 || Double.parseDouble(doubleValue) > 10000) {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }
                variableMap.put(uriPass, Double.valueOf(doubleValue));
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                variableMap.put(doubleValue, variableMap.get(doubleValue));
            }
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] split = req.getPathInfo().split("/");
        String uriPass = split[1];
        if (!uriPass.equals("expression")) {
            try {
                doubleValue = req.getReader().readLine();
                variableMap.remove(uriPass);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                variableMap.remove(uriPass);
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
