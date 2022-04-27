// Generated from TestExpr.g4 by ANTLR 4.9.3
package io.github.afezeria.freedao.processor.core.antlr;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TestExprParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, EQUAL_OP=3, COMPARISON_OP=4, LOGICAL_OP=5, NULL=6, NUMBER=7, 
		CHAR=8, TEXT=9, BOOLEAN=10, INVOKE_CHAIN=11, WS=12;
	public static final int
		RULE_stat = 0, RULE_expr = 1, RULE_simpleExpr = 2;
	private static String[] makeRuleNames() {
		return new String[] {
			"stat", "expr", "simpleExpr"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", null, null, null, "'null'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "EQUAL_OP", "COMPARISON_OP", "LOGICAL_OP", "NULL", 
			"NUMBER", "CHAR", "TEXT", "BOOLEAN", "INVOKE_CHAIN", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "TestExpr.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TestExprParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class StatContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode EOF() { return getToken(TestExprParser.EOF, 0); }
		public StatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stat; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TestExprVisitor ) return ((TestExprVisitor<? extends T>)visitor).visitStat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatContext stat() throws RecognitionException {
		StatContext _localctx = new StatContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_stat);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(6);
			expr(0);
			setState(7);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public SimpleExprContext simpleExpr() {
			return getRuleContext(SimpleExprContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode LOGICAL_OP() { return getToken(TestExprParser.LOGICAL_OP, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TestExprVisitor ) return ((TestExprVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(15);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INVOKE_CHAIN:
				{
				setState(10);
				simpleExpr();
				}
				break;
			case T__0:
				{
				setState(11);
				match(T__0);
				setState(12);
				expr(0);
				setState(13);
				match(T__1);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(22);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExprContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_expr);
					setState(17);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(18);
					match(LOGICAL_OP);
					setState(19);
					expr(2);
					}
					} 
				}
				setState(24);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class SimpleExprContext extends ParserRuleContext {
		public SimpleExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleExpr; }
	 
		public SimpleExprContext() { }
		public void copyFrom(SimpleExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LiteralLogicalOperationContext extends SimpleExprContext {
		public TerminalNode INVOKE_CHAIN() { return getToken(TestExprParser.INVOKE_CHAIN, 0); }
		public TerminalNode LOGICAL_OP() { return getToken(TestExprParser.LOGICAL_OP, 0); }
		public TerminalNode BOOLEAN() { return getToken(TestExprParser.BOOLEAN, 0); }
		public LiteralLogicalOperationContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TestExprVisitor ) return ((TestExprVisitor<? extends T>)visitor).visitLiteralLogicalOperation(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VarOperationContext extends SimpleExprContext {
		public Token op;
		public List<TerminalNode> INVOKE_CHAIN() { return getTokens(TestExprParser.INVOKE_CHAIN); }
		public TerminalNode INVOKE_CHAIN(int i) {
			return getToken(TestExprParser.INVOKE_CHAIN, i);
		}
		public TerminalNode EQUAL_OP() { return getToken(TestExprParser.EQUAL_OP, 0); }
		public TerminalNode COMPARISON_OP() { return getToken(TestExprParser.COMPARISON_OP, 0); }
		public TerminalNode LOGICAL_OP() { return getToken(TestExprParser.LOGICAL_OP, 0); }
		public VarOperationContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TestExprVisitor ) return ((TestExprVisitor<? extends T>)visitor).visitVarOperation(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LiteralComparisonOperationContext extends SimpleExprContext {
		public Token op;
		public Token right;
		public TerminalNode INVOKE_CHAIN() { return getToken(TestExprParser.INVOKE_CHAIN, 0); }
		public TerminalNode EQUAL_OP() { return getToken(TestExprParser.EQUAL_OP, 0); }
		public TerminalNode COMPARISON_OP() { return getToken(TestExprParser.COMPARISON_OP, 0); }
		public TerminalNode NUMBER() { return getToken(TestExprParser.NUMBER, 0); }
		public TerminalNode TEXT() { return getToken(TestExprParser.TEXT, 0); }
		public TerminalNode CHAR() { return getToken(TestExprParser.CHAR, 0); }
		public LiteralComparisonOperationContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TestExprVisitor ) return ((TestExprVisitor<? extends T>)visitor).visitLiteralComparisonOperation(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NullCheckContext extends SimpleExprContext {
		public TerminalNode INVOKE_CHAIN() { return getToken(TestExprParser.INVOKE_CHAIN, 0); }
		public TerminalNode EQUAL_OP() { return getToken(TestExprParser.EQUAL_OP, 0); }
		public TerminalNode NULL() { return getToken(TestExprParser.NULL, 0); }
		public NullCheckContext(SimpleExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TestExprVisitor ) return ((TestExprVisitor<? extends T>)visitor).visitNullCheck(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SimpleExprContext simpleExpr() throws RecognitionException {
		SimpleExprContext _localctx = new SimpleExprContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_simpleExpr);
		int _la;
		try {
			setState(37);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new LiteralComparisonOperationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(25);
				match(INVOKE_CHAIN);
				setState(26);
				((LiteralComparisonOperationContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==EQUAL_OP || _la==COMPARISON_OP) ) {
					((LiteralComparisonOperationContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(27);
				((LiteralComparisonOperationContext)_localctx).right = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NUMBER) | (1L << CHAR) | (1L << TEXT))) != 0)) ) {
					((LiteralComparisonOperationContext)_localctx).right = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 2:
				_localctx = new VarOperationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(28);
				match(INVOKE_CHAIN);
				setState(29);
				((VarOperationContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQUAL_OP) | (1L << COMPARISON_OP) | (1L << LOGICAL_OP))) != 0)) ) {
					((VarOperationContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(30);
				match(INVOKE_CHAIN);
				}
				break;
			case 3:
				_localctx = new NullCheckContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(31);
				match(INVOKE_CHAIN);
				setState(32);
				match(EQUAL_OP);
				setState(33);
				match(NULL);
				}
				break;
			case 4:
				_localctx = new LiteralLogicalOperationContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(34);
				match(INVOKE_CHAIN);
				setState(35);
				match(LOGICAL_OP);
				setState(36);
				match(BOOLEAN);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\16*\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\5\3\22\n\3\3\3\3\3\3\3"+
		"\7\3\27\n\3\f\3\16\3\32\13\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\5\4(\n\4\3\4\2\3\4\5\2\4\6\2\5\3\2\5\6\3\2\t\13\3\2\5\7\2+\2\b"+
		"\3\2\2\2\4\21\3\2\2\2\6\'\3\2\2\2\b\t\5\4\3\2\t\n\7\2\2\3\n\3\3\2\2\2"+
		"\13\f\b\3\1\2\f\22\5\6\4\2\r\16\7\3\2\2\16\17\5\4\3\2\17\20\7\4\2\2\20"+
		"\22\3\2\2\2\21\13\3\2\2\2\21\r\3\2\2\2\22\30\3\2\2\2\23\24\f\3\2\2\24"+
		"\25\7\7\2\2\25\27\5\4\3\4\26\23\3\2\2\2\27\32\3\2\2\2\30\26\3\2\2\2\30"+
		"\31\3\2\2\2\31\5\3\2\2\2\32\30\3\2\2\2\33\34\7\r\2\2\34\35\t\2\2\2\35"+
		"(\t\3\2\2\36\37\7\r\2\2\37 \t\4\2\2 (\7\r\2\2!\"\7\r\2\2\"#\7\5\2\2#("+
		"\7\b\2\2$%\7\r\2\2%&\7\7\2\2&(\7\f\2\2\'\33\3\2\2\2\'\36\3\2\2\2\'!\3"+
		"\2\2\2\'$\3\2\2\2(\7\3\2\2\2\5\21\30\'";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}