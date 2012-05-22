/* 
   Copyright (c) 2012, Kristofer Bengtsson, Sekvensa AB, Chalmers University of Technology
   Developed with the sponsorship of the Defense Advanced Research Projects Agency (DARPA).
   Permission is hereby granted, free of charge, to any person obtaining a copy of this data, including any
   software or models in source or binary form, specifications, algorithms, and documentation (collectively
   "the Data"), to deal in the Data without restriction, including without limitation the rights to use, copy,
   modify, merge, publish, distribute, sublicense, and/or sell copies of the Data, and to permit persons to
   whom the Data is furnished to do so, subject to the following conditions:
   The above copyright notice and this permission notice shall be included in all copies or substantial
   portions of the Data.
   THE DATA IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
   INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
   PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS,
   SPONSORS, DEVELOPERS, CONTRIBUTORS, OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
   CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
   OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE DATA OR THE USE OR
   OTHER DEALINGS IN THE DATA.
*/


package sequenceplanner.IO.optimizer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import sequenceplanner.datamodel.condition.ConditionElement;
import sequenceplanner.datamodel.condition.ConditionExpression;
import sequenceplanner.datamodel.condition.parser.ConditionToJavaStringParser;

/**
 *
 * @author kbe
 */
public enum ExpressionToJavaConverter {
    INSTANCE;
    
    public String convertConditionElement(ConditionElement expr){
        return ConditionToJavaStringParser.INSTANCE.ConvertCondition(expr);
    }
    
    public String appendExpression(String javaExpression, ConditionExpression expr){
        if (javaExpression.isEmpty()) return convertConditionElement(expr);
        return javaExpression + "&&" + convertConditionElement(expr);
    }
    
    public String appendStringExpression(String javaExpression, String expr){
        if (javaExpression.isEmpty()) return expr;
        return javaExpression + " && " + expr;
    }
    
    public List<String> convertActionExpressions(ConditionExpression expr){
        List<String> actions = new ArrayList<String>();
        if (expr.isEmpty()) return actions;
        ArrayDeque<ConditionElement> stack = new ArrayDeque<ConditionElement>();
        stack.push(expr.getExpressionRoot());
        while (!stack.isEmpty()){
            ConditionElement poper = stack.pop();
            if (poper.isExpression() && !((ConditionExpression)poper).isEmpty() ) 
                    stack.push(((ConditionExpression)poper).getExpressionRoot());
            if (poper.isStatement())
                actions.add(convertConditionElement(poper));
            if (poper.hasNextElement())
                stack.push(poper.getNextElement());
            
        }
        
        
        return actions;
    }
 
            
}
