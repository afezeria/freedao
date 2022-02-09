// Generated from TestExpr.g4 by ANTLR 4.9.3
package com.github.afezeria.freedao.processor.core.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TestExprLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, EQUAL_OP=3, COMPARISON_OP=4, LOGICAL_OP=5, NULL=6, NUMBER=7, 
		CHAR=8, TEXT=9, BOOLEAN=10, INVOKE_CHAIN=11, WS=12;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "EQUAL_OP", "COMPARISON_OP", "LOGICAL_OP", "NULL", "NUMBER", 
			"CHAR", "TEXT", "BOOLEAN", "INVOKE_CHAIN", "WS"
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


	public TestExprLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "TestExpr.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\16\u00bf\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\5\4$\n\4\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\5\5,\n\5\3\6\3\6\3\6\3\6\5\6\62\n\6\3\7\3\7\3\7\3"+
		"\7\3\7\3\b\3\b\7\b;\n\b\f\b\16\b>\13\b\3\b\5\bA\n\b\3\b\5\bD\n\b\3\b\3"+
		"\b\7\bH\n\b\f\b\16\bK\13\b\3\b\3\b\6\bO\n\b\r\b\16\bP\3\b\3\b\5\bU\n\b"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\tc\n\t\3\n\3\n\3\n"+
		"\3\n\3\n\6\nj\n\n\r\n\16\nk\3\n\3\n\3\n\7\nq\n\n\f\n\16\nt\13\n\3\n\3"+
		"\n\3\n\6\ny\n\n\r\n\16\nz\3\n\3\n\3\n\7\n\u0080\n\n\f\n\16\n\u0083\13"+
		"\n\3\n\3\n\5\n\u0087\n\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\5\13\u0092\n\13\3\f\3\f\7\f\u0096\n\f\f\f\16\f\u0099\13\f\3\f\3\f\3\f"+
		"\7\f\u009e\n\f\f\f\16\f\u00a1\13\f\3\f\3\f\7\f\u00a5\n\f\f\f\16\f\u00a8"+
		"\13\f\3\f\3\f\3\f\7\f\u00ad\n\f\f\f\16\f\u00b0\13\f\5\f\u00b2\n\f\7\f"+
		"\u00b4\n\f\f\f\16\f\u00b7\13\f\3\r\6\r\u00ba\n\r\r\r\16\r\u00bb\3\r\3"+
		"\r\4r\u0081\2\16\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31"+
		"\16\3\2\n\3\2\63;\3\2\62;\4\2))^^\3\2^^\4\2C\\c|\6\2\62;C\\aac|\7\2//"+
		"\62;C\\aac|\4\2\13\13\"\"\2\u00dc\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2"+
		"\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3"+
		"\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\3\33\3\2\2\2\5\35\3\2\2"+
		"\2\7#\3\2\2\2\t+\3\2\2\2\13\61\3\2\2\2\r\63\3\2\2\2\17T\3\2\2\2\21b\3"+
		"\2\2\2\23\u0086\3\2\2\2\25\u0091\3\2\2\2\27\u0093\3\2\2\2\31\u00b9\3\2"+
		"\2\2\33\34\7*\2\2\34\4\3\2\2\2\35\36\7+\2\2\36\6\3\2\2\2\37 \7?\2\2 $"+
		"\7?\2\2!\"\7#\2\2\"$\7?\2\2#\37\3\2\2\2#!\3\2\2\2$\b\3\2\2\2%,\7@\2\2"+
		"&\'\7@\2\2\',\7?\2\2(,\7>\2\2)*\7>\2\2*,\7?\2\2+%\3\2\2\2+&\3\2\2\2+("+
		"\3\2\2\2+)\3\2\2\2,\n\3\2\2\2-.\7(\2\2.\62\7(\2\2/\60\7~\2\2\60\62\7~"+
		"\2\2\61-\3\2\2\2\61/\3\2\2\2\62\f\3\2\2\2\63\64\7p\2\2\64\65\7w\2\2\65"+
		"\66\7n\2\2\66\67\7n\2\2\67\16\3\2\2\28<\t\2\2\29;\t\3\2\2:9\3\2\2\2;>"+
		"\3\2\2\2<:\3\2\2\2<=\3\2\2\2=A\3\2\2\2><\3\2\2\2?A\7\62\2\2@8\3\2\2\2"+
		"@?\3\2\2\2AC\3\2\2\2BD\7N\2\2CB\3\2\2\2CD\3\2\2\2DU\3\2\2\2EI\t\2\2\2"+
		"FH\t\3\2\2GF\3\2\2\2HK\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JL\3\2\2\2KI\3\2\2\2"+
		"LN\7\60\2\2MO\t\3\2\2NM\3\2\2\2OP\3\2\2\2PN\3\2\2\2PQ\3\2\2\2QU\3\2\2"+
		"\2RS\7/\2\2SU\5\17\b\2T@\3\2\2\2TE\3\2\2\2TR\3\2\2\2U\20\3\2\2\2VW\7)"+
		"\2\2WX\n\4\2\2Xc\7)\2\2YZ\7)\2\2Z[\7^\2\2[\\\7^\2\2\\]\3\2\2\2]c\7)\2"+
		"\2^_\7)\2\2_`\7^\2\2`a\n\5\2\2ac\7)\2\2bV\3\2\2\2bY\3\2\2\2b^\3\2\2\2"+
		"c\22\3\2\2\2de\7$\2\2e\u0087\7$\2\2fi\7$\2\2gh\7^\2\2hj\7^\2\2ig\3\2\2"+
		"\2jk\3\2\2\2ki\3\2\2\2kl\3\2\2\2lm\3\2\2\2m\u0087\7$\2\2nr\7$\2\2oq\13"+
		"\2\2\2po\3\2\2\2qt\3\2\2\2rs\3\2\2\2rp\3\2\2\2su\3\2\2\2tr\3\2\2\2ux\n"+
		"\5\2\2vw\7^\2\2wy\7^\2\2xv\3\2\2\2yz\3\2\2\2zx\3\2\2\2z{\3\2\2\2{|\3\2"+
		"\2\2|\u0087\7$\2\2}\u0081\7$\2\2~\u0080\13\2\2\2\177~\3\2\2\2\u0080\u0083"+
		"\3\2\2\2\u0081\u0082\3\2\2\2\u0081\177\3\2\2\2\u0082\u0084\3\2\2\2\u0083"+
		"\u0081\3\2\2\2\u0084\u0085\n\5\2\2\u0085\u0087\7$\2\2\u0086d\3\2\2\2\u0086"+
		"f\3\2\2\2\u0086n\3\2\2\2\u0086}\3\2\2\2\u0087\24\3\2\2\2\u0088\u0089\7"+
		"v\2\2\u0089\u008a\7t\2\2\u008a\u008b\7w\2\2\u008b\u0092\7g\2\2\u008c\u008d"+
		"\7h\2\2\u008d\u008e\7c\2\2\u008e\u008f\7n\2\2\u008f\u0090\7u\2\2\u0090"+
		"\u0092\7g\2\2\u0091\u0088\3\2\2\2\u0091\u008c\3\2\2\2\u0092\26\3\2\2\2"+
		"\u0093\u0097\t\6\2\2\u0094\u0096\t\7\2\2\u0095\u0094\3\2\2\2\u0096\u0099"+
		"\3\2\2\2\u0097\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098\u00b5\3\2\2\2\u0099"+
		"\u0097\3\2\2\2\u009a\u00b1\7\60\2\2\u009b\u009f\t\6\2\2\u009c\u009e\t"+
		"\7\2\2\u009d\u009c\3\2\2\2\u009e\u00a1\3\2\2\2\u009f\u009d\3\2\2\2\u009f"+
		"\u00a0\3\2\2\2\u00a0\u00b2\3\2\2\2\u00a1\u009f\3\2\2\2\u00a2\u00a6\7$"+
		"\2\2\u00a3\u00a5\t\b\2\2\u00a4\u00a3\3\2\2\2\u00a5\u00a8\3\2\2\2\u00a6"+
		"\u00a4\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a9\3\2\2\2\u00a8\u00a6\3\2"+
		"\2\2\u00a9\u00b2\7$\2\2\u00aa\u00ae\t\3\2\2\u00ab\u00ad\t\3\2\2\u00ac"+
		"\u00ab\3\2\2\2\u00ad\u00b0\3\2\2\2\u00ae\u00ac\3\2\2\2\u00ae\u00af\3\2"+
		"\2\2\u00af\u00b2\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b1\u009b\3\2\2\2\u00b1"+
		"\u00a2\3\2\2\2\u00b1\u00aa\3\2\2\2\u00b2\u00b4\3\2\2\2\u00b3\u009a\3\2"+
		"\2\2\u00b4\u00b7\3\2\2\2\u00b5\u00b3\3\2\2\2\u00b5\u00b6\3\2\2\2\u00b6"+
		"\30\3\2\2\2\u00b7\u00b5\3\2\2\2\u00b8\u00ba\t\t\2\2\u00b9\u00b8\3\2\2"+
		"\2\u00ba\u00bb\3\2\2\2\u00bb\u00b9\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00bd"+
		"\3\2\2\2\u00bd\u00be\b\r\2\2\u00be\32\3\2\2\2\32\2#+\61<@CIPTbkrz\u0081"+
		"\u0086\u0091\u0097\u009f\u00a6\u00ae\u00b1\u00b5\u00bb\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}