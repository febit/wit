
//----------------------------------------------------
// The following code was generated by CUP v0.12for-WebitScript-only
// Wed Dec 18 09:10:08 CST 2013
//----------------------------------------------------

package webit.script.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import webit.script.asm.AsmMethodCaller;
import webit.script.asm.AsmMethodCallerManager;
import webit.script.core.VariantManager.VarAddress;
import webit.script.core.ast.*;
import webit.script.core.ast.expressions.*;
import webit.script.core.ast.method.*;
import webit.script.core.ast.operators.*;
import webit.script.core.ast.statements.*;
import webit.script.exceptions.ParseException;
import webit.script.util.ClassNameBand;
import webit.script.util.ClassUtil;
import webit.script.util.StatementUtil;
import webit.script.util.StringUtil;
import webit.script.util.collection.Stack;

/** CUP v0.12for-WebitScript-only generated parser.
  * @version Wed Dec 18 09:10:08 CST 2013
  */
public class Parser extends lr_parser {



  /** Production table. */
  static final short PRODUCTION_TABLE[][] = loadFromDataFile("Production");

  /** Parse-action table. */
  static final short[][] ACTION_TABLE = loadFromDataFile("Action");

  /** <code>reduce_goto</code> table. */
  static final short[][] REDUCE_TABLE = loadFromDataFile("Reduce");
  /** Indicates start state. */
  final static int START_STATE = 0;



    private int getLabelIndex(String label){
        Integer index;
        if ((index = labelsIndexMap.get(label)) == null) {
            labelsIndexMap.put(label, index = ++currentLabelIndex);
        }
        return index;
    }

    private Expression createContextValue(VarAddress addr, int line, int column) {
        switch (addr.type) {
            case VarAddress.ROOT:
                return new RootContextValue(addr.index, line, column);
            case VarAddress.GLOBAL:
                return new GlobalValue(this.engine.getGlobalManager(), addr.index, line, column);
            case VarAddress.CONST:
                return new DirectValue(addr.constValue, line, column);
            default: //VarAddress.CONTEXT
                if (addr.upstairs == 0) {
                    return new CurrentContextValue(addr.index, line, column);
                } else {
                    return new ContextValue(addr.upstairs, addr.index, line, column);
                }
        }
    }
    
    private Expression createContextValueAtUpstair(int upstair, String name, int line, int column) {
        return createContextValue(varmgr.locateAtUpstair(name, upstair, line, column), line, column);
    }
    
    private Expression createContextValue(int upstair, String name, int line, int column) {
        return createContextValue(varmgr.locate(name, upstair, this.locateVarForce, line, column), line, column);
    }

    private FunctionPart createFunctionPart(int line, int column) {
        varmgr.push();
        varmgr.pushVarWall();
        return new FunctionPart(varmgr.assignVariant("arguments", line, column), line, column); 
    }

    private CommonMethodDeclareExpression popNativeNewArrayDeclare(Class componentType, int line, int column) {
        Class classWaitCheck = componentType;
        while (classWaitCheck.isArray()) {
            classWaitCheck = classWaitCheck.getComponentType();
        }

        if (classWaitCheck == Void.class || classWaitCheck == Void.TYPE) {
            throw new ParseException("ComponentType must not Void.class", line, column);
        }

        final String path;
        if (engine.checkNativeAccess(path = (classWaitCheck.getName().concat(".[]"))) == false) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        return new CommonMethodDeclareExpression(new NativeNewArrayDeclare(componentType), line, column);
    }

    private Expression createNativeStaticValue(ClassNameBand classNameBand, int line, int column) {
        if (classNameBand.size() < 2) {
            throw new ParseException("native static need a filed name.", line, column);
        }
        String fieldName = classNameBand.pop();
        Class cls = nativeImportMgr.toClass(classNameBand, line, column);
        Field field;
        try {
            field = cls.getField(fieldName);
        } catch (NoSuchFieldException ex) {
            throw new ParseException(StringUtil.concat("No such field: ", classNameBand.getClassPureName(), "#", fieldName), line, column);
        }
        if (ClassUtil.isStatic(field)) {
            ClassUtil.setAccessible(field);
            if (ClassUtil.isFinal(field)) {
                try {
                    return new DirectValue(field.get(null), line, column);
                } catch (Exception ex) {
                    throw new ParseException(StringUtil.concat("Failed to static field value: ", classNameBand.getClassPureName(), "#", fieldName), ex, line, column);
                }
            } else {
                return new NativeStaticValue(field, line, column);
            }
        } else {
            throw new ParseException(StringUtil.concat("No a static field: ", classNameBand.getClassPureName(), "#", fieldName), line, column);
        }
    }

