/*
    Laboratorio No. 3 - Recursive Descent Parsing
    CC4 - Compiladores

    Clase que representa el parser

    Actualizado: agosto de 2021, Luis Cu
*/

import java.util.LinkedList;
import java.util.Stack;

public class Parser {

    // Puntero next que apunta al siguiente token
    private int next;
    // Stacks para evaluar en el momento
    private Stack<Double> operandos;
    private Stack<Token> operadores;
    // LinkedList de tokens
    private LinkedList<Token> tokens;

    // Funcion que manda a llamar main para parsear la expresion
    public boolean parse(LinkedList<Token> tokens) {
        this.tokens = tokens;
        this.next = 0;
        this.operandos = new Stack<Double>();
        this.operadores = new Stack<Token>();

        // Recursive Descent Parser
        // Imprime si el input fue aceptado
        System.out.println("Aceptada? " + S());

        // Shunting Yard Algorithm
        // Imprime el resultado de operar el input
        System.out.println("Resultado: " + this.operandos.peek() + "\n");

        // Verifica si terminamos de consumir el input
        if(this.next != this.tokens.size()) {
            return false;
        }
        return true;
    }

    // Verifica que el id sea igual que el id del token al que apunta next
    // Si si avanza el puntero es decir lo consume.
    private boolean term(int id) {
        if(this.next < this.tokens.size() && this.tokens.get(this.next).equals(id)) {
            
            // Codigo para el Shunting Yard Algorithm
            
            if (id == Token.NUMBER) {
				// Encontramos un numero
				// Debemos guardarlo en el stack de operandos
				operandos.push( this.tokens.get(this.next).getVal() );

			} else if (id == Token.SEMI) {
				// Encontramos un punto y coma
				// Debemos operar todo lo que quedo pendiente
				while (!this.operadores.empty()) {
					popOp();
				}
				
			} else {
				// Encontramos algun otro token, es decir un operador
				// Lo guardamos en el stack de operadores
				// Que pushOp haga el trabajo, no quiero hacerlo yo aqui
				pushOp( this.tokens.get(this.next) );
			}
			

            this.next++;
            return true;
        }
        return false;
    }

    // Funcion que verifica la precedencia de un operador
    private int pre(Token op) {
        if (op == null) return -1;
        switch(op.getId()) {
        	case Token.PLUS:
        	case Token.MINUS:
        		return 1;

        	case Token.MULT:
        	case Token.DIV:
        	case Token.MOD:
        		return 2;

            case Token.EXP:
                return 3;

            case Token.UNARY:
                return 4;

            case Token.LPAREN:
                return 0;
        	default:
        		return -1;
        }
    }

    private void popOp() {
        Token op = this.operadores.pop();

        if (this.operandos.size() < 2) {
            return;
        }


        if (op.equals(Token.PLUS)) {
        	double a = this.operandos.pop();
        	double b = this.operandos.pop();
        	// print para debug, quitarlo al terminar
        	//System.out.println("suma " + b + " + " + a);
        	this.operandos.push(a + b);

        } else if (op.equals(Token.MINUS)) {
            double a = this.operandos.pop();
            double b = this.operandos.pop();
            // print para debug, quitarlo al terminar
            //System.out.println("resta " + b + " - " + a);
            this.operandos.push(b - a);

        } else if (op.equals(Token.MULT)) {
            double a = this.operandos.pop();
            double b = this.operandos.pop();
            // print para debug, quitarlo al terminar
            //System.out.println("mult " + b + " * " + a);
            this.operandos.push(b * a);

        } else if (op.equals(Token.DIV)) {
            double a = this.operandos.pop();
            double b = this.operandos.pop();
            // print para debug, quitarlo al terminar
            //System.out.println("div " + b + " / " + a);
            this.operandos.push(b / a);

        } else if (op.equals(Token.MOD)) {
            double a = this.operandos.pop();
            double b = this.operandos.pop();
            // print para debug, quitarlo al terminar
            //System.out.println("mod " + b + " % " + a);
            this.operandos.push(b % a);
            
        } else if (op.equals(Token.EXP)) {
            double a = this.operandos.pop();
            double b = this.operandos.pop();
            // print para debug, quitarlo al terminar
            //System.out.println("potencia " + b + " ^ " + a);
            this.operandos.push(Math.pow(b, a));
        }
    }

    private void pushOp(Token op) {
        if (op.getId() == Token.LPAREN) {
            operadores.push(op);
            return;
        }

        if (op.getId() == Token.RPAREN) {
            while (!operadores.isEmpty() && operadores.peek().getId() != Token.LPAREN) {
                popOp();
            }

            if (operadores.isEmpty()) {
                return;
            }

            operadores.pop();
            return;
        }
        

        while (!this.operadores.isEmpty() && this.operadores.peek().getId() != Token.LPAREN) {
            Token top = this.operadores.peek();
            int precendencia_top = pre(top);
            int precedencia_actual = pre(op);

            if (precendencia_top > precedencia_actual) {
                popOp();

            } else if (precendencia_top == precedencia_actual) {
                if (op.getId() != Token.EXP) {
                    popOp();
                } else {
                    break;
                } 
            } else {
                break;
            }
        }
        this.operadores.push(op);

        /* TODO: Su codigo aqui */

        /* Casi todo el codigo para esta seccion se vera en clase */
    	
    	// Si no hay operandos automaticamente ingresamos op al stack

    	// Si si hay operandos:
    		// Obtenemos la precedencia de op
        	// Obtenemos la precedencia de quien ya estaba en el stack
        	// Comparamos las precedencias y decidimos si hay que operar
        	// Es posible que necesitemos un ciclo aqui, una vez tengamos varios niveles de precedencia
        	// Al terminar operaciones pendientes, guardamos op en stack

    }

    private boolean S() {
        return E() && term(Token.SEMI);
    }

    private boolean E() {
        if (!T()) return false;
        return E2();
    }

    /* TODO: sus otras funciones aqui */

    private boolean E2() {
        while (this.next < this.tokens.size()) {
            int id = this.tokens.get(this.next).getId();
            if (id == Token.PLUS || id == Token.MINUS) {
                if (!term(id))  return false;
                if (!T()) return false;
            } else {
                break;
            }
        }
        return true;
    }

    private boolean T() {
        if (!F()) return false;
        return T2();
    }

    private boolean T2() {
        while (this.next < this.tokens.size()) {
            int id = this.tokens.get(this.next).getId();
            if (id == Token.MULT || id == Token.DIV || id == Token.MOD) {
                if (!term(id)) return false;
                if (!F()) return false;
            } else {
                break;
            }
        }
        return true;
    }

    private boolean F() {
        if (!G()) return false;
        return F2();
    }

    private boolean F2() {
        if (this.next < this.tokens.size()) {
            int id = this.tokens.get(this.next).getId();
            if (id == Token.EXP) {
                if (!term(id)) return false;
                if (!F()) return false;
            }
        }
        return true;
    }

    private boolean G() {
        if (this.next >= this.tokens.size()) return false;
        int id = this.tokens.get(this.next).getId();
        if (id == Token.MINUS) {
            if (!term(Token.MINUS)) return false;
            return G();

        } else if (id == Token.NUMBER) {
            return term(Token.NUMBER);
            
        } else if (id == Token.LPAREN) {
            if (!term(Token.LPAREN)) return false;
            if (!E()) return false;
            if (!term(Token.RPAREN)) return false;
            return true;
        } else {
            return false;
        }
    }
        
}
