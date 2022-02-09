// Generated from TestExpr.g4 by ANTLR 4.9.3
package com.github.afezeria.freedao.processor.core.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TestExprParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TestExprVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TestExprParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat(TestExprParser.StatContext ctx);
	/**
	 * Visit a parse tree produced by {@link TestExprParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(TestExprParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literalComparisonOperation}
	 * labeled alternative in {@link TestExprParser#simpleExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralComparisonOperation(TestExprParser.LiteralComparisonOperationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varOperation}
	 * labeled alternative in {@link TestExprParser#simpleExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarOperation(TestExprParser.VarOperationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nullCheck}
	 * labeled alternative in {@link TestExprParser#simpleExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullCheck(TestExprParser.NullCheckContext ctx);
	/**
	 * Visit a parse tree produced by the {@code literalLogicalOperation}
	 * labeled alternative in {@link TestExprParser#simpleExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralLogicalOperation(TestExprParser.LiteralLogicalOperationContext ctx);
}