    private CommonMethodDeclareExpression popNativeMethodDeclare(Class clazz, String methodName, ClassNameList list, int line, int column) {

        final String path;
        if (engine.checkNativeAccess(path = (StringUtil.concat(clazz.getName(), ".", methodName))) == false) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        try {
            final Method method = ClassUtil.searchMethod(clazz, methodName, list.toArray(), false);
            AsmMethodCaller caller;
            if (engine.isEnableAsmNative()) {
                if (ClassUtil.isPublic(clazz)) {
                    if (ClassUtil.isPublic(method)) {
                        try {
                            if ((caller = AsmMethodCallerManager.getCaller(method)) == null) {
                                logger.error(StringUtil.concat("AsmMethodCaller for '", method.toString(), "' is null, and instead by NativeMethodDeclare"));
                            }
                        } catch (Exception ex) {
                            caller = null;
                            logger.error(StringUtil.concat("Generate AsmMethodCaller for '", method.toString(), "' failed, and instead by NativeMethodDeclare"), ex);
                        }
                    } else {
                        logger.warn(StringUtil.concat("'", method.toString(), "' will not use asm, since this method is not public, and instead by NativeMethodDeclare"));
                        caller = null;
                    }
                } else {
                    logger.warn(StringUtil.concat("'", method.toString(), "' will not use asm, since class is not public, and instead by NativeMethodDeclare"));
                    caller = null;
                }
            } else {
                caller = null;
            }

            return new CommonMethodDeclareExpression(caller != null
                    ? new AsmNativeMethodDeclare(caller)
                    : new NativeMethodDeclare(method),
                    line, column);

        } catch (NoSuchMethodException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        } catch (SecurityException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        }
    }

    @SuppressWarnings("unchecked")
    private CommonMethodDeclareExpression popNativeConstructorDeclare(Class clazz, ClassNameList list, int line, int column) {

        final String path;
        if (engine.checkNativeAccess(path = (clazz.getName() + ".<init>")) == false) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        try {
            final Constructor constructor = clazz.getConstructor(list.toArray());
            AsmMethodCaller caller;
            if (engine.isEnableAsmNative()) {
                if (ClassUtil.isPublic(clazz)) {
                    if (ClassUtil.isPublic(constructor)) {
                        try {
                            if ((caller = AsmMethodCallerManager.getCaller(constructor)) == null) {
                                logger.error(StringUtil.concat("AsmMethodCaller for '", constructor.toString(), "' is null, and instead by NativeConstructorDeclare"));
                            }
                        } catch (Exception ex) {
                            caller = null;
                            logger.error(StringUtil.concat("Generate AsmMethodCaller for '", constructor.toString(), "' failed, and instead by NativeConstructorDeclare"), ex);
                        }
                    } else {
                        logger.warn(StringUtil.concat("'", constructor.toString(), "' will not use asm, since this method is not public, and instead by NativeConstructorDeclare"));
                        caller = null;
                    }
                } else {
                    logger.warn(StringUtil.concat("'" + constructor.toString() + "' will not use asm, since class is not public, and instead by NativeConstructorDeclare"));
                    caller = null;
                }
            } else {
                caller = null;
            }

            return new CommonMethodDeclareExpression(caller != null
                    ? new AsmNativeMethodDeclare(caller)
                    : new NativeConstructorDeclare(constructor),
                    line, column);

        } catch (NoSuchMethodException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        } catch (SecurityException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        }
    }

    private static ResetableValueExpression castToResetableValueExpression(Expression expr) {
        if(expr instanceof ResetableValueExpression){
            return (ResetableValueExpression) expr;
        }else{
            throw new ParseException("Invalid expression to redirect out stream to, must be rewriteable", expr);
        }
    }

