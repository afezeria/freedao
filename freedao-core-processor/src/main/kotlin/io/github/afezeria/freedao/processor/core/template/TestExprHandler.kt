package io.github.afezeria.freedao.processor.core.template

import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.isAssignable
import io.github.afezeria.freedao.processor.core.isSameType
import io.github.afezeria.freedao.processor.core.type
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTree
import java.util.*


/**
 *
 */
class TestExprHandler(val context: TemplateHandler, val test: String) : io.github.afezeria.freedao.processor.core.antlr.TestExprBaseVisitor<Unit>() {
    private val builder = StringBuilder()
    private val condFlag = context.createInternalFlag(Boolean::class.type, false)
    private val parameters = mutableListOf<Any>()

    fun handle(): String {
        val listener = object : BaseErrorListener() {
            override fun syntaxError(
                recognizer: Recognizer<*, *>?,
                offendingSymbol: Any?,
                line: Int,
                charPositionInLine: Int,
                msg: String?,
                e: RecognitionException?,
            ) {
                throwWithExpr("$line:$charPositionInLine $msg")
            }
        }

        val input = CharStreams.fromString(test)
        val lexer = io.github.afezeria.freedao.processor.core.antlr.TestExprLexer(input).apply {
            removeErrorListeners()
            addErrorListener(listener)
        }
        val tokens = CommonTokenStream(lexer)
        val parser = io.github.afezeria.freedao.processor.core.antlr.TestExprParser(tokens).apply {
            removeErrorListeners()
            addErrorListener(listener)
        }

        val tree: ParseTree = parser.stat()
        this.visit(tree)
        context.currentScope {
            addStatement("$condFlag = $builder", *parameters.toTypedArray())
        }
        return condFlag
    }

    fun throwWithExpr(msg: String): Nothing {
        throw HandlerException("[$test] $msg")
    }

    override fun visitExpr(ctx: io.github.afezeria.freedao.processor.core.antlr.TestExprParser.ExprContext) {
        ctx.apply {
            if (childCount == 3) {
                if (children[0].text == "(") {
                    super.visitExpr(ctx)
                } else {
                    builder.append("(")
                    visitExpr(expr(0))
                    builder.append(" ${logicalOp(LOGICAL_OP().text)} ")
                    visitExpr(expr(1))
                    builder.append(")")
                }
            } else {
                builder.append("(")
                super.visitExpr(ctx)
                builder.append(")")
            }
        }
    }

    override fun visitLiteralComparisonOperation(ctx: io.github.afezeria.freedao.processor.core.antlr.TestExprParser.LiteralComparisonOperationContext) {
        val expectType = when (ctx.right.type) {
            io.github.afezeria.freedao.processor.core.antlr.TestExprParser.NUMBER -> {
                if (ctx.right.text.endsWith("L")) {
                    Long::class
                } else if (ctx.right.text.contains(".")) {
                    Double::class
                } else {
                    Int::class
                }
            }
            io.github.afezeria.freedao.processor.core.antlr.TestExprParser.TEXT -> {
                String::class
            }
            io.github.afezeria.freedao.processor.core.antlr.TestExprParser.CHAR -> {
                Char::class
            }
            else -> {
                throw IllegalStateException("unreachable")
            }
        }.type
        val (chainText, _) = context.createInvokeChain(ctx.INVOKE_CHAIN().text, expectType)
        if (ctx.op.type == io.github.afezeria.freedao.processor.core.antlr.TestExprParser.EQUAL_OP) {
            builder.append("${if (ctx.op.text == "==") "" else "!"}\$T.equals(${chainText}, ${ctx.right.text})")
            parameters.add(Objects::class.java)
        } else {
            builder.append("(${chainText}).compareTo(${ctx.right.text}) ${ctx.op.text} 0")
        }

    }

    override fun visitLiteralLogicalOperation(ctx: io.github.afezeria.freedao.processor.core.antlr.TestExprParser.LiteralLogicalOperationContext) {
        val (chainText, _) = context.createInvokeChain(ctx.INVOKE_CHAIN().text, Boolean::class.type)
        builder.append("$chainText ${logicalOp(ctx.getChild(1).text)} ${ctx.getChild(2).text}")
    }

    override fun visitNullCheck(ctx: io.github.afezeria.freedao.processor.core.antlr.TestExprParser.NullCheckContext) {
        val (chainText, _) = context.createInvokeChain(ctx.INVOKE_CHAIN().text)
        builder.append("$chainText ${ctx.getChild(1).text} null")
    }

    override fun visitVarOperation(ctx: io.github.afezeria.freedao.processor.core.antlr.TestExprParser.VarOperationContext) {
        when (ctx.op.type) {
            io.github.afezeria.freedao.processor.core.antlr.TestExprParser.EQUAL_OP -> {
                val (leftText, _) = context.createInvokeChain(ctx.INVOKE_CHAIN(0).text)
                val (rightText, _) = context.createInvokeChain(ctx.INVOKE_CHAIN(1).text)
                builder.append("${if (ctx.op.text == "==") "" else "!"}\$T.equals(${leftText}, ${rightText})")
                parameters.add(Objects::class.java)
            }
            io.github.afezeria.freedao.processor.core.antlr.TestExprParser.LOGICAL_OP -> {
                val (leftText, _) = context.createInvokeChain(ctx.INVOKE_CHAIN(0).text, Boolean::class.type)
                val (rightText, _) = context.createInvokeChain(ctx.INVOKE_CHAIN(1).text, Boolean::class.type)
                builder.append("$leftText ${logicalOp(ctx.op.text)} $rightText")
            }
            io.github.afezeria.freedao.processor.core.antlr.TestExprParser.COMPARISON_OP -> {
                val (leftText, leftType) = context.createInvokeChain(ctx.INVOKE_CHAIN(0).text)
                val (rightText, rightType) = context.createInvokeChain(ctx.INVOKE_CHAIN(1).text)
                if (!leftType.isSameType(Any::class) && !leftType.isAssignable(Comparable::class)) {
                    throwWithExpr("${ctx.INVOKE_CHAIN(0).text} is not comparable")
                }
                if (!rightType.isSameType(Any::class) && !rightType.isAssignable(Comparable::class)) {
                    throwWithExpr("${ctx.INVOKE_CHAIN(1).text} is not comparable")
                }
                if (!leftType.isSameType(Any::class) && !rightType.isSameType(Any::class) && !leftType.isSameType(
                        rightType)
                ) {
                    throwWithExpr("Unable to compare ${ctx.INVOKE_CHAIN(0)}:${leftType} and ${
                        ctx.INVOKE_CHAIN(1)
                    }:${rightType}")
                }
                builder.append("((Comparable)$leftText).compareTo(${rightText}) ${ctx.op.text} 0")
            }
            else -> throw IllegalStateException("unreachable")
        }
    }

    private fun logicalOp(string: String) = when (string) {
        "and" -> "&&"
        "or" -> "||"
        else -> throw IllegalStateException("unreachable")
    }
}
