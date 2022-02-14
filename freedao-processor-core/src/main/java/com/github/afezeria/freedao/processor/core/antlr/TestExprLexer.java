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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\16\u00c7\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\5\4$\n\4\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\5\5,\n\5\3\6\3\6\3\6\3\6\3\6\5\6\63\n\6\3\7\3\7\3"+
		"\7\3\7\3\7\3\b\3\b\7\b<\n\b\f\b\16\b?\13\b\3\b\5\bB\n\b\3\b\5\bE\n\b\3"+
		"\b\3\b\7\bI\n\b\f\b\16\bL\13\b\3\b\3\b\6\bP\n\b\r\b\16\bQ\3\b\3\b\3\b"+
		"\6\bW\n\b\r\b\16\bX\3\b\3\b\5\b]\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\5\tk\n\t\3\n\3\n\3\n\3\n\3\n\6\nr\n\n\r\n\16\ns\3\n\3\n"+
		"\3\n\7\ny\n\n\f\n\16\n|\13\n\3\n\3\n\3\n\6\n\u0081\n\n\r\n\16\n\u0082"+
		"\3\n\3\n\3\n\7\n\u0088\n\n\f\n\16\n\u008b\13\n\3\n\3\n\5\n\u008f\n\n\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13\u009a\n\13\3\f\3\f\7"+
		"\f\u009e\n\f\f\f\16\f\u00a1\13\f\3\f\3\f\3\f\7\f\u00a6\n\f\f\f\16\f\u00a9"+
		"\13\f\3\f\3\f\7\f\u00ad\n\f\f\f\16\f\u00b0\13\f\3\f\3\f\3\f\7\f\u00b5"+
		"\n\f\f\f\16\f\u00b8\13\f\5\f\u00ba\n\f\7\f\u00bc\n\f\f\f\16\f\u00bf\13"+
		"\f\3\r\6\r\u00c2\n\r\r\r\16\r\u00c3\3\r\3\r\4z\u0089\2\16\3\3\5\4\7\5"+
		"\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\3\2\n\3\2\63;\3\2\62;\4"+
		"\2))^^\3\2^^\4\2C\\c|\6\2\62;C\\aac|\7\2//\62;C\\aac|\4\2\13\13\"\"\2"+
		"\u00e6\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2"+
		"\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3"+
		"\2\2\2\2\31\3\2\2\2\3\33\3\2\2\2\5\35\3\2\2\2\7#\3\2\2\2\t+\3\2\2\2\13"+
		"\62\3\2\2\2\r\64\3\2\2\2\17\\\3\2\2\2\21j\3\2\2\2\23\u008e\3\2\2\2\25"+
		"\u0099\3\2\2\2\27\u009b\3\2\2\2\31\u00c1\3\2\2\2\33\34\7*\2\2\34\4\3\2"+
		"\2\2\35\36\7+\2\2\36\6\3\2\2\2\37 \7?\2\2 $\7?\2\2!\"\7#\2\2\"$\7?\2\2"+
		"#\37\3\2\2\2#!\3\2\2\2$\b\3\2\2\2%,\7@\2\2&\'\7@\2\2\',\7?\2\2(,\7>\2"+
		"\2)*\7>\2\2*,\7?\2\2+%\3\2\2\2+&\3\2\2\2+(\3\2\2\2+)\3\2\2\2,\n\3\2\2"+
		"\2-.\7c\2\2./\7p\2\2/\63\7f\2\2\60\61\7q\2\2\61\63\7t\2\2\62-\3\2\2\2"+
		"\62\60\3\2\2\2\63\f\3\2\2\2\64\65\7p\2\2\65\66\7w\2\2\66\67\7n\2\2\67"+
		"8\7n\2\28\16\3\2\2\29=\t\2\2\2:<\t\3\2\2;:\3\2\2\2<?\3\2\2\2=;\3\2\2\2"+
		"=>\3\2\2\2>B\3\2\2\2?=\3\2\2\2@B\7\62\2\2A9\3\2\2\2A@\3\2\2\2BD\3\2\2"+
		"\2CE\7N\2\2DC\3\2\2\2DE\3\2\2\2E]\3\2\2\2FJ\t\2\2\2GI\t\3\2\2HG\3\2\2"+
		"\2IL\3\2\2\2JH\3\2\2\2JK\3\2\2\2KM\3\2\2\2LJ\3\2\2\2MO\7\60\2\2NP\t\3"+
		"\2\2ON\3\2\2\2PQ\3\2\2\2QO\3\2\2\2QR\3\2\2\2R]\3\2\2\2ST\t\3\2\2TV\7\60"+
		"\2\2UW\t\3\2\2VU\3\2\2\2WX\3\2\2\2XV\3\2\2\2XY\3\2\2\2Y]\3\2\2\2Z[\7/"+
		"\2\2[]\5\17\b\2\\A\3\2\2\2\\F\3\2\2\2\\S\3\2\2\2\\Z\3\2\2\2]\20\3\2\2"+
		"\2^_\7)\2\2_`\n\4\2\2`k\7)\2\2ab\7)\2\2bc\7^\2\2cd\7^\2\2de\3\2\2\2ek"+
		"\7)\2\2fg\7)\2\2gh\7^\2\2hi\n\5\2\2ik\7)\2\2j^\3\2\2\2ja\3\2\2\2jf\3\2"+
		"\2\2k\22\3\2\2\2lm\7$\2\2m\u008f\7$\2\2nq\7$\2\2op\7^\2\2pr\7^\2\2qo\3"+
		"\2\2\2rs\3\2\2\2sq\3\2\2\2st\3\2\2\2tu\3\2\2\2u\u008f\7$\2\2vz\7$\2\2"+
		"wy\13\2\2\2xw\3\2\2\2y|\3\2\2\2z{\3\2\2\2zx\3\2\2\2{}\3\2\2\2|z\3\2\2"+
		"\2}\u0080\n\5\2\2~\177\7^\2\2\177\u0081\7^\2\2\u0080~\3\2\2\2\u0081\u0082"+
		"\3\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0084\3\2\2\2\u0084"+
		"\u008f\7$\2\2\u0085\u0089\7$\2\2\u0086\u0088\13\2\2\2\u0087\u0086\3\2"+
		"\2\2\u0088\u008b\3\2\2\2\u0089\u008a\3\2\2\2\u0089\u0087\3\2\2\2\u008a"+
		"\u008c\3\2\2\2\u008b\u0089\3\2\2\2\u008c\u008d\n\5\2\2\u008d\u008f\7$"+
		"\2\2\u008el\3\2\2\2\u008en\3\2\2\2\u008ev\3\2\2\2\u008e\u0085\3\2\2\2"+
		"\u008f\24\3\2\2\2\u0090\u0091\7v\2\2\u0091\u0092\7t\2\2\u0092\u0093\7"+
		"w\2\2\u0093\u009a\7g\2\2\u0094\u0095\7h\2\2\u0095\u0096\7c\2\2\u0096\u0097"+
		"\7n\2\2\u0097\u0098\7u\2\2\u0098\u009a\7g\2\2\u0099\u0090\3\2\2\2\u0099"+
		"\u0094\3\2\2\2\u009a\26\3\2\2\2\u009b\u009f\t\6\2\2\u009c\u009e\t\7\2"+
		"\2\u009d\u009c\3\2\2\2\u009e\u00a1\3\2\2\2\u009f\u009d\3\2\2\2\u009f\u00a0"+
		"\3\2\2\2\u00a0\u00bd\3\2\2\2\u00a1\u009f\3\2\2\2\u00a2\u00b9\7\60\2\2"+
		"\u00a3\u00a7\t\6\2\2\u00a4\u00a6\t\7\2\2\u00a5\u00a4\3\2\2\2\u00a6\u00a9"+
		"\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00ba\3\2\2\2\u00a9"+
		"\u00a7\3\2\2\2\u00aa\u00ae\7$\2\2\u00ab\u00ad\t\b\2\2\u00ac\u00ab\3\2"+
		"\2\2\u00ad\u00b0\3\2\2\2\u00ae\u00ac\3\2\2\2\u00ae\u00af\3\2\2\2\u00af"+
		"\u00b1\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b1\u00ba\7$\2\2\u00b2\u00b6\t\3"+
		"\2\2\u00b3\u00b5\t\3\2\2\u00b4\u00b3\3\2\2\2\u00b5\u00b8\3\2\2\2\u00b6"+
		"\u00b4\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\u00ba\3\2\2\2\u00b8\u00b6\3\2"+
		"\2\2\u00b9\u00a3\3\2\2\2\u00b9\u00aa\3\2\2\2\u00b9\u00b2\3\2\2\2\u00ba"+
		"\u00bc\3\2\2\2\u00bb\u00a2\3\2\2\2\u00bc\u00bf\3\2\2\2\u00bd\u00bb\3\2"+
		"\2\2\u00bd\u00be\3\2\2\2\u00be\30\3\2\2\2\u00bf\u00bd\3\2\2\2\u00c0\u00c2"+
		"\t\t\2\2\u00c1\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3"+
		"\u00c4\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\u00c6\b\r\2\2\u00c6\32\3\2\2"+
		"\2\33\2#+\62=ADJQX\\jsz\u0082\u0089\u008e\u0099\u009f\u00a7\u00ae\u00b6"+
		"\u00b9\u00bd\u00c3\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}