    private static Expression createSelfOperator(Expression lexpr, int sym, Expression rightExpr, int line, int column){
        ResetableValueExpression leftExpr = castToResetableValueExpression(lexpr);
        SelfOperator oper;
        switch (sym) {

            // (+ - * / %)=
            case Operators.PLUSEQ:
                oper = new SelfPlus(leftExpr, rightExpr, line, column);
                break;
            case Operators.MINUSEQ:
                oper = new SelfMinus(leftExpr, rightExpr, line, column);
                break;
            case Operators.MULTEQ:
                oper = new SelfMult(leftExpr, rightExpr, line, column);
                break;
            case Operators.DIVEQ:
                oper = new SelfDiv(leftExpr, rightExpr, line, column);
                break;
            case Operators.MODEQ:
                oper = new SelfMod(leftExpr, rightExpr, line, column);
                break;

            // (<< >> >>>)=
            case Operators.LSHIFTEQ:
                oper = new SelfLShift(leftExpr, rightExpr, line, column);
                break;
            case Operators.RSHIFTEQ:
                oper = new SelfRShift(leftExpr, rightExpr, line, column);
                break;
            case Operators.URSHIFTEQ:
                oper = new SelfURShift(leftExpr, rightExpr, line, column);
                break;

            // (& ^ |)=
            case Operators.ANDEQ:
                oper = new SelfBitAnd(leftExpr, rightExpr, line, column);
                break;
            case Operators.XOREQ:
                oper = new SelfBitXor(leftExpr, rightExpr, line, column);
                break;
            case Operators.OREQ:
                oper = new SelfBitOr(leftExpr, rightExpr, line, column);
                break;

            default:
                throw new ParseException("Unsupported Operator", line, column);
        }

        return StatementUtil.optimize(oper);
    }

    private static Expression createBinaryOperator(Expression leftExpr, int sym, Expression rightExpr, int line, int column) {

        BinaryOperator oper;
        switch (sym) {
            case Tokens.ANDAND: // &&
                oper = new And(leftExpr, rightExpr, line, column);
                break;
            case Tokens.AND: // &
                oper = new BitAnd(leftExpr, rightExpr, line, column);
                break;
            case Tokens.OR: // |
                oper = new BitOr(leftExpr, rightExpr, line, column);
                break;
            case Tokens.XOR: // ^
                oper = new BitXor(leftExpr, rightExpr, line, column);
                break;
            case Tokens.DIV: // /
                oper = new Div(leftExpr, rightExpr, line, column);
                break;
            case Tokens.EQEQ: // ==
                oper = new Equals(leftExpr, rightExpr, line, column);
                break;
            case Tokens.GTEQ: // >=
                oper = new GreaterEquals(leftExpr, rightExpr, line, column);
                break;
            case Tokens.GT: // >
                oper = new Greater(leftExpr, rightExpr, line, column);
                break;
            case Tokens.LSHIFT: // <<
                oper = new LShift(leftExpr, rightExpr, line, column);
                break;
            case Tokens.LTEQ: // <=
                oper = new LessEquals(leftExpr, rightExpr, line, column);
                break;
            case Tokens.LT: // <
                oper = new Less(leftExpr, rightExpr, line, column);
                break;
            case Tokens.MINUS: // -
                oper = new Minus(leftExpr, rightExpr, line, column);
                break;
            case Tokens.MOD: // %
                oper = new Mod(leftExpr, rightExpr, line, column);
                break;
            case Tokens.MULT: // *
                oper = new Mult(leftExpr, rightExpr, line, column);
                break;
            case Tokens.NOTEQ: // !=
                oper = new NotEquals(leftExpr, rightExpr, line, column);
                break;
            case Tokens.OROR: // ||
                oper = new Or(leftExpr, rightExpr, line, column);
                break;
            case Tokens.PLUS: // +
                oper = new Plus(leftExpr, rightExpr, line, column);
                break;
            case Tokens.RSHIFT: // >>
                oper = new RShift(leftExpr, rightExpr, line, column);
                break;
            case Tokens.URSHIFT: // >>>
                oper = new URShift(leftExpr, rightExpr, line, column);
                break;
            case Tokens.QUESTION_COLON: // ?:
                oper = new IfOrOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.DOTDOT: // ..
                oper = new IntStep(leftExpr, rightExpr, line, column);
                break;
            default:
                throw new ParseException("Unsupported Operator", line, column);
        }
        return StatementUtil.optimize(oper);
    }


  final Object do_action(int actionId) throws ParseException {
      Stack<Symbol> myStack = this._stack;

      /* select the action based on the action number */
      switch (actionId){
	case 116: // contextValueIdent ::= FOR DOT IDENTIFIER 
	{
		 return ("for." + (String) myStack.peek(0).value).intern(); 
	}

	case 17: // statement ::= forInPart 
	{
		 return ((AbstractForInPart) myStack.peek(0).value).pop(); 
	}

	case 18: // statement ::= IDENTIFIER COLON forInPart 
	{
		 return ((AbstractForInPart) myStack.peek(0).value).pop(getLabelIndex((String) myStack.peek(2).value)); 
	}

	case 55: // forInPart ::= forInBody ELSE block 
	{
		 return ((AbstractForInPart) myStack.peek(2).value).setElse((IBlock) myStack.peek(0).value); 
	}

	case 53: // forInBody ::= forInHead LBRACE statementList RBRACE 
	{
		 return ((AbstractForInPart) myStack.peek(3).value).setStatementList((StatementList) myStack.peek(1).value); 
	}

	case 5: // classPureName ::= classPureName DOT IDENTIFIER 
	{
		 return ((ClassNameBand) myStack.peek(2).value).append((String) myStack.peek(0).value); 
	}

	case 7: // className ::= className LBRACK RBRACK 
	{
		 return ((ClassNameBand) myStack.peek(2).value).plusArrayDepth(); 
	}

	case 122: // expressionList1 ::= expressionList1 COMMA expression 
	{
		 return ((ExpressionList) myStack.peek(2).value).add((Expression) myStack.peek(0).value); 
	}

	case 103: // expression ::= funcHeadPrepare LBRACE statementList RBRACE 
	{
		 return ((FunctionPart) myStack.peek(3).value).pop(varmgr, (StatementList) myStack.peek(1).value); 
	}

	case 104: // expression ::= funcHead RPAREN LBRACE statementList RBRACE 
	{
		 return ((FunctionPart) myStack.peek(4).value).pop(varmgr, (StatementList) myStack.peek(1).value); 
	}

	case 102: // expression ::= funcHeadPrepare LPAREN RPAREN LBRACE statementList RBRACE 
	{
		 return ((FunctionPart) myStack.peek(5).value).pop(varmgr, (StatementList) myStack.peek(1).value); 
	}

	case 48: // ifStat ::= ifPart 
	{
		 return ((IfPart) myStack.peek(0).value).pop(); 
	}

	case 49: // ifStat ::= ifPart ELSE block 
	{
		 return ((IfPart) myStack.peek(2).value).pop((IBlock) myStack.peek(0).value); 
	}

	case 50: // ifStat ::= ifPart ELSE ifStat 
	{
		 return ((IfPart) myStack.peek(2).value).pop((Statement) myStack.peek(0).value); 
	}

	case 30: // statement ::= importPart1 SEMICOLON 
	case 31: // statement ::= importPart2 SEMICOLON 
	{
		 return ((ImportPart) myStack.peek(1).value).pop(this.template); 
	}

	case 41: // importPart2 ::= importPart1 contextValueExpr EQ IDENTIFIER 
	{
		 return ((ImportPart) myStack.peek(3).value).append((String) myStack.peek(0).value, (Expression) myStack.peek(2).value); 
	}

	case 43: // importPart2 ::= importPart2 COMMA contextValueExpr EQ IDENTIFIER 
	{
		 return ((ImportPart) myStack.peek(4).value).append((String) myStack.peek(0).value, (Expression) myStack.peek(2).value); 
	}

	case 126: // mapValuePart ::= mapValuePart COMMA DIRECT_VALUE COLON expression 
	{
		 return ((MapValuePart) myStack.peek(4).value).add((Object) myStack.peek(2).value, (Expression) myStack.peek(0).value); 
	}

	case 0: // templateAST ::= statementList 
	{
		 return ((StatementList) myStack.peek(0).value).popTemplateAST(varmgr.pop()); 
	}

	case 3: // statementList ::= statementList statement 
	{
		 return ((StatementList) myStack.peek(1).value).add((Statement) myStack.peek(0).value); 
	}

	case 13: // statement ::= switchPart 
	{
		 return ((SwitchPart) myStack.peek(0).value).pop(); 
	}

	case 14: // statement ::= IDENTIFIER COLON switchPart 
	{
		 return ((SwitchPart) myStack.peek(0).value).pop(getLabelIndex((String) myStack.peek(2).value)); 
	}

	case 15: // statement ::= whilePart 
	{
		 return ((WhilePart) myStack.peek(0).value).pop(); 
	}

	case 16: // statement ::= IDENTIFIER COLON whilePart 
	{
		 return ((WhilePart) myStack.peek(0).value).pop(getLabelIndex((String) myStack.peek(2).value)); 
	}

	case 54: // forInPart ::= forInBody 
	{
		 return (AbstractForInPart) myStack.peek(0).value; 
	}

	case 6: // className ::= classPureName 
	{
		 return (ClassNameBand) myStack.peek(0).value; 
	}

	case 137: // classNameList ::= classNameList1 
	{
		 return (ClassNameList) myStack.peek(0).value; 
	}

	case 71: // expression_statementable ::= funcExecuteExpr 
	case 72: // expression ::= expression_statementable 
	case 111: // expression ::= contextValueExpr 
	{
		 return (Expression) myStack.peek(0).value; 
	}

	case 8: // statement ::= expression_statementable SEMICOLON 
	case 101: // expression ::= LPAREN expression RPAREN 
	{
		 return (Expression) myStack.peek(1).value; 
	}

	case 124: // expressionList ::= expressionList1 
	{
		 return (ExpressionList) myStack.peek(0).value; 
	}

	case 11: // statement ::= block 
	{
		 return (IBlock) myStack.peek(0).value; 
	}

	case 114: // superCount ::= superCount SUPER DOT 
	{
		 return (Integer) myStack.peek(2).value + 1; 
	}

	case 112: // expression ::= mapValue 
	{
		 return (MapValue) myStack.peek(0).value; 
	}

	case 12: // statement ::= ifStat 
	{
		 return (Statement) myStack.peek(0).value; 
	}

	case 115: // contextValueIdent ::= IDENTIFIER 
	{
		 return (String) myStack.peek(0).value; 
	}

	case 113: // superCount ::= SUPER DOT 
	{
		 return 1; 
	}

	case 9: // statement ::= SEMICOLON 
	case 21: // statement ::= varPart SEMICOLON 
	{
		 return NoneStatement.getInstance(); 
	}

	case 4: // classPureName ::= IDENTIFIER 
	{
		 return new ClassNameBand((String) myStack.peek(0).value); 
	}

	case 136: // classNameList ::= 
	{
		 return new ClassNameList(this.nativeImportMgr); 
	}

	case 121: // expressionList1 ::= expression 
	{
		 return new ExpressionList().add((Expression) myStack.peek(0).value); 
	}

	case 123: // expressionList ::= 
	{
		 return new ExpressionList(); 
	}

	case 125: // mapValuePart ::= DIRECT_VALUE COLON expression 
	{
		 return new MapValuePart().add((Object) myStack.peek(2).value, (Expression) myStack.peek(0).value); 
	}

	case 2: // statementList ::= 
	{
		 return new StatementList(); 
	}

	case 59: // switchPart0 ::= 
	{
		 return new SwitchPart(); 
	}

	case 10: // statement ::= expression PLACE_HOLDER_END 
	{
		 return placeHolderFactory.creatPlaceHolder((Expression) myStack.peek(1).value); 
	}

	case 44: // blockPrepare ::= 
	case 45: // blockPrepare2 ::= LBRACE 
	{
		 varmgr.push(); return null; 
	}

	case 1: // $START ::= templateAST EOF 
	{
		/* ACCEPT */
		this.goonParse = false;
		return myStack.peek(1).value;
	}

	case 133: // funcExecuteExpr ::= expression AT contextValueExpr LPAREN expressionList RPAREN 
	{
		Symbol funcExpr$Symbol = myStack.peek(3);
		 return new FunctionExecute((Expression) funcExpr$Symbol.value, ((ExpressionList) myStack.peek(1).value).addFirst((Expression) myStack.peek(5).value).toArray(), funcExpr$Symbol.line, funcExpr$Symbol.column); 
	}

	case 132: // funcExecuteExpr ::= expression LPAREN expressionList RPAREN 
	{
		Symbol funcExpr$Symbol = myStack.peek(3);
		 return new FunctionExecute((Expression) funcExpr$Symbol.value, ((ExpressionList) myStack.peek(1).value).toArray(), funcExpr$Symbol.line, funcExpr$Symbol.column); 
	}

	case 129: // funcHead ::= funcHeadPrepare LPAREN IDENTIFIER 
	case 130: // funcHead ::= funcHead COMMA IDENTIFIER 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 return ((FunctionPart) myStack.peek(2).value).appendArgIndexs(varmgr.assignVariant((String) ident$Symbol.value, ident$Symbol.line, ident$Symbol.column)); 
	}

	case 40: // importPart2 ::= importPart1 IDENTIFIER 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 return ((ImportPart) myStack.peek(1).value).append((String) ident$Symbol.value, createContextValue(0, (String) ident$Symbol.value, ident$Symbol.line, ident$Symbol.column)); 
	}

	case 42: // importPart2 ::= importPart2 COMMA IDENTIFIER 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 return ((ImportPart) myStack.peek(2).value).append((String) ident$Symbol.value, createContextValue(0, (String) ident$Symbol.value, ident$Symbol.line, ident$Symbol.column)); 
	}

	case 119: // contextValueExpr ::= superCount contextValueIdent 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 return createContextValue((Integer) myStack.peek(1).value, (String) ident$Symbol.value, ident$Symbol.line, ident$Symbol.column); 
	}

	case 117: // contextValueExpr ::= contextValueIdent 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 return createContextValue(0, (String) ident$Symbol.value, ident$Symbol.line, ident$Symbol.column); 
	}

	case 120: // contextValueExpr ::= superCount THIS DOT contextValueIdent 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 return createContextValueAtUpstair((Integer) myStack.peek(3).value, (String) ident$Symbol.value, ident$Symbol.line, ident$Symbol.column); 
	}

	case 118: // contextValueExpr ::= THIS DOT contextValueIdent 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 return createContextValueAtUpstair(0, (String) ident$Symbol.value, ident$Symbol.line, ident$Symbol.column); 
	}

	case 35: // varPart ::= VAR IDENTIFIER 
	case 36: // varPart ::= varPart COMMA IDENTIFIER 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 varmgr.assignVariant((String) ident$Symbol.value,ident$Symbol.line,ident$Symbol.column); return null;
	}

	case 64: // expression_statementable ::= VAR IDENTIFIER EQ expression 
	{
		Symbol ident$Symbol = myStack.peek(2);
		Symbol sym$Symbol = myStack.peek(1);
		 return new Assign(castToResetableValueExpression(createContextValue(varmgr.assignVariantAddress((String) ident$Symbol.value,ident$Symbol.line,ident$Symbol.column), ident$Symbol.line, ident$Symbol.column)), (Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 135: // classNameList1 ::= classNameList1 COMMA className 
	{
		Symbol nameBand$Symbol = myStack.peek(0);
		 return ((ClassNameList) myStack.peek(2).value).add((ClassNameBand) nameBand$Symbol.value, nameBand$Symbol.line, nameBand$Symbol.column); 
	}

	case 109: // expression ::= NATIVE classPureName 
	{
		Symbol nameBand$Symbol = myStack.peek(0);
		 return createNativeStaticValue((ClassNameBand) nameBand$Symbol.value, nameBand$Symbol.line, nameBand$Symbol.column); 
	}

	case 134: // classNameList1 ::= className 
	{
		Symbol nameBand$Symbol = myStack.peek(0);
		 return new ClassNameList(this.nativeImportMgr).add((ClassNameBand) nameBand$Symbol.value, nameBand$Symbol.line, nameBand$Symbol.column); 
	}

	case 58: // caseBlockStat ::= blockPrepare statementList 
	{
		Symbol prepare$Symbol = myStack.peek(1);
		 return ((StatementList) myStack.peek(0).value).popIBlock(varmgr.pop(), prepare$Symbol.line, prepare$Symbol.column); 
	}

	case 46: // block ::= blockPrepare2 statementList RBRACE 
	{
		Symbol prepare$Symbol = myStack.peek(2);
		 return ((StatementList) myStack.peek(1).value).popIBlock(varmgr.pop(), prepare$Symbol.line, prepare$Symbol.column); 
	}

	case 131: // funcHeadPrepare ::= FUNCTION 
	{
		Symbol sym$Symbol = myStack.peek(0);
		 return createFunctionPart(sym$Symbol.line, sym$Symbol.column); 
	}

	case 100: // expression ::= DIRECT_VALUE 
	{
		Symbol sym$Symbol = myStack.peek(0);
		 return new DirectValue((Object) sym$Symbol.value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 68: // expression_statementable ::= expression MINUSMINUS 
	{
		Symbol sym$Symbol = myStack.peek(0);
		 return new MinusMinusAfter(castToResetableValueExpression((Expression) myStack.peek(1).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 66: // expression_statementable ::= expression PLUSPLUS 
	{
		Symbol sym$Symbol = myStack.peek(0);
		 return new PlusPlusAfter(castToResetableValueExpression((Expression) myStack.peek(1).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 20: // statement ::= TEXT_STATEMENT 
	{
		Symbol sym$Symbol = myStack.peek(0);
		 return textStatementFactory.getTextStatement(template, (char[]) sym$Symbol.value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 73: // expression ::= COMP expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return StatementUtil.optimize(new BitNot((Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column)); 
	}

	case 74: // expression ::= MINUS expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return StatementUtil.optimize(new Negative((Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column)); 
	}

	case 75: // expression ::= NOT expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return StatementUtil.optimize(new Not((Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column)); 
	}

	case 78: // expression ::= expression MULT expression 
	case 79: // expression ::= expression DIV expression 
	case 80: // expression ::= expression MOD expression 
	case 81: // expression ::= expression PLUS expression 
	case 82: // expression ::= expression MINUS expression 
	case 83: // expression ::= expression LSHIFT expression 
	case 84: // expression ::= expression RSHIFT expression 
	case 85: // expression ::= expression URSHIFT expression 
	case 86: // expression ::= expression LT expression 
	case 87: // expression ::= expression LTEQ expression 
	case 88: // expression ::= expression GT expression 
	case 89: // expression ::= expression GTEQ expression 
	case 90: // expression ::= expression EQEQ expression 
	case 91: // expression ::= expression NOTEQ expression 
	case 92: // expression ::= expression AND expression 
	case 93: // expression ::= expression OR expression 
	case 94: // expression ::= expression XOR expression 
	case 95: // expression ::= expression ANDAND expression 
	case 96: // expression ::= expression DOTDOT expression 
	case 97: // expression ::= expression OROR expression 
	case 98: // expression ::= expression QUESTION_COLON expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return createBinaryOperator((Expression) myStack.peek(2).value, (Integer) sym$Symbol.value, (Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 69: // expression_statementable ::= expression SELFEQ expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return createSelfOperator((Expression) myStack.peek(2).value, (Integer) sym$Symbol.value, (Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 63: // expression_statementable ::= expression EQ expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new Assign(castToResetableValueExpression((Expression) myStack.peek(2).value), (Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 24: // statement ::= BREAK SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new Break(0, sym$Symbol.line, sym$Symbol.column); 
	}

	case 26: // statement ::= CONTINUE SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new Continue(0, sym$Symbol.line, sym$Symbol.column); 
	}

	case 37: // importPart1 ::= IMPORT expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new ImportPart((Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 128: // mapValue ::= LBRACE RBRACE 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new MapValue(new Object[0], new Expression[0], sym$Symbol.line,sym$Symbol.column); 
	}

	case 67: // expression_statementable ::= MINUSMINUS expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new MinusMinusBefore(castToResetableValueExpression((Expression) myStack.peek(0).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 65: // expression_statementable ::= PLUSPLUS expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new PlusPlusBefore(castToResetableValueExpression((Expression) myStack.peek(0).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 76: // expression ::= expression DOT IDENTIFIER 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new PropertyOperator((Expression) myStack.peek(2).value, (String) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 70: // expression_statementable ::= funcExecuteExpr EQGT expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new RedirectOutExpression((Expression) myStack.peek(2).value, castToResetableValueExpression((Expression) myStack.peek(0).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 28: // statement ::= RETURN SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new Return(null, sym$Symbol.line, sym$Symbol.column); 
	}

	case 23: // statement ::= NATIVE_IMPORT classPureName SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 nativeImportMgr.registClass((ClassNameBand) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); return NoneStatement.getInstance(); 
	}

	case 127: // mapValue ::= LBRACE mapValuePart RBRACE 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return ((MapValuePart) myStack.peek(1).value).pop(sym$Symbol.line, sym$Symbol.column); 
	}

	case 105: // expression ::= LBRACK expressionList RBRACK 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new ArrayValue(((ExpressionList) myStack.peek(1).value).toArray(), sym$Symbol.line, sym$Symbol.column); 
	}

	case 25: // statement ::= BREAK IDENTIFIER SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new Break(getLabelIndex((String) myStack.peek(1).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 27: // statement ::= CONTINUE IDENTIFIER SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new Continue(getLabelIndex((String) myStack.peek(1).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 22: // statement ::= ECHO expression SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new Echo((Expression) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 38: // importPart1 ::= IMPORT expression mapValue 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new ImportPart((Expression) myStack.peek(1).value, (MapValue) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 32: // statement ::= INCLUDE expression SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new Include((Expression) myStack.peek(1).value, null, this.template, sym$Symbol.line, sym$Symbol.column); 
	}

	case 77: // expression ::= expression LBRACK expression RBRACK 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new IndexOperator((Expression) myStack.peek(3).value, (Expression) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 19: // statement ::= block EQGT expression SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new RedirectOut((IBlock) myStack.peek(3).value, castToResetableValueExpression((Expression) myStack.peek(1).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 29: // statement ::= RETURN expression SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new Return((Expression) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 61: // switchPart0 ::= DEFAULT COLON caseBlockStat switchPart0 
	{
		Symbol sym$Symbol = myStack.peek(3);
		 return ((SwitchPart) myStack.peek(0).value).appendCase(null, (IBlock) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 99: // expression ::= expression QUESTION expression COLON expression 
	{
		Symbol sym$Symbol = myStack.peek(3);
		 return new IfOperator((Expression) myStack.peek(4).value, (Expression) myStack.peek(2).value, (Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 33: // statement ::= INCLUDE expression mapValue SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(3);
		 return new Include((Expression) myStack.peek(2).value, (MapValue) myStack.peek(1).value, this.template, sym$Symbol.line, sym$Symbol.column); 
	}

	case 106: // expression ::= NATIVE LBRACK RBRACK className 
	{
		Symbol sym$Symbol = myStack.peek(3);
		Symbol nameBand$Symbol = myStack.peek(0);
		 return popNativeNewArrayDeclare(nativeImportMgr.toClass((ClassNameBand) nameBand$Symbol.value, nameBand$Symbol.line, nameBand$Symbol.column), sym$Symbol.line, sym$Symbol.column); 
	}

	case 107: // expression ::= NATIVE LBRACK className RBRACK 
	{
		Symbol sym$Symbol = myStack.peek(3);
		Symbol nameBand$Symbol = myStack.peek(1);
		 return popNativeNewArrayDeclare(nativeImportMgr.toClass((ClassNameBand) nameBand$Symbol.value, nameBand$Symbol.line, nameBand$Symbol.column), sym$Symbol.line, sym$Symbol.column); 
	}

	case 60: // switchPart0 ::= CASE DIRECT_VALUE COLON caseBlockStat switchPart0 
	{
		Symbol sym$Symbol = myStack.peek(4);
		 return ((SwitchPart) myStack.peek(0).value).appendCase((Object) myStack.peek(3).value, (IBlock) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 47: // ifPart ::= IF LPAREN expression RPAREN block 
	{
		Symbol sym$Symbol = myStack.peek(4);
		 return new IfPart((Expression) myStack.peek(2).value, (IBlock) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 39: // importPart1 ::= IMPORT expression LBRACE expression RBRACE 
	{
		Symbol sym$Symbol = myStack.peek(4);
		 return new ImportPart((Expression) myStack.peek(3).value, (Expression) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 56: // whilePart ::= WHILE LPAREN expression RPAREN block 
	{
		Symbol sym$Symbol = myStack.peek(4);
		 return new WhilePart((Expression) myStack.peek(2).value, (IBlock) myStack.peek(0).value, true, sym$Symbol.line, sym$Symbol.column); 
	}

	case 51: // forInHead ::= FOR LPAREN IDENTIFIER COLON expression RPAREN 
	{
		Symbol sym$Symbol = myStack.peek(5);
		 return new ForInPart((String) myStack.peek(3).value, (Expression) myStack.peek(1).value, this.varmgr, sym$Symbol.line, sym$Symbol.column); 
	}

	case 34: // statement ::= INCLUDE expression LBRACE expression RBRACE SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(5);
		 return new Include((Expression) myStack.peek(4).value, (Expression) myStack.peek(2).value, this.template, sym$Symbol.line, sym$Symbol.column); 
	}

	case 110: // expression ::= NATIVE NEW classPureName LPAREN classNameList RPAREN 
	{
		Symbol sym$Symbol = myStack.peek(5);
		Symbol nameBand$Symbol = myStack.peek(3);
		 return popNativeConstructorDeclare(nativeImportMgr.toClass((ClassNameBand) nameBand$Symbol.value, nameBand$Symbol.line, nameBand$Symbol.column), (ClassNameList) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 62: // switchPart ::= SWITCH LPAREN expression RPAREN LBRACE switchPart0 RBRACE 
	{
		Symbol sym$Symbol = myStack.peek(6);
		 return ((SwitchPart) myStack.peek(1).value).setSwitchExpr((Expression) myStack.peek(4).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 57: // whilePart ::= DO block WHILE LPAREN expression RPAREN SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(6);
		 return new WhilePart((Expression) myStack.peek(2).value, (IBlock) myStack.peek(5).value, false, sym$Symbol.line, sym$Symbol.column); 
	}

	case 108: // expression ::= NATIVE classPureName DOT IDENTIFIER LPAREN classNameList RPAREN 
	{
		Symbol sym$Symbol = myStack.peek(6);
		Symbol nameBand$Symbol = myStack.peek(5);
		 return popNativeMethodDeclare(nativeImportMgr.toClass((ClassNameBand) nameBand$Symbol.value, nameBand$Symbol.line, nameBand$Symbol.column), (String) myStack.peek(3).value, (ClassNameList) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 52: // forInHead ::= FOR LPAREN IDENTIFIER COMMA IDENTIFIER COLON expression RPAREN 
	{
		Symbol sym$Symbol = myStack.peek(7);
		 return new ForMapPart((String) myStack.peek(5).value, (String) myStack.peek(3).value, (Expression) myStack.peek(1).value, this.varmgr, sym$Symbol.line, sym$Symbol.column); 
	}

	default:
		throw new ParseException("Invalid action number found in internal parse table");

      }
  }

}
